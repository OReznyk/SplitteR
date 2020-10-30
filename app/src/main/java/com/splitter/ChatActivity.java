package com.splitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.splitter.Model.Uploader;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imgTv;
    TextView nameTv, statusTv;
    EditText msgIv;
    ImageButton sendMsgBtn;
    RecyclerView recyclerView;
    Uploader uploader;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseDatabase fDb;
    DatabaseReference dbRef;

    String frID, userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        Intent intent = getIntent();
        frID = intent.getStringExtra("friendsID");
        fDb = FirebaseDatabase.getInstance();
        dbRef =fDb.getReference("Users");
        getAndSetFriendsData();

        //handle send message btn clicked
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msgIv.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)){
                    //send msg
                    uploader = new Uploader();
                    uploader.sendMessage(msg, frID);
                    //reset msgIv
                    msgIv.setText("");

                }else{}
            }
        });



    }



    private void initView(){
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        toolbar.setTitle("");

        recyclerView = findViewById(R.id.chat_recView);
        imgTv = findViewById(R.id.chat_avatar);
        nameTv = findViewById(R.id.chat_name);
        statusTv = findViewById(R.id.chat_user_status);
        msgIv = findViewById(R.id.chat_msgIv);
        sendMsgBtn = findViewById(R.id.chat_send_msg_btn);

    }

    private void checkUserStatus(){
        if(fUser != null){
            userId = fUser.getUid();
        }
        else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }
    private void getAndSetFriendsData(){
        Query userQuery = dbRef.orderByChild("id").equalTo(frID);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String name = ""+dataSnapshot.child("name").getValue();
                    String img = ""+dataSnapshot.child("avatar").getValue();
                    nameTv.setText(name);
                    try {
                        Picasso.get().load(img).placeholder(R.drawable.ic_default_avatar).into(imgTv);

                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_default_avatar).into(imgTv);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.menu_action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart(){
        checkUserStatus();
        super.onStart();
    }
}
