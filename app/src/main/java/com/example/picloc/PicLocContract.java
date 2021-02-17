package com.example.picloc;

import android.graphics.Bitmap;
import android.provider.BaseColumns;

public class PicLocContract {
    private PicLocContract() {}

    /* Inner class that defines the table contents */
    public static class PicLoc implements BaseColumns {
        public static final String TABLE_NAME = "photo_location";
        public static final String COLUMN_NAME_PHOTO = "photo";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
    }
}
