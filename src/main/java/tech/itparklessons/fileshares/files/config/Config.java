package tech.itparklessons.fileshares.files.config;

import feign.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public Client feignClient() {
        return new Client.Default(null, null);
    }
}