package io.getmedusa.medusa.core.util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpCookie;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TestRequest implements ServerRequest {

    public TestRequest() {}

    @Override
    public String methodName() {
        return null;
    }

    @Override
    public URI uri() {
        return null;
    }

    @Override
    public UriBuilder uriBuilder() {
        return null;
    }

    @Override
    public Headers headers() {
        return null;
    }

    @Override
    public MultiValueMap<String, HttpCookie> cookies() {
        return null;
    }

    @Override
    public Optional<InetSocketAddress> remoteAddress() {
        return Optional.empty();
    }

    @Override
    public Optional<InetSocketAddress> localAddress() {
        return Optional.empty();
    }

    @Override
    public List<HttpMessageReader<?>> messageReaders() {
        return null;
    }

    @Override
    public <T> T body(BodyExtractor<T, ? super ServerHttpRequest> extractor) {
        return null;
    }

    @Override
    public <T> T body(BodyExtractor<T, ? super ServerHttpRequest> extractor, Map<String, Object> hints) {
        return null;
    }

    @Override
    public <T> Mono<T> bodyToMono(Class<? extends T> elementClass) {
        return null;
    }

    @Override
    public <T> Mono<T> bodyToMono(ParameterizedTypeReference<T> typeReference) {
        return null;
    }

    @Override
    public <T> Flux<T> bodyToFlux(Class<? extends T> elementClass) {
        return null;
    }

    @Override
    public <T> Flux<T> bodyToFlux(ParameterizedTypeReference<T> typeReference) {
        return null;
    }

    @Override
    public Map<String, Object> attributes() {
        return null;
    }

    @Override
    public MultiValueMap<String, String> queryParams() {
        return null;
    }

    @Override
    public Map<String, String> pathVariables() {
        return new HashMap<>();
    }

    @Override
    public Mono<WebSession> session() {
        return null;
    }

    @Override
    public Mono<? extends Principal> principal() {
        return null;
    }

    @Override
    public Mono<MultiValueMap<String, String>> formData() {
        return null;
    }

    @Override
    public Mono<MultiValueMap<String, Part>> multipartData() {
        return null;
    }

    @Override
    public ServerWebExchange exchange() {
        return MockServerWebExchange.builder(MockServerHttpRequest.get("/test").build()).build();
    }
}
