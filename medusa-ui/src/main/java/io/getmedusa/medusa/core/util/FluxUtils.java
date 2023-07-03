package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.attributes.Attribute;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class FluxUtils {

    private FluxUtils() {}

    private static final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();

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

    public static DataBuffer stringToDataBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = bufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }

    @SafeVarargs
    public static Mono<List<Attribute>> join(CorePublisher<List<Attribute>> ... publishers) {
        return Flux.concat(publishers).collectList().map(FluxUtils::mergeAttributes);
    }

    private static List<Attribute> mergeAttributes(List<List<Attribute>> multiList) {
        List<Attribute> finalList = new ArrayList<>();
        multiList.parallelStream().forEach(finalList::addAll);
        return finalList;
    }
}
