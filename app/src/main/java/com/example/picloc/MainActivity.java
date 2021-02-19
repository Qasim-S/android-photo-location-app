package com.example.picloc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
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
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Bitmap image = null;
    double latitude = 0.0;
    double longitude = 0.0;

    ArrayList<PicLocObject> photoList = null;

    PicLocDbHelper dbHelper = null;
    RecyclerView rvPicLoc = null;
    RecyclerView.Adapter dataAdapter = null;
    TextView noData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new PicLocDbHelper(this);
        noData = (TextView) findViewById(R.id.textViewNoData);

        RecyclerViewClickListener listener = (view, position) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Entry")
                    .setMessage("Are you sure you want to delete this entry?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                           TextView id = (TextView) view.findViewById(R.id.textViewID);
                           long ID = Long.parseLong(id.getText().toString());
                           deleteFromDB(ID);
                           photoList.remove(position);
                           dataAdapter.notifyItemRemoved(position);
                           loadPhotoList();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        };

        rvPicLoc = (RecyclerView) findViewById(R.id.recyclerViewPicLoc);
        photoList = getData();
        dataAdapter = new PicLocAdapter(photoList, listener);
        rvPicLoc.setAdapter(dataAdapter);
        rvPicLoc.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        loadPhotoList();
    }

    public void takePhoto(View v) {
        dispatchTakePictureIntent();
    }

    public void getLocation(View v) {
        LOCATION_HAS_BEEN_CAPTURED = true;
    }

    public void savePhotoAndLocToDB(View v) {
        if(PHOTO_HAS_BEEN_TAKEN && LOCATION_HAS_BEEN_CAPTURED) {
            putInfoToDB();
            photoList.add(photoList.size(), new PicLocObject(image, latitude, longitude, 0));
            dataAdapter.notifyItemInserted(photoList.size()-1);
            showToast("Photo and location saved!");
            loadPhotoList();
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

    private ArrayList<PicLocObject> getData() {
        return readInfoFromDB();
    }

    private void loadPhotoList() {
        if(photoList.size() > 0) {
            noData.setVisibility(View.INVISIBLE);
            rvPicLoc.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.VISIBLE);
            rvPicLoc.setVisibility(View.INVISIBLE);
        }
    }

    private void deleteFromDB(long id) {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = PicLocContract.PicLoc._ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(id) };
        // Issue SQL statement.
        int deletedRows = db.delete(PicLocContract.PicLoc.TABLE_NAME, selection, selectionArgs);
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

    private ArrayList<PicLocObject> readInfoFromDB() {
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

        ArrayList picLocObjects = new ArrayList<>();
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

            PicLocObject obj = new PicLocObject(bm, lat, lng, itemId);

            picLocObjects.add(obj);
        }
        cursor.close();

        return picLocObjects;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image = imageBitmap;
            PHOTO_HAS_BEEN_TAKEN = true;
        }
    }
}