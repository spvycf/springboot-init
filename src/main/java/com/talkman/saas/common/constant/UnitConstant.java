package com.talkman.saas.common.constant;

/**
 * @author doger.wang
 * @date 2019/7/1 18:05
 */
public class UnitConstant {
    //redis 保留时间 秒
    public static final long REDIS_KEEP_TIME = 3600;
    //jwt 保留时间 分钟
    public static final long JWT_EXPIRE_TIME = 200;
    //feign连接时间
    public static int FEIGN_CONNECT_TIME = 30000;
    //feign读时间
    public static int FEIGN_READ_TIME = 30000;

    //待支付订单支付信息保留时间  5分钟
    public static final long WAIT_PAY_TIME = 5;
    //异常消息推送 半个小时才推送一次
    public static final long ERROR_WAITPUSH_TIME = 30;


}
