package sample.getmedusa.showcase.samples.input.special;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.bidirectional.ServerToClient;
import io.getmedusa.medusa.core.router.action.DataChunk;
import io.getmedusa.medusa.core.router.action.UploadableUI;
import io.getmedusa.medusa.core.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

import static io.getmedusa.medusa.core.attributes.Attribute.$;
import static io.getmedusa.medusa.core.attributes.Attribute.$$;

@UIEventPage(path = "/detail/sample/uploads", file = "/pages/sample/uploads")
public class UploadsController implements UploadableUI {
    
    private static final Logger logger = LoggerFactory.getLogger(UploadsController.class);
    final ServerToClient serverToClient;

    public UploadsController(ServerToClient serverToClient) {
        this.serverToClient = serverToClient;
    }

    public List<Attribute> setupAttributes() {
        return $$("progress", 0 );
    }

    @Override
    public void uploadChunk(DataChunk dataChunk, Session session) {
        String fileName = dataChunk.getFileName();
        SessionImage image = session.getAttribute("image");
        if(image == null) {
            image = new SessionImage(fileName, dataChunk.getMimeType());
            session.getLastParameters().add($("image", image));
        }
        image.writeBytes(dataChunk.getChunk());
        image.setCompletion(dataChunk.getCompletion());
        if (image.hasProgress(10) || image.isCompleted()) {
            logger.debug("partial file: {}, progress: {}, completion: {}", fileName, image.progress, image.completion);
            serverToClient.sendAttributesToSession($$("image", image), session);
        }
    }


    public static class SessionImage {
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
//            logger.debug("writing " + bytes.length + " to data for " + name);
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

}
