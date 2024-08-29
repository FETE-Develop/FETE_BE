package fete.be.global.config;

import fete.be.BeApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = BeApplication.class)
public class FeignClientConfig {
}
