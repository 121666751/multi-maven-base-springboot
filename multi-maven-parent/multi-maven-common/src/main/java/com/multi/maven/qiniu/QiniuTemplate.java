package com.multi.maven.qiniu;

import com.multi.maven.dao.redis.RedisClient;
import com.multi.maven.enums.qiniu.BucketScopeEnum;
import com.multi.maven.enums.qiniu.ImageStyleEnum;
import com.multi.maven.exception.BaseException;
import com.multi.maven.qiniu.beans.Crop;
import com.multi.maven.qiniu.beans.Gravity;
import com.multi.maven.qiniu.beans.RoundPic;
import com.multi.maven.qiniu.beans.Thumbnail;
import com.multi.maven.utils.JsonUtil;
import com.multi.maven.utils.UUIDUtil;
import com.qiniu.common.Config;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 七牛云存储Template
 * </p>
 * 提供第三方对七牛云存储的访问，提供文件上传、图片处理、文件处理等功能
 *
 * @author yangsongbo
 * @since 2.3
 */
public class QiniuTemplate {

    private static final Logger logger = LoggerFactory.getLogger(QiniuTemplate.class);


    /**
     * 获取上传凭证
     * </p>
     * 上传凭证存储在Redis中，有效期为30分钟，缓存失效重新加载
     *
     * @return 上传凭证
     */
    public UploadToken getUploadToken(boolean isNew, BucketScopeEnum scope) {
        String uploadToken = null;
        String key = null;
        long expires = 0;
        if (BucketScopeEnum.SCOPE_PRIVATE.equals(scope)) {
            key = uptokenPrivateKey;
        } else {
            key = uptokenPublicKey;
        }
        if (isNew) {
            expires = uptokenExpires / 2;
            uploadToken = createUptoken(scope);
            redisClient.addString(key, uploadToken, false);
            redisClient.expireKey(key, expires);
        } else {
            if (!redisClient.existsKey(key)) {
                expires = uptokenExpires / 2;
                uploadToken = createUptoken(scope);
                redisClient.addString(key, uploadToken, false);
                // 设置上传凭证缓存有效时间
                redisClient.expireKey(key, expires);
            } else {
                uploadToken = redisClient.getString(key);
                expires = redisClient.getExpire(key);
            }
        }

        if (expires < 0) {
            expires = 0;
        }
        return new UploadToken(uploadToken, expires);
    }

    /**
     * 上传文件
     *
     * @param key      指定上传文件的Key
     * @param filePath 待上传的文件路径
     * @return {@link FileKey}
     */
    public FileKey uploadFile(String key, String filePath, BucketScopeEnum scope) throws QiniuException {
        // 创建上传对象
        UploadManager uploadManager = new UploadManager();
        try {
            Response res = uploadManager.put
                    (filePath, key, getUploadToken(false, scope).getToken());

            if (res == null) {
                return null;
            }
            String bodyString = res.bodyString();
            FileKey fileKey = JsonUtil.fromJson(bodyString, FileKey.class);
            if (fileKey != null) {
                fileKey.setScope(scope.getValue());
            }
            return fileKey;
        } catch (QiniuException e) {
            Response r = e.response;
            logger.error("上传文件失败:" + r.toString(), e);
            throw e;
        }
    }

    /**
     * 上传文件
     *
     * @param key  指定上传文件的Key
     * @param file 待上传的文件对象
     * @return {@link FileKey}
     */
    public FileKey uploadFile(String key, File file, BucketScopeEnum scope) throws QiniuException {
        // 创建上传对象
        UploadManager uploadManager = new UploadManager();
        try {
            Response res = uploadManager.put
                    (file, key, getUploadToken(false, scope).getToken());
            if (res == null) {
                return null;
            }
            String bodyString = res.bodyString();
            FileKey fileKey = JsonUtil.fromJson(bodyString, FileKey.class);
            if (fileKey != null) {
                fileKey.setScope(scope.getValue());
            }
            return fileKey;
        } catch (QiniuException e) {
            Response r = e.response;
            logger.error("上传文件失败:" + r.toString(), e);
            throw e;
        }
    }

    /**
     * 上传文件
     *
     * @param key      指定上传文件的Key
     * @param fileData 待上传的文件的byte数组
     * @return {@link FileKey}
     */
    public FileKey uploadFile(String key, byte[] fileData, BucketScopeEnum scope) throws QiniuException {
        // 创建上传对象
        UploadManager uploadManager = new UploadManager();
        try {
            Response res = uploadManager.put
                    (fileData, key, getUploadToken(false, scope).getToken());
            if (res == null) {
                return null;
            }
            String bodyString = res.bodyString();
            FileKey fileKey = JsonUtil.fromJson(bodyString, FileKey.class);
            if (fileKey != null) {
                fileKey.setScope(scope.getValue());
            }
            return fileKey;
        } catch (QiniuException e) {
            Response r = e.response;
            logger.error("上传文件失败:" + r.toString(), e);
            throw e;
        }
    }

    /**
     * 上传图片
     * </p>
     * 可支持的Image类型：psd、jpeg、png、gif、webp、tiff、bmp
     *
     * @param key      指定上传图片的Key
     * @param filePath 待上传的文件路径
     * @return {@link FileKey}
     * @throws QiniuException
     */
    public ImageKey uploadImage(String key, String filePath, BucketScopeEnum scope) throws QiniuException {
        // 创建上传对象
        UploadManager uploadManager = new UploadManager();
        try {
            Response res = uploadManager.put
                    (filePath, key, getUploadToken(false, scope).getToken());
            if (res == null) {
                return null;
            }
            String bodyString = res.bodyString();
            ImageKey imageKey = JsonUtil.fromJson(bodyString, ImageKey.class);
            if (imageKey != null) {
                imageKey.setScope(scope.getValue());
            }
            return imageKey;
        } catch (QiniuException e) {
            Response r = e.response;
            logger.error("上传文件失败:" + r.toString(), e);
            throw e;
        }
    }

    /**
     * 上传图片
     * </p>
     * 可支持的Image类型：psd、jpeg、png、gif、webp、tiff、bmp
     *
     * @param key  指定上传图片的Key
     * @param file 待上传的图片File对象
     * @return {@link FileKey}
     */
    public ImageKey uploadImage(String key, File file, BucketScopeEnum scope) {
        // 创建上传对象
        UploadManager uploadManager = new UploadManager();
        try {
            Response res = uploadManager.put
                    (file, key, getUploadToken(false, scope).getToken());
            if (res == null) {
                return null;
            }
            String bodyString = res.bodyString();
            ImageKey imageKey = JsonUtil.fromJson(bodyString, ImageKey.class);
            if (imageKey != null) {
                imageKey.setScope(scope.getValue());
            }
            return imageKey;
        } catch (QiniuException e) {
            Response r = e.response;
            logger.error("上传文件失败:" + r.toString(), e);
            throw new BaseException(e);
        }
    }

    /**
     * 上传图片
     * </p>
     * 可支持的Image类型：psd、jpeg、png、gif、webp、tiff、bmp
     *
     * @param key      指定上传图片的Key
     * @param fileData 待上传的图片的byte[]数组
     * @return {@link FileKey}
     */
    public ImageKey uploadImage(String key, byte[] fileData, BucketScopeEnum scope) throws QiniuException {
        // 创建上传对象
        UploadManager uploadManager = new UploadManager();
        try {
            Response res = uploadManager.put
                    (fileData, key, getUploadToken(false, scope).getToken());
            if (res == null) {
                return null;
            }
            String bodyString = res.bodyString();
            ImageKey imageKey = JsonUtil.fromJson(bodyString, ImageKey.class);
            if (imageKey != null) {
                imageKey.setScope(scope.getValue());
            }
            return imageKey;
        } catch (QiniuException e) {
            Response r = e.response;
            logger.error("上传文件失败:" + r.toString(), e);
            throw e;
        }
    }

    /**
     * 上传图片
     * </p>
     * 可支持的Image类型：psd、jpeg、png、gif、webp、tiff、bmp
     *
     * @param key   指定上传图片的Key
     * @param scope 待上传的图片的byte[]数组
     * @return {@link FileKey}
     */
    public ImageKey uploadImage(String key, URL url, BucketScopeEnum scope) throws QiniuException {
        ImageKey imageKey = null;
        try {
            byte[] bs = IOUtils.toByteArray(url);
            imageKey = uploadImage(key, bs, scope);
            if (imageKey != null) {
                imageKey.setScope(scope.getValue());
            }
        } catch (IOException e) {
            throw new QiniuException(e);
        }
        return imageKey;
    }

    /**
     * 判断是否为七牛云存储的URL
     *
     * @param url
     * @return
     */
    public boolean isQiniuUrl(String url) {
        if (StringUtils.isBlank(url) || !url.startsWith("http"))
            return false;
        if (StringUtils.isNotBlank(privateDomain) && url.indexOf(privateDomain) > 0)
            return true;
        return StringUtils.isNotBlank(publicDomain) && url.indexOf(publicDomain) > 0;

    }

    /**
     * imageUrl to ImageKey
     *
     * @param imageUrl 图片路径
     * @return ImageKey 对象
     */
    public ImageKey imageUrlToImageKey(String imageUrl) {
        if (StringUtils.isBlank(imageUrl)) {
            return null;
        }

        if (!imageUrl.startsWith("http")) {
            return null;
        }
        String domain = "";
        if (StringUtils.isNotBlank(privateDomain) && imageUrl.indexOf(privateDomain) > 0) {
            domain = privateDomain;
        } else if (StringUtils.isNotBlank(publicDomain) && imageUrl.indexOf(publicDomain) > 0) {
            domain = publicDomain;
        } else {
            return null;
        }

        String key = "";
        String hash = "";
        int width = 0;
        int height = 0;
        int fsize = 0;
        String scope = "1"; // 空间类型 1 公开 1 私密

        int keyBegin = imageUrl.indexOf(domain) + domain.length() + 1;
        key = imageUrl.substring(keyBegin, imageUrl.indexOf("?", keyBegin));

        if (ImageStyleEnum.hasWatermark(key)) {
            key = key.substring(0, key.lastIndexOf(ImageStyleEnum.PARTITION));
        }

        int hashBegin = imageUrl.indexOf(HASH) + HASH.length();
        hash = imageUrl.substring(hashBegin, imageUrl.indexOf("&", hashBegin));

        int widthIndex = imageUrl.indexOf(WIDTH);
        if (widthIndex >= 0) {
            int widthBegin = widthIndex + WIDTH.length();
            width = Integer.valueOf(imageUrl.substring(widthBegin,
                    imageUrl.indexOf("&", widthBegin) < 0 ? imageUrl.length()
                            : imageUrl.indexOf("&", widthBegin)));
        }

        int heightIndex = imageUrl.indexOf(HEIGHT);
        if (heightIndex >= 0) {
            int heightBegin = imageUrl.indexOf(HEIGHT) + HEIGHT.length();
            height = Integer.valueOf(imageUrl.substring(heightBegin,
                    imageUrl.indexOf("&", heightBegin) < 0 ? imageUrl.length()
                            : imageUrl.indexOf("&", heightBegin)));
        }

        int fsizeIndex = imageUrl.indexOf(FSIZE);
        if (fsizeIndex >= 0) {
            int fsizeBegin = imageUrl.indexOf(FSIZE) + FSIZE.length();
            fsize = Integer.valueOf(imageUrl.substring(fsizeBegin,
                    imageUrl.indexOf("&", fsizeBegin) < 0 ? imageUrl.length()
                            : imageUrl.indexOf("&", fsizeBegin)));
        }

        int scopeIndex = imageUrl.indexOf(SCOPE);
        if (scopeIndex >= 0) {
            int scopeBegin = imageUrl.indexOf(SCOPE) + SCOPE.length();
            scope = imageUrl.substring(scopeBegin,
                    imageUrl.indexOf("&", scopeBegin) < 0 ? imageUrl.length()
                            : imageUrl.indexOf("&", scopeBegin));
        }

        return new ImageKey(key, hash, width, height, fsize, scope);
    }

    /**
     * fileUrl to FileKey
     *
     * @param fileUrl 图片路径
     * @return FileKey 对象
     */
    public FileKey fileUrlToFileKey(String fileUrl) {
        if (StringUtils.isBlank(fileUrl) || !fileUrl.startsWith("http")) {
            return null;
        }
        String domain = "";
        if (StringUtils.isNotBlank(privateDomain) && fileUrl.indexOf(privateDomain) > 0) {
            domain = privateDomain;
        } else if (StringUtils.isNotBlank(publicDomain) && fileUrl.indexOf(publicDomain) > 0) {
            domain = publicDomain;
        } else {
            return null;
        }
        String key = "";
        String hash = "";
        int fsize = 0;
        String scope = "1";       //空间类型 1 公开 1 私密

        int keyBegin = fileUrl.indexOf(domain) + domain.length() + 1;
        key = fileUrl.substring(keyBegin, fileUrl.indexOf("?", keyBegin));
        int hashBegin = fileUrl.indexOf(HASH) + HASH.length();
        hash = fileUrl.substring(hashBegin, fileUrl.indexOf("&", hashBegin) < 0
                ? fileUrl.length()
                : fileUrl.indexOf("&", hashBegin));

        int fsizeIndex = fileUrl.indexOf(FSIZE);
        if (fsizeIndex >= 0) {
            int fsizeBegin = fileUrl.indexOf(FSIZE) + FSIZE.length();
            fsize = Integer.valueOf(fileUrl.substring(fsizeBegin, fileUrl.indexOf("&", fsizeBegin) < 0
                    ? fileUrl.length()
                    : fileUrl.indexOf("&", fsizeBegin)));
        }

        int scopeIndex = fileUrl.indexOf(SCOPE);
        if (scopeIndex >= 0) {
            int scopeBegin = fileUrl.indexOf(SCOPE) + SCOPE.length();
            scope = fileUrl.substring(scopeBegin, fileUrl.indexOf("&", scopeBegin) < 0
                    ? fileUrl.length()
                    : fileUrl.indexOf("&", scopeBegin));
        }

        return new FileKey(key, hash, fsize, scope);

    }


    /**
     * 将imageKey 转换为 imageUrl
     *
     * @param imageKey
     * @param thumbnail 缩放参数
     * @param gravity   图片处理重心参数表，默认为左上角(NorthWest)
     * @param crop      裁剪参数，默认为不裁剪
     * @return
     */
    public String imageKeyToImageUrl
    (ImageKey imageKey, Thumbnail thumbnail, Gravity gravity, Crop crop, RoundPic roundPic) {

        if (imageKey == null) {
            return null;
        }
        if (StringUtils.isEmpty(imageKey.getKey())) {
            return null;
        }
        if (StringUtils.isEmpty(imageKey.getHash())) {
            return null;
        }

        String key = imageKey.getKey();
        String scope = imageKey.getScope();
        boolean hasParams = false;
        StringBuilder params = new StringBuilder("&imageMogr2");
        StringBuilder roundPicStr = new StringBuilder("");
        if (thumbnail != null) {
            params.append(thumbnail.getParams());
            hasParams = true;
        }
        if (gravity != null) {
            params.append(gravity.getParams());
            hasParams = true;
        }
        if (crop != null) {
            params.append(crop.getParams());
            hasParams = true;
        }

        if (roundPic != null) {
            if (StringUtils.isNotBlank(roundPic.getRadius())) {
                roundPicStr.append("&roundPic/radius/" + roundPic.getRadius());
            } else if (StringUtils.isNotBlank(roundPic.getRadiusx()) && StringUtils.isNotBlank(roundPic.getRadiusy())) {
                roundPicStr.append("&roundPic/radiusx/" + roundPic.getRadiusx() + "/radiusy/" + roundPic.getRadiusy());
            }
        }


        String spliceUrl = key + "?" + HASH + imageKey.getHash()
                + (hasParams ? params.toString() : "")
                + roundPicStr
                + "&" + WIDTH + imageKey.getWidth()
                + "&" + HEIGHT + imageKey.getHeight()
                + "&" + FSIZE + imageKey.getFsize()
                + "&" + SCOPE + imageKey.getScope();

        if (BucketScopeEnum.SCOPE_PRIVATE.getValue().equals(scope)) {
            String baseUrl = makeBaseUrl(privateDomain, spliceUrl);
            return auth.privateDownloadUrl(baseUrl, downloadExpires);
        } else {
            return makeBaseUrl(publicDomain, spliceUrl);
        }
    }


    /**
     * imageKey to imageUrl
     *
     * @param imageKey
     * @param imageStyle 图片样式，可在www.qiniu.com管理平台进行设置
     * @return
     */
    public String imageKeyToImageUrl(ImageKey imageKey, ImageStyleEnum imageStyle) {
        if (imageKey == null) {
            return null;
        }
        if (StringUtils.isEmpty(imageKey.getKey())) {
            return null;
        }
        if (StringUtils.isEmpty(imageKey.getHash())) {
            return null;
        }
        String key = imageKey.getKey();
        String scope = imageKey.getScope();

        if (imageStyle != null) {
            key += imageStyle.getValue();
        }

        String spliceUrl = key + "?" + HASH + imageKey.getHash() + "&" + WIDTH + imageKey.getWidth()
                + "&" + HEIGHT + imageKey.getHeight() + "&" + FSIZE + imageKey.getFsize()
                + "&" + SCOPE + imageKey.getScope();


        if (BucketScopeEnum.SCOPE_PRIVATE.getValue().equals(scope)) {
            String baseUrl = makeBaseUrl(privateDomain, spliceUrl);
            return auth.privateDownloadUrl(baseUrl, downloadExpires);
        } else {
            return makeBaseUrl(publicDomain, spliceUrl);
        }

    }


    /**
     * FileKey to fileUrl
     *
     * @param fileKey {@link FileKey}
     * @return
     */
    public String fileKeyToFileUrl(FileKey fileKey) {
        if (fileKey == null) {
            return null;
        }
        if (StringUtils.isEmpty(fileKey.getKey())) {
            return null;
        }
        if (StringUtils.isEmpty(fileKey.getHash())) {
            return null;
        }
        String key = fileKey.getKey();
        String scope = fileKey.getScope();
        String spliceUrl = key + "?" + HASH + fileKey.getHash() + "&" + FSIZE + fileKey.getFsize() + "&" + SCOPE + fileKey.getScope();

        if (BucketScopeEnum.SCOPE_PRIVATE.getValue().equals(scope)) {
            String baseUrl = makeBaseUrl(privateDomain, spliceUrl);
            return auth.privateDownloadUrl(baseUrl, downloadExpires);
        } else {
            return makeBaseUrl(publicDomain, spliceUrl);
        }


    }

    /**
     * 获取文件信息
     *
     * @return
     */
    public FileInfo getFileInfo(ImageKey imageKey) {
        Assert.notNull(imageKey, "this imageKey is required; it must not be null");
        String scope = imageKey.getScope();

        BucketManager bucketManager = new BucketManager(auth);
        if (BucketScopeEnum.SCOPE_PRIVATE.getValue().equals(scope)) {
            try {
                return bucketManager.stat(privateBucket, imageKey.getKey());
            } catch (QiniuException e) {
                logger.info("获取文件信息异常,可能的原因是imageKey不存在[" + imageKey.getKey() + "]");
                return null;
            }
        } else {
            try {
                return bucketManager.stat(publicBucket, imageKey.getKey());
            } catch (QiniuException e) {
                logger.info("获取文件信息异常,可能的原因是imageKey不存在[" + imageKey.getKey() + "]");
                return null;
            }
        }

    }

    /**
     * 复制文件
     *
     * @param from 起始位置
     * @param to   终止位置
     * @return
     */
    public boolean copyFile(CopyFileBean from, CopyFileBean to) {
        //实例化一个BucketManager对象
        BucketManager bucketManager = new BucketManager(auth);

        try {
            //调用copy方法移动文件
            bucketManager.copy(from.getBucket(), from.getKey().getKey(), to.getBucket(), to.getKey().getKey());
        } catch (QiniuException e) {
            //捕获异常信息
            Response r = e.response;
            logger.error("copyFile error !", e);
            logger.error("copyFile error !", r);
            return false;
        }
        return true;
    }

    /**
     * 复制图片
     *
     * @param from 起始位置
     * @param to   终止位置
     * @return
     */
    public boolean copyImage(CopyImageBean from, CopyImageBean to) {
        //实例化一个BucketManager对象
        BucketManager bucketManager = new BucketManager(auth);

        try {
            //调用copy方法移动文件
            bucketManager.copy(from.getBucket(), from.getKey().getKey(), to.getBucket(), to.getKey().getKey());
        } catch (QiniuException e) {
            //捕获异常信息
            Response r = e.response;
            logger.error("copyFile error !", e);
            logger.error("copyFile error !", r);
            return false;
        }
        return true;
    }


    public static class CopyFileBean {

        private String bucket;

        private FileKey key;

        public CopyFileBean(String bucket, FileKey key) {
            this.bucket = bucket;
            this.key = key;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public FileKey getKey() {
            return key;
        }

        public void setKey(FileKey key) {
            this.key = key;
        }
    }


    public static class CopyImageBean {

        private String bucket;

        private ImageKey key;

        public CopyImageBean(String bucket, ImageKey key) {
            this.bucket = bucket;
            this.key = key;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public ImageKey getKey() {
            return key;
        }

        public void setKey(ImageKey key) {
            this.key = key;
        }
    }


    /**
     * 获取文件信息
     *
     * @return
     */
    public FileInfo getFileInfo(FileKey fileKey) {
        Assert.notNull(fileKey, "this imageKey is required; it must not be null");
        String scope = fileKey.getScope();

        BucketManager bucketManager = new BucketManager(auth);
        if (BucketScopeEnum.SCOPE_PRIVATE.getValue().equals(scope)) {
            try {
                return bucketManager.stat(privateBucket, fileKey.getKey());
            } catch (QiniuException e) {
                logger.error("获取文件信息异常,imageKey[" + fileKey.getKey() + "]", e);
                return null;
            }
        } else {
            try {
                return bucketManager.stat(publicBucket, fileKey.getKey());
            } catch (QiniuException e) {
                logger.error("获取文件信息异常,imageKey[" + fileKey.getKey() + "]", e);
                return null;
            }
        }

    }

    public void moveFile(String from_bucket, String from_key, String to_bucket, String to_key) throws QiniuException {

        BucketManager bucketManager = new BucketManager(auth);
        bucketManager.move(from_bucket, from_key, to_bucket, to_key);

    }


    private String makeBaseUrl(String domain, String spliceUrl) {
        return protocol + "://" + domain + "/" + spliceUrl;
    }


    /**
     * 获取上传凭证
     * </p>
     * 上传策略包含： key、Hash、width、height、fsize
     *
     * @return 生成的上传凭证
     * @author yangsongbo
     * @since v3.0
     */
    private String createUptoken(BucketScopeEnum scope) {
        ReturnBody returnBody = new ReturnBody("$(key)", "$(etag)", "$(imageInfo.width)", "$(imageInfo.height)",
                "$(fsize)");

        StringMap sm = new StringMap();
        String json = JsonUtil.toJson(returnBody);
        sm.put("returnBody", json);
        String bucketName = BucketScopeEnum.SCOPE_PRIVATE.equals(scope) ? privateBucket : publicBucket;
        String uploadToken = auth.uploadToken(bucketName, null, uptokenExpires, sm);
        return uploadToken;
    }

    /**
     * 上传凭证
     *
     * @author yangsongbo
     */
    public static class UploadToken {

        /**
         * 上传凭证
         */
        private String token;

        /**
         * 有效时间（秒）
         */
        private long expires;


        public UploadToken() {

        }

        public UploadToken(String token, long expires) {
            this.token = token;
            this.expires = expires;
        }


        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public long getExpires() {
            return expires;
        }

        public void setExpires(long expires) {
            this.expires = expires;
        }

        @Override
        public String toString() {
            return "UploadToken [token=" + token + ", expires=" + expires + "]";
        }


    }


    /**
     * FileKey对象
     * </p>
     * 包含key、hash、fsize
     *
     * @author yangsongbo
     * @since 2.3
     */
    public static class FileKey {
        private String key;

        private String hash;

        private int fsize;

        //默认为公开空间
        private String scope = BucketScopeEnum.SCOPE_PUBLIC.getValue();

        public FileKey() {

        }

        public FileKey(String key, String hash, int fsize, String scope) {
            this.key = key;
            this.hash = hash;
            this.fsize = fsize;
            this.scope = scope;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public int getFsize() {
            return fsize;
        }

        public void setFsize(int fsize) {
            this.fsize = fsize;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        /**
         * 将FileKeyBean 转换为 key?hash?fsize
         *
         * @return 返回 key?hash?fsize
         * @author yangsongbo
         * @since 3.0
         */
        public String toFileKeyString() {
            if (key == null) {
                logger.error("this {} is required; it must not be null", "key");
                return null;
            }
            if (hash == null) {
                logger.error("this {} is required; it must not be null", "hash");
                return null;
            }
            StringBuilder fileKey = new StringBuilder();
            fileKey.append(key + KEY_SPLIT + hash);
            fileKey.append(KEY_SPLIT + fsize);
            fileKey.append(KEY_SPLIT + scope);

            return fileKey.toString();
        }


        public FileKey toFileKey(String imageKeyString) {
            if (StringUtils.isBlank(imageKeyString)) {
                return null;
            }
            String[] strs = imageKeyString.split("\\?");
            int len = strs.length;
            this.setKey(strs[0]);
            this.setHash(len >= 2 ? strs[1] : null);
            this.setFsize(len >= 3 ? Integer.valueOf(strs[2]) : 0);
            this.setScope(len >= 4 ? strs[3] : BucketScopeEnum.SCOPE_PUBLIC.getValue());
            return this;
        }

        /**
         * 生成文件key
         *
         * @return
         */
        public static String genKey(String suffix) {
            String uuid = UUIDUtil.getJavaUUID();
            if (StringUtils.isNotEmpty(suffix)) {
                if (!suffix.startsWith(".")) {
                    suffix = "." + suffix;
                }
            } else {
                suffix = "";
            }
            return "files/2/" + uuid + suffix;
        }

    }


    /**
     * ImageKey对象
     * </p>
     * 包含key、hash、width、height、fsize、scope
     *
     * @author yangsongbo
     * @since 2.3
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class ImageKey {

        private String key;

        private String hash;

        private int width;

        private int height;

        private int fsize;
        /**
         * 公开空间 or 私有空间
         */
        private String scope = BucketScopeEnum.SCOPE_PUBLIC.getValue();

        /**
         * 将ImageKeyBean 转换为 key?hash?width?height?fsize
         *
         * @return 返回 key?hash?width?height?fsize
         * @author yangsongbo
         * @since 3.0
         */
        public String toImageKeyString() {
            if (key == null) {
                logger.error("this {} is required; it must not be null", "key");
                return null;
            }
            if (hash == null) {
                logger.error("this {} is required; it must not be null", "hash");
                return null;
            }
            StringBuilder fileKey = new StringBuilder();
            fileKey.append(key + KEY_SPLIT + hash);
            fileKey.append(KEY_SPLIT + width);
            fileKey.append(KEY_SPLIT + height);
            fileKey.append(KEY_SPLIT + fsize);
            fileKey.append(KEY_SPLIT + scope);

            return fileKey.toString();
        }


        public ImageKey toImageKey(String imageKeyString) {
            if (StringUtils.isBlank(imageKeyString)) {
                return null;
            }
            String[] strs = imageKeyString.split("\\?");
            int len = strs.length;
            this.setKey(strs[0]);
            this.setHash(len >= 2 ? strs[1] : null);
            this.setWidth(len >= 3 ? Integer.valueOf(strs[2]) : 0);
            this.setHeight(len >= 4 ? Integer.valueOf(strs[3]) : 0);
            this.setFsize(len >= 5 ? Integer.valueOf(strs[4]) : 0);
            this.setScope(len >= 6 ? strs[5] : BucketScopeEnum.SCOPE_PUBLIC.getValue());
            return this;
        }

        /**
         * 生成图片key
         *
         * @return
         */
        public static String genKey(String suffix) {
            String uuid = UUIDUtil.getJavaUUID();
            if (StringUtils.isNotEmpty(suffix)) {
                if (!suffix.startsWith(".")) {
                    suffix = "." + suffix;
                }
            } else {
                suffix = "";
            }
            return "images/2/" + uuid + suffix;
        }

    }

    /**
     * 用来定义上传策略
     *
     * @author yangsongbo
     * @since v3.0
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public class ReturnBody {
        private String key;
        private String hash;
        private String width;
        private String height;
        private String fsize;

    }

    /**
     * access密钥
     */
    private String accessKey;

    /**
     * secret密钥
     */
    private String secretKey;

    /**
     * 公开空间
     */
    private String publicBucket;

    /**
     * 私密空间
     */
    private String privateBucket;

    /**
     * 私密域名
     */
    private String privateDomain;

    /**
     * 公开域名
     */
    private String publicDomain;

    /**
     * 上传凭证有效时间
     */
    private long uptokenExpires;

    /**
     * 下载凭证有效时间
     **/
    private long downloadExpires;

    /**
     * 水印图片 大
     */
    private String watermarkImageBig;

    /**
     * 水印图片 大
     */
    private String watermarkImageSmall;
    /**
     * 协议
     */
    private String protocol;
    /**
     * 区域
     */
    private String zone;

    /**
     * 上传凭证 public key
     */
    private String uptokenPublicKey;

    /**
     * 上传凭证 private key
     */
    private String uptokenPrivateKey;

    /**
     * hash
     */
    private static final String HASH = "hash=";

    /**
     * 宽度
     */
    private static final String WIDTH = "width=";

    /**
     * 高度
     */
    private static final String HEIGHT = "height=";

    /**
     * 文件大小
     */
    private static final String FSIZE = "fsize=";

    /**
     * 空间类型 1 公开 2 私密
     */
    private static final String SCOPE = "scope=";

    /**
     * key分隔符
     */
    private static final String KEY_SPLIT = "?";

    /**
     * 密钥配置
     */
    private Auth auth;

    @Autowired
    private RedisClient redisClient;

    public void init() {
        auth = Auth.create(accessKey, secretKey);
        //0:华东,1:华北
        if ("0".equals(zone)) {
            Config.zone = Zone.zone0();
        } else if ("1".equals(zone)) {
            Config.zone = Zone.zone1();
        }
    }


    public String getZone() {
        return zone;
    }


    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getUptokenPublicKey() {
        return uptokenPublicKey;
    }


    public void setUptokenPublicKey(String uptokenPublicKey) {
        this.uptokenPublicKey = uptokenPublicKey;
    }


    public String getUptokenPrivateKey() {
        return uptokenPrivateKey;
    }


    public void setUptokenPrivateKey(String uptokenPrivateKey) {
        this.uptokenPrivateKey = uptokenPrivateKey;
    }


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    /**
     * 文件类型
     */
    public enum FileType {
        IMAGE, FILE
    }

    /**
     * 上传凭证有效时间
     */
    public long getUptokenExpires() {
        return uptokenExpires;
    }

    /**
     * 上传凭证有效时间
     */
    public void setUptokenExpires(long uptokenExpires) {
        this.uptokenExpires = uptokenExpires;
    }

    /**
     * 下载凭证有效时间
     */
    public long getDownloadExpires() {
        return downloadExpires;
    }

    /**
     * 下载凭证有效时间
     */
    public void setDownloadExpires(long downloadExpires) {
        this.downloadExpires = downloadExpires;
    }


    public String getPrivateDomain() {
        return privateDomain;
    }

    public void setPrivateDomain(String privateDomain) {
        this.privateDomain = privateDomain;
    }

    public String getPublicDomain() {
        return publicDomain;
    }

    public void setPublicDomain(String publicDomain) {
        this.publicDomain = publicDomain;
    }

    /**
     * access密钥
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * access密钥
     */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * secret密钥
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * secret密钥
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPublicBucket() {
        return publicBucket;
    }

    public void setPublicBucket(String publicBucket) {
        this.publicBucket = publicBucket;
    }

    public String getPrivateBucket() {
        return privateBucket;
    }

    public void setPrivateBucket(String privateBucket) {
        this.privateBucket = privateBucket;
    }

    public String getWatermarkImageBig() {
        return watermarkImageBig;
    }

    public void setWatermarkImageBig(String watermarkImageBig) {
        this.watermarkImageBig = watermarkImageBig;
    }

    public String getWatermarkImageSmall() {
        return watermarkImageSmall;
    }

    public void setWatermarkImageSmall(String watermarkImageSmall) {
        this.watermarkImageSmall = watermarkImageSmall;
    }

}
