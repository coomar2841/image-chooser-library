
package com.beanie.imagechooser.api;

public interface ImageChooserListener {
    /**
     * When the processing is complete, you will receive this callback with
     * {@link ChosenImage}
     * 
     * @param image
     */
    public void onImageChosen(ChosenImage image);

    /**
     * Handle any error conditions if at all, when you receieve this callback
     * 
     * @param reason
     */
    public void onError(String reason);
}
