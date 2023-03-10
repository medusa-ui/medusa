package io.getmedusa.medusa.core.router.action;

import io.getmedusa.medusa.core.session.Session;

public interface UploadableUI {

    void uploadChunk(DataChunk dataChunk, Session session);

    default void onError(FileUploadMeta uploadMeta, Session session){}

    default void onCancel(FileUploadMeta uploadMeta, Session session){}
}
