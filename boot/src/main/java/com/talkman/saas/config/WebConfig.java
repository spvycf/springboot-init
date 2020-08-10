package com.talkman.saas.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author doger.wang
 * @date 2019/6/28 17:16
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
   @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new AuthInterceptor())
                    .addPathPatterns("/**")
                    .excludePathPatterns("/sys/**","**/login");
        }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解决静态资源无法访问
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        // 解决swagger无法访问
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // 解决swagger的js文件无法访问
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
