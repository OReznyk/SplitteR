package com.splitter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.splitter.Model.User;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {


    ImageView avatarIv;
    TextView nameTv, emailTv, phoneTv;

    User user;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseDatabase fDb;
    DatabaseReference dbRef;

    public ProfileFragment(){}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseInit();
        avatarIv = view.findViewById(R.id.ps_avatar);
        nameTv = view.findViewById(R.id.ps_name_txt);
        phoneTv = view.findViewById(R.id.ps_phone_txt);
        emailTv = view.findViewById(R.id.ps_email_txt);
        user = new User();

        Query query = dbRef.orderByChild("email").equalTo(fUser.getEmail());
        ProfileFragment profileFragment = new ProfileFragment();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    user.setName((String) ds.child("name").getValue());
                    user.setEmail((String) ds.child("email").getValue());
                    user.setPhone((String) ds.child("phone").getValue());
                    user.setAvatarURL((String) ds.child("avatar").getValue());
                    //set data to txts on page
                    nameTv.setText(user.getName());
                    emailTv.setText(user.getEmail());
                    phoneTv.setText(user.getPhone());
                    try {
                        Picasso.get().load(user.getAvatarURL()).into(avatarIv);
                    }
                    catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_add_avatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    private void firebaseInit(){
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fDb = FirebaseDatabase.getInstance();
        dbRef = fDb.getReference("Users");
    }
}
