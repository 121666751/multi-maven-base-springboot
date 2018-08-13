package com.multi.maven.enums.redis;

import com.multi.maven.enums.CacheKeyEnum;
import com.multi.maven.enums.base.PropertiesKeyEnum;
import com.multi.maven.utils.PropertyUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

/**
 * Redis Hash Key Enum
 * </p>
 *
 * @author yangsongbo
 * @since v1.0
 */
@ConditionalOnBean(name = "propertyUtil")
public enum HashKeyEnum implements CacheKeyEnum<String> {

    /**
     * 开发者账户表
     */
    HASH_DEVELOPER_ACCOUNT("hash_developer_account", "biz_developer_account"),;


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

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    private HashKeyEnum(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

}
