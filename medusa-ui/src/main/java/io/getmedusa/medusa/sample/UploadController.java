package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.List;

@UIEventPage(path = "/upload", file = "/pages/upload")
public class UploadController {

    public List<Attribute> setupAttributes(ServerRequest serverRequest, Session session) {
        return new ArrayList<>();
    }

    public List<Attribute> uploadFileMethod(byte[] byteBuffer) {
        System.out.println(byteBuffer.length);

        return new ArrayList<>();
    }

}
