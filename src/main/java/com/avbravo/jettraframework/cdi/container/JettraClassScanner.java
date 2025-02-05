/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.avbravo.jettraframework.cdi.container;

/**
 *
 * @author avbravo
 */
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JettraClassScanner {

    public static List<Class<?>> scanPackage(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());

                if (directory.exists()) {
                    findClassesInDirectory(directory, packageName, classes);
                } else {
//                    System.out.println("JAR files are not supported in this example.");
                     String jarFilePath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                    findClassesInJar(jarFilePath, packagePath, packageName, classes);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan package: " + packageName, e);
        }

        return classes;
    }

    private static void findClassesInDirectory(File directory, String packageName, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                findClassesInDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Failed to load class: " + className, e);
                }
            }
        }
    }
      private static void findClassesInJar(String jarFilePath, String packagePath, String packageName, List<Class<?>> classes) {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                    String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                    try {
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Failed to load class: " + className, e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JAR file: " + jarFilePath, e);
        }
    }
}