package com.talkman.saas.utils;


import com.talkman.saas.common.constant.CommonConstant;
import com.talkman.saas.common.constant.UnitConstant;
import com.talkman.saas.common.exception.ResultCode;
import com.talkman.saas.common.exception.ResultException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import io.jsonwebtoken.*;

/**
 * @author doger.wang
 * @date 2019/7/11 14:14
 */
@Slf4j
public class JWTUtil {


    private static final long EXPIRE_TIME = UnitConstant.JWT_EXPIRE_TIME;

    private static final String ISSUSER = CommonConstant.JWT_ISSUSER;

    private static final String SECRET = CommonConstant.JWT_SECRET;


    public static String build(Object subject, String uId) {
        SecretKey secretKey = null;
        try {
            secretKey = Keys.hmacShaKeyFor(SECRET.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.warn("jwt secretKey生成异常");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime exp = now.plus(EXPIRE_TIME, ChronoUnit.MINUTES);
        Date nowDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date expDate = Date.from(exp.atZone(ZoneId.systemDefault()).toInstant());
        String uuid = UUID.randomUUID().toString();
        return Jwts.builder()
                .setIssuer(ISSUSER)
                .setAudience(uId)
                .setSubject(subject.toString())
                .setExpiration(expDate)
                .setIssuedAt(nowDate)
                .signWith(secretKey)
                .setId(uuid)
                .compact();

    }

    public static Claims parse(String jwt) {
        SecretKey secretKey = null;
        try {
            secretKey = Keys.hmacShaKeyFor(SECRET.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResultException(ResultCode.PARAMER_EXCEPTION,"jwt不合法");
        }
        Jws<Claims> claimsJws;
        try {
            claimsJws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwt);
        } catch (SignatureException | MalformedJwtException e) {
            throw new ResultException(ResultCode.PARAMER_EXCEPTION, "token非法或用户已在其它地点登录");
        } catch (ExpiredJwtException e) {
            throw new ResultException(ResultCode.TOKEN_EXPIRE, "账号过期，请重新登录");
        }
        Claims body = claimsJws.getBody();

        return body;

    }


}
