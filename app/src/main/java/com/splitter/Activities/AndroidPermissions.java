package com.splitter.Activities;

import android.Manifest;
import android.content.Context;

public class AndroidPermissions {
    private String cameraPermissions[];
    private String storagePermissions[];
    private Context context;

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;

    public AndroidPermissions(Context context) {
        this.context = context;
    }

    //permissions block
    private void permissionsInit() {
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }
/*
    private boolean checkStoragePermission() {
        boolean ans = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return ans;
    }

    private void requestStoragePermissions() {
        requestPermissions(storagePermissions, STORAGE_REQUEST);
    }

    private boolean checkCameraPermission() {
        boolean a = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean b = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
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
                        permissionsOK();
                    } else {
                        Toast.makeText(context, "Enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean wStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (wStorageAccepted) {
                        permissionsOK();
                    } else {
                        Toast.makeText(context, "Enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    private boolean permissionsOK(){
        return true;
    }*/

}
