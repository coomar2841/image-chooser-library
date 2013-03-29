
package com.beanie.imagechooser.api;

public class ChosenImage {
    private String filePathOriginal;

    private String fileThumbnail;

    private String fileThumbnailSmall;

    public String getFilePathOriginal() {
        return filePathOriginal;
    }

    public void setFilePathOriginal(String filePathOriginal) {
        this.filePathOriginal = filePathOriginal;
    }

    public String getFileThumbnail() {
        return fileThumbnail;
    }

    public void setFileThumbnail(String fileThumbnail) {
        this.fileThumbnail = fileThumbnail;
    }

    public String getFileThumbnailSmall() {
        return fileThumbnailSmall;
    }

    public void setFileThumbnailSmall(String fileThumbnailSmall) {
        this.fileThumbnailSmall = fileThumbnailSmall;
    }

}
