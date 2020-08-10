package com.talkman.saas.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author doger.wang
 * @date 2019/8/6 11:50
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

/*    public static boolean isHasEmpty(String...params){
        boolean flag=false;
        for (String param : params) {
            if (isEmpty(param)){
                flag=true;
                break;
            }
        }
        return flag;
    }*/

    public static boolean isEmpty(CharSequence cs){
        if (null==cs){
            return true;
        }
        String s = deleteWhitespace(cs.toString());
        return s.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs){
        return !isEmpty(cs);
    }

    public static boolean isAnyEmpty(CharSequence... css) {
        if (ArrayUtils.isEmpty(css)) {
            return false;
        } else {
            CharSequence[] var1 = css;
            int var2 = css.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                CharSequence cs = var1[var3];
                if (isEmpty(cs)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isAllEmpty(CharSequence... css) {
        if (ArrayUtils.isEmpty(css)) {
            return true;
        } else {
            CharSequence[] var1 = css;
            int var2 = css.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                CharSequence cs = var1[var3];
                if (isNotEmpty(cs)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isEmptyList(List list) {
        return list == null || list.size() == 0;
    }

    public static boolean isNotEmptyList(List list) {
        return !isEmptyList(list);
    }

    public static boolean isEmptyAllObject(Object data) {
        if ((data == null) || ("".equals(data.toString().trim())) || ("null".equals(data.toString().trim().toLowerCase()))) {
            return (data == null) || ("".equals(data.toString().trim())) || ("null".equals(data.toString().trim().toLowerCase()));
        } else if (data instanceof Map) {
            Map map = (Map) data;
            return (data == null) || (map.size() == 0);
        } else if (data instanceof Collection) {
            Collection cc = (Collection) data;
            return (data == null) || (cc.size() == 0);
        } else if (data == null || (data instanceof String && data.equals("[]"))) {
            return true;
        } else {
            return (data == null) || ("".equals(data.toString().trim()));
        }


    }

    public static String changeAmount(BigDecimal amount, int length) {
        if (length <= 0) {
            length = 0;
        }
        if (null == amount) {
            amount = new BigDecimal(0);
        }
        ;
        //如果传入了null值则默认赋0
        return amount.setScale(length, BigDecimal.ROUND_HALF_UP) + "";

    }

    public static boolean isIdCard(String idCard) {
        String EXP_IdCard = "(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}$)";
        Pattern pattern = Pattern.compile(EXP_IdCard);
        Matcher matcher = pattern.matcher(idCard);
        return matcher.matches();
    }
}
