package com.splitter.Fragments;

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
import com.splitter.Adapters.UsersAdapter;
import com.splitter.Model.User;
import com.splitter.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersListFragment extends Fragment {
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    UsersAdapter adapter;
    List<User> userList;
    FirebaseUser fUser;
    DatabaseReference dbRef;
    String groupID;
    Boolean addParticipant, isAdmin;


    public UsersListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.hide();
        recyclerView = view.findViewById(R.id.list_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (this.getArguments() != null) {
            groupID = this.getArguments().getString("groupID");
            addParticipant = this.getArguments().getBoolean("settings");
            isAdmin = this.getArguments().getBoolean("isAdmin");
        }
        else{
            groupID = "";
            addParticipant = false;
            isAdmin = false;
        }
        //init & all get users to list
        //ToDo change all to friends only
        userList = new ArrayList<>();
        getAllUsers();
        
        return view;
    }

    private void getAllUsers() {
        if(dbRef == null) initFirebase();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    if(!fUser.getUid().equals(user.getId())){
                        userList.add(user);
                    }
                    adapter = new UsersAdapter(getActivity(), userList, groupID, addParticipant, isAdmin);
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void searchUsers(String s){
        if(dbRef == null) initFirebase();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    if(!user.getId().equals(fUser.getUid())){
                        if(user.getName().toLowerCase().contains(s.toLowerCase()) || user.getEmail().toLowerCase().contains(s.toLowerCase())) {
                            userList.add(user);
                        }
                    }
                    adapter = new UsersAdapter(getActivity(), userList, groupID, addParticipant, isAdmin);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //TODO fill onCanceled
            }
        });
    }
    private void initFirebase(){
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
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
                if(!TextUtils.isEmpty(query.trim())){searchUsers(query);}
                else getAllUsers();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){searchUsers(newText);}
                else getAllUsers();
                return false;
            }
        });

         super.onCreateOptionsMenu(menu, inflater);
    }

}