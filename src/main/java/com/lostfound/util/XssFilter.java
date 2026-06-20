package com.lostfound.util;

import org.owasp.encoder.Encode;

/**
 * XSS 过滤器工具类
 * 使用 OWASP Java Encoder 对用户输入进行编码过滤，防止跨站脚本攻击
 * 所有来自用户表单、URL 参数、数据库输入的数据都应经过此类过滤
 * 再输出到 HTML 页面或 JavaScript 环境中
 */
public class XssFilter {

    /**
     * 将输入字符串进行 HTML 实体编码
     * 将 < > " ' & 等特殊字符转义为 HTML 实体，防止在 HTML 内容中执行恶意脚本
     * @param input 原始输入字符串
     * @return 编码后的安全字符串
     */
    public static String filter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Encode.forHtml(input);
    }

    /**
     * 将输入字符串进行 HTML 属性编码
     * 用于输出到 HTML 标签属性（如 value、title、alt 等）中
     * 比 HTML 内容编码更严格，防止通过属性注入脚本
     * @param input 原始输入字符串
     * @return 编码后的安全字符串
     */
    public static String filterForAttribute(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Encode.forHtmlAttribute(input);
    }

    /**
     * 将输入字符串进行 JavaScript 编码
     * 用于输出到 JavaScript 代码或 JSON 字符串中
     * 防止通过 JS 字符串注入执行恶意脚本
     * @param input 原始输入字符串
     * @return 编码后的安全字符串
     */
    public static String filterForJavaScript(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Encode.forJavaScript(input);
    }

    /**
     * 移除输入字符串中的所有 HTML 标签
     * 使用正则表达式匹配 <...> 并替换为空字符串
     * @param input 原始输入字符串
     * @return 去除 HTML 标签后的纯文本
     */
    public static String stripTags(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("<[^>]*>", "");
    }

    /**
     * 综合净化方法：先去除 HTML 标签，再进行 HTML 实体编码
     * 这是最安全的处理方式，适用于所有用户输入的表单数据
     * 推荐在 Servlet 中处理表单提交时使用此方法
     * @param input 原始输入字符串
     * @return 双重净化后的安全字符串
     */
    public static String sanitize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        // 第一步：移除 HTML 标签
        String stripped = stripTags(input);
        // 第二步：对纯文本进行 HTML 编码
        return Encode.forHtml(stripped);
    }
}
