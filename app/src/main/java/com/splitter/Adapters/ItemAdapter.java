package com.splitter.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.splitter.Model.Basket;
import com.splitter.Model.BasketItem;
import com.splitter.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyHolder> {
    Context context;
    List<BasketItem> itemsList;
    String basketID;
    Boolean isAdmin, isAddItems;

    public ItemAdapter(Context context, List<BasketItem> basketItemsList, String basketID, Boolean isAdmin, Boolean isAddItems) {
        this.context = context;
        this.itemsList = basketItemsList;
        this.basketID = basketID;
        this.isAddItems = isAddItems;
        this.isAdmin = isAdmin;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        CardView card;
        ImageView imgTv, settingsTv, deleteTv;
        TextView titleTv, typeTv, priceTv;

        public MyHolder(@NonNull View view) {
            super(view);
            card = view.findViewById(R.id.item_card);
            imgTv = view.findViewById(R.id.item_imgIv);
            settingsTv = view.findViewById(R.id.item_settingsTv);
            deleteTv = view.findViewById(R.id.item_deleteTv);
            typeTv = view.findViewById(R.id.item_typeTv);
            titleTv = view.findViewById(R.id.item_title_Tv);
            priceTv = view.findViewById(R.id.item_priceTv);

        }
    }
    @NonNull
    @Override
    public ItemAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_row_item, parent, false);
        return new ItemAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.MyHolder holder, int position) {
        BasketItem item = itemsList.get(position);
        holder.titleTv.setText(item.getItem().getName());
        try {
            Picasso.get().load(item.getItem().getImg())
                    .placeholder(R.drawable.ic_item)
                    .into(holder.imgTv);
        } catch (Exception e) {
        }
        if(isAddItems){

            String q = "type: " + item.getItem().getType();
            String price =  "price: " + item.getItem().getPrice();
            holder.typeTv.setText(q);
            holder.priceTv.setText(price);
            holder.settingsTv.setVisibility(View.GONE);
            holder.deleteTv.setVisibility(View.GONE);
        }
        else{
            // for items in basket
            int reservedQuantity = 0;
            if(item.getBuyersAndQuantities() != null){
                for (String value: item.getBuyersAndQuantities().values()) {
                    reservedQuantity += Integer.parseInt(value);
                }
            }
            String price =  "price: " + item.getItem().getPrice();
            holder.typeTv.setText(price);
            String q = "quantity to buy: " + item.getItem().getPrice();
            holder.priceTv.setText(q);

            if(reservedQuantity != Integer.parseInt(item.getQuantity())){
                // if still not all items reserved change color
                holder.card.setCardBackgroundColor(Color.CYAN);
            }
            //ToDO: items settings / deleting options
            if(isAdmin){

            }
            else{

            }
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAddItems){
                    if(isAdmin){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Set quantity:");
                        final EditText quantity = new EditText(context);
                        quantity.setInputType(InputType.TYPE_CLASS_NUMBER);
                        builder.setView(quantity);
                        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String q = quantity.getText().toString();
                                item.getItem().setCreator(basketID);
                                item.setQuantity(q);
                                addItemToBasket(item);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                    else{
                        //no admin permissions
                        Toast.makeText(context, "Only admins can add items", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    //for basket items settings
                    String [] dialogOptions;
                    if(isAdmin){
                        //add remove option to dialog
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                }
            }
        });
    }

    private void addItemToBasket(BasketItem item) {
        //ToDo add item to basket & delete from list view
        DatabaseReference newID = FirebaseDatabase.getInstance().getReference("Baskets").child(basketID).child("listToBye").push();
        item.setId(newID.getKey());
        newID.setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                changeBasketTotalPrice(item);
                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                itemsList.remove(item);
                notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Item not added "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeBasketTotalPrice(BasketItem item) {
        int addedPrice = Integer.parseInt(item.getQuantity()) * Integer.parseInt(item.getItem().getPrice());
        DatabaseReference basketRef = FirebaseDatabase.getInstance().getReference("Baskets");
        basketRef.child(basketID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Basket basket = dataSnapshot.getValue(Basket.class);
                    int total = Integer.parseInt(basket.getTotalPrice()) + addedPrice;
                    basket.setTotalPrice(String.valueOf(total));
                    basketRef.child(basketID).setValue(basket);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
}
