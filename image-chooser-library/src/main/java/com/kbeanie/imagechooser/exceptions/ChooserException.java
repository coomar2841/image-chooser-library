package com.kbeanie.imagechooser.exceptions;

import android.content.ActivityNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by vervik on 9/27/15.
 */
public class ChooserException extends Exception {

    public ChooserException(String msg) {
        super(msg);
    }

    public ChooserException(ActivityNotFoundException e) {
        super(e);
    }

    public ChooserException(FileNotFoundException e) {
        super(e);
    }

    public ChooserException(IOException e) {
        super(e);
    }
}
