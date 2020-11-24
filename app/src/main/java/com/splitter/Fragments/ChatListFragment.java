package com.splitter.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import com.splitter.Activities.NewGroupActivity;
import com.splitter.Adapters.FriendsListAdapter;
import com.splitter.Model.User;
import com.splitter.R;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {
    FloatingActionButton actionButton;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FriendsListAdapter adapter;
    DatabaseReference chatsRef, groupsRef;
    RecyclerView recyclerView;
    List<User> chatsList;
    public ChatListFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chats_list, container, false);

        recyclerView = view.findViewById(R.id.chat_list_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        actionButton = view.findViewById(R.id.chats_floatingActionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewGroupActivity.class));
            }
        });

        //init & all get users to list
        //ToDo change all to friends only
        chatsList = new ArrayList<>();
        getAllChatsAndGroups();
        return view;
    }

    private void getAllChatsAndGroups() {
        if(chatsRef == null || groupsRef == null) initFirebase();
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatsList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    /*if(user.participantsIDs().contains(fUser.getUid())){
                        chatsList.add(user);
                    }*/
                    adapter = new FriendsListAdapter(getActivity(), chatsList);
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //ToDo: use searchChats
    public void searchChats(String s){
        /*if(dbRef == null) initFirebase();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!chatsList.isEmpty()) chatsList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    if(!user.getId().equals(fUser.getUid())){
                        if(user.getName().toLowerCase().contains(s.toLowerCase()) || user.getEmail().toLowerCase().contains(s.toLowerCase())) {
                            chatsList.add(user);
                        }
                    }
                    adapter = new UsersAdapter(getActivity(), chatsList);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //TODO fill onCanceled
            }
        });*/
    }
    private void initFirebase(){
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        chatsRef = FirebaseDatabase.getInstance().getReference("Chats");
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
    }


}