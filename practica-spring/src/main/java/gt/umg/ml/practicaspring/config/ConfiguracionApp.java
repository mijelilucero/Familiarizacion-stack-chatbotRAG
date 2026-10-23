package gt.umg.ml.practicaspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class ConfiguracionApp {

    @Bean
    public DateTimeFormatter formatoHora() {
        System.out.println(">>> Creando el bean DateTimeFormatter");
        return DateTimeFormatter.ofPattern("HH:mm:ss");
    }
}
