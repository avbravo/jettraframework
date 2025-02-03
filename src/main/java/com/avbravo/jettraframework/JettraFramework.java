/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.avbravo.jettraframework;

import com.avbravo.jettraframework.enumerations.Protocol;
import com.avbravo.jettraframework.model.JettraContext;
import com.avbravo.jettraframework.utils.JettraLogo;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author avbravo
 */
public class JettraFramework {

    /**
     * Runs this example.
     *
     * @param args configuration to be used in exact this order:
     * {@code PROTOCOL HOST PORT ROOT_PATH CLIENT_AUTH} where the protocol can
     * be either { HTTP} or {HTTPS} and the client authentication is one of
     */
    private Protocol protocol = Protocol.HTTP;
    private Integer port;
    private Integer backlog;
    private Boolean logo;
    private List<JettraContext> jettraContext;

    public JettraFramework() {
    }

    public JettraFramework(Integer port, Protocol protocol, Integer backlog, Boolean logo, List<JettraContext> jettraContext) {
        this.port = port;
        this.protocol = protocol;
        this.backlog = backlog;
        this.logo = logo;
        this.jettraContext = jettraContext;
    }

    public static class Builder {

        private Protocol protocol = Protocol.HTTP;
        private Integer port;
        private Integer backlog;
        private Boolean logo;
        List<JettraContext> jettraContext;

        public Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder jettraContext(List<JettraContext> jettraContext) {
            this.jettraContext = jettraContext;
            return this;
        }

        public Builder port(Integer port) {
            this.port = port;
            return this;
        }
        public Builder logo(Boolean  logo) {
            this.logo = logo;
            return this;
        }

        public Builder backlog(Integer backlog) {
            this.backlog = backlog;
            return this;
        }

        public JettraFramework start() {
            try {
                long start = System.currentTimeMillis();

                System.out.println("\n___________________________________________________________________________");
                System.out.println("                          JettraFramework configuration....");
                 if (port == null || port.equals("")) {
                     System.out.println("\n Port no ha sido especificado..");
                     return new JettraFramework();
                }
                if (backlog == null) {
                    backlog = 0;
                }
                System.out.println("\n___________________________________________________________________________");
                System.out.println("                          JettraServer prepare to starting....");
                // Crear un servidor HTTP en el puerto 8080
                HttpServer server = HttpServer.create(new InetSocketAddress(port), backlog);
                jettraContext.forEach(jc -> {
                    server.createContext(jc.path(), jc.httpHandler());
                });

                // Iniciar el servidor
               // server.setExecutor(null); // Usa el executor por defecto
                 // Configurar executor con virtual threads
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
                server.start();
                long finish = System.currentTimeMillis();
                long timeElapsed = finish - start;
                JettraLogo.blurVision();
             
                System.out.println("\n\n");

                System.out.println("\tServer started in WITH TEST: " + timeElapsed + "ms");
                System.out.println("Servidor iniciado en http://localhost:"+port);
            } catch (Exception ex) {
                Logger.getLogger(JettraFramework.class.getName()).log(Level.SEVERE, null, ex);
            }
          return new JettraFramework(port, protocol, backlog, logo, jettraContext);
        }

    }

   
}
