package tn.esprit.microserviceplanification.Configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Properties;

@Configuration //  Add this to ensure Spring Boot loads it!
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {


                registry.addMapping("/**") // Apply to all endpoints
                        .allowedOrigins("http://localhost:4200", "http://192.168.1.13:4200")
                        .allowedMethods(
                                HttpMethod.GET.name(),
                                HttpMethod.POST.name(),
                                HttpMethod.PUT.name(),
                                HttpMethod.DELETE.name(),
                                HttpMethod.OPTIONS.name() // ✅ Needed for preflight!
                        )
                        .allowedHeaders("*") // ✅ Allow all headers
                        .allowCredentials(true); // ✅ Allow cookies/auth headers
            }
            @Override

            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Expose the images directory with caching disabled (cache period 0)
                registry.addResourceHandler("/images/**")
                        .addResourceLocations("file:C:/Users/Lenovo/Desktop/Equipements/microservices/equip/src/main/resources/static/images")
                        .setCachePeriod(0);
            }};
    }



    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("olfamaddeh@gmail.com");
        mailSender.setPassword("dlglxrxhhzasabgc");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}
