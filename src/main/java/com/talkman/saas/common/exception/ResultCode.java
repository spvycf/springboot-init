package com.talkman.saas.common.exception;

/**
 * @author doger.wang
 * @date 2019/7/1 18:07
 */
public class ResultCode {
    //-2 参数错误
    public static final Integer PARAMER_EXCEPTION = -2;
    //-1 token过期
    public static final Integer TOKEN_EXPIRE = -1;
    //0 错误
    public static final Integer FAIL = 0;
    //1 成功
    public static final Integer SUCCESS = 1;

    public static final Integer NO_AUTHENTICATION = -401;
}
