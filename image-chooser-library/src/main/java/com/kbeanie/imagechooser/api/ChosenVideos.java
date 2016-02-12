package com.kbeanie.imagechooser.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kbibek on 2/12/16.
 */
public class ChosenVideos {
    private List<ChosenVideo> videos;

    public ChosenVideos() {
        this.videos = new ArrayList<>();
    }

    public void addVideo(ChosenVideo video) {
        this.videos.add(video);
    }

    public int size(){
        return videos.size();
    }

    public ChosenVideo getImage(int index){
        return videos.get(index);
    }
}
