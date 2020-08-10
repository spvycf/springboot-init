package com.talkman.saas.utils;

import com.talkman.saas.common.xxljob.XxlJobInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author doger.wang
 * @date 2020/1/3 16:37
 */
@Slf4j
public class XxlJobUtil {


    public static void sendJob(String url, XxlJobInfo info) {
        //对接查询开始
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        url = url + "/open/add";
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        try {
            post.setEntity(info.toHttpEntity());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            log.info("新增定时任务:" + info.toString());
            response = httpClient.execute(post);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            //System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                String res = EntityUtils.toString(responseEntity, "utf-8");
                log.info("返回内容为:" + res);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            log.error("调用job服务异常");
        } catch (ParseException e) {
            e.printStackTrace();
            log.error("调用job服务异常");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("调用job服务异常");
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("调用job服务异常");
            }
        }


    }
}
