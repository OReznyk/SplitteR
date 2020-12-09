package com.splitter.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.splitter.Model.Uploader;
import com.splitter.Model.User;
import com.splitter.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    //views from xml
    ImageView avatarIv, coverIv, settingsIv;
    TextView nameTv, emailTv, phoneTv;
    //classes
    User user;
    //firebase
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseDatabase fDb;
    DatabaseReference dbRef;
    StorageReference storageReference;
    // progress dialog
    ProgressDialog progressDialog;
    //permissions constants
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int PICK_IMAGE_FROM_GALLERY_REQUEST = 300;
    private static final int PICK_IMAGE_FROM_CAMERA_REQUEST = 400;
    String[] cameraPermissions;
    String[] storagePermissions;
    String avatarOrCover;
    Uri imgURI;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        firebaseInit();
        permissionsInit();
        avatarIv = view.findViewById(R.id.ps_avatar);
        coverIv = view.findViewById(R.id.ps_coverIv);
        settingsIv = view.findViewById(R.id.settings);
        nameTv = view.findViewById(R.id.ps_name_txt);
        phoneTv = view.findViewById(R.id.ps_phone_txt);
        emailTv = view.findViewById(R.id.ps_email_txt);
        user = new User();
        user.setId(fUser.getUid());
        progressDialog = new ProgressDialog(getActivity());

        Query query = dbRef.orderByChild("id").equalTo(fUser.getUid());
        //ProfileFragment profileFragment = new ProfileFragment();
        //ToDo settings option
        settingsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    user = ds.getValue(User.class);
                    //update view
                    nameTv.setText(user.getName());
                    emailTv.setText(user.getEmail());
                    phoneTv.setText(user.getPhone());
                    try {
                        Picasso.get().load(user.getAvatar()).placeholder(R.drawable.ic_default_avatar);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_avatar);
                    }
                    try {
                        Picasso.get().load(user.getCover()).into(coverIv);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    //permissions block
    private void permissionsInit() {
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private boolean checkStoragePermission() {
        boolean ans = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return ans;
    }

    private void requestStoragePermissions() {
        requestPermissions(storagePermissions, STORAGE_REQUEST);
    }

    private boolean checkCameraPermission() {
        boolean a = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean b = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
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
                        Toast.makeText(getActivity(), "Enable camera & storage permission", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Enable storage permission", Toast.LENGTH_SHORT).show();
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
        imgURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

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
            Uploader uploader = new Uploader();
            if (requestCode == PICK_IMAGE_FROM_GALLERY_REQUEST) {
                imgURI = data.getData();
                uploader.uploadPhoto(imgURI, avatarOrCover);
            }
            if (requestCode == PICK_IMAGE_FROM_CAMERA_REQUEST) {
                uploader.uploadPhoto(imgURI, avatarOrCover);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //dialogs block
    private void showEditProfileDialog() {
        String[] options = {"Change Profile Picture", "Change Cover Photo", "Change Name", "Change Phone"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Profile Settings");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    progressDialog.setMessage("Updating Profile Picture");
                    avatarOrCover = "avatar";
                    showImagePicDialog();
                } else if (which == 1) {
                    progressDialog.setMessage("Updating Cover Picture");
                    avatarOrCover = "cover";
                    showImagePicDialog();
                } else if (which == 2) {
                    progressDialog.setMessage("Updating Name");
                    showNamePhoneUpdateDialog("name");
                } else if (which == 3) {
                    progressDialog.setMessage("Updating Phone Number");
                    showNamePhoneUpdateDialog("phone");
                }
            }
        });
        builder.create().show();
    }

    private void showNamePhoneUpdateDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update " + key);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getActivity());
        editText.setHint("Enter " + key);
        linearLayout.addView(editText);
        builder.setView(linearLayout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)) {
                    progressDialog.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);
                    System.out.println("RESULT " + result.toString());
                    dbRef.child(user.getId()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Updated...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    });
                } else {
                    Toast.makeText(getActivity(), "Please enter " + key, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    //init block
    private void firebaseInit() {
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fDb = FirebaseDatabase.getInstance();
        dbRef = fDb.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
    }
}
