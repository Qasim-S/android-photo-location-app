package com.example.picloc;

import android.graphics.Bitmap;

public class PicLocObject {
    Bitmap photo;
    double latitude;
    double longitude;

    PicLocObject() {
        this.photo = null;
        this.latitude = this.longitude = 0.0;
    }

    PicLocObject(Bitmap img, double lat, double lng) {
        this.photo = img;
        this.latitude = lat;
        this.longitude = lng;
    }

    public void setPhoto(Bitmap img) {
        this.photo = img;
    }

    public void setLatitude(double lat) {
        this.latitude = lat;
    }

    public void setLongitude(double lng) {
        this.longitude = lng;
    }

    public Bitmap getPhoto() {
        return this.photo;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }
}
