package com.evangeliakostop.paymentsystem.config.rest;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

public class RestLoggingCustomiser implements RestTemplateCustomizer {

    private final ClientHttpRequestFactory requestFactory;
    private final ClientHttpRequestInterceptor log;
    private final RestInterceptor logInterceptor;

    public RestLoggingCustomiser(ClientHttpRequestFactory requestFactory, ClientHttpRequestInterceptor log, RestInterceptor logInterceptor) {
        super();
        this.requestFactory = requestFactory;
        this.log = log;
        this.logInterceptor = logInterceptor;
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(requestFactory));
        restTemplate.getInterceptors().add(logInterceptor);
    }
}
