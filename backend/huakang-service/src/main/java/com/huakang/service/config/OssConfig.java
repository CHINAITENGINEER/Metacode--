package com.huakang.service.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 阿里云OSS配置类
 *
 * @author huakang
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssConfig {

    /**
     * OSS endpoint（地域节点）
     * 示例：https://oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;

    /**
     * AccessKey ID
     */
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * 存储桶名称（Bucket）
     */
    private String bucketName;

    /**
     * 文件访问域名（CDN域名或OSS域名）
     * 示例：https://your-bucket.oss-cn-hangzhou.aliyuncs.com
     */
    private String domain;

    /**
     * 文件上传路径前缀
     * 示例：images/ 或 uploads/
     */
    private String pathPrefix = "images/";

    /**
     * 创建OSS客户端
     * 如果OSS配置为空，返回null（允许不配置OSS）
     */
    @Bean
    public OSS ossClient() {
        // 如果配置为空或为空字符串，返回null（不创建OSS客户端）
        if (!StringUtils.hasText(endpoint) || 
            !StringUtils.hasText(accessKeyId) || 
            !StringUtils.hasText(accessKeySecret)) {
            return null; // OSS未配置，返回null
        }
        // 配置完整，创建OSS客户端
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
