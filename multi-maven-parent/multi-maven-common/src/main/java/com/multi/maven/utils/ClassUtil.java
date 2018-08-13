package com.multi.maven.utils;


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author: litao
 * @see:
 * @description: Class类工具
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */


public class ClassUtil {
    //
    private static final HashMap<String, Class<?>> PRIMITIVE_CLASS_MAP = new HashMap<String, Class<?>>();
    private static final HashMap<Class<?>, String> CLASS_TO_SIGNATURE_MAP = new HashMap<Class<?>, String>();

    static {
        //
        PRIMITIVE_CLASS_MAP.put("boolean", boolean.class);
        PRIMITIVE_CLASS_MAP.put("Z", boolean.class);
        PRIMITIVE_CLASS_MAP.put("byte", byte.class);
        PRIMITIVE_CLASS_MAP.put("B", byte.class);
        PRIMITIVE_CLASS_MAP.put("char", char.class);
        PRIMITIVE_CLASS_MAP.put("C", char.class);
        PRIMITIVE_CLASS_MAP.put("short", short.class);
        PRIMITIVE_CLASS_MAP.put("S", short.class);
        PRIMITIVE_CLASS_MAP.put("int", int.class);
        PRIMITIVE_CLASS_MAP.put("I", int.class);
        PRIMITIVE_CLASS_MAP.put("long", long.class);
        PRIMITIVE_CLASS_MAP.put("J", long.class);
        PRIMITIVE_CLASS_MAP.put("float", float.class);
        PRIMITIVE_CLASS_MAP.put("F", float.class);
        PRIMITIVE_CLASS_MAP.put("double", double.class);
        PRIMITIVE_CLASS_MAP.put("D", double.class);
        PRIMITIVE_CLASS_MAP.put("void", void.class);
        PRIMITIVE_CLASS_MAP.put("V", void.class);

        //
        CLASS_TO_SIGNATURE_MAP.put(boolean.class, "Z");
        CLASS_TO_SIGNATURE_MAP.put(byte.class, "B");
        CLASS_TO_SIGNATURE_MAP.put(char.class, "C");
        CLASS_TO_SIGNATURE_MAP.put(short.class, "S");
        CLASS_TO_SIGNATURE_MAP.put(int.class, "I");
        CLASS_TO_SIGNATURE_MAP.put(long.class, "J");
        CLASS_TO_SIGNATURE_MAP.put(float.class, "F");
        CLASS_TO_SIGNATURE_MAP.put(double.class, "D");
        CLASS_TO_SIGNATURE_MAP.put(void.class, "V");
    }

    /**
     *
     */
    public static Set<Class<?>> getAllTypes(Class<?> clazz) {
        //
        Set<Class<?>> r = new LinkedHashSet<Class<?>>();
        Set<Class<?>> set = getClasses(clazz);
        r.addAll(set);

        //
        for (Class<?> c : set) {
            r.addAll(getInterfaces(c));
        }
        return r;
    }

    public static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
        //
        Set<Class<?>> set = getClasses(clazz);

        //
        Set<Class<?>> r = new LinkedHashSet<Class<?>>();
        for (Class<?> c : set) {
            r.addAll(getInterfaces(c));
        }
        return r;
    }

    public static Class<?>[] getAllInterfacesAsArray(Class<?> clazz) {
        //
        final Set<Class<?>> set = getAllInterfaces(clazz);

        //
        int i = 0;
        final Class<?> r[] = new Class<?>[set.size()];
        for (Class<?> c : set) {
            r[i++] = c;
        }
        return r;
    }

    public static Class<?> primitiveToClass(Class<?> clazz) {
        return clazz.isPrimitive() ? typeNameToClass(clazz.getName()) : clazz;
    }

    public static Class<?> loadClass(String className, ClassLoader classLoader)
            throws ClassNotFoundException {
        // Precondition checking
        if (className == null) {
            throw new IllegalArgumentException("parameter className can not be null");
        }
        if (classLoader == null) {
            throw new IllegalArgumentException("parameter classLoader can not be null");
        }

        //
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException ignore) {
            // NOP
        }

        //
        Class<?> resolvedClass = (Class<?>) PRIMITIVE_CLASS_MAP.get(className);
        if (resolvedClass != null) {
            return resolvedClass;
        }

        // Ljava.lang.String;
        if (className.endsWith(";") && className.startsWith("L")) {
            String typeName = className.substring(1, className.length() - 1);
            return classLoader.loadClass(typeName);
        }

        // [[[[I or [[[[java.lang.String
        if (className.charAt(0) == '[') {
            int count = 0;
            int nameLen = className.length();
            while (count < nameLen && className.charAt(count) == '[') {
                count++;
            }

            String arrayTypeName = className.substring(count, className.length());
            Class<?> arrayType = loadClass(arrayTypeName, classLoader);
            return getArrayClass(arrayType, count);
        }

        // I[] or java.lang.Strng[]
        if (className.endsWith("[]")) {
            int count = 0;
            int position = className.length();
            while (position > 1 && className.substring(position - 2, position).equals("[]")) {
                count++;
                position -= 2;
            }

            String typeName = className.substring(0, position);
            Class<?> arrayType = loadClass(typeName, classLoader);
            return getArrayClass(arrayType, count);
        }

        // Failed
        throw new ClassNotFoundException("could not load class: " + className + " from classloader: " + classLoader);
    }

    /**
     *
     */
    private static Class<?> typeNameToClass(String typeName) {
        typeName = typeName.intern();
        if (typeName == "boolean") {
            return Boolean.class;
        } else if (typeName == "byte") {
            return Byte.class;
        } else if (typeName == "char") {
            return Character.class;
        } else if (typeName == "short") {
            return Short.class;
        } else if (typeName == "int") {
            return Integer.class;
        } else if (typeName == "long") {
            return Long.class;
        } else if (typeName == "float") {
            return Float.class;
        } else if (typeName == "double") {
            return Double.class;
        } else if (typeName == "void") {
            return Void.class;
        }
        return null;
    }

    private static Set<Class<?>> getClasses(Class<?> clazz) {
        //
        Set<Class<?>> r = new LinkedHashSet<Class<?>>();
        r.add(clazz);

        //
        for (Class<?> superClass = clazz.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
            r.add(superClass);
        }
        return r;
    }

    private static Set<Class<?>> getInterfaces(Class<?> clazz) {
        Set<Class<?>> r = new LinkedHashSet<Class<?>>();
        LinkedList<Class<?>> stack = new LinkedList<Class<?>>();
        stack.addAll(Arrays.asList(clazz.getInterfaces()));
        while (!stack.isEmpty()) {
            Class<?> intf = (Class<?>) stack.removeFirst();
            if (!r.contains(intf)) {
                r.add(intf);
                stack.addAll(Arrays.asList(intf.getInterfaces()));
            }
        }
        return r;
    }

    private static Class<?> getArrayClass(Class<?> type, int dimension) {
        int dimensions[] = new int[dimension];
        return Array.newInstance(type, dimensions).getClass();
    }

    @SuppressWarnings("unused")
    private static String getClassName(Class<?> type) {
        //
        StringBuilder name = new StringBuilder();
        while (type.isArray()) {
            name.append('[');
            type = type.getComponentType();
        }

        //
        if (type.isPrimitive()) {
            name.append((String) CLASS_TO_SIGNATURE_MAP.get(type));
        } else {
            name.append('L');
            name.append(type.getName());
            name.append(';');
        }
        return name.toString();
    }
}
