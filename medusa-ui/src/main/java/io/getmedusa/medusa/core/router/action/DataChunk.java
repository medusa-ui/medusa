package io.getmedusa.medusa.core.router.action;

/**
 * DataChunks are part of an upload streaming through. The idea is that you do not wish to keep an entire file in memory.
 * Use the @MaxFileSize annotation to specify a maximum size. If not specified, we default to 10MB
 */
public class DataChunk {

    private String fileId;
    private String fileName;
    private String mimeType;
    private byte[] chunk;
    private double completion;
    private long size;

    public static DataChunk from(FileUploadMeta chunkMetaData, FileUploadMeta originalMetadata) {
        DataChunk d = new DataChunk();
        d.chunk = chunkMetaData.getChunk();
        if(d.chunk == null) {
            d.chunk = new byte[0];
        }
        d.completion = chunkMetaData.getPercentage();
        d.fileId = originalMetadata.getFileId();
        d.fileName = originalMetadata.getFileName();
        d.mimeType = originalMetadata.getMimeType();
        d.size = originalMetadata.getSize();
        return d;
    }

    public long getSize() {
        return size;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public byte[] getChunk() {
        return chunk;
    }

    public void setChunk(byte[] chunk) {
        this.chunk = chunk;
    }

    public double getCompletion() {
        return completion;
    }

    public void setCompletion(double completion) {
        this.completion = completion;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isCompleted() {
        return getCompletion() == 100D;
    }
}
