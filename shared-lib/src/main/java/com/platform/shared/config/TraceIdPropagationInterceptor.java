package com.platform.shared.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Register on your RestTemplate bean:
 *   restTemplate.getInterceptors().add(new TraceIdPropagationInterceptor());
 *
 * For WebClient, use an ExchangeFilterFunction equivalent instead:
 *   ExchangeFilterFunction.ofRequestProcessor(req ->
 *       Mono.just(ClientRequest.from(req)
 *           .header(TraceIdFilter.TRACE_ID_HEADER, TraceIdProvider.getTraceId())
 *           .build()));
 *
 * This is what makes the SAME traceId show up in logs across every service the
 * request touches, instead of every hop minting its own (TraceIdFilter already
 * reuses an inbound X-Trace-Id header if present - this is the other half).
 */
public class TraceIdPropagationInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        request.getHeaders().set(TraceIdFilter.TRACE_ID_HEADER, TraceIdProvider.getTraceId());
        return execution.execute(request, body);
    }
}