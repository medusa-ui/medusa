package io.getmedusa.medusa.core.boot.hydra;

import io.getmedusa.medusa.core.boot.Fragment;
import io.getmedusa.medusa.core.boot.RefDetection;
import io.getmedusa.medusa.core.boot.hydra.model.FragmentRequestWrapper;
import io.getmedusa.medusa.core.boot.hydra.model.meta.RenderedFragment;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.util.FluxUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@ConditionalOnProperty(name = "medusa.hydra.uri")
public class IncomingFragmentRequestController {

    private final Renderer renderer;
    private final String publicKey;
    private final String privateKey;

    public IncomingFragmentRequestController(@Value("${medusa.hydra.secret.public-key}") String publicKey,
                                             @Value("${medusa.hydra.secret.private-key}") String privateKey,
                                             Renderer renderer) {
        this.renderer = renderer;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @PostMapping("/h/fragment/{publicKey}/requestFragment")
    public Mono<List<RenderedFragment>> requestFragmentRender(@RequestBody FragmentRequestWrapper fragmentRequestWrapper,
                                                              @PathVariable String publicKey,
                                                              ServerHttpResponse response) {
        Flux<RenderedFragment> flux = Flux.empty();
        if(this.publicKey.equals(publicKey)) {
            for (Fragment request : fragmentRequestWrapper.getRequests()) {
                final String rawHTML = RefDetection.INSTANCE.findRef(request.getRef());
                final Flux<DataBuffer> bufferFlux = renderer.renderFragment(rawHTML, fragmentRequestWrapper.getAttributes());
                flux = flux.concatWith(bufferFlux.map(dataBuffer -> {
                    final RenderedFragment renderedFragment = new RenderedFragment();
                    renderedFragment.setId(request.getId());
                    renderedFragment.setRenderedHTML(FluxUtils.dataBufferToString(dataBuffer));
                    return renderedFragment;
                }));
            }
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND);
        }
        return flux.collectList();
    }

}
