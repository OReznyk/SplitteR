package com.splitter.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.splitter.Adapters.ItemAdapter;
import com.splitter.Model.BasketItem;
import com.splitter.Model.Item;
import com.splitter.R;

import java.util.ArrayList;
import java.util.List;

public class ItemsListFragment extends Fragment {
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ItemAdapter adapter;
    List<BasketItem> basketItemsList;
    FirebaseUser fUser;
    DatabaseReference dbRef;
    String basketID;
    List<String>itemsTypes;
    Boolean onlyBasketItems, onlyUserItems, isAdmin;


    public ItemsListFragment() {
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
        itemsTypes = new ArrayList<>();
        itemsTypes = this.getArguments().getStringArrayList("itemsTypes");
        basketID = this.getArguments().getString("basketID", "");
        onlyBasketItems = this.getArguments().getBoolean("onlyBasketItems", false);
        onlyUserItems = this.getArguments().getBoolean("onlyUserItems", false);
        isAdmin = this.getArguments().getBoolean("isAdmin", false);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        getItems();
        if(basketItemsList.isEmpty()) Toast.makeText(getContext(), "no items here", Toast.LENGTH_LONG).show();
        return view;
    }

    private void getItems() {
        basketItemsList = new ArrayList<>();
        if(!onlyBasketItems && !onlyUserItems) {
            // for add items to basket option
            getBasketItems();
            List<BasketItem> existingItems = new ArrayList<>(basketItemsList);
            basketItemsList.clear();
            for (String type:itemsTypes) {
                getOtherItems(existingItems, type);
            }
            adapter = new ItemAdapter(getActivity(), basketItemsList, basketID, isAdmin, true);
        }
        else {
            getBasketItems();
            adapter = new ItemAdapter(getActivity(), basketItemsList, basketID, isAdmin, false);
        }
        if(!basketItemsList.isEmpty())recyclerView.setAdapter(adapter);
    }

    private void getBasketItems() {
        dbRef = FirebaseDatabase.getInstance().getReference("Baskets").child(basketID).child("listToBye");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                basketItemsList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    BasketItem item = snapshot.getValue(BasketItem.class);
                    basketItemsList.add(item);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*public void searchUsers(String s){
        if(dbRef == null) initFirebase();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemsList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    if(!user.getId().equals(fUser.getUid())){
                        if(user.getName().toLowerCase().contains(s.toLowerCase()) || user.getEmail().toLowerCase().contains(s.toLowerCase())) {
                            itemsList.add(user);
                        }
                    }
                    adapter = new UsersViewAdapter(getActivity(), itemsList, basketID, onlyBasketItems, isAdmin);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //TODO fill onCanceled
            }
        });
    }*/
    private void getOtherItems(List<BasketItem> existingItems, String type){
        dbRef = FirebaseDatabase.getInstance().getReference("ItemsByTypes/"+type);
        //ToDO: search items: not working!!!!!!
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Item item = snapshot.getValue(Item.class);
                    //creates basket item from item with "" parameters for new rows
                    if(!existingItems.contains(item)){
                        BasketItem basketItem = new BasketItem(item);
                        basketItemsList.add(basketItem);}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
