package com.daowei.bean;

import com.daowei.anno.Bean;
import com.daowei.anno.Di;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnnotationApplicationContext implements ApplicationContext {

    //创建map集合 放bean的实例对象
    private static HashMap<Class, Object> beanFactory = new HashMap<>();
    private static String rootPath;

    //创建有参数构造
    public AnnotationApplicationContext(String basePackage) {
//    public static void path(String basePackage){
        //设置扫描规则

        try {
            //1、把.用\替换
            String packagePath = basePackage.replaceAll("\\.", "\\\\");

            //2、获取包的绝对路径
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packagePath);

            //3、遍历得到路径
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();

                //4、处理路径中的转义
                String filePath = URLDecoder.decode(url.getFile(), "utf-8");

                //4、获取包前面路径部分 字符串截取
                rootPath = filePath.substring(0, filePath.length() - packagePath.length());
                //                System.out.println(filePath);
                //5、包扫描
                loadBean(new File(filePath));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //属性注入
        loadDi();
    }
    //设置包扫描规则 com.daowei以及子包 是否有bean注解 有则返回反射实例

    //返回对象
    @Override
    public Object getBean(Class clazz) {
        return beanFactory.get(clazz);
    }

    //包扫描过程 用反射实例化
    private void loadBean(File file) throws Exception {
        //1、判断当前内容是否是文件夹
        if (file.isDirectory()) {
            //2、是 获取文件夹里面的所有内容
            File[] childrenFiles = file.listFiles();
            //3、若文件夹为空 直接返回
            if (childrenFiles == null || childrenFiles.length == 0) {
                return;
            }
            //4、若不为空 遍历所有内容
            for (File child : childrenFiles) {
                //4.1、遍历得到每个file对象 继续判断 是文件夹->继续遍历 -->为空
                if (child.isDirectory()) {
                    //递归
                    loadBean(child);
                } else {
                    //4.2、遍历得到不是文件夹 是文件 判断是否有注解
                    //4.3、文件——>得到包路径+类名称部分   字符串截取
                    String pathWithClass = child.getAbsolutePath().substring(rootPath.length() - 1);
                    //4.4、判断当前文件是否是 .class
                    if (pathWithClass.contains(".class")) {
                        //4.5、是 把\替换成. 把.class去掉
                        String allName = pathWithClass.replaceAll("\\\\", ".").replace
                                (".class", "");
                        //4.6、判断类上是否有@Bean注解 如果有 实例化
                        //4.6.1 获取类的class对象
                        Class<?> clazz = Class.forName(allName);
                        //4.6.2 判断不是接口
                        if (!clazz.isInterface()) {
                            //4.6.3 判断是否有@Bean注解
                            Bean annotation = clazz.getAnnotation(Bean.class);
                            if (annotation != null) {
                                //4.6.4 实例化
                                Object instance = clazz.getConstructor().newInstance();
                                //4.7、实例化之后 放入map集合中beanFactory
                                //4.7.1 判断当前类有接口 接口class作为map的key
                                if (clazz.getInterfaces().length > 0) {
                                    beanFactory.put(clazz.getInterfaces()[0], instance);
                                } else {
                                    beanFactory.put(clazz, instance);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //属性注入
    private void loadDi() {

        //  实例化对象都是在beanFactory的map集合中
        //1、遍历集合
        Set<Map.Entry<Class, Object>> entries = beanFactory.entrySet();
        for(Map.Entry<Class, Object> entry:entries){
            //2、获取集合中的每个对象 Value 获取到每个对象属性
            Object obj = entry.getValue();

            //获取对象class
            Class<?> clazz = obj.getClass();

            //获取到每个对象所有属性
            Field[] declaredFields = clazz.getDeclaredFields();
            //3、遍历得到每个对象属性数组 得到每个属性

            for(Field field: declaredFields){
                //4、判断是否有@Di注解
                Di di = field.getAnnotation(Di.class);
                //4、判断是否有@Di注解
                if(di!=null){
                    //私有属性设置值
                    field.setAccessible(true);
                    //5、若有@Di注解 把对象进行设置 注入
                    try {
                        field.set(obj,beanFactory.get(field.getType()));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }

                }
            }

        }





    }
}
