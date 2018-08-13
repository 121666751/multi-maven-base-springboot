package com.multi.maven.enums.redis;

import com.multi.maven.enums.CacheKeyEnum;
import com.multi.maven.enums.base.PropertiesKeyEnum;
import com.multi.maven.utils.PropertyUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

/**
 * Redis Set Key Enum
 * </p>
 *
 * @author yangsongbo
 * @since v1.0
 */
public enum SetKeyEnum implements CacheKeyEnum<String> {

    SET_BLACK_IP("set_black_ip", "IP黑名单"),;
    private String value;
    private String displayName;


    /**
     * redis 空间，用于避免同一redis种不同系统的key重复
     */
    private static String REDIS_SCOPE = PropertyUtil.getProperty(PropertiesKeyEnum.REDIS_SCOPE.getValue());

    @Override
    public String getKey(String... keySuffixs) {
        StringBuilder builder = new StringBuilder(this.value);

        if (StringUtils.isNotBlank(REDIS_SCOPE)) {
            builder.append("_" + REDIS_SCOPE);
        }

        if (ArrayUtils.isNotEmpty(keySuffixs)) {
            for (String keySuffix : keySuffixs) {
                builder.append("_" + keySuffix);
            }
        }

        return builder.toString();
    }

    /**
     * 获取Redis Key
     *
     * @param scope      redis scope，可以为空
     * @param keySuffixs key后缀
     * @return
     */
    public String getKey(String scope, String[] keySuffixs) {
        StringBuilder builder = new StringBuilder(this.value);

        if (StringUtils.isNotBlank(scope)) {
            builder.append("_" + scope);
        }

        if (ArrayUtils.isNotEmpty(keySuffixs)) {
            for (String keySuffix : keySuffixs) {
                builder.append("_" + keySuffix);
            }
        }

        return builder.toString();
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    private SetKeyEnum(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

}
