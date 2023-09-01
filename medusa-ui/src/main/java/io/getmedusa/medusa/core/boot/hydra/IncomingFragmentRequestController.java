package io.getmedusa.medusa.core.boot.hydra;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.boot.Fragment;
import io.getmedusa.medusa.core.boot.RefDetection;
import io.getmedusa.medusa.core.boot.hydra.model.FragmentRequestWrapper;
import io.getmedusa.medusa.core.boot.hydra.model.meta.RenderedFragment;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.util.FluxUtils;
import io.getmedusa.medusa.core.util.FragmentUtils;
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

import java.util.*;

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

    //this is on the receiving side; see HydraConnectionController#askHydraForFragment for logic on the requester side
    @PostMapping("/h/fragment/{publicKey}/requestFragment")
    public Mono<List<RenderedFragment>> requestFragmentRender(@RequestBody FragmentRequestWrapper fragmentRequestWrapper,
                                                              @PathVariable String publicKey,
                                                              ServerHttpResponse response) {
        Flux<RenderedFragment> flux = Flux.empty();
        if(this.publicKey.equals(publicKey)) {
            for (final Fragment request : fragmentRequestWrapper.getRequests()) {
                final String rawHTML = RefDetection.INSTANCE.findRef(request.getRef());

                //not just the attributes from the origin, but also from the fragment's controller ...
                final Mono<List<Attribute>> localAttributes = RefDetection.INSTANCE.findBeanByRef(request.getRef()).setupAttributes(null, null);

                final Mono<Map<String, Object>> attributeMerge = localAttributes
                        .map(a -> {
                            final Map<String, Object> attributes = new HashMap<>();
                            for(Attribute localAttribute : a) {
                                attributes.put(localAttribute.name(), localAttribute.value());
                            }

                            for(String i : request.getImports()) {
                                String alias = i;
                                String key = i;
                                if(i.contains(" as ")) {
                                    String[] splitI = i.split(" as ");
                                    key = splitI[0];
                                    alias = splitI[1];
                                }
                                final Object o = fragmentRequestWrapper.getAttributes().getOrDefault(key, null);
                                if(o != null) {
                                    attributes.put(alias, o);
                                }
                            }

                            return attributes;
                        });

                final Flux<DataBuffer> bufferFlux = attributeMerge.flatMapMany(merged -> {
                    return renderer.renderFragment(rawHTML, merged, Locale.US); //TODO locale
                });

                flux = flux.concatWith(bufferFlux.map(dataBuffer -> {
                    final RenderedFragment renderedFragment = new RenderedFragment();
                    renderedFragment.setId(request.getId());
                    renderedFragment.setRenderedHTML(FragmentUtils.addFragmentRefToHTML(FluxUtils.dataBufferToString(dataBuffer), request.getRef()));
                    return renderedFragment;
                }));
            }
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND);
        }
        return flux.collectList();
    }

}
