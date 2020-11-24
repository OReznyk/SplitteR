package com.splitter.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.splitter.Adapters.MessageAdapter;
import com.splitter.Model.Message;
import com.splitter.Model.Uploader;
import com.splitter.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    ImageView imgTv;
    TextView nameTv, statusTv;
    EditText msgIv;
    ImageButton sendBasketBtn, sendAttachmentBtn, sendMsgBtn;
    RecyclerView recyclerView;
    Uploader uploader;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseDatabase fDb;
    DatabaseReference dbRef;
    DatabaseReference refForSeen;
    ValueEventListener seenListener;

    String otherID, userId;

    List<Message> chatList;
    MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        initFirebase();
        getAndSetFriendsData();
        readMessage();
        seenMessage();
    }


    //from https://www.geeksforgeeks.org/how-to-programmatically-hide-android-soft-keyboard/
    private void closeKeyboard()
    {
        // this will give us the view which is currently focus in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently focus then this will protect the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }
    private void readMessage() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats/");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Message chat = dataSnapshot.getValue(Message.class);
                    if(chat.getReceiver().equals(userId) && chat.getSender().equals(otherID) ||
                            chat.getReceiver().equals(otherID) && chat.getSender().equals(userId)){
                        chatList.add(chat);
                    }
                    messageAdapter = new MessageAdapter(ChatActivity.this, chatList);
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void seenMessage() {
        refForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = refForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message chat = dataSnapshot.getValue(Message.class);
                    if(!chat.getMsg().equals("This message was deleted...")){
                        if (chat.getId() == dataSnapshot.getKey()) {
                            HashMap<String, Object> hashMap= new HashMap<>();
                            hashMap.put("isSeen", true);
                            dataSnapshot.getRef().updateChildren(hashMap);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void removeSeenListener(){
        refForSeen.removeEventListener(seenListener);
    }
    private void initView(){
        // init page view
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_recView);

        // init user data
        imgTv = findViewById(R.id.chat_avatar);
        nameTv = findViewById(R.id.chat_name);
        statusTv = findViewById(R.id.chat_user_status);
        
        // init msg view part
        msgIv = findViewById(R.id.chat_msgIv);
        sendBasketBtn = findViewById(R.id.chat_send_basket);
        sendAttachmentBtn = findViewById(R.id.chat_send_attachment);
        sendMsgBtn = findViewById(R.id.chat_send_msg_btn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        chatList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(ChatActivity.this, chatList);
        recyclerView.setAdapter(messageAdapter);
        
        //handle btn clicked
        sendBasketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewBasketActivity.class));
            }
        });
        sendAttachmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "You can open this option by buying app", Toast.LENGTH_SHORT).show();
            }
        });
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msgIv.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)){
                    //send msg
                    uploader = new Uploader();
                    uploader.sendMessage(msg, otherID);
                    //reset msgIv
                    msgIv.setText("");
                    closeKeyboard();
                    readMessage();
                    seenMessage();
                }else{
                    Toast.makeText(ChatActivity.this, "Can`t send empty message!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        msgIv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() == 0){
                    checkTypingStatus("noOne");
                }
                else {
                    checkTypingStatus(otherID);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    private void initFirebase(){
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        Intent intent = getIntent();
        otherID = intent.getStringExtra("friendsID");
        fDb = FirebaseDatabase.getInstance();
        dbRef =fDb.getReference("Users");
    }

    private void getAndSetFriendsData(){
        Query userQuery = dbRef.orderByChild("id").equalTo(otherID);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    // To get & set image & name
                    String name = ""+dataSnapshot.child("name").getValue();
                    String img = ""+dataSnapshot.child("avatar").getValue();
                    nameTv.setText(name);
                    try {
                        Picasso.get().load(img).placeholder(R.drawable.ic_default_avatar).into(imgTv);

                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_default_avatar).into(imgTv);
                    }
                    // To get & set typing/online status
                    String typingStatus = ""+ dataSnapshot.child("typingTo").getValue();
                    String onlineStatus = ""+ dataSnapshot.child("onlineStatus").getValue();
                    if(typingStatus.equals(userId)){
                        statusTv.setText("typing...");
                    }
                    else{
                        if(onlineStatus.equals("online")){
                            statusTv.setText(onlineStatus);
                        }
                        else{
                            statusTv.setText("Last seen at: "+onlineStatus);
                        }
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
        //ToDo finish menu init
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle(R.string.which_basket)
                .setItems(R.array.send_basket_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        //ToDo finish it
                        if(which == 0){
                            Toast.makeText(ChatActivity.this, "first option", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(getApplicationContext(), NewBasketActivity.class));
                            //finish();
                        }
                        else{
                            Toast.makeText(ChatActivity.this, "second option", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return builder.create();
    }
    // Functions for checking statuses
    private void checkUserStatus(){
        if(fUser != null){
            userId = fUser.getUid();
        }
        else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
    private void checkOnlineStatus(String status){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        databaseReference.updateChildren(hashMap);
    }
    private void checkTypingStatus(String typing){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onStart(){
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }
    @Override
    protected void onPause(){
        super.onPause();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ");
        String currentDateAndTime = sdf.format(new Date());
        checkOnlineStatus(currentDateAndTime);
        removeSeenListener();
    }
    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
