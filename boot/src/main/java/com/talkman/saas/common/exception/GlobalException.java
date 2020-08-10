package com.talkman.saas.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalException {

        @ResponseBody
        @ExceptionHandler(ResultException.class)
        public Object handleException(ResultException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", e.getMsgCode());
            map.put("message", e.getMessage());
            return map;
        }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public Object handleException(RuntimeException e) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", ResultCode.PARAMER_EXCEPTION);
        map.put("message", "系统检测到异常："+e.getMessage());
        return map;
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", ResultCode.PARAMER_EXCEPTION);
        map.put("message", "系统检测到异常："+e.getMessage());
        return map;
    }


}
