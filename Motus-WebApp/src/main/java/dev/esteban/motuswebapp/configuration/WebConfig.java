package dev.esteban.motuswebapp.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationVerify authenticationVerify;

    public WebConfig(AuthenticationVerify authenticationVerify) {
        this.authenticationVerify = authenticationVerify;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationVerify)
                .excludePathPatterns("/","/login", "/register", "/css/**", "/js/**", "/images/**")
                .addPathPatterns("/game/**");
    }
}