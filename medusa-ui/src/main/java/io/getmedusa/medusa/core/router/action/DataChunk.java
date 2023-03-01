package io.getmedusa.medusa.core.router.action;

public class DataChunk {

    private String fileId;
    private String fileName;
    private String mimeType;
    private byte[] chunk;
    private double completion;

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
        return d;
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
}
