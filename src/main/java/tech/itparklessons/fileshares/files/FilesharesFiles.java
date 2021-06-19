package tech.itparklessons.fileshares.files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FilesharesFiles {
    public static void main(String[] args) {
        SpringApplication.run(FilesharesFiles.class, args);
    }
}