package com.micro.wish.mongo;

import com.multi.maven.utils.StringUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 根据Bean 生成 相应代码  mongodb
 * @author yangsongbo
 *
 */
public class GenBeanCodeRun {
	/**指定需要生成代码的Class*/
	private static  Class<?> beanClass = Object.class;

	public static void main(String[] args) {
		String toStr = genPkGetSet(beanClass);
		toStr += "\n\n";
		toStr += genToString(beanClass);
		toStr += "\n\n";
		toStr += genPutFieldValueToMap(beanClass);
		toStr += "\n\n";
		toStr += genDoMapToBeanValue(beanClass);
		toStr += "\n\n";
		toStr += genFieldsInnerClass(beanClass);
		toStr += "\n\n";
		System.out.println(toStr);
	

	}

	private static List<Field> getAllFieldList(Class<?> beanClass){
		Class<?> cls = beanClass;
		List<Field> list = new ArrayList<Field>();
		while(true){
			if("Object".equals(cls.getSimpleName())) {
				break;
			}
			Field[] fields = cls.getDeclaredFields();
			list.addAll(Arrays.asList(fields));
			cls = cls.getSuperclass();
		}
		return list;
	}
	
	
	/**
	 * 生成toString方法
	 * @param beanClass
	 * @return
	 */
	private static String genToString(Class<?> beanClass) {
		if(beanClass == null) {
			throw new NullPointerException();
		}
		List<Field> fields = getAllFieldList(beanClass)	;
		if(fields ==null || fields.isEmpty()){
			System.err.println(beanClass.getName()+" fields is not exist!");
			return null;
		}
        StringBuffer toStr = new StringBuffer();
        toStr.append("    public String toString(){" + "\n");
        toStr.append("        String sep = \"; \";" + "\n");
        toStr.append("        StringBuffer sb = new StringBuffer();" + "\n");
        toStr.append("        sb.append(\""+beanClass.getSimpleName()+"\").append(\":\");" + "\n");
		for(Field field : fields){
			String fieldName = field.getName();
			toStr.append("        sb.append(\"["+fieldName+"]\").append(\" = \").append(get"+firstCodeUpper(fieldName)+"()).append(sep);" + "\n");
		}
        toStr.append("        return sb.toString();" + "\n");
        toStr.append("    }" + "\n");

		return toStr.toString();
		
	}
	
	
	/**
	 * 生成字段常量内部类方法
	 * @param beanClass
	 * @return
	 */
	private static String genFieldsInnerClass(Class<?> beanClass) {
		if(beanClass == null) {
			throw new NullPointerException();
		}
		List<Field> fields = getAllFieldList(beanClass)	;
		if(fields ==null || fields.isEmpty()){
			System.err.println(beanClass.getName()+" fields is not exist!");
			return null;
		}
        StringBuffer toStr = new StringBuffer();
//        toStr.append("    public static class "+beanClass.getSimpleName()+"Fields{" + "\n");
      
		for(Field field : fields){
			String fieldName = field.getName();
			toStr.append("    public static final String field_"+fieldName+" = \""+fieldName+"\";" + "\n");
		}
//        toStr.append("    }" + "\n");

		return toStr.toString();
		
	}
	
	
	/**
	 * 生成PutFieldValueToMap方法
	 * @param beanClass
	 * @return
	 */
	private static String genPutFieldValueToMap(Class<?> beanClass) {
		if(beanClass == null) {
			throw new NullPointerException();
		}
		List<Field> fields = getAllFieldList(beanClass)	;
		if(fields ==null || fields.isEmpty()){
			System.err.println(beanClass.getName()+" fields is not exist!");
			return null;
		}
        StringBuffer toStr = new StringBuffer();
        toStr.append("    public Map<String,Object> putFieldValueToMap() {" + "\n");
        toStr.append("   		Map<String,Object> map_=new HashMap<String,Object>();" + "\n");

		for(Field field : fields){
			String fieldName = field.getName();
			toStr.append("    	map_.put(\""+fieldName+"\",get"+firstCodeUpper(fieldName)+"());" + "\n");
		}
        toStr.append("		return map_;" + "\n");
        toStr.append("    }" + "\n");

		return toStr.toString();
		
	}
	
	
	/**
	 * 生成doMapToBeanValue方法
	 * @param beanClass
	 * @return
	 */
	private static String genDoMapToBeanValue(Class<?> beanClass) {
		if(beanClass == null) {
			throw new NullPointerException();
		}
		List<Field> fields = getAllFieldList(beanClass)	;
		if(fields ==null || fields.isEmpty()){
			System.err.println(beanClass.getName()+" fields is not exist!");
			return null;
		}
        StringBuffer toStr = new StringBuffer();
        
        toStr.append("    @SuppressWarnings(\"unchecked\")" + "\n");
        toStr.append("    public void doMapToDtoValue(Map<String, Object> map,boolean isDealNull) {" + "\n");

		for(Field field : fields){
			String fieldName = field.getName();
			String fieldType = field.getType().getSimpleName();

			Class<?> genericClass = getCenericClass(field);
			toStr.append("    	if(null!=map.get(\""+fieldName+"\")){" + "\n");
			if(genericClass==null) {
				toStr.append("    		this.set"+firstCodeUpper(fieldName)+"(("+fieldType+")map.get(\""+fieldName+"\"));" + "\n");
			}
			else {
				toStr.append("    		this.set"+firstCodeUpper(fieldName)+"(("+fieldType+"<"+genericClass.getSimpleName()+">)map.get(\""+fieldName+"\"));" + "\n");
			}

			toStr.append("    	}else{" + "\n");
			toStr.append("    		if(isDealNull && map.containsKey(\""+fieldName+"\"))" + "\n");
			if(genericClass==null) {
				toStr.append("    			this.set"+firstCodeUpper(fieldName)+"(("+fieldType+")map.get(\""+fieldName+"\"));" + "\n");
			}
			else {
				toStr.append("    			this.set"+firstCodeUpper(fieldName)+"(("+fieldType+"<"+genericClass.getSimpleName()+">)map.get(\""+fieldName+"\"));" + "\n");
			}

			toStr.append("    	}	" + "\n");

		}
        toStr.append("    }" + "\n");

		return toStr.toString();
		
	}
	
	
	public static String genPkGetSet(Class<?> beanClass){
		Class<?>[] interfaces = beanClass.getInterfaces();
		if(ArrayUtils.isEmpty(interfaces)) {
			return "";
		}
		boolean flag = false;
		for(Class<?> cls : interfaces){
			if("IMongoBean".equals(cls.getSimpleName())){
				flag = true;
			}
		}
		if(!flag) {
			return "";
		}

		
        StringBuffer toStr = new StringBuffer();
        toStr.append("    @Override"+"\n");
        toStr.append("    public String getPk() {"+"\n");
        toStr.append("    	return _id !=null ? _id.toHexString() : null;"+"\n");
        toStr.append("    }"+"\n");
        
        
        toStr.append("    @Override"+"\n");
        toStr.append("    public void setPk(String pk) {"+"\n");
        toStr.append("    	this._id = new ObjectId(pk);"+"\n");
        toStr.append("    }"+"\n");
        return toStr.toString();
	}
	
	
	
	private static String firstCodeUpper(String fieldName){
		if(!StringUtil.hasText(fieldName)) {
			return fieldName;
		}

		return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}
	
	
	
	private static Class<?> getCenericClass(Field field){
		if(field.getType().isPrimitive()) {
			return null;
		}

		Type fc = field.getGenericType();
		if(fc == null) {
			return null;
		}
		if(fc instanceof ParameterizedType){
			ParameterizedType pt = (ParameterizedType) fc;  
			Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
			return genericClazz;
		}
	
		return null;
	}
	
	
	
}
