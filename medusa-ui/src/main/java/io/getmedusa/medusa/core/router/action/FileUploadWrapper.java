package io.getmedusa.medusa.core.router.action;

import java.nio.ByteBuffer;

public class FileUploadWrapper {

    private byte[] byteArray = new byte[0];
    private final String method;
    private final String fileName;
    private final String mimeType;
    private final long size;
    private final String fileId;
    private boolean completed = false;

    public FileUploadWrapper(FileUploadMeta fileMeta) {
        this.method = fileMeta.getMethod();
        this.fileName = fileMeta.getFileName();
        this.mimeType = fileMeta.getMimeType();
        this.size = fileMeta.getSize();
        this.fileId = fileMeta.getFileId();
    }

    void add(byte[] bytes) {
        byte[] allByteArray = new byte[byteArray.length + bytes.length];

        ByteBuffer buff = ByteBuffer.wrap(allByteArray);
        buff.put(byteArray);
        buff.put(bytes);

        byteArray = buff.array();
    }

    public byte[] getByteBuffer() {
        return byteArray;
    }

    public String getMethod() {
        return method;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getSize() {
        return size;
    }

    public String getFileId() {
        return fileId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
