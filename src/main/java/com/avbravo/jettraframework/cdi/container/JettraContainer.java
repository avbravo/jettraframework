/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.avbravo.jettraframework.cdi.container;

/**
 *
 * @author avbravo
 */
import com.avbravo.jettraframework.cdi.Inject;
import com.avbravo.jettraframework.cdi.Prototype;
import com.avbravo.jettraframework.cdi.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JettraContainer {
    private final Map<Class<?>, Object> singletonBeans = new HashMap<>();
    private final Map<Class<?>, Class<?>> beanDefinitions = new HashMap<>();

    public void scanAndRegister(String packageName) {
        List<Class<?>> classes = JettraClassScanner.scanPackage(packageName);

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Singleton.class) || clazz.isAnnotationPresent(Prototype.class)) {
                registerBean(clazz);
            }
        }
    }

    public void registerBean(Class<?> beanClass) {
        if (beanClass.isInterface()) {
            throw new IllegalArgumentException("Cannot register an interface directly: " + beanClass.getName());
        }
        beanDefinitions.put(beanClass, beanClass);

        // Si la clase implementa una interfaz, registramos la relación
        for (Class<?> iface : beanClass.getInterfaces()) {
            beanDefinitions.put(iface, beanClass);
        }
    }

    public <T> T getBean(Class<T> beanClass) {
        Class<?> implementationClass = beanDefinitions.get(beanClass);
        if (implementationClass == null) {
            throw new RuntimeException("No implementation found for: " + beanClass.getName());
        }

        if (implementationClass.isAnnotationPresent(Singleton.class)) {
            return getSingletonBean(implementationClass);
        } else {
            return createNewInstance(implementationClass);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getSingletonBean(Class<?> beanClass) {
        return (T) singletonBeans.computeIfAbsent(beanClass, this::createNewInstance);
    }

    @SuppressWarnings("unchecked")
    private <T> T createNewInstance(Class<?> beanClass) {
        try {
            T instance = (T) beanClass.getDeclaredConstructor().newInstance();
            injectDependencies(instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean: " + beanClass.getName(), e);
        }
    }

    private <T> void injectDependencies(T instance) {
        for (var field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                Object dependency = getBean(fieldType);
                try {
                    field.set(instance, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject dependency: " + field.getName(), e);
                }
            }
        }
    }
}