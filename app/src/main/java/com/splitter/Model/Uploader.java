package com.splitter.Model;

import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/* This class helps to upload images/files to firebase
*/
public class Uploader {
    //firebase
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    Intent intent;

    private final boolean uploaded = false;


    public Uploader() {
        firebaseInit();
    }

    public boolean uploadPhoto(Uri uri, String path, String parentId, String imageTag) {
        DatabaseReference dbRef = firebaseDatabase.getReference("");
        String tagPath = "images" + "/" + path + "/" + parentId + "/" + imageTag;
        StorageReference sReference = storageReference.child(tagPath);
        sReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                if (uriTask.isSuccessful()) {
                    HashMap<String, Object> results = new HashMap<>();
                    dbRef.child(parentId).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Success: image uploaded ");
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
        return uploaded;
    }

    public void sendMessage(String msg, String thisUserID, String otherId) {
        String timeStamp = getCurrentTime();
        DatabaseReference reference = firebaseDatabase.getReference("Chats");
        reference.push();
        Message m = new Message(reference.getKey(), msg, otherId, thisUserID, timeStamp, false);
        reference.setValue(m);
        }
    //ToDo delete ONLY users messages
    public void deleteMsg(String key) {
        DatabaseReference dbRefToMSG = FirebaseDatabase.getInstance().getReference("Chats").child(key);
        //dbRefToMSG.removeValue();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("msg", "This message was deleted...");
        dbRefToMSG.updateChildren(hashMap);
    }
    private String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ");
        String currentDateAndTime = sdf.format(new Date());
        return currentDateAndTime;
    }


    //init block
    private void firebaseInit(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }


}