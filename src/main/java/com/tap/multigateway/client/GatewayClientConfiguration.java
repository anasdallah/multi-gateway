package com.tap.multigateway.client;

import com.tap.multigateway.constant.ApiErrors;
import com.tap.multigateway.exception.TapException;
import feign.Logger;
import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class GatewayClientConfiguration {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }


    @Bean
    public Request.Options options() {
        return new Request.Options(60, TimeUnit.SECONDS, 30, TimeUnit.SECONDS, true);
    }

   

    @Bean
    public ErrorDecoder errorDecoder() {
        return new GatewayErrorDecoder();
    }


    @Bean
    public HttpMessageConverters feignHttpMessageConverter() {
        final HttpMessageConverter<?> converter = new MappingJackson2HttpMessageConverter();
        return new HttpMessageConverters(converter);
    }

    static class GatewayErrorDecoder implements ErrorDecoder {

        private final ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            log.error("Res Error:" + response);

            if (response.status() >= 400 && response.status() <= 499) {
                return TapException.badRequest(ApiErrors.THIRD_PARTY);
            }
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }


}