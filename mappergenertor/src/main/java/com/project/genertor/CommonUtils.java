package com.project.genertor;

import com.project.genertor.domain.BaseScenicspot;
import com.project.genertor.domain.EarRentorder;

import java.lang.reflect.Field;

/**
 * @author doger.wang
 * @date 2019/8/8 17:13
 */
public class CommonUtils {

    /**列出domain所有get方法
     * @param object
     * @param name
     */
    public static void listGet(Class object,String name){
        Field[] fields = object.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String type = fields[i].getType().getName();
            String realType = type.substring(type.lastIndexOf(".")+1);
            String value = fields[i].getName();
            String last = value.substring(0, 1).toUpperCase() + value.substring(1);
            if (last.toLowerCase().contains("serialversionuid")) {
                continue;
            }
            System.out.println(realType+" "+value+" = "+name+".get"+last+"();");
        }
        System.out.println("====================================");
    }

    /**列出domain所有set方法
     * @param object
     * @param name
     */
    public static void listSet(Class object,String name){
        Field[] fields = object.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String value = fields[i].getName();
            String last = value.substring(0, 1).toUpperCase() + value.substring(1);
            if (last.toLowerCase().contains("serialversionuid")) {
                continue;
            }
            System.out.println(name+".set"+last+"();");
        }
        System.out.println("====================================");
    }

    public static void main(String[] args) {
        CommonUtils.listSet(EarRentorder.class,"eorder");

    }
}
