package com.multi.maven.enums.redis;

import com.multi.maven.config.PropertiesListenerConfig;
import com.multi.maven.enums.CacheKeyEnum;
import com.multi.maven.enums.base.PropertiesKeyEnum;
import com.multi.maven.utils.PropertyUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;

/**
 * Redis List Key Enum
 * </p>
 *
 * @author yangsongbo
 * @since v1.0
 */
@ConditionalOnBean(name = "propertyUtil")
public enum ListKeyEnum implements CacheKeyEnum<String> {

    /**课程运动节点列表 后缀为 courseId_courseDadys*/
//	LIST_COURSE_SPORT_NODE_JSON("list_course_sport_node_json","BIZ_COURSE_SPORT_NODE"),
    ;
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

    private ListKeyEnum(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

}
