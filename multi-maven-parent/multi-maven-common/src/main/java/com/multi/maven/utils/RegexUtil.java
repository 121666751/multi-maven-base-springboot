package com.multi.maven.utils;

import com.multi.maven.enums.MobileOperationEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author bingo 刑天
 * @ClassName: RegexUtils
 * @Description: 正则工具类
 * @date 2016年4月25日 下午12:40:43
 */
public final class RegexUtil {
    private final static Logger logger = LoggerFactory.getLogger(RegexUtil.class);


    /**
     * 中国电信号码格式验证 手机段： 133,153,180,181,189,177,1700
     **/
    private static final String CHINA_TELECOM_PATTERN = "(^1(33|53|77|8[019])\\d{8}$)|(^1700\\d{7}$)";

    /**
     * 中国联通号码格式验证 手机段：130,131,132,155,156,185,186,145,176,1709
     **/
    private static final String CHINA_UNICOM_PATTERN = "(^1(3[0-2]|4[5]|5[56]|7[6]|8[56])\\d{8}$)|(^1709\\d{7}$)";

    /**
     * 中国移动号码格式验证
     * 手机段：134,135,136,137,138,139,150,151,152,157,158,159,182,183,184
     * ,187,188,147,178,1705
     **/
    private static final String CHINA_MOBILE_PATTERN = "(^1(3[4-9]|4[7]|5[0-27-9]|7[8]|8[2-478])\\d{8}$)|(^1705\\d{7}$)";


    /**
     * 验证Email
     *
     * @param email email地址，格式：zhangsan@sina.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkEmail(String email) {
        String regex =
                "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        return Pattern.matches(regex, email);
    }

    /**
     * @return : boolean
     * @Description : 验证整数和浮点数（正负整数和正负浮点数）
     * @Creation Date : 2016年4月27日 上午10:41:46
     * @Author : chichangchao
     */
    public static boolean checkDecimals(String decimals) {
        String regex = "\\-?\\d+(\\.\\d+)*";
        return Pattern.matches(regex, decimals);
    }

    /**
     * 验证字符串是否为正整数
     *
     * @param string 被验证的字符串
     * @param min    最小位数
     * @param max    最大位数
     * @return
     */
    public static boolean isNumber(String string, int min, int max) {
        String reg = "^[\\d]{" + min + "," + max + "}$";
        logger.info(reg);
        return Pattern.matches(reg, string);
    }


    /**
     * 验证字符串是否为正整数
     *
     * @return
     */
    public static boolean isNumber2(String string) {
        String reg = "^([1-9][0-9]*)$";
        logger.info(reg);
        return Pattern.matches(reg, string);
    }


    /**
     * ip地址
     *
     * @param string
     * @return
     */
    public static boolean isIP(String string) {
        String reg = "^([\\d]\\.|[1-9][\\d]\\.|1[\\d]{2}\\.|2[0-4][\\d]\\.|25[0-5]\\.){3}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])$";
        logger.info(reg);
        return Pattern.matches(reg, string);
    }

    /**
     * 经度坐标
     *
     * @param string
     * @return
     */
    public static boolean isLon(String string) {
        String reg = "^[-]?(\\d|([1-9]\\d)|(1[0-7]\\d)|(180))(\\.\\d*)?$";
        logger.info(reg);
        return Pattern.matches(reg, string);
    }

    /**
     * 经度坐标
     *
     * @param string
     * @return
     */
    public static boolean isLat(String string) {
        String reg = "^[-]?(\\d|([1-8]\\d)|(90))(\\.\\d*)?$";
        logger.info(reg);
        return Pattern.matches(reg, string);
    }


    /**
     * desc:验证手机号码
     * date:2017年5月22日
     * author:chepeijiang
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(mobiles);
        return matcher.matches();
    }

    /**
     * 返回类型
     * 1 中国移动
     * 2 中国联通
     * 3 中国电信
     * 0 无法识别
     **/
    public static byte getMobileOperation(String mobile) {
        //移动 判断
        if (match(CHINA_MOBILE_PATTERN, mobile)) {
            return MobileOperationEnum.CHINA_MOBILE.getCode();
        }
        //联通判断
        if (match(CHINA_UNICOM_PATTERN, mobile)) {
            return MobileOperationEnum.CHINA_UNICOM.getCode();
        }
        //电信判断
        if (match(CHINA_TELECOM_PATTERN, mobile)) {
            return MobileOperationEnum.CHINA_TELECOM.getCode();
        }
        return MobileOperationEnum.UNDEFINED.getCode();
    }

    /**
     * 执行正则表达式
     *
     * @param pat 表达式
     * @param str 待验证字符串
     * @return 返回true, 否则为false
     */
    private static boolean match(String pat, String str) {
        Pattern pattern = Pattern.compile(pat);
        Matcher match = pattern.matcher(str);
        return match.find();
    }


    public static void main(String[] args) {
//        System.out.println(isIP("141.250.250.10"));
//        System.out.println(isLon("-110.1"));


    }


}
