package com.talkman.saas.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author doger.wang
 * @date 2019/8/26 14:27
 */

@Configuration
public class LocalDateTimeSerializerConfig {
    @org.springframework.beans.factory.annotation.Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;

    public LocalDateTimeSerializer localDateTimeSerializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
    }

    public LocalDateTimeDeserializer localDateTimeDeserializer() {
        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(pattern));
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.serializerByType(LocalDateTime.class, localDateTimeSerializer())
                .deserializerByType(LocalDateTime.class, localDateTimeDeserializer());
    }


}

