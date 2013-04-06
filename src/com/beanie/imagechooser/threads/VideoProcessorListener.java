
package com.beanie.imagechooser.threads;

import com.beanie.imagechooser.api.ChosenVideo;

public interface VideoProcessorListener {
    public void onProcessedVideo(ChosenVideo video);

    public void onError(String reason);
}
