package com.splitter.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.splitter.Activities.LoginActivity;
import com.splitter.Adapters.ChatListAdapter;
import com.splitter.Model.Chat;
import com.splitter.Model.Message;
import com.splitter.Model.User;
import com.splitter.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatListFragment extends Fragment {
    FloatingActionButton actionButton;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    ChatListAdapter adapter;
    DatabaseReference usersRef, chatListRef, msgsRef;
    RecyclerView recyclerView;
    List<Chat> chatsIDs;
    List<User> usersForChatsDataFull, usersForChatsData;
    public ChatListFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_list, container, false);
        initFirebase();
        recyclerView = view.findViewById(R.id.list_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //create new group
        actionButton = view.findViewById(R.id.floatingActionButton);
        actionButton.hide();
        //init & all get users to list
        usersForChatsData = new ArrayList<>();
        chatsIDs = new ArrayList<>();
        getAllChats();
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==1)
        {
            String groupID = data.getStringExtra("newGroupID");
            //TODO: add group to view
        }
    }

    private void getAllChats() {
        chatListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsIDs.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    chatsIDs.add(chat);
                }
                getChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getChats() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersForChatsData.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                        for(Chat chat: chatsIDs){
                            if(user.getId() != null && user.getId().equals(chat.getId())){
                                usersForChatsData.add(user);
                                break;
                            }
                        }
                }
                createChatsListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createChatsListView(){
        adapter = new ChatListAdapter(getActivity(), usersForChatsData);
        recyclerView.setAdapter(adapter);
        //set last msg
        for(int i=0; i<usersForChatsData.size(); i++){
            lastMsg(usersForChatsData.get(i).getId());
        }
    }

    private void lastMsg(String otherUserId) {
        msgsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             String lastMsg = "default";
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message msg = snapshot.getValue(Message.class);
                    if(msg == null){
                       continue;
                    }
                    if(msg.getSender() == null || msg.getReceiver() == null){
                        continue;
                    }
                    if(msg.getReceiver().equals(fUser.getUid()) && msg.getSender().equals(otherUserId) ||
                            msg.getReceiver().equals(otherUserId) && msg.getSender().equals(fUser.getUid())){
                            lastMsg = msg.getMsg();
                    }
                }
                adapter.setLastMessage(otherUserId, lastMsg);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //ToDo: use searchChats
    public void searchChats(String s){
        usersForChatsDataFull = new ArrayList<>(usersForChatsData);
        usersForChatsData.clear();
        for(int i=0; i<usersForChatsDataFull.size(); i++){
            if(usersForChatsDataFull.get(i).getName().toLowerCase().contains(s) || usersForChatsDataFull.get(i).getEmail().toLowerCase().contains(s)){
                usersForChatsData.add(usersForChatsDataFull.get(i));
            }
        }
        createChatsListView();
    }
    private void initFirebase(){
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        chatListRef = FirebaseDatabase.getInstance().getReference("ChatList").child(fUser.getUid());
        msgsRef = FirebaseDatabase.getInstance().getReference("Chats");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        //inflating menu
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){searchChats(query);}
                else getAllChats();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){searchChats(newText);}
                else getAllChats();
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_action_logout:
                setOnlineStatus("offline");
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setOnlineStatus(String status){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        databaseReference.updateChildren(hashMap);
    }

}