package com.qunshuo.common;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BeanFactory {

    private static Map<String, BeanConfig> beans = new HashMap<String, BeanConfig>();

    private static Map<String, Object> objects = new HashMap<String, Object>();

    private static BeanFactory beanFactory;

    private BeanFactory() {

    }

    public Object getBean(String id) {

        if (beans.containsKey(id)) {
            BeanConfig bean = beans.get(id);
            String scope = bean.getScope();
            if (scope == null || scope.equals("")) {
                scope = "singleton";
            }

            // 如果是单例，忽视大小写，获取当前类id，如果已经存在实例返回
            if (scope.equalsIgnoreCase("singleton")) {
                if (objects.containsKey(id)) {
                    return objects.get(id);
                }
            }

            // 如果不存在，创建实例，如果是单例将其实例引用保存进objects，用于下次使用
            String className = bean.getClassName();
            Class<?> clz = null;
            try {
                clz = Class.forName(className);
                Object object = clz.newInstance();

                if (scope.equalsIgnoreCase("singleton")) {
                    objects.put(id, object);
                }
                // 保存依赖关系
                List<BeanProperty> beanProperies = bean.getProperties();

                if (beanProperies != null && !beanProperies.isEmpty()) {
                    for (BeanProperty beanProperty : beanProperies) {
                        String propertyName = beanProperty.getName();
                        String firstChar = propertyName.substring(0, 1);
                        String leaveChar = propertyName.substring(1);
                        // 将方法名首字母设为大写以使后面依赖注入调用set方法
                        String methodName = firstChar.toUpperCase() + leaveChar;

                        Method method = null;
                        Method[] methods = clz.getMethods();

                        for (Method methodInClass : methods) {
                            String methodNameInClass = methodInClass.getName();
                            if (methodNameInClass.equals("set" + methodName)) {
                                method = methodInClass;
                                break;
                            }
                        }
                        String ref = beanProperty.getRef();
                        String value = beanProperty.getValue();
                        if (ref != null && !"".equals(ref.trim())) {
                            // 在此处调用getBean其实就是依赖的递归
                            Object refObject = this.getBean(ref);
                            // 当我们初始化完到没有依赖的那个类，开始执行上面72行的set方法，传入参数为依赖的类
                            method.invoke(object, refObject);
                        } else if (value != null && !value.trim().equals("")) {
                            // 如果不是对象依赖，比如我们需要的是一些确定的值，就比如一个人的身高这种，就可以在set方法里面传参
                            Class<?>[] parmts = method.getParameterTypes();
                            String propertyValue = beanProperty.getValue();
                            if (parmts[0] == String.class) {
                                method.invoke(object, propertyValue);
                            }
                            if (parmts[0] == int.class) {
                                method.invoke(object, Integer.parseInt(propertyValue));
                            }
                            if (parmts[0] == boolean.class) {
                                method.invoke(object, Boolean.parseBoolean(propertyValue));
                            }
                        }
                    }
                }

                // com.augmentum.book.service.impl.UserSeriveImpl 被代理对象/目标
                // 动态的管理数据库连接
                if (object.getClass().getPackage().getName().equals("com.augmentum.book.service.impl")) {
                    ConnectionDynamicProxy connectionDynamicProxy = new ConnectionDynamicProxy();
                    connectionDynamicProxy.setTarget(object);
                    Object proxyObject = Proxy.newProxyInstance(object.getClass().getClassLoader(),
                            object.getClass().getInterfaces(), connectionDynamicProxy);

                    return proxyObject;
                }

                return object;
            } catch (Exception e) {
                // Do nothing
                e.printStackTrace();
            }
        }
        return null;
    }

    // beanFactory是单例的
    public static BeanFactory getInstance() {

        if (beanFactory == null) {
            beanFactory = new BeanFactory();
            beanFactory.init();
        }
        return beanFactory;
    }

    // 初始换函数是用来解析bean.xml文件的
    private void init() {

        InputStream inputStream = null;
        try {
            inputStream = BeanFactory.class.getClassLoader().getResourceAsStream("bean.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            Element element = document.getDocumentElement();

            NodeList beanNodes = element.getElementsByTagName("bean");
            if (beanNodes == null) {
                return;
            }
            int beanLength = beanNodes.getLength();
            for (int i = 0; i < beanLength; i++) {
                Element beanElement = (Element) beanNodes.item(i);
                BeanConfig bean = new BeanConfig();
                String id = beanElement.getAttribute("id");
                bean.setId(id);

                String className = beanElement.getAttribute("class");
                bean.setClassName(className);

                String scope = beanElement.getAttribute("scope");
                bean.setScope(scope);
                beans.put(id, bean);

                //初始化函数中用来提取xml文件中的依赖关系并保存
                NodeList beanPropertyNodes = beanElement.getElementsByTagName("property");
                if (beanPropertyNodes != null) {
                    int beanPropertyLength = beanPropertyNodes.getLength();
                    for (int j = 0; j < beanPropertyLength; j++) {
                        Element beanPropertyElement = (Element) beanPropertyNodes.item(j);
                        BeanProperty beanProperty = new BeanProperty();
                        beanProperty.setName(beanPropertyElement.getAttribute("name"));
                        beanProperty.setRef(beanPropertyElement.getAttribute("ref"));
                        beanProperty.setValue(beanPropertyElement.getAttribute("value"));
                        bean.addProperty(beanProperty);
                    }
                }
            }

        } catch (Exception e) {
            // Do nothing.
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Do nothing.
                }
            }
        }

    }

}
