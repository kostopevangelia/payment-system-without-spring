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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class RestConfig {

    private final CorrelationIdInterceptor correlationIdInterceptor;

    public RequestConfig createRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(5000))
                .setResponseTimeout(Timeout.ofMilliseconds(30000))
                .build();
    }

    public CloseableHttpClient createHttpClient(final RequestConfig requestConfig) {
        return HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public RestTemplate restTemplateFraudApi(final CloseableHttpClient httpClient) {

        CookieStore cookieStore = new BasicCookieStore();

        CloseableHttpClient customHttpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(createRequestConfig())
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(customHttpClient);
        factory.setConnectTimeout(30000);

        RestTemplateBuilder builder = new RestTemplateBuilder();

        RestTemplate restTemplate = builder.customizers(new RestLoggingCustomiser(
                                                        factory,
                                                        correlationIdInterceptor,
                                                        new RestInterceptor())
                                                ).build();

        log.info("Generic Spring's RestTemplate Initialized for fraud api");

        return restTemplate;
    }

    public RestConfig(CorrelationIdInterceptor correlationIdInterceptor) {
        this.correlationIdInterceptor = correlationIdInterceptor;
    }
}
