package de.outlookklon.config;

import de.outlookklon.serializers.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Services {

    @Bean
    public Serializer getSerializer() {
        return new Serializer();
    }
}
