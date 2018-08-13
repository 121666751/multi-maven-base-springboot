package com.multi.maven.config;

import com.multi.maven.qiniu.QiniuTemplate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author: litao
 * @see:
 * @description:
 *              springboot暂不支持qiniu自动配置  qiniu模板类配置
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/8.
 */
@Setter
@Getter
@Configuration
//@ConfigurationProperties(prefix = "qiniu")
public class QiniuTemplateConfiguration {
    //    secret密钥
//    @Value("${qiniu.access.key}")
    private String accessKey;
//    @Value("${qiniu.secret.key}")
    private String secretKey;
    //    公开云储存空间
//    @Value("${qiniu.public.bucket}")
    private String publicBucket;
    //    公开空间域名
//    @Value("${qiniu.public.domain}")
    private String publicDomain;
    //    私密空间域名
//    @Value("${qiniu.private.domain}")
    private String privateDomain;
    //    私密云储存空间
//    @Value("${qiniu.private.bucket}")
    private String privateBucket;
    //    上传凭证有效时间  单位s
//    @Value("${qiniu.uptoken.expires}")
    private Long uptokenExpires;
    //    下载凭证有效时间 单位s
//    @Value("${qiniu.download.expires}")
    private Long downloadExpires;
    //    http协议 http or https
//    @Value("${qiniu.protocol}")
    private String protocol;
    //    区域 0华东 1华北
//    @Value("${qiniu.zone}")
    private String zone;
    //    uploadToken存储在redis中的Key
//    @Value("${qiniu.upload.token.private.key}")
    private String uptokenPrivateKey;
//    @Value("${qiniu.upload.token.public.key}")
    private String uptokenPublicKey;

//    @Bean(name = "qiniuTemplate")
    public QiniuTemplate qiniuTemplate() {
        QiniuTemplate qiniuTemplate = new QiniuTemplate();
        qiniuTemplate.setAccessKey(accessKey);
        qiniuTemplate.setSecretKey(secretKey);
        qiniuTemplate.setPublicBucket(publicBucket);
        qiniuTemplate.setPublicDomain(publicDomain);
        qiniuTemplate.setPrivateBucket(privateBucket);
        qiniuTemplate.setPrivateDomain(privateDomain);
        qiniuTemplate.setUptokenExpires(uptokenExpires);
        qiniuTemplate.setDownloadExpires(downloadExpires);
        qiniuTemplate.setUptokenPrivateKey(uptokenPrivateKey);
        qiniuTemplate.setUptokenPublicKey(uptokenPrivateKey);
        qiniuTemplate.setProtocol(protocol);
        qiniuTemplate.setZone(zone);
        return qiniuTemplate;
    }
}
