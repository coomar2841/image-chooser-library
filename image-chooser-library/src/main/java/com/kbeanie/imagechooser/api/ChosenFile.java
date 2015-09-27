package com.kbeanie.imagechooser.api;

/**
 * Created by kbibek on 14/11/14.
 */
public class ChosenFile {
    private String filePath;
    private String mimeType;
    private String fileName;
    private long fileSize;
    private String extension;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        if (mimeType == null) {
            for (String ext : imagesExtensions) {
                if (extension.startsWith(ext)) {
                    mimeType = "image";
                    break;
                }
            }
            if (mimeType == null) {
                for (String ext : videoExtensions) {
                    if (extension.startsWith(ext)) {
                        mimeType = "video";
                        break;
                    }
                }
            }
        }
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    private String[] imagesExtensions = {".jpg", ".jpeg", ".bpm", ".png", "gif"};
    private String[] videoExtensions = {".mp4", ".mpeg", ".3gp"};
}
