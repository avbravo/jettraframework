/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.avbravo.jettraframework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author avbravo
 */
public interface JettraConfig {

    /**
     * Obtiene propiedad del archivo MicroprofileConfig
     *
     * @param propertie
     * @return
     */
    default String getMicroprofileConfig(String propertie) {
        var result = "";
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("META-INF/microprofile-config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return result;
            }
            prop.load(input);
            result = prop.getProperty(propertie);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
