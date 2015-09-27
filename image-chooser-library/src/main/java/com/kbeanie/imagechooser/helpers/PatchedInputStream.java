package com.kbeanie.imagechooser.helpers;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kbibek on 9/28/15.
 */
public class PatchedInputStream extends FilterInputStream {
    public PatchedInputStream(InputStream input) {
        super(input);
    }

    public long skip(long n) throws IOException {
        long m = 0L;
        while (m < n) {
            long _m = in.skip(n - m);
            if (_m == 0L) break;
            m += _m;
        }
        return m;
    }
}
