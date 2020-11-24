package com.splitter.Activities;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.splitter.R;

public class NewGroupActivity  extends AppCompatActivity {
    // permissions code
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int PICK_IMAGE_FROM_GALLERY_REQUEST = 300;
    private static final int PICK_IMAGE_FROM_CAMERA_REQUEST = 400;
    String[] cameraPermissions;
    String[] storagePermissions;

    private final Uri imgURI = null;

    private static final String TAG = "TAG";
    private ImageView groupImg;
    private EditText title, description;
    private RecyclerView participantsView;
    private FloatingActionButton addParticipantBtn;
    private Button saveBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupImg = findViewById(R.id.group_img);
        title = findViewById(R.id.group_title);
        description = findViewById(R.id.group_description);
        participantsView = findViewById(R.id.group_participantsIv);
        addParticipantBtn = findViewById(R.id.group_addBtn);
        saveBtn = findViewById(R.id.group_saveBtn);

        // init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        groupImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showImagePickDialog();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
