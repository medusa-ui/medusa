package io.getmedusa.medusa.core.util;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class FluxUtils {

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

    public static String dataBufferToString(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
