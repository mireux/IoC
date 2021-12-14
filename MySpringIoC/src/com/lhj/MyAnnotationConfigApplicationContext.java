package com.lhj;

import com.lhj.Annotation.Autowired;
import com.lhj.Annotation.Component;
import com.lhj.Annotation.Qualifier;
import com.lhj.Annotation.Value;
import com.lhj.entity.User;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MyAnnotationConfigApplicationContext {

    /**
     * 1、自定义一个 com.lhj.MyAnnotationConfigApplicationContext，构造器中传入要扫描的包。
     * <p>
     * 2、获取这个包下的所有类。
     * <p>
     * 3、遍历这些类，找出添加了 @Component 注解的类，获取它的 Class 和对应的 beanName，封装成一个 com.lhj.BeanDefinition，存入集合 Set，这个机会就是 IoC 自动装载的原材料。
     * <p>
     * 4、遍历 Set 集合，通过反射机制创建对象，同时检测属性有没有添加 @Value 注解，如果有还需要给属性赋值，再将这些动态创建的对象以 k-v 的形式存入缓存区。
     * <p>
     * 5、提供 getBean 等方法，通过 beanName 取出对应的 bean 即可。
     */

    // 缓存
    private static Map<String,Object> ioc = new HashMap<>();
    private  static Set<BeanDefinition> beanDefinitions;

    public static void main(String[] args) {
        new MyAnnotationConfigApplicationContext("com.lhj");
        User user = (User) ioc.get("user");
        System.out.println(user.getAccount() + " " + user.getPassword() + " " + user.getUserInfo().getName() + " " + user.getUserInfo().getAge());
    }


    public MyAnnotationConfigApplicationContext(String pack) {
        //遍历包，找到目标类
        beanDefinitions = findBeanDefinitions(pack);
        // 获得目标类之后，通过反射机制创建对象
        createObject(beanDefinitions);
        // 通过注解@Autowried 自动注入到工厂类之中 自动装配机制
        autowireObject(beanDefinitions);
    }

    private void autowireObject(Set<BeanDefinition> beanDefinitions) {
        Iterator<BeanDefinition> iterator = beanDefinitions.iterator();
        while (iterator.hasNext()) {
            BeanDefinition beanDefinition = iterator.next();
            Class clazz = beanDefinition.getBeanClass();
            String beanName = beanDefinition.getBeanName();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                Autowired autowired = declaredField.getAnnotation(Autowired.class);
                if(autowired != null) {
                    Qualifier qualifier = declaredField.getAnnotation(Qualifier.class);
                    if(qualifier != null) {
                        // by name
                        // 获取qualifier对应的类名
                        try {
                            String qualifierName = qualifier.value();
                            // 获取对应的类型的类
                            Object object = ioc.get(qualifierName);
                            String fieldName = declaredField.getName();
                            String methodName = "set"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1); // 获取各字段的构造方法
                            Method method = clazz.getMethod(methodName, declaredField.getType());
                            method.invoke(ioc.get(beanName),object);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // by type

                    }
                }
            }
        }
    }

    private void createObject(Set<BeanDefinition> beanDefinitions) {
        // 获得目标类之后，通过反射机制创建对象
        Iterator<BeanDefinition> iterator = beanDefinitions.iterator();
        while (iterator.hasNext()) {
            BeanDefinition beanDefinition = iterator.next();
            Class Clazz = beanDefinition.getBeanClass();
            String beanName = beanDefinition.getBeanName();
            try {
                // 构造一个没有数据的对象
                Object object = Clazz.getConstructor().newInstance();
                // 放入对象
                Field[] declaredFields = Clazz.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    Value valueAnnotation = declaredField.getAnnotation(Value.class);
                    if (valueAnnotation != null) {
                        String value = valueAnnotation.value(); // 获取注解Value的数据
                        String fieldName = declaredField.getName(); // 获取字段的名称
                        String methodName = "set"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1); // 获取各字段的构造方法
                        Method method = Clazz.getMethod(methodName, declaredField.getType()); // 获取构造方法的参数类型
                        Object val = null;
                        switch (declaredField.getType().getName()) {
                            case "java.lang.Integer":
                                val = Integer.parseInt(value);
                                break;
                            case "java.lang.String":
                                val = value;
                                break;
                            case "java.lang.Float":
                                val = Float.parseFloat(value);
                                break;
                        }
                        method.invoke(object,val);
                    }
                }
                // 存入缓存
                ioc.put(beanName,object);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    private Set<BeanDefinition> findBeanDefinitions(String pack) {
        // 1. 获取包下的所有类
        Set<Class<?>> classes = MyTools.getClasses(pack); // 获取所有的class
        Iterator<Class<?>> iterator = classes.iterator();
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        // 2. 遍历这些类 找到拥有注解的类
        while (iterator.hasNext()) {
            // 3. 获取Component中的值
            Class<?> next = iterator.next();
            Component componentAnnotation = next.getAnnotation(Component.class);
            if (componentAnnotation != null) {
                String beanName = componentAnnotation.value();
                if ("".equals(beanName)) {
                    // 获取类名
                    String classname = next.getName().replaceAll(next.getPackage().getName() + ".", "");
//                    beanName = classname.substring(0, 1).toLowerCase()+classname.substring(1); //首字母小写
                    beanName = classname.toLowerCase(); //  全部小写
                }
                // 4. 将这些类封装成BeanDefinition，装载到集合中
                beanDefinitions.add(new BeanDefinition(beanName, next));
            }

        }

        return beanDefinitions;
    }
}
