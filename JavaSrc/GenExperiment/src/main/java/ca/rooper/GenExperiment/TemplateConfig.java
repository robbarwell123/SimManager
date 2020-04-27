package ca.rooper.GenExperiment;

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
        templateEngine.addTemplateResolver(output_file_resolver());
        return templateEngine;
    }

    @Bean
    public SpringResourceTemplateResolver output_file_resolver(){
        SpringResourceTemplateResolver myConverter = new SpringResourceTemplateResolver();
        myConverter.setPrefix("classpath:/templates/");
        myConverter.setSuffix(".txt");
        myConverter.setTemplateMode(TemplateMode.TEXT);
        myConverter.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return myConverter;
    }

}
