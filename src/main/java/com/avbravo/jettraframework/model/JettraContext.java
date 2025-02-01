/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.avbravo.jettraframework.model;

import com.sun.net.httpserver.HttpHandler;

/**
 *
 * @author avbravo
 */
public record JettraContext(String path, HttpHandler httpHandler) {
    
}
