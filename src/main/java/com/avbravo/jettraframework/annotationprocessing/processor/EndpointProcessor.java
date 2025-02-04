/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.avbravo.jettraframework.annotationprocessing.processor;

/**
 *
 * @author avbravo
 */
import com.avbravo.jettraframework.annotationprocessing.EndpointController;
import com.avbravo.jettraframework.annotationprocessing.GetEndpoint;
import com.google.auto.service.AutoService;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import javax.annotation.processing.Processor;
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.avbravo.jettraframework.annotationprocessing.EndpointController")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class EndpointProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(EndpointController.class)) {
            if (annotatedElement instanceof TypeElement) {
                TypeElement controller = (TypeElement) annotatedElement;
                String className = controller.getSimpleName().toString();
                String packageName = processingEnv.getElementUtils().getPackageOf(controller).getQualifiedName().toString();
                EndpointController endpointController = controller.getAnnotation(EndpointController.class);

                try {
                    generateHandlerCode(packageName, className, endpointController.basePath(), controller);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private void generateHandlerCode(String packageName, String className, String basePath, TypeElement controller) throws IOException {
        StringBuilder code = new StringBuilder();
        code.append("package ").append(packageName).append(";\n\n");
        code.append("import com.sun.net.httpserver.HttpHandler;\n");
        code.append("import com.sun.net.httpserver.HttpExchange;\n");
        code.append("import java.io.OutputStream;\n");
        code.append("import java.io.IOException;\n\n");

        code.append("public class ").append(className).append("Handler {\n");

        for (Element enclosedElement : controller.getEnclosedElements()) {
            if (enclosedElement instanceof ExecutableElement) {
                ExecutableElement method = (ExecutableElement) enclosedElement;
                GetEndpoint getEndpoint = method.getAnnotation(GetEndpoint.class);
                if (getEndpoint != null) {
                    String path = getEndpoint.path();
                    String methodName = method.getSimpleName().toString();

                    code.append("    public static class ").append(methodName).append("Handler implements HttpHandler {\n");
                    code.append("        @Override\n");
                    code.append("        public void handle(HttpExchange exchange) throws IOException {\n");
                    code.append("            if (\"GET\".equals(exchange.getRequestMethod())) {\n");
                    code.append("                String response = \"Respuesta desde ").append(basePath).append(path).append("\";\n");
                    code.append("                exchange.sendResponseHeaders(200, response.getBytes().length);\n");
                    code.append("                OutputStream os = exchange.getResponseBody();\n");
                    code.append("                os.write(response.getBytes());\n");
                    code.append("                os.close();\n");
                    code.append("            } else {\n");
                    code.append("                exchange.sendResponseHeaders(405, -1);\n");
                    code.append("            }\n");
                    code.append("        }\n");
                    code.append("    }\n\n");
                }
            }
        }

        code.append("}\n");

        // Escribir el archivo generado
        JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + "." + className + "Handler");
        try (PrintWriter out = new PrintWriter(file.openWriter())) {
            out.print(code.toString());
        }
    }
}
