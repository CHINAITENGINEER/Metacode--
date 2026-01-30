package com.huakang.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.huakang.common.exception.BusinessException;
import com.huakang.service.config.OssConfig;
import com.huakang.service.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * OSS文件上传服务实现类
 *
 * @author huakang
 */
@Slf4j
@Service
public class OssServiceImpl implements OssService {

    private final OssConfig ossConfig;
    private final OSS ossClient;

    public OssServiceImpl(OssConfig ossConfig, 
                         @org.springframework.beans.factory.annotation.Autowired(required = false) OSS ossClient) {
        this.ossConfig = ossConfig;
        this.ossClient = ossClient;
    }

    // 允许的图片格式
    private static final String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };

    // 最大文件大小：10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Override
    public String uploadImage(MultipartFile file, String folder) {
        // 如果OSS未配置，抛出异常提示
        if (ossClient == null) {
            throw new BusinessException("OSS未配置，请先配置阿里云OSS相关信息");
        }

        // 1. 验证文件
        validateFile(file);

        try {
            // 2. 生成文件路径
            String fileName = generateFileName(file, folder);

            // 3. 上传到OSS
            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossConfig.getBucketName(),
                    fileName,
                    inputStream
            );

            ossClient.putObject(putObjectRequest);
            inputStream.close();

            // 4. 构建访问URL
            String fileUrl = buildFileUrl(fileName);
            log.info("图片上传成功：{} -> {}", fileName, fileUrl);

            return fileUrl;
        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new BusinessException("图片上传失败：" + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (ossClient == null) {
            log.warn("OSS未配置，无法删除文件：{}", fileUrl);
            return false;
        }

        try {
            // 从URL中提取文件路径
            String fileName = extractFileNameFromUrl(fileUrl);
            if (fileName == null) {
                log.warn("无法从URL中提取文件名：{}", fileUrl);
                return false;
            }

            // 删除文件
            ossClient.deleteObject(ossConfig.getBucketName(), fileName);
            log.info("文件删除成功：{}", fileName);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败：{}", fileUrl, e);
            return false;
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过10MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedImageType(contentType)) {
            throw new BusinessException("不支持的文件类型，仅支持：JPG、PNG、GIF、WebP");
        }
    }

    /**
     * 检查是否为允许的图片类型
     */
    private boolean isAllowedImageType(String contentType) {
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成文件名
     * 格式：{pathPrefix}{folder}/{yyyyMMdd}/{uuid}.{ext}
     */
    private String generateFileName(MultipartFile file, String folder) {
        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            // 根据ContentType推断扩展名
            String contentType = file.getContentType();
            if (contentType != null) {
                if (contentType.contains("jpeg") || contentType.contains("jpg")) {
                    extension = ".jpg";
                } else if (contentType.contains("png")) {
                    extension = ".png";
                } else if (contentType.contains("gif")) {
                    extension = ".gif";
                } else if (contentType.contains("webp")) {
                    extension = ".webp";
                }
            }
        }

        // 生成唯一文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 构建完整路径
        String pathPrefix = ossConfig.getPathPrefix();
        if (!pathPrefix.endsWith("/")) {
            pathPrefix += "/";
        }

        if (folder != null && !folder.isEmpty()) {
            if (!folder.endsWith("/")) {
                folder += "/";
            }
            return pathPrefix + folder + datePath + "/" + uuid + extension;
        } else {
            return pathPrefix + datePath + "/" + uuid + extension;
        }
    }

    /**
     * 构建文件访问URL
     */
    private String buildFileUrl(String fileName) {
        String domain = ossConfig.getDomain();
        if (StringUtils.hasText(domain)) {
            // 使用配置的域名（可能是CDN域名）
            if (!domain.endsWith("/")) {
                domain += "/";
            }
            return domain + fileName;
        } else {
            // 使用OSS默认域名
            String endpoint = ossConfig.getEndpoint();
            String bucketName = ossConfig.getBucketName();
            // 移除endpoint中的协议前缀
            if (endpoint.startsWith("https://")) {
                endpoint = endpoint.substring(8);
            } else if (endpoint.startsWith("http://")) {
                endpoint = endpoint.substring(7);
            }
            return "https://" + bucketName + "." + endpoint + "/" + fileName;
        }
    }

    /**
     * 从URL中提取文件名
     */
    private String extractFileNameFromUrl(String fileUrl) {
        try {
            // 移除协议和域名部分
            if (fileUrl.contains("://")) {
                int index = fileUrl.indexOf("://");
                fileUrl = fileUrl.substring(index + 3);
                int slashIndex = fileUrl.indexOf("/");
                if (slashIndex > 0) {
                    fileUrl = fileUrl.substring(slashIndex + 1);
                }
            }

            // 移除查询参数
            if (fileUrl.contains("?")) {
                fileUrl = fileUrl.substring(0, fileUrl.indexOf("?"));
            }

            return fileUrl;
        } catch (Exception e) {
            log.warn("提取文件名失败：{}", fileUrl, e);
            return null;
        }
    }
}
