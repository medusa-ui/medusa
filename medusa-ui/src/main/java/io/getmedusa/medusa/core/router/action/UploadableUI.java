package io.getmedusa.medusa.core.router.action;

import io.getmedusa.medusa.core.session.Session;

public interface UploadableUI {

    void uploadChunk(DataChunk dataChunk, Session session);

}
