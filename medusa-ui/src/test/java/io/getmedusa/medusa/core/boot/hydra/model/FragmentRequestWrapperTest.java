package io.getmedusa.medusa.core.boot.hydra.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.boot.Fragment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

class FragmentRequestWrapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSerializable() throws Exception {
        FragmentRequestWrapper wrapper = new FragmentRequestWrapper();
        Fragment request = new Fragment();
        request.setService("xyz");
        request.setRef("234");
        request.setImports(List.of("XYZ as TRW"));
        wrapper.setRequests(List.of(request));
        wrapper.setAttributes(new HashMap<>());

        String value = objectMapper.writeValueAsString(wrapper);
        Assertions.assertNotNull(value);
        System.out.println(value);

        FragmentRequestWrapper wrapper1 = objectMapper.readValue(value, FragmentRequestWrapper.class);
        Assertions.assertNotNull(wrapper1);
        System.out.println(wrapper1);
    }

}