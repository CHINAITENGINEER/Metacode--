package com.huakang.service.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * OSS文件上传服务接口
 *
 * @author huakang
 */
public interface OssService {

    /**
     * 上传图片到OSS
     *
     * @param file 图片文件
     * @param folder 文件夹路径（如：products、qrcode等）
     * @return 图片访问URL
     */
    String uploadImage(MultipartFile file, String folder);

    /**
     * 删除OSS中的文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);
}
