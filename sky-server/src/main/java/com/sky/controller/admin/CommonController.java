package com.sky.controller.admin;

/**
 * @Author worlt
 * @Date 2025/3/18 上午9:29
 */

import com.sky.utils.BitifulUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private BitifulUtil bitifulUtil;

    /**
     * 上传文件
     * @param file 要上传的文件
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("接收到文件上传请求: 文件名={}, 大小={}字节, 类型={}",
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        try {
            String fileName = bitifulUtil.uploadFile(file);

            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("message", "文件上传成功");

            log.info("文件上传成功: 文件名={}", fileName);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("文件上传失败: 文件名={}, 错误={}", file.getOriginalFilename(), e.getMessage(), e);

            Map<String, String> response = new HashMap<>();
            response.put("error", "文件上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取预签名下载URL
     * @param key 文件的key
     * @param expiration URL的有效时间（秒），默认300秒
     * @return 预签名URL
     */
    @GetMapping("/presigned")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @RequestParam String key,
            @RequestParam(defaultValue = "300") long expiration) {

        log.info("接收到获取预签名URL请求: 文件名={}, 有效期={}秒", key, expiration);

        String url = bitifulUtil.getPresignedUrl(key, expiration);

        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        response.put("expiration", expiration + "秒");

        log.info("预签名URL生成成功: 文件名={}", key);
        return ResponseEntity.ok(response);
    }

    /**
     * 直接下载文件
     * @param key 文件的key
     * @return 文件响应
     */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String key) {
        log.info("接收到文件下载请求: 文件名={}", key);

        try {
            ResponseInputStream<GetObjectResponse> object = bitifulUtil.downloadFile(key);
            GetObjectResponse response = object.response();

            // 获取文件的MIME类型
            String contentType = response.contentType();
            // 如果没有获取到MIME类型，则根据文件扩展名推断
            if (contentType == null || contentType.isEmpty() || contentType.equals("application/octet-stream")) {
                contentType = getMimeTypeFromKey(key);
                log.debug("根据文件名推断ContentType: 文件名={}, ContentType={}", key, contentType);
            }

            // 处理文件名编码
            String filename = key;
            if (filename.lastIndexOf("/") > -1) {
                filename = filename.substring(filename.lastIndexOf("/") + 1);
            }
            String encodedFilename = java.net.URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");

            HttpHeaders headers = new HttpHeaders();
            // 设置正确的Content-Disposition，支持中文文件名
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);

            log.info("文件下载响应准备就绪: 文件名={}, 内容类型={}, 大小={}字节", key, contentType, response.contentLength());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new InputStreamResource(object));
        } catch (Exception e) {
            log.error("文件下载失败: 文件名={}, 错误={}", key, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据文件扩展名获取MIME类型
     * @param key 文件的key
     * @return MIME类型
     */
    private String getMimeTypeFromKey(String key) {
        String extension = "";
        if (key.contains(".")) {
            extension = key.substring(key.lastIndexOf(".")).toLowerCase();
        }

        switch (extension) {
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
     * 获取文件列表
     * @return 文件列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        log.info("接收到获取文件列表请求");

        List<String> files = bitifulUtil.listFiles();

        log.info("文件列表获取成功: 共{}个文件", files.size());
        return ResponseEntity.ok(files);
    }

    /**
     * 删除文件
     * @param key 文件的key
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteFile(@RequestParam String key) {
        log.info("接收到文件删除请求: 文件名={}", key);

        try {
            bitifulUtil.deleteFile(key);

            Map<String, String> response = new HashMap<>();
            response.put("message", "文件删除成功");

            log.info("文件删除成功: 文件名={}", key);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("文件删除失败: 文件名={}, 错误={}", key, e.getMessage(), e);

            Map<String, String> response = new HashMap<>();
            response.put("error", "文件删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}
