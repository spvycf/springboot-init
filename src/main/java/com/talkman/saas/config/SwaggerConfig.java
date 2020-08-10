package com.talkman.saas.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author doger.wang
 * @date 2019/10/9 14:54
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    //指定扫描哪些包
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //要扫描的包路径（这里无需通过命名空间，太low了）
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))//显示API注解的接口
                .paths(PathSelectors.any())
                .build();
    }

    //配置swagger的基本信息
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SaaS接口文档")
                //创始人
                .contact(new Contact("saas接口文档", "http://106.15.207.27:8800/swagger-ui.html", ""))
                .description("测试地址 https://testsaas.talkmantech.cn/api/guide/------- \n" +
                        "正式地址 https://saas.talkmantech.cn/api/guide/------- \n" +
                        "请求头中需带入登录返回的token 键值对为\n" +
                        "Authentication:TOKEN值\n" +
                        "统一登录接口(测试为例application/json):\n" +
                        "https://testtalkman.talkmantech.cn/api/center/login\n" +
                        "{\n" +
                        "\t\"loginName\":\"wny\",\n" +
                        "\t\"password\":\"111111\"\n" +
                        "}\n" +
                        "\n免token访问加入以下请求头" +
                        "\nFromtalkman:guidetour" +
                        "\n此页面直接执行请求接口返所回参数为直连返回结果，未经过网关中心，实际返回样例为\n" +
                        "{\n" +
                        "\"code\": 1,\n" +
                        "\"message\": \"登录成功\",\n" +
                        "\"data\": {\n" +
                        "\t\"uid\": \"2c9313346b1df112016b203ade0b0025\",\n" +
                        "\t\"userType\": \"1\"\n" +
                        "},\n" +
                        "\"clientType\": \"web\"\n" +
                        "}"
                        + "\n 说明:" +
                        "\n 所有是否类参数 1表示是 0表示未选择，没有启动等等 如 是否推荐 1推荐0不推荐"
                        + "\n 字段注释可以直连数据库参考talkman_guide库" +
                        "\n 地址=>106.15.207.27:3306"
                        + "\n user: root"
                        + "\n password:123456"

                )
                //版本
                .version("1.0")
                //描述
                .build();
    }
}
