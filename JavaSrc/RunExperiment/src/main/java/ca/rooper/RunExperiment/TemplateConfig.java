package ca.rooper.RunExperiment;

import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
public class TemplateConfig
{
    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(simTemplateConverter());
        return templateEngine;
    }

    @Bean
    public SpringResourceTemplateResolver simTemplateConverter(){
        SpringResourceTemplateResolver myConverter = new SpringResourceTemplateResolver();
        myConverter.setPrefix("classpath:/templates/");
        myConverter.setSuffix(".txt");
        myConverter.setTemplateMode(TemplateMode.TEXT);
        myConverter.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return myConverter;
    }
}
