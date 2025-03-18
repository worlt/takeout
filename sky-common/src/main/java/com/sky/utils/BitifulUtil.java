package com.sky.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Author worlt
 * @Date 2025/3/18 上午9:32
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BitifulUtil {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${s4.bucket-name}")
    private String bucketPathConfig;

    // 存储桶名称和前缀路径
    private String bucketName;
    private String prefixPath = "";

    // 初始化方法，解析存储桶配置
    @PostConstruct
    public void init() {
        // 检查是否包含路径分隔符
        if (bucketPathConfig.contains("/")) {
            int slashIndex = bucketPathConfig.indexOf("/");
            bucketName = bucketPathConfig.substring(0, slashIndex);
            prefixPath = bucketPathConfig.substring(slashIndex + 1);

            // 确保前缀路径以/结尾
            if (!prefixPath.isEmpty() && !prefixPath.endsWith("/")) {
                prefixPath = prefixPath + "/";
            }
        } else {
            // 没有子目录的情况
            bucketName = bucketPathConfig;
        }

        log.info("S4服务初始化完成: 存储桶={}, 前缀路径={}", bucketName, prefixPath);
    }

    /**
     * 上传文件到S4
     * @param file 要上传的文件
     * @return 上传后的文件路径
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 使用原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            // 如果没有原始文件名，则生成一个
            originalFilename = "file_" + System.currentTimeMillis();
        }

        // 直接使用原始文件名
        String fileName = originalFilename;

        // 如果有前缀路径，添加到文件名前
        String fullKey = prefixPath + fileName;

        log.info("准备上传文件: 文件名={}, 文件大小={}字节, 完整路径={}", fileName, file.getSize(), fullKey);

        // 获取文件的ContentType
        String contentType = file.getContentType();
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 如果没有获取到或是通用类型，则根据文件扩展名推断
        if (contentType == null || contentType.isEmpty() || contentType.equals("application/octet-stream")) {
            contentType = getMimeTypeFromExtension(extension);
            log.debug("根据扩展名推断ContentType: 扩展名={}, ContentType={}", extension, contentType);
        }

        // 上传文件到S4
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .contentType(contentType)
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            log.info("文件上传成功: 文件名={}, 路径={}", fileName, fullKey);
        } catch (Exception e) {
            log.error("文件上传失败: 文件名={}, 错误信息={}", fileName, e.getMessage(), e);
            throw e;
        }

        // 返回文件名（不包含前缀路径）
        return fileName;
    }

    /**
     * 根据文件扩展名获取MIME类型
     * @param extension 文件扩展名（包括点，如 ".jpg"）
     * @return MIME类型
     */
    private String getMimeTypeFromExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return "application/octet-stream";
        }

        String ext = extension.toLowerCase();
        switch (ext) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".pdf":
                return "application/pdf";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".xls":
                return "application/vnd.ms-excel";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".txt":
                return "text/plain";
            case ".mp4":
                return "video/mp4";
            case ".mp3":
                return "audio/mpeg";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 获取文件预签名下载URL
     * @param key 文件的key
     * @param expirationTime URL的有效时间（秒）
     * @return 预签名URL
     */
    public String getPresignedUrl(String key, long expirationTime) {
        // 确保使用完整路径
        String fullKey = prefixPath.isEmpty() ? key : prefixPath + key;

        log.info("获取预签名URL: 文件名={}, 完整路径={}, 有效期={}秒", key, fullKey, expirationTime);

        // 获取文件元数据，包括Content-Type
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .build();

        HeadObjectResponse headObjectResponse = null;
        try {
            headObjectResponse = s3Client.headObject(headObjectRequest);
            log.debug("获取文件元数据成功: 文件={}, ContentType={}", key, headObjectResponse.contentType());
        } catch (Exception e) {
            log.warn("获取文件元数据失败: 文件={}, 错误={}", key, e.getMessage());
            // 如果获取元数据失败，忽略错误继续
        }

        GetObjectRequest.Builder requestBuilder = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey);

        // 如果获取到了Content-Type，则设置到请求中
        if (headObjectResponse != null && headObjectResponse.contentType() != null) {
            // 添加响应内容类型，确保文件正确显示
            requestBuilder.responseContentType(headObjectResponse.contentType());

            // 处理文件名编码
            String filename = key;
            if (filename.lastIndexOf("/") > -1) {
                filename = filename.substring(filename.lastIndexOf("/") + 1);
            }

            try {
                String encodedFilename = java.net.URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
                // 添加Content-Disposition，使浏览器下载文件而不是显示它
                requestBuilder.responseContentDisposition("attachment; filename=\"" + encodedFilename + "\"");
            } catch (Exception e) {
                log.warn("文件名编码失败: 文件名={}, 错误={}", filename, e.getMessage());
                // 如果编码失败，使用原始文件名
                requestBuilder.responseContentDisposition("attachment; filename=\"" + filename + "\"");
            }
        }

        GetObjectRequest getObjectRequest = requestBuilder.build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(expirationTime))
                .getObjectRequest(getObjectRequest)
                .build();

        String url = s3Presigner.presignGetObject(presignRequest).url().toString();
        log.info("预签名URL生成成功: 文件={}, URL长度={}", key, url.length());
        log.debug("预签名URL: {}", url);

        return url;
    }

    /**
     * 下载文件
     * @param key 文件的key
     * @return 文件输入流
     */
    public ResponseInputStream<GetObjectResponse> downloadFile(String key) {
        // 确保使用完整路径
        String fullKey = prefixPath.isEmpty() ? key : prefixPath + key;

        log.info("准备下载文件: 文件名={}, 完整路径={}", key, fullKey);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .build();

        try {
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);
            log.info("文件下载成功: 文件={}, ContentType={}, 内容长度={}",
                    key, response.response().contentType(), response.response().contentLength());
            return response;
        } catch (Exception e) {
            log.error("文件下载失败: 文件={}, 错误={}", key, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取存储桶中的文件列表
     * @return 文件列表
     */
    public List<String> listFiles() {
        log.info("获取文件列表: 存储桶={}, 前缀路径={}", bucketName, prefixPath);

        ListObjectsRequest.Builder requestBuilder = ListObjectsRequest.builder()
                .bucket(bucketName);

        // 如果有前缀路径，添加到请求中
        if (!prefixPath.isEmpty()) {
            requestBuilder.prefix(prefixPath);
        }

        try {
            ListObjectsResponse listObjectsResponse = s3Client.listObjects(requestBuilder.build());
            List<String> files = listObjectsResponse.contents().stream()
                    .map(object -> {
                        // 移除前缀路径，返回相对路径
                        String key = object.key();
                        if (!prefixPath.isEmpty() && key.startsWith(prefixPath)) {
                            return key.substring(prefixPath.length());
                        }
                        return key;
                    })
                    .collect(Collectors.toList());

            log.info("获取文件列表成功: 文件数量={}", files.size());
            if (log.isDebugEnabled() && !files.isEmpty()) {
                log.debug("文件列表: {}", String.join(", ", files));
            }

            return files;
        } catch (Exception e) {
            log.error("获取文件列表失败: 错误={}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 删除文件
     * @param key 文件的key
     */
    public void deleteFile(String key) {
        // 确保使用完整路径
        String fullKey = prefixPath.isEmpty() ? key : prefixPath + key;

        log.info("准备删除文件: 文件名={}, 完整路径={}", key, fullKey);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .build();

        try {
            s3Client.deleteObject(deleteObjectRequest);
            log.info("文件删除成功: 文件={}", key);
        } catch (Exception e) {
            log.error("文件删除失败: 文件={}, 错误={}", key, e.getMessage(), e);
            throw e;
        }
    }
}
