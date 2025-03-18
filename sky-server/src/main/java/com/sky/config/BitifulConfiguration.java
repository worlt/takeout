package com.sky.config;

/**
 * @Author worlt
 * @Date 2025/3/18 上午9:27
 */
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Slf4j
@Configuration
public class BitifulConfiguration {

    @Value("${s4.endpoint}")
    private String endpoint;

    @Value("${s4.region}")
    private String region;

    @Value("${s4.access-key}")
    private String accessKey;

    @Value("${s4.secret-key}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        log.info("初始化S3Client: 端点={}, 区域={}", endpoint, region);

        S3Client client = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();

        log.info("S3Client初始化完成");
        return client;
    }

    @Bean
    public S3Presigner s3Presigner() {
        log.info("初始化S3Presigner: 端点={}, 区域={}", endpoint, region);

        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();

        log.info("S3Presigner初始化完成");
        return presigner;
    }
}
