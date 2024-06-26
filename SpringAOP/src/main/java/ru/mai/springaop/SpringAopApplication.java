package ru.mai.springaop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringAopApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringAopApplication.class, args);
        CacheService cache = context.getBean(CacheService.class);
        cache.put("13");
        System.out.println(cache.get("13"));
        cache.remove("13");
        System.out.println(cache.get("13"));
    }

}
