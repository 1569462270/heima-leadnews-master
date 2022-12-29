package com.heima.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @Author : MR.wu
 * @Description : MinIOConfigProperties
 * @Date : 2022/12/29 15:03
 * @Version : 1.0
 */
@Data
@ConfigurationProperties(prefix = "file.minio")
public class MinIOConfigProperties implements Serializable {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String endpoint;
    private String readPath;
}
