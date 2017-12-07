package com.example.mark.myapplication;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Vector;

import static com.example.mark.myapplication.MainActivity.MY_PERMISSION_REQUEST_CODE;

/**
 * Created by mark on 12/6/17.
 * adapted from https://android--examples.blogspot.com/2017/08/android-get-all-audio-files-from-sd-card.html
 */

class MusicQuery {


    private final MainActivity mainActivity;

    Vector<String> uriList = new Vector<>(5, 5);

    MusicQuery(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    // Custom method to get all audio files list from external storage
    void getMediaFileList(Context mContext) {

        ContentResolver contentResolver = mContext.getContentResolver();


        addSongLinks(contentResolver, MediaStore.Audio.Media.getContentUriForPath(Environment.getExternalStorageDirectory().getPath()));
        addSongLinks(contentResolver, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        //addSongLinks(contentResolver, MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
        addSongLinks(contentResolver, MediaStore.Audio.Media.getContentUriForPath(Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.google.android.music/cache/music"));


    }

    private void addSongLinks(ContentResolver contentResolver, Uri uri) {

        Cursor cursor = contentResolver.query(
                uri, // Uri
                null, // Projection
                null, // Selection
                null, // Selection args
                MediaStore.Audio.Media.DISPLAY_NAME + " ASC" // Sort order
        );

        if (cursor == null) {
            Log.e("ERROR!", "Query failed, handle error.");
        } else if (!cursor.moveToFirst()) {
            // no media on the device
            Log.e("ERROR", "No music found on the sd card.");
            addRadioButton("No music found on the sd card. Note we do not look inside app folders like \'Google Music\'", "");
        } else {
            int dataCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int titleCol = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            do {

                //Get path to audio
                String uriData = cursor.getString(dataCol);

                // Process current music here
                if (!uriList.contains(uriData)) {//no idea why, but songs pop up twice
                    // Get the current audio title
                    String title = cursor.getString(titleCol);
                    addRadioButton(title, uriData);
                }

            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void addRadioButton(String title, String uri) {

        RadioGroup ll = (RadioGroup) mainActivity.findViewById(R.id.songGroup);

        RadioButton rdbtn = new RadioButton(mainActivity);
        uriList.add(uri);
        rdbtn.setId(uriList.size() - 1);
        rdbtn.setText(title);
        ll.addView(rdbtn);
        Log.d("T", title + " " + rdbtn.getId());
    }

    void checkPermission() {
        if (mainActivity.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (mainActivity.shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.mActivity);
                builder.setMessage("Read external storage permission is required.");
                builder.setTitle("Please grant permission");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                mainActivity.mActivity,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSION_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // Request permission
                ActivityCompat.requestPermissions(
                        mainActivity.mActivity,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_CODE
                );
            }
        } else {
            // Permission already granted
        }
    }


}
