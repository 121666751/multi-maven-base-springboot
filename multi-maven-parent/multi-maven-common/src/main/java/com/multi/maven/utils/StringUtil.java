package com.multi.maven.utils;


/**
 * 字符串操作工具类
 *
 * @author YangSongbo
 * @version 1.0 2013-12-02
 */
public class StringUtil {
    /**
     * 当参数str为null，返�?"
     * 当参数str不为null，返回str
     *
     * @param str 字符�?
     * @return 转换后的str
     */
    public static String toNotNull(String str) {
        String inner = str;
        if (str == null) {
            inner = "";
        }
        return inner;
    }

    /**
     * 按字典顺序比较两个字符串
     *
     * @param s1 字符�?
     * @param s2 字符�?
     * @return 如果参数字符串等于此字符串，则返�?0 �?
     * 如果按字典顺序此字符串小于字符串参数，则返回�?��小于 0 的�?;
     * 如果按字典顺序此字符串大于字符串参数，则返回�?��大于 0 的�?.
     * @see String compareTo()
     */
    public static int compareString(String s1, String s2) {
        if ((s1 == null) && (s2 == null))
            return 0;
        if ((s1 == null) && (s2 != null))
            return -1;
        if ((s1 != null) && (s2 == null)) {
            return 1;
        }
        if (s1 != null) {
            return s1.compareTo(s2);
        }
        return 0;
    }

    /**
     * 判断字符串的长度是否大于0
     *
     * @param str 字符�?
     * @return 长度大于0，返回ture,否则返回false
     */
    public static boolean hasLength(String str) {
        return (str != null) && (str.length() > 0);
    }

    /**
     * �?��字符串中是否有可显示的字�?
     *
     * @param str 目标字符�?
     * @return 为空白字符串、NULL返回false
     * 单个或连续的空格，该方法会返回false
     */
    public static boolean hasText(String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0))
            return false;
        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否为�?
     *
     * @param str
     * @return 为空返回 true
     */
    public static boolean isNull(String str) {
        if (str != null && !"".equals(str)) {
            return true;
        }
        return false;
    }


}
