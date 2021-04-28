package com.demo.helloapi;

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean mapping = new ServletRegistrationBean();
        mapping.setName("CamelServlet");
        mapping.setLoadOnStartup(1);
        mapping.setServlet(new CamelHttpTransportServlet());
        mapping.addUrlMappings("/*");
        return mapping;
    }

    @Override
    public void run(String... args) throws Exception {
        ApplicationContext subApplicationContext = null;
        SpringCamelContext springCamelContext = null;
        // use *-context.xml to avoid load bean.xml(Since bean.xml doesn't contain
        // camel-context)
        Resource[] resources = applicationContext.getResources("classpath*:**fuse/**/*-context.xml");

        Set<String> set = new HashSet<>();
        for (Resource resource : resources) {
            set.add(resource.getFilename());
        }

        for (String filename : set) {
            subApplicationContext = new ClassPathXmlApplicationContext(
                    new String[] { "classpath*:**fuse/**/" + filename }, applicationContext);
            springCamelContext = subApplicationContext.getBean(SpringCamelContext.class);
            springCamelContext.start();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
