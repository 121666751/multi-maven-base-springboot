package com.multi.maven.service;

import com.multi.maven.enums.qiniu.BucketScopeEnum;
import com.multi.maven.enums.qiniu.ImageHeightEnum;
import com.multi.maven.enums.qiniu.ImageStyleEnum;
import com.multi.maven.enums.qiniu.ImageWidthEnum;
import com.multi.maven.exception.BaseException;
import com.multi.maven.qiniu.QiniuTemplate;
import com.multi.maven.qiniu.QiniuTemplate.CopyFileBean;
import com.multi.maven.qiniu.QiniuTemplate.CopyImageBean;
import com.multi.maven.qiniu.QiniuTemplate.FileKey;
import com.multi.maven.qiniu.QiniuTemplate.FileType;
import com.multi.maven.qiniu.QiniuTemplate.ImageKey;
import com.multi.maven.qiniu.QiniuTemplate.UploadToken;
import com.multi.maven.qiniu.beans.Crop;
import com.multi.maven.qiniu.beans.Crop.CropSizeEnum;
import com.multi.maven.qiniu.beans.Gravity;
import com.multi.maven.qiniu.beans.Gravity.GravityEnum;
import com.multi.maven.qiniu.beans.RoundPic;
import com.multi.maven.qiniu.beans.Thumbnail;
import com.multi.maven.qiniu.beans.Thumbnail.ThumbnailEnum;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.model.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 七牛云存储Service
 *
 * @author yangsongbo
 * @since v1.0
 */
@Service
@Slf4j
public class QiniuService {


//    @Autowired
    private QiniuTemplate qiniuTemplate;

    /**
     * imageKey to imageUrl
     * </p>
     * 支持“|”分割的多图
     *
     * @param imageKey
     * @param w        0为原图，不缩放；指定目标图片宽度，高度等比缩放，Width取值范围1-9999。
     * @return
     */
    public String imageKeyToImageUrl(String imageKey, ImageWidthEnum w) {
        return this.imageKeyToImageUrl(imageKey, w, null, null, null);
    }

    /**
     * imageKey to imageUrl
     * </p>
     * 支持“|”分割的多图
     *
     * @param imageKey
     * @param w        0为原图，不缩放；指定目标图片宽度，高度等比缩放，Width取值范围1-9999。
     * @param cropSize 居中裁剪为正方形
     * @return
     */
    public String imageKeyToImageUrl(String imageKey, ImageWidthEnum w, Integer cropSize) {
        return this.imageKeyToImageUrl(imageKey, w, null, cropSize, null);
    }

    /**
     * 将imageKey 转换为 imageUrl
     *
     * @param imageKey  支持“|”分割的多图
     * @param thumbnail 缩放参数，可以为空，默认不缩放
     * @param gravity   图片处理重心参数表，可以为空，默认为左上角(NorthWest)
     * @param crop      裁剪参数，可以为空，默认为不裁剪
     * @param roundPic  圆角参数，可以为空。
     * @return
     */
    public String imageKeyToImageUrl(String imageKey, Thumbnail thumbnail, Gravity gravity, Crop crop,
                                     RoundPic roundPic) {
        if (StringUtils.isBlank(imageKey))
            return null;
        List<String> urls = new ArrayList<String>();
        imageKey = imageKey.replaceAll("｜", "\\|");

        String[] keys = imageKey.split("\\|");
        for (String key : keys) {
            String imageUrl = "";
            if (StringUtils.isNotBlank(key) && key.startsWith("http")) {
                imageUrl = key;
            } else {
                ImageKey ik = new ImageKey().toImageKey(key);
                imageUrl = qiniuTemplate.imageKeyToImageUrl(ik, thumbnail, gravity, crop, roundPic);
            }
            if (StringUtils.isNotBlank(imageUrl))
                urls.add(imageUrl);
        }
        return StringUtils.join(urls, "|");
    }

    /**
     * imageKey to imageUrl
     * </p>
     * 支持“|”分割的多图
     * </p>
     * w 和 h 都指定的情况：按指定宽高值强行缩略，可能导致目标图片变形，width和height取值范围1-9999。
     * </p>
     * 仅指定 w 的情况：指定目标图片宽度，高度等比缩放，Width取值范围1-9999。
     * </p>
     * 仅指定 h 的情况 ：指定目标图片高度，宽度等比缩放，Height取值范围1-9999。
     *
     * @param imageKey 图片Key
     * @param w        图片宽度
     * @param h        图片高度
     * @param cropSize 裁剪的尺寸，执行简单的裁剪（居中呈正方形裁剪）
     * @param roundPic 圆角参数，可以为空
     * @return
     */
    public String imageKeyToImageUrl(String imageKey, ImageWidthEnum w, ImageHeightEnum h, Integer cropSize,
                                     RoundPic roundPic) {
        return this.imageKeyToImageUrl
                (imageKey, w != null ? w.getValue() : null, h != null ? h.getValue() : null,
                        cropSize, roundPic);
    }


    /**
     * imageKey to imageUrl
     * </p>
     * 支持“|”分割的多图
     * </p>
     * w 和 h 都指定的情况：按指定宽高值强行缩略，可能导致目标图片变形，width和height取值范围1-9999。
     * </p>
     * 仅指定 w 的情况：指定目标图片宽度，高度等比缩放，Width取值范围1-9999。
     * </p>
     * 仅指定 h 的情况 ：指定目标图片高度，宽度等比缩放，Height取值范围1-9999。
     *
     * @param imageKey 图片Key
     * @param w        图片宽度
     * @param h        图片高度
     * @param cropSize 裁剪的尺寸，执行简单的裁剪（居中呈正方形裁剪）
     * @param roundPic 圆角参数，可以为空
     * @return
     */
    public String imageKeyToImageUrl(String imageKey, Integer w, Integer h, Integer cropSize,
                                     RoundPic roundPic) {
        if (StringUtils.isBlank(imageKey))
            return null;
        imageKey = imageKey.replaceAll("｜", "\\|");
        String[] keys = imageKey.split("\\|");

        List<String> urls = new ArrayList<String>(keys.length);

        for (String key : keys) {
            String imageUrl = "";
            if (StringUtils.isNotBlank(key) && key.startsWith("http")) {
                imageUrl = key;
            } else {
                ImageKey ik = new ImageKey().toImageKey(key);
                Gravity gravity = null;
                Crop crop = null;
                Thumbnail thumbnail = null;

                if (cropSize != null && cropSize > 0) {
                    gravity = Gravity.instance(GravityEnum.Center);
                    crop = Crop.instance(CropSizeEnum.MODE_3, null).setH(cropSize).setW(cropSize);
                }

                // 宽、高都指定的情况,强行缩略
                if (w != null && !w.equals(ImageWidthEnum.SIZE_0.getValue())
                        && h != null && !h.equals(ImageHeightEnum.SIZE_0.getValue())) {
                    thumbnail = Thumbnail.instance(ThumbnailEnum.MODE_8).setW(w).setH(h);

                } else if (w != null && !w.equals(ImageWidthEnum.SIZE_0.getValue())) {
                    thumbnail = Thumbnail.instance(ThumbnailEnum.MODE_4).setW(w);
                } else if (h != null && !h.equals(ImageHeightEnum.SIZE_0.getValue())) {
                    thumbnail = Thumbnail.instance(ThumbnailEnum.MODE_5).setH(h);
                }

                imageUrl = qiniuTemplate.imageKeyToImageUrl(ik, thumbnail, gravity, crop, roundPic);
            }

            if (StringUtils.isNotBlank(imageUrl))
                urls.add(imageUrl);
        }
        return StringUtils.join(urls, "|");
    }

    /**
     * imageKey to imageUrl
     * </p>
     * 支持“|”分割的多图
     * </p>
     *
     * @param imageKey   图片Key
     * @param imageStyle 指定图片样式
     * @return
     */
    public String imageKeyToImageUrl(String imageKey, ImageStyleEnum imageStyle) {
        if (StringUtils.isBlank(imageKey))
            return null;
        List<String> urls = new ArrayList<String>();
        imageKey = imageKey.replaceAll("｜", "\\|");

        String[] keys = imageKey.split("\\|");
        for (String key : keys) {
            String imageUrl = "";
            if (StringUtils.isNotBlank(key) && key.startsWith("http")) {
                imageUrl = key;
            } else {
                ImageKey ik = new ImageKey().toImageKey(key);
                imageUrl = qiniuTemplate.imageKeyToImageUrl(ik, imageStyle);
            }
            if (StringUtils.isNotBlank(imageUrl))
                urls.add(imageUrl);
        }
        return StringUtils.join(urls, "|");
    }

    /**
     * imageUrl to ImageKey
     * </p>
     * 支持“|”分隔的多图
     * </p>
     * imageUrl不以http://开头的,不做转换，直接返回
     *
     * @param imageUrl 图像链接路径
     * @return
     */
    public String imageUrlToImageKey(String imageUrl) {
        if (StringUtils.isBlank(imageUrl))
            return null;

        List<String> keys = new ArrayList<String>();
        imageUrl = imageUrl.replaceAll("｜", "\\|");
        String[] urls = imageUrl.split("\\|");
        for (String url : urls) {
            if (StringUtils.isBlank(url))
                continue;
            if (!url.startsWith("http")) {
                keys.add(url);
            } else {
                ImageKey imageKey = qiniuTemplate.imageUrlToImageKey(url);
                if (imageKey != null) {
                    String imageKeyStr = imageKey.toImageKeyString();
                    if (StringUtils.isNotBlank(imageKeyStr))
                        keys.add(imageKeyStr);
                }
            }
        }

        return StringUtils.join(keys, "|");
    }

    /**
     * fileKey to fileUrl
     * </p>
     * 支持“|”分隔的多文件
     *
     * @param fileKey
     * @return
     */
    public String fileKeyToFileUrl(String fileKey) {
        if (StringUtils.isBlank(fileKey))
            return null;
        List<String> urls = new ArrayList<String>();
        fileKey = fileKey.replaceAll("｜", "\\|");
        String[] keys = fileKey.split("\\|");
        for (String key : keys) {
            String fileUrl = "";
            if (StringUtils.isNotBlank(key) && key.startsWith("http")) {
                fileUrl = key;
            } else {
                FileKey fk = new FileKey().toFileKey(key);
                fileUrl = qiniuTemplate.fileKeyToFileUrl(fk);
            }

            if (StringUtils.isNotBlank(fileUrl))
                urls.add(fileUrl);
        }
        return StringUtils.join(urls, "|");
    }

    /**
     * fileUrl to fileKey
     * </p>
     * 支持“|”分隔的多文件
     * </p>
     * fileUrl不以http://开头的,不做转换，直接返回
     *
     * @param fileUrl 文件链接路径
     * @return
     */
    public String fileUrlToFileKey(String fileUrl) {
        if (StringUtils.isBlank(fileUrl))
            return null;

        List<String> keys = new ArrayList<String>();
        fileUrl = fileUrl.replaceAll("｜", "\\|");

        String[] urls = fileUrl.split("\\|");
        for (String url : urls) {
            if (StringUtils.isBlank(url))
                continue;
            if (!url.startsWith("http")) {
                keys.add(url);
            } else {
                FileKey fileKey = qiniuTemplate.fileUrlToFileKey(url);
                if (fileKey != null) {
                    String fileKeyStr = fileKey.toFileKeyString();
                    if (StringUtils.isNotBlank(fileKeyStr))
                        keys.add(fileKeyStr);
                }
            }
        }

        return StringUtils.join(keys, "|");
    }

    /**
     * 获取上传凭证
     *
     * @param isNew 是否生成新的uploadToken,为false时，优先从缓存中获取uploadToken
     * @return
     */
    public UploadToken getUploadToken(boolean isNew, BucketScopeEnum scope) {
        return qiniuTemplate.getUploadToken(isNew, scope);
    }

    /**
     * 上传网络图片
     *
     * @param key 指定图片的Key: images/<0 IOS 1 Android 2 服务器>/<uuid
     *            字母小写>.jpg(或.png)
     * @param url 网络图片URL
     * @return imageKey ,格式：key?hash?width?height?fsize
     */
    public String uploadImage(String key, URL url, BucketScopeEnum scope) {
        ImageKey ik = null;
        try {
            ik = qiniuTemplate.uploadImage(key, url, scope);
        } catch (QiniuException e) {
            throw new BaseException("上传网络图片异常：url=" + url, e);
        }
        if (ik != null)
            return ik.toImageKeyString();
        return null;
    }

    /**
     * 上传图片
     *
     * @param key      指定图片的Key: images/<0 IOS 1 Android 2 服务器>/<uuid
     *                 字母小写>.jpg(或.png)
     * @param fileData 图像数据
     * @return
     */
    public String uploadImage(String key, byte[] fileData, BucketScopeEnum scope) {
        ImageKey ik = null;
        try {
            ik = qiniuTemplate.uploadImage(key, fileData, scope);
        } catch (QiniuException e) {
            throw new BaseException("上传图片异常", e);
        }
        if (ik != null)
            return ik.toImageKeyString();
        return null;
    }

    /**
     * 上传图片
     *
     * @param key      指定图片的Key: images/<0 IOS 1 Android 2 服务器>/<uuid
     *                 字母小写>.jpg(或.png)
     * @param filePath 图像路径
     * @return
     */
    public String uploadImage(String key, String filePath, BucketScopeEnum scope) {
        ImageKey ik = null;
        try {
            ik = qiniuTemplate.uploadImage(key, filePath, scope);
        } catch (QiniuException e) {
            throw new BaseException("上传图片异常", e);
        }
        if (ik != null)
            return ik.toImageKeyString();
        return null;
    }

    /**
     * 上传文件
     *
     * @param key      指定图片的Key: files/<0 IOS 1 Android 2 服务器>/<uuid 字母小写>.<文件后缀>
     * @param fileData 文件数据
     * @return
     */
    public String uploadFile(String key, byte[] fileData, BucketScopeEnum scope) {
        FileKey fk = null;
        try {
            fk = qiniuTemplate.uploadFile(key, fileData, scope);
        } catch (QiniuException e) {
            throw new BaseException("上传文件异常", e);
        }
        if (fk != null)
            return fk.toFileKeyString();
        return null;
    }

    /**
     * 上传文件
     *
     * @param key      指定图片的Key: files/<0 IOS 1 Android 2 服务器>/<uuid 字母小写>.<文件后缀>
     * @param filePath 文件路径
     * @return
     */
    public String uploadFile(String key, String filePath, BucketScopeEnum scope) {
        FileKey fk = null;
        try {
            fk = qiniuTemplate.uploadFile(key, filePath, scope);
        } catch (QiniuException e) {
            throw new BaseException("上传文件异常", e);
        }
        if (fk != null)
            return fk.toFileKeyString();
        return null;
    }

    /**
     * 上传文件
     *
     * @param key  指定图片的Key: files/<0 IOS 1 Android 2 服务器>/<uuid 字母小写>.<文件后缀>
     * @param file 文件
     * @return
     */
    public String uploadFile(String key, File file, BucketScopeEnum scope) {
        FileKey fk = null;
        try {
            fk = qiniuTemplate.uploadFile(key, file, scope);
        } catch (QiniuException e) {
            throw new BaseException("上传文件异常", e);
        }
        if (fk != null)
            return fk.toFileKeyString();
        return null;
    }

    /**
     * 生成一个Key
     *
     * @param fileType 文件类型 {@link FileType}
     * @param suffix   文件后缀，如 .jpg
     * @return
     */
    public String generateKey(FileType fileType, String suffix) {

        switch (fileType) {
            case IMAGE:
                return QiniuTemplate.ImageKey.genKey(suffix);

            case FILE:
                return QiniuTemplate.FileKey.genKey(suffix);
            default:
                break;
        }
        return null;

    }

    /**
     * 上传网络图片到qiniu服务器，并获取imageKeg
     *
     * @param url
     * @return
     * @throws MalformedURLException
     */

    public String uploadNetURLImage(String url, BucketScopeEnum scope) {
        if (url == null)
            return null;
        // 适配url为imageKey的情况，直接返回
        if (!url.startsWith("http"))
            return url;

        if (qiniuTemplate.isQiniuUrl(url)) {
            return this.imageUrlToImageKey(url);
        }

        String qiniuKey = generateKey(FileType.IMAGE, ".jpg");
        String imageUrl = null;
        try {
            imageUrl = uploadImage(qiniuKey, new URL(url), scope);
        } catch (MalformedURLException e) {
            log.error("获取网络图片，并上传到七牛服务器出现错误");
        }
        return imageUrl;
    }

    /**
     * 获取图片信息
     *
     * @param imageKey
     * @return
     */
    public FileInfo getImageInfo(String imageKey) {
        if (StringUtils.isBlank(imageKey))
            return null;
        ImageKey ik = new ImageKey();
        ik.toImageKey(imageKey);

        return qiniuTemplate.getFileInfo(ik);
    }

    /**
     * 获取文件信息
     *
     * @param fileKey
     * @return
     */
    public FileInfo getFileInfo(String fileKey) {
        if (StringUtils.isBlank(fileKey))
            return null;
        FileKey fk = new FileKey();
        fk.toFileKey(fileKey);
        return qiniuTemplate.getFileInfo(fk);
    }

    /**
     * 复制图片
     *
     * @param fromBucket
     * @param fromImageKey
     * @param toBucket
     * @param toImageKey
     * @return
     */
    public String copyImage(String fromBucket, String fromImageKey, String toBucket, String toImageKey) {
        ImageKey fromIK = new ImageKey();
        fromIK.toImageKey(fromImageKey);

        ImageKey toIK = new ImageKey();
        toIK.toImageKey(toImageKey);

        boolean result = qiniuTemplate.copyImage(new CopyImageBean(fromBucket, fromIK),
                new CopyImageBean(toBucket, toIK));
        if (result)
            return toIK.toImageKeyString();
        else
            return null;
    }

    /**
     * 复制图片
     *
     * @param fromBucket
     * @param fromImageKey
     * @param toBucket
     * @return
     */
    public String copyImage(String fromBucket, String fromImageKey, String toBucket) {
        ImageKey fromIK = new ImageKey();
        fromIK.toImageKey(fromImageKey);

        ImageKey toIK = new ImageKey();
        toIK.toImageKey(fromImageKey);
        toIK.setKey(this.generateKey(FileType.IMAGE, ""));

        boolean result = qiniuTemplate.copyImage(new CopyImageBean(fromBucket, fromIK),
                new CopyImageBean(toBucket, toIK));
        if (result)
            return toIK.toImageKeyString();
        else
            return null;
    }

    /**
     * 复制文件
     *
     * @param fromBucket
     * @param fromFileKey
     * @param toBucket
     * @return
     */
    public String copyFile(String fromBucket, String fromFileKey, String toBucket) {
        FileKey fromFK = new FileKey();
        fromFK.toFileKey(fromFileKey);

        FileKey toFK = new FileKey();
        toFK.toFileKey(fromFileKey);
        toFK.setKey(this.generateKey(FileType.FILE, ""));

        boolean result = qiniuTemplate.copyFile(new CopyFileBean(fromBucket, fromFK), new CopyFileBean(toBucket, toFK));
        if (result)
            return toFK.toFileKeyString();
        else
            return null;
    }

    public String copyFile(String fromBucket, String fromFileKey, String toBucket, String toFileKey) {
        FileKey fromFK = new FileKey();
        fromFK.toFileKey(fromFileKey);

        FileKey toFK = new FileKey();
        toFK.toFileKey(toFileKey);

        boolean result = qiniuTemplate.copyFile(new CopyFileBean(fromBucket, fromFK), new CopyFileBean(toBucket, toFK));
        if (result)
            return toFK.toFileKeyString();
        else
            return null;
    }

}
