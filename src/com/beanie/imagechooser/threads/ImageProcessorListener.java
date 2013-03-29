
package com.beanie.imagechooser.threads;

import com.beanie.imagechooser.api.ChosenImage;

public interface ImageProcessorListener {
    public void onProcessedImage(ChosenImage image);

    public void onError(String reason);
}
