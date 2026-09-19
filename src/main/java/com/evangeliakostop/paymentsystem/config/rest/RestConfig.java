package com.evangeliakostop.paymentsystem.config.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class RestConfig {

    private final CorrelationIdInterceptor correlationIdInterceptor;

    @Autowired
    public RestConfig(CorrelationIdInterceptor correlationIdInterceptor) {
        this.correlationIdInterceptor = correlationIdInterceptor;
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    @Bean
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(5000))
                .setResponseTimeout(Timeout.ofMilliseconds(30000))
                .build();
    }

    @Bean
    public CloseableHttpClient httpClient(final RequestConfig requestConfig) {
        return HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Bean
    @Qualifier("restTemplateStripe")
    public RestTemplate restTemplateStripe(final RestTemplateBuilder builder, final CloseableHttpClient httpClient) {

        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        factory.setConnectTimeout(30000);

        final RestTemplate restTemplate = builder.customizers(new RestLoggingCustomiser(factory, correlationIdInterceptor, new RestInterceptor())).
                build();
        log.info("Generic Spring's RestTemplate Initialized");

        return restTemplate;
    }

    @Bean
    @Qualifier("restTemplateFraudApi")
    public RestTemplate restTemplateFraudApi(RestTemplateBuilder builder, final CloseableHttpClient httpClient) {
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient customHttpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(requestConfig())
                .build();

        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(customHttpClient);
        factory.setConnectTimeout(30000);

        final RestTemplate restTemplate = builder.customizers(new RestLoggingCustomiser(factory, correlationIdInterceptor, new RestInterceptor()))
                .build();

        log.info("Generic Spring's RestTemplate Initialized for fraud api");

        return restTemplate;
    }
}
