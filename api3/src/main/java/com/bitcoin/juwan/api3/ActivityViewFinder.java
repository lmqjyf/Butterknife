package com.bitcoin.juwan.api3;

import android.app.Activity;
import android.view.View;

/**
 * FileName：ActivityViewFinder
 * Create By：liumengqiang
 * Description：TODO
 */
public class ActivityViewFinder implements ViewFinder {
    @Override
    public View binView(Object o, int resId) {
        return ((Activity)o).findViewById(resId);
    }
}
