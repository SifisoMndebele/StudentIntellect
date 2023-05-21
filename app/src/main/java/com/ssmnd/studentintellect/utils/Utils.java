package com.ssmnd.studentintellect.utils;

import android.content.res.Resources;

public class Utils {
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    public static int pxToDp(int dp) {
        return (int) (dp / Resources.getSystem().getDisplayMetrics().density);
    }

}
