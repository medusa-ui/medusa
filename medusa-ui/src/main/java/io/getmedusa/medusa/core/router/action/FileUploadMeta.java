package io.getmedusa.medusa.core.router.action;

public class FileUploadMeta {

    private String sAct;
    private String fileName;
    private String mimeType;
    private long size;
    private String fileId;
    private byte[] chunk;
    private double percentage;
    private String message;
    private String fragment;

    public String getsAct() {
        return sAct;
    }

    public void setsAct(String sAct) {
        this.sAct = sAct;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
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

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    @Override
    public String toString() {
        return "FileUploadMeta{" +
                "sAct='" + sAct + '\'' +
                ((fileName != null) ? ", fileName='" + fileName + '\'' : "") +
                ((mimeType != null) ? ", mimeType='" + mimeType + '\'' : "") +
                ((size != 0) ? ", size='" + size + '\'' : "") +
                ((fileId != null) ? ", fileId='" + fileId + '\'' : "") +
                ((percentage != 0) ? ", percentage='" + percentage + '\'' : "") +
                ((message != null) ? ", message='" + message + '\'' : "") +
                '}';
    }
}
