package com.talkman.commom.base.util;


import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    public static String encode(String origin, String charsetname) {
        String resultString = null;
        resultString = new String(origin);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        if (charsetname == null || "".equals(charsetname)) {
            resultString = Hex.encodeHexString(md.digest(resultString.getBytes()));
        } else {
            try {
                resultString = Hex.encodeHexString(md.digest(resultString.getBytes(charsetname)));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return resultString;
    }

    /**
     * 提供一个MD5多次加密方法
     */
    public static String getMD5ofStr(String origString, int times) {
        String md5 = encode(origString, "utf-8");
        for (int i = 0; i < times - 1; i++) {
            md5 = encode(md5, "utf-8");
        }
        return md5;
    }

    /**
     * 获取一个文件的md5值(可处理大文件)
     *
     * @return md5 value
     */
    public static String getMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public static void main(String[] args) {
//		
///*		System.out.println("cus1:"+getMD5ofStr("cus1123456", 2));
//		System.out.println("cus2:"+getMD5ofStr("cus2123456", 2));
//		System.out.println("cus3:"+getMD5ofStr("cus3123456", 2));
//		System.out.println("cus4:"+getMD5ofStr("cus4123456", 2));
//		System.out.println("cus5:"+getMD5ofStr("cus5123456", 2));
//		System.out.println("cus6:"+getMD5ofStr("cus6123456", 2));
//		
//		System.out.println("test1:"+getMD5ofStr("test1123456", 2));
//		System.out.println("test2:"+getMD5ofStr("test2123456", 2));
//		System.out.println("test3:"+getMD5ofStr("test3123456", 2));
//		System.out.println("test4:"+getMD5ofStr("test4123456", 2));
//		System.out.println("test5:"+getMD5ofStr("test5123456", 2));
//		System.out.println("test6:"+getMD5ofStr("test6123456", 2));*/
//		
//		
//		
//		System.out.println("15150125287:" + getMD5ofStr(""+"jiaojing123456",2));
//		System.out.println("18205051190:" + getMD5ofStr(""+"123456",2));
//		System.out.println("13913130118:" + getMD5ofStr(""+"123456",2));
//		System.out.println("18362592363:" + getMD5ofStr(""+"123456",2));
//		System.out.println("15962185668:" + getMD5ofStr(""+"123456",2));
//		System.out.println("18913141580:" + getMD5ofStr(""+"123456",2));
//		System.out.println("15962342502:" + getMD5ofStr(""+"123456",2));
//		System.out.println("18896947396:" + getMD5ofStr(""+"123456",2));
//		System.out.println("18934595619:" + getMD5ofStr(""+"123456",2));
////		System.out.println("18114357925:" + getMD5ofStr(""+"123456",2));
//		System.out.println("11111111111:" + getMD5ofStr(""+"123456",2));
//		System.out.println("22222222222:" + getMD5ofStr(""+"123456",2));
//		System.out.println("18859662731:" + getMD5ofStr(""+"123456",2));
///*		System.out.println("u1:"+getMD5ofStr("u1123456", 2));
//		System.out.println("u2:"+getMD5ofStr("u2123456", 2));
//		System.out.println("u3:"+getMD5ofStr("u3123456", 2));
//		System.out.println("u4:"+getMD5ofStr("u4123456", 2));
//		System.out.println("u5:"+getMD5ofStr("u5123456", 2));
//		System.out.println("u6:"+getMD5ofStr("u6123456", 2));
//*/	}

}