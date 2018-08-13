package com.multi.maven.utils;


import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: litao
 * @see:
 * @description:
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */
public class ReflectionUtil {
    /**
     *
     */
    public static boolean isVoid(Class<?> clazz) {
        return clazz == void.class || clazz == Void.class;
    }

    /**
     * Property
     */
    public static Object getProperty(Object target, String name) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(target);
    }

    public static void setProperty(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * Field
     */
    public static List<Field> getAllFields(Class<?> clazz) throws Exception {
        return getAllFields(clazz, null, true);
    }

    public static List<Field> getAllFields(Class<?> clazz, boolean excludeStaticFileds) throws Exception {
        return getAllFields(clazz, null, excludeStaticFileds);
    }

    public static List<Field> getAllFields(Class<?> clazz, Class<? extends Annotation> annotation, boolean excludeStaticFileds) throws Exception {
        if (clazz == null) {
            return null;
        }

        List<Field> r = new LinkedList<Field>();
        Class<?> parent = clazz;
        while (parent != null) {
            for (Field f : parent.getDeclaredFields()) {
                f.setAccessible(true);
                if (excludeStaticFileds && (f.getModifiers() & Modifier.STATIC) != 0) {
                    continue;
                }
                if (annotation != null && !f.isAnnotationPresent(annotation)) {
                    continue;
                }
                r.add(f);
            }

            parent = parent.getSuperclass();
        }
        return r;
    }

    /**
     * Method
     */
    public static String getFieldName(Method m) {
        if (m == null) {
            return null;
        }
        if (!isGetterMethod(m) && !isSetterMethod(m)) {
            return null;
        }

        StringBuilder r = new StringBuilder();
        if (isIsMethod(m)) {
            r.append(m.getName().substring(2));
        } else if (isGetterMethod(m)) {
            r.append(m.getName().substring(3));
        } else if (isSetterMethod(m)) {
            r.append(m.getName().substring(3));
        }
        r.replace(0, 1, r.substring(0, 1).toLowerCase());
        return r.toString();
    }

    public static boolean isIsMethod(Method method) {
        if (method == null) {
            return false;
        }

        if (!method.getName().startsWith("is")) {
            return false;
        }
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        if (!method.getReturnType().equals(boolean.class)) {
            return false;
        }
        return true;
    }

    public static boolean isGetterMethod(Method method) {
        if (method == null) {
            return false;
        }

        if (isIsMethod(method)) {
            return true;
        }

        if (!method.getName().startsWith("get")) {
            return false;
        }
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        if (isVoid(method.getReturnType())) {
            return false;
        }
        return true;
    }

    public static boolean isSetterMethod(Method method) {
        if (method == null) {
            return false;
        }

        if (!method.getName().startsWith("set")) {
            return false;
        }
        if (method.getParameterTypes().length != 1) {
            return false;
        }
        if (!isVoid(method.getReturnType())) {
            return false;
        }
        return true;
    }

    public static Method findGetterMethod(Class<?> clazz, Field field) {
        StringBuilder sb = new StringBuilder(field.getName());
        sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
        sb.insert(0, "get");

        Method r = findPublicMethod(clazz, sb.toString(), new Class<?>[]{});
        if (r == null) {
            if (field.getType().equals(boolean.class)) {
                sb.replace(0, 3, "is");
            }
            r = findPublicMethod(clazz, sb.toString(), new Class<?>[]{});
        }
        return r;
    }

    public static Method findSetterMethod(Class<?> clazz, Field field) {
        StringBuilder sb = new StringBuilder(field.getName());
        sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
        sb.insert(0, "set");

        Class<?> type = field.getType();
        if (type.isPrimitive()) {
            type = ClassUtil.primitiveToClass(type);
        }
        return findPublicMethod(clazz, sb.toString(), new Class<?>[]{type});
    }

    public static Method getPublicMethod(Class<?> clazz, String name, Class<?> signatures[])
            throws NoSuchMethodException {
        Method r = findPublicMethod(clazz, name, signatures);
        if (r == null) {
            throw new NoSuchMethodException(clazz.getName() + "." + name + ArrayUtils.toString(signatures));
        }
        return r;
    }

    public static Method findPublicMethod(Class<?> clazz, String name, Class<?> signatures[]) {
        // If the method takes no arguments, we can the following optimization
        // which avoids the expensive call to getMethods().
        if (signatures.length == 0) {
            try {
                return clazz.getMethod(name, signatures);
            } catch (NoSuchMethodException e) {
                return null;
            } catch (SecurityException se) {
            }
        }

        //
        List<Method> methods = new ArrayList<Method>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(name)) {
                if (matchArguments(signatures, method.getParameterTypes(), false)) {
                    methods.add(method);
                }
            }
        }

        //
        if (methods.size() == 0) {
            return null;
        } else if (methods.size() == 1) {
            return (Method) methods.get(0);
        } else {
            for (Method method : methods) {
                if (matchArguments(signatures, method.getParameterTypes(), true)) {
                    return method;
                }
            }
            return getMostSpecificMethod(methods, signatures);
        }
    }

    /**
     *
     */
    private static Method getMostSpecificMethod(List<Method> methods, Class<?> signatures[]) {
        //
        int maxMatches = 0;
        Method method = null;
        for (Method m : methods) {
            int matches = 0;
            Class<?> paramTypes[] = m.getParameterTypes();
            for (int i = 0; i < signatures.length; i++) {
                Class<?> paramType = paramTypes[i];
                if (paramType.isPrimitive() && !signatures[i].isPrimitive()) {
                    paramType = ClassUtil.primitiveToClass(paramType);
                }
                if (signatures[i] == paramType) {
                    matches++;
                }
            }

            if (matches == 0 && maxMatches == 0) {
                if (method == null) {
                    method = m;
                } else {
                    // If the current method parameters is higher in the inheritance hierarchy then replace it.
                    if (!matchArguments(method.getParameterTypes(), m.getParameterTypes(), false)) {
                        method = m;
                    }
                }
            } else if (matches > maxMatches) {
                maxMatches = matches;
                method = m;
            } else if (matches == maxMatches) { // Ambiguous method
                method = null;
            }
        }
        return method;
    }

    private static boolean matchArguments(Class<?> signatures[], Class<?> paramTypes[], boolean explicit) {
        if (signatures.length != paramTypes.length) {
            return false;
        }

        for (int j = 0; j < signatures.length; j++) {
            Class<?> paramType = paramTypes[j];
            if (paramType.isPrimitive() && !signatures[j].isPrimitive()) {
                paramType = ClassUtil.primitiveToClass(paramType);
            }

            if (explicit) {
                if (signatures[j] != paramType) { // Test each element for equality
                    return false;
                }
            } else { // Consider null an instance of all classes.
                if (signatures[j] != null && !(paramType.isAssignableFrom(signatures[j]))) {
                    return false;
                }
            }
        }
        return true;
    }
}
