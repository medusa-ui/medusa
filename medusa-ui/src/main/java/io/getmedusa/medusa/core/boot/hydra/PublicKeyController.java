package io.getmedusa.medusa.core.boot.hydra;

import io.getmedusa.medusa.core.security.JWTTokenInterpreter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class PublicKeyController {

    //TODO login via private key as basic auth

    private final String publicKey;
    private final String privateKey;

    public PublicKeyController(@Value("${medusa.hydra.secret.public-key}") String publicKey,
                               @Value("${medusa.hydra.secret.private-key}") String privateKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @PostMapping("/h/public-key-update/{publicKey}")
    public Mono<Boolean> updateHydraPublicKey(@RequestBody Map<String, String> reqMap,
                                                              @PathVariable String publicKey,
                                                              ServerHttpResponse response) throws Exception {
        if(this.publicKey.equals(publicKey)) {
            JWTTokenInterpreter.handleUpdateToPublicKey(reqMap.get("k"));
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND);
        }
        return Mono.just(true);
    }

}
