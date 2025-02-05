/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.avbravo.jettraframework.cdi.container;

/**
 *
 * @author avbravo
 */
public class JettraConfigApplication {
     private static final JettraContainer container = new JettraContainer();

    public static void initialize(String packageName) {
        container.scanAndRegister(packageName);
    }

    public static <T> T getBean(Class<T> beanClass) {
        return container.getBean(beanClass);
    }
}
