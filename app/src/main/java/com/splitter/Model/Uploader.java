package com.splitter.Model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseDatabase fDb;
    DatabaseReference dbRef;

    StorageReference storageReference;
    //storage paths
    String imagesPath = "Users_Profile_Images/";

    private boolean uploaded = false;


    public Uploader() {
        firebaseInit();
    }

    public boolean uploadPhoto(Uri uri, String nameOfImg) {
        String filePathAndName = imagesPath + "" + fUser.getUid() + "_" + nameOfImg;
        StorageReference storgRef = storageReference.child(filePathAndName);
        storgRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri downloadUri = uriTask.getResult();
                if (uriTask.isSuccessful()) {
                    HashMap<String, Object> results = new HashMap<>();
                    results.put(nameOfImg, downloadUri.toString());
                    dbRef.child(fUser.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            uploaded = true;
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

    public void sendMessage(String msg, String otherId) {
        String timeStamp = getCurrentTime();
        dbRef = fDb.getReference("Chats");
        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("sender", fUser.getUid());
        hashMap.put("receiver", otherId);
        hashMap.put("msg", msg);
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("isSeen", false);
        //To get objects id
        DatabaseReference msgID = dbRef.push();
        hashMap.put("id", msgID.getKey());
        msgID.setValue(hashMap);
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
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fDb = FirebaseDatabase.getInstance();
        dbRef = fDb.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
    }

}