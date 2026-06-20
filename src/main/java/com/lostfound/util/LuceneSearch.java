package com.lostfound.util;

import com.lostfound.model.LostItem;
import com.lostfound.model.FoundItem;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Apache Lucene 全文检索工具类
 * 提供失物招领系统的全文索引重建和搜索功能
 * 使用单例模式（Singleton）确保全局只有一个索引实例
 * 索引存储在系统临时目录中
 */
public class LuceneSearch {
    // 单例实例
    private static LuceneSearch instance;
    // Lucene 索引存储目录（文件系统临时目录）
    private Path indexDir;
    // 标准分析器（支持英文分词，适用于中文需配置 IK Analyzer 等）
    private StandardAnalyzer analyzer;

    // 私有构造方法：初始化索引目录
    private LuceneSearch() {
        try {
            // 在系统临时目录中创建索引文件夹
            indexDir = Files.createTempDirectory("lucene-index");
            // 创建标准分析器实例
            analyzer = new StandardAnalyzer();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create Lucene index directory", e);
        }
    }

    /**
     * 获取 LuceneSearch 单例实例（线程安全）
     * @return LuceneSearch 实例
     */
    public static synchronized LuceneSearch getInstance() {
        if (instance == null) {
            instance = new LuceneSearch();
        }
        return instance;
    }

    /**
     * 重建全文索引
     * 清除旧索引，重新添加所有失物和招领记录
     * @param lostItems 失物列表
     * @param foundItems 招领列表
     * @throws Exception 索引操作异常
     */
    public void rebuildIndex(List<LostItem> lostItems, List<FoundItem> foundItems) throws Exception {
        // 打开文件系统索引目录
        Directory directory = FSDirectory.open(indexDir);
        // 创建索引写入配置（使用标准分析器）
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter writer = new IndexWriter(directory, config)) {
            writer.deleteAll(); // 清除旧索引
            // 遍历失物列表，为每条记录创建索引文档
            for (LostItem item : lostItems) {
                Document doc = new Document();
                // StringField：精确匹配字段，不拆分，用于 ID 和类型过滤
                doc.add(new StringField("id", item.getId().toString(), Field.Store.YES));
                doc.add(new StringField("type", "lost", Field.Store.YES));
                // TextField：可拆分的文本字段，用于全文搜索
                doc.add(new TextField("item_name", item.getItemName() != null ? item.getItemName() : "", Field.Store.YES));
                doc.add(new TextField("description", item.getDescription() != null ? item.getDescription() : "", Field.Store.YES));
                doc.add(new TextField("category", item.getCategory() != null ? item.getCategory() : "", Field.Store.YES));
                doc.add(new TextField("location", item.getLocation() != null ? item.getLocation() : "", Field.Store.YES));
                // content：综合搜索字段（不存储，仅用于索引）
                doc.add(new TextField("content", (item.getItemName() + " " + item.getDescription() + " " + item.getCategory() + " " + item.getLocation()), Field.Store.NO));
                writer.addDocument(doc);
            }
            // 遍历招领列表，为每条记录创建索引文档
            for (FoundItem item : foundItems) {
                Document doc = new Document();
                doc.add(new StringField("id", item.getId().toString(), Field.Store.YES));
                doc.add(new StringField("type", "found", Field.Store.YES));
                doc.add(new TextField("item_name", item.getItemName() != null ? item.getItemName() : "", Field.Store.YES));
                doc.add(new TextField("description", item.getDescription() != null ? item.getDescription() : "", Field.Store.YES));
                doc.add(new TextField("category", item.getCategory() != null ? item.getCategory() : "", Field.Store.YES));
                doc.add(new TextField("location", item.getLocation() != null ? item.getLocation() : "", Field.Store.YES));
                doc.add(new TextField("content", (item.getItemName() + " " + item.getDescription() + " " + item.getCategory() + " " + item.getLocation()), Field.Store.NO));
                writer.addDocument(doc);
            }
        }
    }

    /**
     * 标准全文搜索（精确匹配）
     * @param keyword 搜索关键词
     * @param maxResults 最大返回结果数
     * @return 搜索结果列表
     * @throws Exception 搜索异常
     */
    public List<SearchResult> search(String keyword, int maxResults) throws Exception {
        List<SearchResult> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }
        // 打开索引目录
        Directory directory = FSDirectory.open(indexDir);
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            // 使用查询解析器解析关键词（在 content 字段中搜索）
            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(keyword);
            TopDocs topDocs = searcher.search(query, maxResults);
            // 遍历搜索结果
            for (ScoreDoc sd : topDocs.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                results.add(new SearchResult(
                        doc.get("id"),
                        doc.get("type"),
                        doc.get("item_name"),
                        sd.score
                ));
            }
        }
        return results;
    }

    /**
     * 模糊全文搜索（多字段匹配）
     * 在多个字段（物品名称、描述、类别、地点、综合内容）中搜索
     * 使用 BooleanQuery 的 SHOULD 子句实现 OR 逻辑，适合匹配相似物品
     * @param keyword 搜索关键词
     * @param maxResults 最大返回结果数
     * @return 搜索结果列表
     * @throws Exception 搜索异常
     */
    public List<SearchResult> fuzzySearch(String keyword, int maxResults) throws Exception {
        List<SearchResult> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }
        Directory directory = FSDirectory.open(indexDir);
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            // 构建布尔查询：在多个字段中搜索，使用 SHOULD 实现 OR 逻辑
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            String[] fields = {"item_name", "description", "category", "location", "content"};
            for (String field : fields) {
                QueryParser parser = new QueryParser(field, analyzer);
                Query q = parser.parse(keyword);
                builder.add(q, BooleanClause.Occur.SHOULD); // SHOULD = OR 逻辑
            }
            Query query = builder.build();
            TopDocs topDocs = searcher.search(query, maxResults);
            for (ScoreDoc sd : topDocs.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                results.add(new SearchResult(
                        doc.get("id"),
                        doc.get("type"),
                        doc.get("item_name"),
                        sd.score
                ));
            }
        }
        return results;
    }

    /**
     * 搜索结果内部类
     * 存储一条搜索结果的元数据：ID、类型、名称、相关性分数
     */
    public static class SearchResult {
        private String id;      // 记录 ID
        private String type;    // 类型：lost 或 found
        private String itemName; // 物品名称
        private float score;     // 相关性分数（越高越匹配）

        public SearchResult(String id, String type, String itemName, float score) {
            this.id = id;
            this.type = type;
            this.itemName = itemName;
            this.score = score;
        }

        public String getId() { return id; }
        public String getType() { return type; }
        public String getItemName() { return itemName; }
        public float getScore() { return score; }
    }
}
