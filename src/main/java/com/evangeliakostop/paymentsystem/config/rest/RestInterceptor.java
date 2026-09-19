package com.evangeliakostop.paymentsystem.config.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MimeType;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
public class RestInterceptor implements ClientHttpRequestInterceptor {

    private static Charset getCharset(final HttpMessage message) {
        return Optional.ofNullable(message)
                .map(HttpMessage::getHeaders)
                .map(HttpHeaders::getContentType)
                .map(MimeType::getCharset)
                .orElse(Charset.defaultCharset());
    }


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Charset charset = getCharset(request);

        log.info("Request: {} {} {}", request.getMethod(), request.getURI(), new String(body, getCharset(request)));

        ClientHttpResponse response = execution.execute(request, body);
        stopWatch.stop();

        byte[] responseBytes = response.getBody() != null ? response.getBody().readAllBytes() : new byte[0];
        CachedResponse cached = new CachedResponse(response, responseBytes);


        String respBody = new String(responseBytes, getCharset(cached));

        log.info("{} {} {} {} {}", new Timestamp(System.currentTimeMillis()), "OUTB_CALL",
                request.getURI(),
                response.getStatusCode().value(),
                stopWatch.getLastTaskTimeMillis());

        log.info("Response: {} {} {}", request.getMethod(), request.getURI(), respBody);

        return response;
    }
}
