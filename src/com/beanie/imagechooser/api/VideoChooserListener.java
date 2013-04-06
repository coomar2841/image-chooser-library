
package com.beanie.imagechooser.api;

public interface VideoChooserListener {
    public void onChosenVideo(ChosenVideo video);

    public void onError(String reason);
}
