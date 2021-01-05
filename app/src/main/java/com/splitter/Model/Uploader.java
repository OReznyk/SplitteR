package com.splitter.Model;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

/* This class helps to upload images/files to firebase
*/
public class Uploader {
    //firebase
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    String downloadUri;

    private final boolean uploaded = false;


    public Uploader() {
        downloadUri = "";
        firebaseInit();
    }

    public String uploadPhoto(Uri uri, String path, String parentId, String imageTag) {
        DatabaseReference dbRef = firebaseDatabase.getReference("");
        String tagPath = "images" + "/" + path + "/" + parentId + "/" + imageTag;
        StorageReference sReference = storageReference.child(tagPath);
        sReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                while (!task.isSuccessful()) ;
                if (task.isSuccessful()) {
                    HashMap<String, Object> results = new HashMap<>();
                    dbRef.child(parentId).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Success: image uploaded ");
                            downloadUri = task.getResult().toString();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Failure: image not uploaded " + e.getMessage());
                        }
                    });
                } else {
                    Log.d(TAG, "Failure: uriTask not successful ");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failure: uriTask not successful " + e.getMessage());
            }
        });
        return downloadUri;
    }

    //ToDo delete ONLY users messages


    //init block
    private void firebaseInit(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }


}