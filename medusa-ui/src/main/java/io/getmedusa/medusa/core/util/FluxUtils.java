package io.getmedusa.medusa.core.util;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FluxUtils {

    private FluxUtils() {}

    public static String dataBufferFluxToString(Flux<DataBuffer> flux) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            DataBufferUtils
                    .write(flux, byteArrayOutputStream)
                    .subscribe(DataBufferUtils.releaseConsumer());

            return byteArrayOutputStream.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
