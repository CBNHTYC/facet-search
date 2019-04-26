package ru.kubsu.fs.configuration;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.dozer.DozerBeanMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import ru.kubsu.fs.repository.ElastDao;

@Configuration
@EntityScan("ru.kubsu.fs.*")
public class FcAppConfig {
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
        return restTemplate;
    }

    @Bean
    DozerBeanMapper dozerBeanMapper() { return new DozerBeanMapper(); }
}
