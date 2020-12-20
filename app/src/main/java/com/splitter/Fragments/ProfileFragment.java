package com.splitter.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.splitter.Activities.ImagePicker;
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
        avatarIv = view.findViewById(R.id.ps_avatar);
        coverIv = view.findViewById(R.id.ps_coverIv);
        settingsIv = view.findViewById(R.id.settings);
        nameTv = view.findViewById(R.id.ps_name_txt);
        phoneTv = view.findViewById(R.id.ps_phone_txt);
        emailTv = view.findViewById(R.id.ps_email_txt);
        user = new User();
        user.setId(fUser.getUid());
        progressDialog = new ProgressDialog(getActivity());

        Query query = dbRef.orderByChild("id").equalTo(user.getId());
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
                        Picasso.get().load(user.getAvatar()).placeholder(R.drawable.ic_default_avatar).into(avatarIv);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if (resultCode == RESULT_OK) {
                imgURI = Uri.parse(data.getStringExtra("uriAsString"));
                Uploader uploader = new Uploader();
                uploader.uploadPhoto(imgURI, "profile", user.getId(), avatarOrCover);
                updateUserData(avatarOrCover, imgURI.toString());
            }
            //TODO get result not ok
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showImagePicDialog() {
        Intent i = new Intent(getActivity(), ImagePicker.class);
        startActivityForResult(i, 1);
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
                    updateUserData(key, value);
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

    private void updateUserData(String key, String value){
        HashMap<String, Object> result = new HashMap<>();
        result.put(key, value);
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
