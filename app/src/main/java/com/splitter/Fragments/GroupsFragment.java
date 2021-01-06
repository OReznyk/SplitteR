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
import com.splitter.Activities.NewGroupActivity;
import com.splitter.Adapters.GroupListAdapter;
import com.splitter.Model.Group;
import com.splitter.Model.Message;
import com.splitter.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupsFragment extends Fragment {
    FloatingActionButton actionButton;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    GroupListAdapter adapter;
    DatabaseReference msgsRef, groupsRef;
    RecyclerView recyclerView;
    List<Group> groups;
    public GroupsFragment(){}

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
        actionButton.show();
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), NewGroupActivity.class), 1);
            }
        });
        groups = new ArrayList<>();
        getAllGroups();
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

    private void getAllGroups() {
        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groups.clear();
                String userID = fUser.getUid();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(dataSnapshot.child("participants").hasChild(userID)){
                        Group group = dataSnapshot.getValue(Group.class);
                        groups.add(group);
                    }
                }
                adapter = new GroupListAdapter(getActivity(), groups);
                recyclerView.setAdapter(adapter);
                for(int i=0; i<groups.size(); i++){
                    lastMsg(groups.get(i).getId());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void searchGroups(String s) {
        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groups.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(dataSnapshot.child("participants").child(fUser.getUid()).exists()){
                        if(dataSnapshot.child("title").toString().toLowerCase().contains(s.toLowerCase())){
                            Group group = dataSnapshot.getValue(Group.class);
                            groups.add(group);
                        }
                    }
                }
                adapter = new GroupListAdapter(getActivity(), groups);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initFirebase(){
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        msgsRef = FirebaseDatabase.getInstance().getReference("Chats");
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
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
                if(!TextUtils.isEmpty(query.trim())){searchGroups(query);}
                else getAllGroups();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){searchGroups(newText);}
                else getAllGroups();
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