package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.bidirectional.ServerToClient;
import io.getmedusa.medusa.core.router.action.DataChunk;
import io.getmedusa.medusa.core.router.action.FileUploadMeta;
import io.getmedusa.medusa.core.router.action.UploadableUI;
import io.getmedusa.medusa.core.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import static io.getmedusa.medusa.core.attributes.Attribute.$$;

@UIEventPage(path = "/uploads", file = "/pages/uploads")
public class UploadsController implements UploadableUI {
    private static final Logger logger = LoggerFactory.getLogger(UploadsController.class);
    private final ServerToClient serverToClient;

    public UploadsController(ServerToClient serverToClient) {
        this.serverToClient = serverToClient;
    }
    public List<Attribute> setupAttributes() {
        return $$("progress", new HashMap<String, Double>(), "images" , new ArrayList<SessionImage>());
    }

    @Override
    public void onError(FileUploadMeta uploadMeta, Session session) {
        String message = uploadMeta.getFileName() + " [" + uploadMeta.getSize() + "] is to big, message:" + uploadMeta.getMessage();
        serverToClient.sendAttributesToSession($$("error", message), session);
        // clear error after new action
        // maybe there should be a serverToClient.sendAttributesToSessionOnce(....)
    }

    @Override
    public void uploadChunk(DataChunk dataChunk, Session session) {

        String fileName = dataChunk.getFileName();
        List<SessionImage> images = session.getAttribute("images");
        SessionImage image = images.stream().filter(i -> i.name.equals(fileName)).findFirst().orElse(null);
        if (image == null) {
            image = new SessionImage(fileName, dataChunk.getMimeType());
            images.add(image);
        }
        image.writeBytes(dataChunk.getChunk());
        image.setCompletion(dataChunk.getCompletion());
        if (image.hasProgress(10) || image.isCompleted()) {
            logger.debug("partial file: {}, progress: {}, completion: {}", fileName, image.progress, image.completion);
            serverToClient.sendAttributesToSession($$("images", images), session);
        }
    }
}

class SessionImage {
    String base64PrefixFormat = "data:%s;base64, ";
    String name;
    String mimeType;
    ByteArrayOutputStream data;
    int progress;
    double completion;

    public SessionImage(String name, String mimeType) {
        this.name = name;
        this.mimeType = mimeType;
        this.data = new ByteArrayOutputStream();
    }

    public boolean hasProgress(int step){
        if(completion > (progress + step) || completion >= 100){
            progress = (int) completion;
            return true;
        }
        return false;
    }

    public void writeBytes(byte[] bytes) {
        data.writeBytes(bytes);
    }

    public String base64ImageString() {
        String imageString = Base64.getEncoder().encodeToString(data.toByteArray());
        return base64PrefixFormat.formatted(mimeType) + imageString;
    }

    public String getName() {
        return name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = (int) progress;
    }

    public void setCompletion(double completion) {
        this.completion = completion;
    }

    public double getCompletion() {
        return completion;
    }

    public boolean isCompleted() {
        return completion >= 100;
    }
}
