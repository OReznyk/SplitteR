package com.splitter.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.splitter.Adapters.MessageAdapter;
import com.splitter.Fragments.BasketsListFragment;
import com.splitter.Fragments.UsersListFragment;
import com.splitter.Model.Group;
import com.splitter.Model.Message;
import com.splitter.Model.User;
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
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersRef, groupsRef;
    DatabaseReference refForSeen;
    ValueEventListener seenListener;

    String otherID, userId, groupRole;
    Boolean isGroup;
    HashMap<String, String> basketsIDs;
    List<Message> messageList;
    MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initFirebase();
        initView();
        getAndSetData();
        readMessage();
        seenMessage();
    }

    private void initView(){
        Intent intent = getIntent();
        isGroup = intent.getBooleanExtra("isGroup", false);
        otherID = intent.getStringExtra("chatID");
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

        messageList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(ChatActivity.this, messageList, isGroup);
        recyclerView.setAdapter(messageAdapter);

        //handle btn clicked
        sendBasketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(ChatActivity.this, NewBasketActivity.class);
                //ToDo regular chat basket parentID?
                i.putExtra("userID", userId);
                i.putExtra("parentID", otherID);
                startActivityForResult(i, 1);
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
                    sendMessage(msg, userId, otherID);
                    //reset msgIv
                    msgIv.setText("");
                    closeKeyboard();
                }else{
                    Toast.makeText(ChatActivity.this, "Can`t send empty message!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(isGroup){
            loadMyGroupPermissions();
        }
        else groupRole = "";
        readMessage();
        seenMessage();
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
        userId = fUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("Users");
        groupsRef = firebaseDatabase.getReference("Groups");
    }

    public void sendMessage(String msg, String thisUserID, String otherId) {
        String timeStamp = getCurrentTime();
        DatabaseReference reference = firebaseDatabase.getReference("Chats");
        DatabaseReference newID = reference.push();
        Message m = new Message(newID.getKey(), msg, otherId, thisUserID, timeStamp, false);
        newID.setValue(m).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //to log failure
                Log.d("TAG", "Msg not saved "+ m.getId() + " "+ e.getMessage());
                setResult(RESULT_CANCELED);
            }
        });
        //create chat list node
         final DatabaseReference chatReference1 = firebaseDatabase.getReference("ChatList").child(userId).child(otherId);
         chatReference1.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(!snapshot.exists()){
                     chatReference1.child("id").setValue(otherId);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
        final DatabaseReference chatReference2 = firebaseDatabase.getReference("ChatList").child(otherId).child(userId);
        chatReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatReference2.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Message msg = dataSnapshot.getValue(Message.class);
                    if(isGroup){
                        if(msg.getReceiver().equals(otherID)){
                            messageList.add(msg);
                        }
                    }
                    else{
                        if(msg.getReceiver().equals(userId) && msg.getSender().equals(otherID) ||
                                msg.getReceiver().equals(otherID) && msg.getSender().equals(userId)){
                            messageList.add(msg);
                        }
                    }
                    messageAdapter = new MessageAdapter(ChatActivity.this, messageList, isGroup);
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

    private void getAndSetData(){
        if(!isGroup){
            Query chatQuery = usersRef.orderByChild("id").equalTo(otherID);
            chatQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        User other = dataSnapshot.getValue(User.class);
                        if(other.getBasketsIDs() != null) basketsIDs = new HashMap<String, String>(other.getBasketsIDs());
                        else basketsIDs = new HashMap<String, String>();
                        nameTv.setText(other.getName());
                        try {
                            Picasso.get().load(other.getAvatar()).placeholder(R.drawable.ic_default_avatar).into(imgTv);

                        }catch (Exception e){
                        }
                        // To get & set typing/online status
                        if(other.getTypingTo().equals(userId)){
                            statusTv.setText("typing...");
                        }
                        else{
                            if(other.getOnlineStatus().equals("online")){
                                statusTv.setText(other.getOnlineStatus());
                            }
                            else{
                                statusTv.setText("Last seen at: "+ other.getOnlineStatus());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });}
        else {
            Query chatQuery = groupsRef.orderByChild("id").equalTo(otherID);
            chatQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Group group = dataSnapshot.getValue(Group.class);
                        if(group.getBasketsIDs() != null) basketsIDs = new HashMap<String, String>(group.getBasketsIDs());
                        else basketsIDs = new HashMap<String, String>();
                        nameTv.setText(group.getTitle());
                        try {
                            Picasso.get().load(group.getGroupImg()).placeholder(R.drawable.ic_default_avatar).into(imgTv);

                        } catch (Exception e) {
                        }
                        //ToDo: fill it with participants names in future
                        statusTv.setText(group.getDescription());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
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
    private String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ");
        String currentDateAndTime = sdf.format(new Date());
        return currentDateAndTime;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            String basketID = data.getStringExtra("basketID");
            String basketTitle = data.getStringExtra("basketTitle");
            if(isGroup) addBasketToGroupList(basketID, basketTitle);
            //TODO else add to chatID
        }
    }

    private void addBasketToGroupList(String basketID, String basketTitle) {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        // TODO: in future set basket status as value:
        groupsRef.child(otherID).child("basketsIDs").child(basketID).setValue("")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendMessage(basketTitle + " created", userId, otherID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this , "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //inflating menu
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_menu_settings:
                if(isGroup) {
                    //start usersListFragment and not activity
                    Bundle args = new Bundle();
                    args.putString("groupID", otherID);
                    // send my permissions
                    args.putBoolean("isAdmin", groupRole.equals("admin"));
                    // TODO: change for more options
                    args.putBoolean("settings", true);
                    UsersListFragment fragment = new UsersListFragment();
                    fragment.setArguments(args);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack("users");
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                }
                else Toast.makeText(this, "chat Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.chat_menu_baskets:
                    //start basketsListFragment and not activity
                //TODO change to baskets st
                Bundle args = new Bundle();
                args.putString("chatID", otherID);
                args.putBoolean("isGroup", isGroup);
                ArrayList<String> ids =new ArrayList<>(basketsIDs.keySet());
                args.putStringArrayList("basketsIDs", ids);
                BasketsListFragment fragment = new BasketsListFragment();
                fragment.setArguments(args);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack("baskets");
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commitAllowingStateLoss();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // Functions for checking statuses

    private void loadMyGroupPermissions() {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        groupsRef.child(otherID).child("participants").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            groupRole = "" + snapshot.getValue();
                        }
                        else groupRole = "";
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
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