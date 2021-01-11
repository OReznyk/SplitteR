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
import android.widget.Toast;

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
import com.splitter.Adapters.BasketsViewAdapter;
import com.splitter.Model.Basket;
import com.splitter.R;

import java.util.ArrayList;
import java.util.List;

public class BasketsListFragment extends Fragment {
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    BasketsViewAdapter adapter;
    List<Basket> basketList;
    FirebaseUser fUser;
    DatabaseReference dbRef;
    String parentID;
    Boolean isGroup, isAdmin;


    public BasketsListFragment() {
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
            parentID = this.getArguments().getString("chatID");
            isGroup = this.getArguments().getBoolean("isGroup", false);
            isAdmin = this.getArguments().getBoolean("isAdmin",false);
        }
        else{
            parentID = "";
        }

        basketList = new ArrayList<>();
        getAllBaskets();
        return view;
    }

    private void getAllBaskets() {
        if(dbRef == null) initFirebase();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                basketList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Basket basket = snapshot.getValue(Basket.class);
                    if(isGroup){
                        if(basket.getAdminsID().contains(parentID)){
                            basketList.add(basket);
                        }
                    }
                    else{
                        if(basket.getAdminsID().contains(parentID) && basket.getAdminsID().contains(fUser.getUid())){
                            basketList.add(basket);
                        }
                    }
                }
                if(basketList.isEmpty()) {
                    Toast.makeText(getContext(), "No baskets added" , Toast.LENGTH_LONG).show();
                }
                else {
                    adapter = new BasketsViewAdapter(getActivity(), basketList, isGroup, isAdmin);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void searchBaskets(String s){
        if(dbRef == null) initFirebase();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                basketList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Basket basket = snapshot.getValue(Basket.class);
                    if (isGroup) {
                        if (basket.getAdminsID().contains(parentID)) {
                            if (basket.getTitle().toLowerCase().contains(s.toLowerCase())) {
                                basketList.add(basket);
                            }
                        }
                    } else {
                        if (basket.getAdminsID().contains(parentID) && basket.getAdminsID().contains(fUser.getUid())) {
                            if (basket.getTitle().toLowerCase().contains(s.toLowerCase())) {
                                basketList.add(basket);
                            }
                        }
                    }
                }
                if (basketList.isEmpty()) {
                    Toast.makeText(getContext(), "No baskets added", Toast.LENGTH_LONG).show();
                } else {
                    adapter = new BasketsViewAdapter(getActivity(), basketList, isGroup, isAdmin);
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
        dbRef = FirebaseDatabase.getInstance().getReference("Baskets");
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
                if(!TextUtils.isEmpty(query.trim())){
                    searchBaskets(query);}
                else getAllBaskets();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    searchBaskets(newText);}
                else getAllBaskets();
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


}
