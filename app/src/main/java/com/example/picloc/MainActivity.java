package com.example.picloc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.HORIZONTAL;

public class MainActivity extends AppCompatActivity {

    private static boolean LOCATION_HAS_BEEN_CAPTURED = false;
    private static boolean PHOTO_HAS_BEEN_TAKEN = false;

    private Bitmap image = null;
    double latitude = 0.0;
    double longitude = 0.0;

    PicLocDbHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new PicLocDbHelper(this);
        refreshPhotoList();
    }

    public void takePhoto(View v) {
        PHOTO_HAS_BEEN_TAKEN = true;
    }

    public void getLocation(View v) {
        LOCATION_HAS_BEEN_CAPTURED = true;
    }

    public void savePhotoAndLocToDB(View v) {
        if(PHOTO_HAS_BEEN_TAKEN && LOCATION_HAS_BEEN_CAPTURED) {
            putInfoToDB();
            showToast("Photo and location saved!");
            refreshPhotoList();
            PHOTO_HAS_BEEN_TAKEN = LOCATION_HAS_BEEN_CAPTURED = false;
        } else if (LOCATION_HAS_BEEN_CAPTURED) {
            showToast("Please take a photo to save along with the location!");
        } else if (PHOTO_HAS_BEEN_TAKEN) {
            showToast("Please capture the location to save along with the photo!");
        } else {
            showToast("Please take a photo and capture the location to save!");
        }
    }

    /***********************************************************************************************
     * ********************************** HELPER METHODS *******************************************
    * *********************************************************************************************/
    private void showToast(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private PicLocObject[] getData() {
        return readInfoFromDB();
    }

    private void refreshPhotoList() {
        PicLocObject[] photoList = getData();
        if(photoList.length > 0) {
            RecyclerView rvPicLoc = (RecyclerView) findViewById(R.id.recyclerViewPicLoc);
            rvPicLoc.setAdapter(new PicLocAdapter(photoList));
            rvPicLoc.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        } else {
            TextView noData = (TextView) findViewById(R.id.textViewNoData);
            noData.setVisibility(View.VISIBLE);
        }
    }

    private void putInfoToDB() {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Converts Bitmap to BLOB for storage in DB
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] imageBLOB = bos.toByteArray();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PicLocContract.PicLoc.COLUMN_NAME_PHOTO, imageBLOB);
        values.put(PicLocContract.PicLoc.COLUMN_NAME_LONGITUDE, longitude);
        values.put(PicLocContract.PicLoc.COLUMN_NAME_LATITUDE, latitude);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(PicLocContract.PicLoc.TABLE_NAME, null, values);
    }

    private PicLocObject[] readInfoFromDB() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                PicLocContract.PicLoc.COLUMN_NAME_PHOTO,
                PicLocContract.PicLoc.COLUMN_NAME_LONGITUDE,
                PicLocContract.PicLoc.COLUMN_NAME_LATITUDE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = "";
        String[] selectionArgs = { };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                PicLocContract.PicLoc.COLUMN_NAME_LATITUDE + " DESC";

        Cursor cursor = db.query(
                PicLocContract.PicLoc.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        List picLocObjects = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(PicLocContract.PicLoc._ID));
            byte[] imageBLOB = cursor.getBlob(
                    cursor.getColumnIndexOrThrow(PicLocContract.PicLoc.COLUMN_NAME_PHOTO));
            double lat = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(PicLocContract.PicLoc.COLUMN_NAME_LATITUDE));
            double lng = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(PicLocContract.PicLoc.COLUMN_NAME_LONGITUDE));

            // Converts BLOB to Bitmap for display
            Bitmap bm = BitmapFactory.decodeByteArray(imageBLOB, 0 ,imageBLOB.length);

            PicLocObject obj = new PicLocObject(bm, lat, lng);

            picLocObjects.add(obj);
        }
        cursor.close();

        return listToArray(picLocObjects);
    }

    private PicLocObject[] listToArray(List<PicLocObject> picLocObjects) {
        PicLocObject[] arr = new PicLocObject[picLocObjects.size()];

        // ArrayList to Array Conversion
        for (int i = 0; i < picLocObjects.size(); i++)
            arr[i] = picLocObjects.get(i);

        return arr;
    }
}