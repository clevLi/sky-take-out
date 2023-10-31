package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AliOssConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil OssUtil(AliOssProperties aliOssProperties){
        log.info("正在创建阿里云文件上传工具类对象");
        AliOssUtil aliOssUtil = new AliOssUtil(aliOssProperties.getEndpoint(),
                                                aliOssProperties.getAccessKeyId(),
                                                aliOssProperties.getAccessKeySecret(),
                                                aliOssProperties.getBucketName());
        return aliOssUtil;
    }
}
