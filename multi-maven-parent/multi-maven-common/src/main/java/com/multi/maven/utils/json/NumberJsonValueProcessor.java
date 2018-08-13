package com.multi.maven.utils.json;


import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import java.text.DecimalFormat;

public class NumberJsonValueProcessor implements JsonValueProcessor {

    public static NumberJsonValueProcessor instance = new NumberJsonValueProcessor();

    @Override
    public Object processArrayValue(Object value, JsonConfig arg1) {
        return process(value);
    }

    @Override
    public Object processObjectValue(String key, Object value, JsonConfig arg2) {
        return process(value);
    }

    private Object process(Object value) {
        if (value == null) {
            return "";
        } else {
            DecimalFormat format = new DecimalFormat("#.##");
            return format.format(value);
        }
    }

}