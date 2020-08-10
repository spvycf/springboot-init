package com.talkman.saas.config;


import com.talkman.saas.common.constant.CommonConstant;
import com.talkman.saas.common.exception.ResultCode;
import com.talkman.saas.common.exception.ResultException;
import com.talkman.saas.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;

/**
 * @author doger.wang
 * @date 2019/6/28 16:58
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final String Header_Token = CommonConstant.Authentication;

    public static AuthInterceptor authInterceptor;

    @PostConstruct
    public void init() {
        authInterceptor = this;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //token校验
        String jwt = request.getHeader(Header_Token);
        if (StringUtils.isEmpty(jwt)) {
            log.info("用户登录信息为空");
            throw new ResultException(ResultCode.PARAMER_EXCEPTION, "用户登录信息为空");
        }
        //jwt校验
        Claims claims = JWTUtil.parse(jwt);
        String uid = claims.getAudience();
        //redis对比
        Boolean bool = redisTemplate.hasKey(uid);
        if (!bool) {
            throw new ResultException(ResultCode.TOKEN_EXPIRE, "账号已过期请重新登录");
        }
        String value = redisTemplate.opsForValue().get(uid);
        if (!jwt.equals(value)) {
            throw new ResultException(ResultCode.TOKEN_EXPIRE, "该账号在其它地点登录，当前登录状态已失效");
        }
        Class<? extends HttpServletRequest> requestClass = request.getClass();
        Field request1 = requestClass.getDeclaredField("request");
        request1.setAccessible(true);
        Object o = request1.get(request);
        Field coyoteRequest = o.getClass().getDeclaredField("coyoteRequest");
        coyoteRequest.setAccessible(true);
        Object o1 = coyoteRequest.get(o);
        Field headers = o1.getClass().getDeclaredField("headers");
        headers.setAccessible(true);
        MimeHeaders o2 = (MimeHeaders)headers.get(o1);
        o2.addValue(CommonConstant.UId).setString(uid);



        if (request != null && ServletFileUpload.isMultipartContent(request)) {
            ServletRequestContext context = new ServletRequestContext(request);
            long size = context.contentLength();
            if (size > 100000000) {
                throw new ResultException(ResultCode.PARAMER_EXCEPTION, "文件大于100M");
            }
        }
        return true;
/*        HandlerMethod handlerMethod = null;
        try {
            handlerMethod = (HandlerMethod) handler;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResultException(ResultCode.FAIL, "资源路径不存在");
        }
        Method method = handlerMethod.getMethod();*/
/*        Auth auth = method.getAnnotation(Auth.class);
        if (auth == null) {
            // 如果注解为null, 说明不需要拦截, 直接放过
            return true;
        }*/
/*
        String tour = request.getHeader(CommonConstant.Fromtalkman);
        if ("guidetour".equals(tour)) {
            log.info("语音导游目前免验证" + request.getRequestURI());
            return true;
        }
        String token = request.getHeader(Header_Token);
        String authValue = auth.value();
        Result result = authInterceptor.authFeignApi.checkAuth(token, authValue);
        boolean flag = (boolean) result.getData();
        //log.info("鉴权结果==>"+flag);
        if (!flag) {
            //返回错误结果
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            JSONObject res = new JSONObject();
            res.put("code", ResultCode.NO_AUTHENTICATION);
            res.put("message", "您没有访问此资源的权限!");
            PrintWriter out = null;
            out = response.getWriter();
            out.write(res.toString());
            out.flush();
            out.close();
            return false;

        }*/
        //return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
