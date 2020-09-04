package com.weird.runner;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.web.WebApplicationInitializer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JettyRunner {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        //set the context path
        WebAppContext webAppContext = new WebAppContext();

        webAppContext.setConfigurations(new Configuration[] {
                new AnnotationConfiguration() {
                    @Override
                    public void preConfigure(WebAppContext context) throws Exception {
                        ClassInheritanceMap map = new ClassInheritanceMap();
                        Set<String> hashSet = ConcurrentHashMap.newKeySet();
                        hashSet.add(Initializer.class.getName());
                        map.put(WebApplicationInitializer.class.getName(), hashSet);
                        context.setAttribute(CLASS_INHERITANCE_MAP, map);
                        _classInheritanceHandler = new ClassInheritanceHandler(map);
                    }
                }
        });
        server.setHandler(webAppContext);

        //start the server and make it available when initialization is complete
        server.start();
        server.join();
    }
}
