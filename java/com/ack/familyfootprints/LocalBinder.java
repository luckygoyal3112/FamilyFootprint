package com.ack.familyfootprints;

/**
 * Created by Lucky Goyal on 2/18/2016.
 */
import android.os.Binder;

import java.lang.ref.WeakReference;

public class LocalBinder<S> extends Binder {
    private final WeakReference<S> mService;

    public LocalBinder(final S service) {
        mService = new WeakReference<S>(service);
    }

    public S getService() {
        return mService.get();
    }

}

