package com.talkman.saas.utils;


import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.payscore.PostPayment;
import com.github.binarywang.wxpay.bean.payscore.WxPayScoreResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author doger.wang
 * @date 2019/8/8 17:13
 */
@Slf4j
public class CommonUtils {

    /**
     * 列出domain所有get方法
     *
     * @param object
     * @param name
     */
    public static void listGet(Class object, String name) {
        Field[] fields = object.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String type = fields[i].getType().getName();
            String realType = type.substring(type.lastIndexOf(".") + 1);
            String value = fields[i].getName();
            String last = value.substring(0, 1).toUpperCase() + value.substring(1);
            if (last.toLowerCase().contains("serialversion")) {
                continue;
            }
            System.out.println(realType + " " + value + " = " + name + ".get" + last + "();");
        }
        System.out.println("====================================");
    }

    /**
     * 列出domain所有set方法
     *
     * @param object
     * @param name
     */
    public static void listSet(Class object, String name) {
        Field[] fields = object.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String value = fields[i].getName();
            String last = value.substring(0, 1).toUpperCase() + value.substring(1);
            if (last.toLowerCase().contains("serialversion")) {
                continue;
            }
            System.out.println(name + ".set" + last + "();");
        }
        System.out.println("====================================");
    }


    public static void copyUrlToFile(String url, String pathFile) throws Exception {
        //把外网地址更新成内网的
        String replace = url.replace(OssUtil.OSS_END_POINT_OUT, OssUtil.OSS_END_POINT);
        FileUtils.copyURLToFile(new URL(replace), new File(pathFile));
    }


    public static void toZip(String zipFileName, File file)
            throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                zipFileName)); // 创建ZipOutputStream类对象
        zip(out, file, "");
        // 调用方法
        out.close(); // 将流关闭
    }


    private static void zip(ZipOutputStream out, File f, String base)
            throws Exception {
        if (f.isDirectory()) { // 测试此抽象路径名表示的文件是否是一个目录
            File[] fl = f.listFiles(); // 获取路径数组
            out.putNextEntry(new ZipEntry(base + "/")); // 写入此目录的entry
            base = base.length() == 0 ? "" : base + "/"; // 判断参数是否为空
            for (int i = 0; i < fl.length; i++) { // 循环遍历数组中文件
                zip(out, fl[i], fl[i] + "");
            }
        } else {
            out.putNextEntry(new ZipEntry(base)); // 创建新的进入点
            // 创建FileInputStream对象
            FileInputStream in = new FileInputStream(f);
            int b; // 定义int型变量
            log.info("压缩==>" + base);
            while ((b = in.read()) != -1) { // 如果没有到达流的尾部
                out.write(b); // 将字节写入当前ZIP条目
            }
            in.close(); // 关闭流
        }
    }

    public static boolean checkPayResultIsSuccess(WxPayOrderNotifyResult result) {
        return "SUCCESS".equals(result.getReturnCode()) && "SUCCESS".equals(result.getResultCode());
    }

    public static boolean checkRefundResultIsSuccess(WxPayRefundResult result) {
        return "SUCCESS".equals(result.getReturnCode()) && "SUCCESS".equals(result.getResultCode());
    }

    public static boolean checkRefundReqInfoIsSuccess(WxPayRefundNotifyResult.ReqInfo reqInfo) {
        return "SUCCESS".equals(reqInfo.getRefundStatus());
    }

    public static boolean checkEntPayResultIsSuccess(EntPayResult result) {
        return "SUCCESS".equals(result.getReturnCode()) && "SUCCESS".equals(result.getResultCode());
    }

    public static boolean checkPayScoreResultIsSuccess(WxPayScoreResult result) {
        int amount = 0;
        List<PostPayment> postPayments = result.getPostPayments();
        for (PostPayment postPayment : postPayments) {
            amount = postPayment.getAmount() + amount;
        }
        if (amount == 0) {
            return true;
        }
        String state = result.getCollection().getState();
        return "USER_PAID".equals(state);

    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }


}
