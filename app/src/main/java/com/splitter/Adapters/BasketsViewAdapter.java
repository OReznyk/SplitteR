package com.splitter.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.splitter.Activities.NewProductActivity;
import com.splitter.Fragments.ItemsListFragment;
import com.splitter.Model.Basket;
import com.splitter.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BasketsViewAdapter extends RecyclerView.Adapter<BasketsViewAdapter.MyHolder> {
    Context context;
    List<Basket> basketsList;
    Boolean isGroup, isAdmin;

    public BasketsViewAdapter(Context context, List<Basket> basketList, Boolean isGroup, Boolean isAdmin) {
        this.context = context;
        this.basketsList = basketList;
        this.isGroup = isGroup;
        this.isAdmin = isAdmin;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView mAvatarIv;
        TextView mTitleTv, mTotalPriceTv;

        public MyHolder(@NonNull View view) {
            super(view);
            mAvatarIv = view.findViewById(R.id.row_imgTv);
            mTotalPriceTv = view.findViewById(R.id.row_bottom_textField);
            mTitleTv = view.findViewById(R.id.row_top_textField);

        }
    }

    @NonNull
    @Override
    public BasketsViewAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_row_user_or_chat, parent, false);
        return new BasketsViewAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BasketsViewAdapter.MyHolder holder, int position) {
        //get data
        Basket basket = basketsList.get(position);
        //set data
        holder.mTitleTv.setText(basket.getTitle());
        holder.mTotalPriceTv.setText(basket.getTotalPrice());
        holder.mAvatarIv.setVisibility(View.INVISIBLE);

        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] dialogOptions;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose Option:");
                //TODO: add options
                dialogOptions = new String[]{"Open basket", "Open my basket", "", ""};
                if(isAdmin){
                    dialogOptions[2] = "Add Items";
                    dialogOptions[3] = "Remove basket";
                }
                builder.setItems(dialogOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> types = new ArrayList<>();
                        if(which == 0){
                            // open basket: products list
                            openProductsList(basket.getBasketID(), types, true, false);
                        }
                        if(which == 1){
                            // open my basket: products list
                            openProductsList(basket.getBasketID(),types, false, true);
                        }
                        if(which == 2){
                            // open dialog to choose product type and then open add products list
                            if(isAdmin){
                                openDialogForItemsTypes(basket.getBasketID());

                            }

                        }
                        else{
                            // remove basket
                            if(isAdmin){
                                removeBasketFromDB(basket);
                                basketsList.remove(position);
                                notifyDataSetChanged();
                            }
                        }
                    }
                });
                builder.show();
            }
        });

    }

    private List<String> openDialogForItemsTypes(String basketID) {
        List<String>types = new ArrayList<>();
        String[]allTypes = getItemTypesFromDB();
        AlertDialog.Builder typesDialog = new AlertDialog.Builder(context);
        if(allTypes.length != 0){
            //if there are items in db
            boolean[] selected =  new boolean[allTypes.length];
            Arrays.fill(selected, Boolean.FALSE);
            typesDialog.setTitle("Choose Items Types:");
            typesDialog.setMultiChoiceItems(allTypes, selected, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    selected[which] = isChecked;
                }
            });
            typesDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (int i = 0; i<allTypes.length; i++){
                        if (selected[i]) {
                            types.add(allTypes[i]);
                        }
                    }
                    if(!types.isEmpty()) {
                        openProductsList(basketID, types, false, false);
                    }
                    else Toast.makeText(context, "no types selected", Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            typesDialog.setTitle("No Items Created");
            typesDialog.setMessage("Do you want to create new item?");
        }
        typesDialog.setNeutralButton("Create item", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(context, NewProductActivity.class);
                context.startActivity(i);
            }
        });
        typesDialog.setNegativeButton("Cancel", null);
        AlertDialog alert = typesDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
        return types;
    }

    private String [] getItemTypesFromDB() {
        List<String>t = new ArrayList<>();
        DatabaseReference itemTypes = FirebaseDatabase.getInstance().getReference("ItemsByTypes");
        itemTypes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("name")) {
                    // run some code
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //TODO: fix the problem
        t.add("food");
        String [] types = t.toArray(new String[0]);
        return types;
    }

    private void openProductsList(String basketID, List<String>itemsTypes, Boolean onlyBasketItems, Boolean onlyUserItems) {
        Bundle args = new Bundle();
        args.putBoolean("isAdmin", isAdmin);
        args.putBoolean("onlyBasketItems", onlyBasketItems);
        args.putBoolean("onlyUserItems", onlyUserItems);
        args.putString("basketID", basketID);
        args.putStringArrayList("itemsTypes", (ArrayList<String>) itemsTypes);
        ItemsListFragment fragment = new ItemsListFragment();
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack("items").commitAllowingStateLoss();

    }

    private void removeBasketFromDB(Basket basket) {
        DatabaseReference basketsRef = FirebaseDatabase.getInstance().getReference("Baskets");
        basketsRef.child(basket.getBasketID()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(isGroup){
                            deleteBasketFromList("Groups", basket);
                        }
                        else{
                            deleteBasketFromList("Users", basket);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteBasketFromList(String path, Basket basket) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(path);
        for (String adminID: basket.getAdminsID()) {
            dbRef.child(adminID).child("basketsIDs").child(basket.getBasketID()).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Basket Successfully deleted! " + basket.getBasketID());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return basketsList.size();
    }
}
