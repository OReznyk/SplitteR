package com.splitter.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ImagePicker extends AppCompatActivity {
    //permissions constants
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int PICK_IMAGE_FROM_GALLERY_REQUEST = 300;
    private static final int PICK_IMAGE_FROM_CAMERA_REQUEST = 400;
    String[] cameraPermissions;
    String[] storagePermissions;
    Uri imgURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        permissionsInit();
        showImagePicDialog();
    }


    //permissions block
    private void permissionsInit() {
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private boolean checkStoragePermission() {
        boolean ans = ContextCompat.checkSelfPermission(ImagePicker.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return ans;
    }

    private void requestStoragePermissions() {
        requestPermissions(storagePermissions, STORAGE_REQUEST);
    }

    private boolean checkCameraPermission() {
        boolean a = ContextCompat.checkSelfPermission(ImagePicker.this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean b = ContextCompat.checkSelfPermission(ImagePicker.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return a && b;
    }

    private void requestCameraPermissions() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean wStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camAccepted && wStorageAccepted) {
                        pickImageFromCamera();
                    } else {
                        Toast.makeText(ImagePicker.this, "Enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean wStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (wStorageAccepted) {
                        pickImageFromGallery();
                    } else {
                        Toast.makeText(ImagePicker.this, "Enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    private void pickImageFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        imgURI = ImagePicker.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgURI);
        startActivityForResult(cameraIntent, PICK_IMAGE_FROM_CAMERA_REQUEST);


    }

    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_FROM_GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent resultIntent = new Intent();
            if (requestCode == PICK_IMAGE_FROM_GALLERY_REQUEST) {
                imgURI = data.getData();
            }
            resultIntent.putExtra("uriAsString", imgURI.toString());
            setResult(RESULT_OK, resultIntent);
        }
        if(resultCode == RESULT_CANCELED){
            setResult(RESULT_CANCELED);
        }
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ImagePicker.this);
        builder.setTitle("Pick Image From:");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                //Camera option picked
                if (!checkCameraPermission()) {
                    requestCameraPermissions();
                } else {
                    pickImageFromCamera();
                }
            } else if (which == 1) {
                //Gallery option picked
                if (!checkStoragePermission()) {
                    requestStoragePermissions();
                } else {
                    pickImageFromGallery();
                }
            }
        });
        builder.create().show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
