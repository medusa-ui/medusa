package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.MaxFileSize;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.bidirectional.ServerToClient;
import io.getmedusa.medusa.core.router.action.DataChunk;
import io.getmedusa.medusa.core.router.action.FileUploadMeta;
import io.getmedusa.medusa.core.router.action.UploadableUI;
import io.getmedusa.medusa.core.session.Session;
import jakarta.validation.Valid;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

import static io.getmedusa.medusa.core.attributes.Attribute.$$;

@UIEventPage(path = "/upload", file = "/pages/upload")
public class UploadController implements UploadableUI {

    private final ServerToClient serverToClient;

    public UploadController(ServerToClient serverToClient) {
        this.serverToClient = serverToClient;
    }

    public List<Attribute> setupAttributes(ServerRequest serverRequest, Session session) {
        return $$("percentage", 0);
    }

    @Override
    public void uploadChunk(@Valid @MaxFileSize("${files.max-size:11MB}") DataChunk dataChunk, Session session) {
        serverToClient.sendUploadCompletionPercentage("percentage", dataChunk, session);
    }

    @Override
    public void onCancel(FileUploadMeta uploadMeta, Session session) {
        System.out.println("Upload cancelled: " + uploadMeta.getFileId());
    }
}
