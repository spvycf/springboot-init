package com.talkman.saas.common.exception;


public class ResultException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    //信息对应码
    private Integer msgCode;

    private String message;


    public ResultException(Integer msgCode, String message) {
        super(message);

        this.msgCode = msgCode;
        this.message = message;

    }

    public Integer getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(Integer msgCode) {
        this.msgCode = msgCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}