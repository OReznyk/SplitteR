package com.splitter.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.splitter.Model.Basket;
import com.splitter.Model.Product;
import com.splitter.R;

import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BasketsViewAdapter extends RecyclerView.Adapter<BasketsViewAdapter.MyHolder> {
    Context context;
    List<Basket> basketsList;
    Boolean isGroup;

    public BasketsViewAdapter(Context context, List<Basket> basketList, Boolean isGroup) {
        this.context = context;
        this.basketsList = basketList;
        this.isGroup = isGroup;
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
                dialogOptions = new String[]{"Open", "Remove basket"};
                builder.setItems(dialogOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            // open basket: products list
                            openProductsList(basket.getBasketID(), basket.getListToBye());
                        }
                        else{
                            // remove basket
                            removeBasketFromDB(basket);
                            basketsList.remove(position);
                            notifyDataSetChanged();
                        }
                    }
                });
                builder.show();
            }
        });

    }

    private void openProductsList(String basketID, HashMap<Product, Integer> listToBye) {
       // show alert list dialog with custom product adapter
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
