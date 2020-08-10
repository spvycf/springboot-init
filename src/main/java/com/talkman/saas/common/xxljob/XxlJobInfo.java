package com.talkman.saas.common.xxljob;

import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * xxl-job info
 *
 * @author xuxueli  2016-1-12 18:25:49
 */
@Data
public class XxlJobInfo {

    private static final long serialVersionUID = 89621565571L;

    private int id;                // 主键ID

    private int jobGroup;        // 执行器主键ID
    private String jobCron;        // 任务执行CRON表达式
    private String jobDesc;

    private Date addTime;
    private Date updateTime;

    private String author;        // 负责人
    private String alarmEmail;    // 报警邮件

    private String executorRouteStrategy;    // 执行器路由策略
    private String executorHandler;            // 执行器，任务Handler名称
    private String executorParam;            // 执行器，任务参数
    private String executorBlockStrategy;    // 阻塞处理策略
    private int executorTimeout;            // 任务执行超时时间，单位秒
    private int executorFailRetryCount;        // 失败重试次数

    private String glueType;        // GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
    private String glueSource;        // GLUE源代码
    private String glueRemark;        // GLUE备注
    private Date glueUpdatetime;    // GLUE更新时间

    private String childJobId;        // 子任务ID，多个逗号分隔

    private int triggerStatus;        // 调度状态：0-停止，1-运行
    private long triggerLastTime;    // 上次调度时间
    private long triggerNextTime;    // 下次调度时间


    public HttpEntity toHttpEntity() throws UnsupportedEncodingException {
        List<NameValuePair> pairs = new ArrayList<>();
        //pairs.add(new BasicNameValuePair("amount",amount));
        pairs.add(new BasicNameValuePair("jobGroup", jobGroup + ""));
        pairs.add(new BasicNameValuePair("jobDesc", jobDesc));
        pairs.add(new BasicNameValuePair("executorRouteStrategy", executorRouteStrategy));
        pairs.add(new BasicNameValuePair("jobCron", jobCron));
        pairs.add(new BasicNameValuePair("glueType", glueType));
        pairs.add(new BasicNameValuePair("executorHandler", executorHandler));
        pairs.add(new BasicNameValuePair("executorBlockStrategy", executorBlockStrategy));
        pairs.add(new BasicNameValuePair("executorTimeout", executorTimeout + ""));
        pairs.add(new BasicNameValuePair("executorFailRetryCount", executorFailRetryCount + ""));
        pairs.add(new BasicNameValuePair("author", author));
        pairs.add(new BasicNameValuePair("glueRemark", glueRemark));
        pairs.add(new BasicNameValuePair("triggerStatus", "1"));
        pairs.add(new BasicNameValuePair("executorParam", executorParam));
        return new UrlEncodedFormEntity(pairs, "utf-8");
    }


}
