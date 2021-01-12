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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    String userID;
    Context context;
    List<BasketItem> itemsList;
    String basketID;
    Boolean isAdmin, isAddItems, isMyItems;

    public ItemAdapter(Context context, List<BasketItem> itemsList, String basketID, Boolean isAdmin, Boolean isAddItems, Boolean isMyItems) {
        this.context = context;
        this.itemsList = itemsList;
        this.basketID = basketID;
        this.isAdmin = isAdmin;
        this.isAddItems = isAddItems;
        this.isMyItems = isMyItems;
        this.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
            if(isMyItems){
                int priceForMe = Integer.parseInt(item.getItem().getPrice()) * Integer.parseInt(item.getBuyersAndQuantities().get(userID));
                String price =  "price: " + priceForMe;
                holder.typeTv.setText(price);
                String q = "quantity to buy: " + item.getBuyersAndQuantities().get(userID);
                holder.priceTv.setText(q);
            }
            else{
                String price =  "price: " + item.getItem().getPrice();
                holder.typeTv.setText(price);
                String q = "quantity to buy: " + item.getQuantity();
                holder.priceTv.setText(q);
            }
            // for items in basket
            int reservedQuantity = 0;
            if(item.getBuyersAndQuantities() != null){
                for (String value: item.getBuyersAndQuantities().values()) {
                    reservedQuantity += Integer.parseInt(value);
                }
            }

            if(reservedQuantity != Integer.parseInt(item.getQuantity())){
                // if still not all items reserved change color
                holder.card.setCardBackgroundColor(Color.CYAN);
            }
            //ToDO: items settings / deleting options
            holder.settingsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isAdmin){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        String[]options = new String[]{"Change total quantity", "Change quantity you'll buy"};
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    // true for total changes
                                    getQuantityDialog(item, true);
                                }
                                if(which == 1){
                                    // false for private changes
                                    getQuantityDialog(item, false);
                                }
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }
                    else{
                        getQuantityDialog(item, false);
                    }
                }
            });
            holder.deleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isAdmin){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        String[]options = new String[]{"Remove from basket", "Remove from my basket"};
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    removeItemFromBasket(item);
                                }
                                if(which == 1){
                                    removeItemFromPersonalBasket(item);
                                }
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }
                    else{
                        removeItemFromPersonalBasket(item);
                    }
                }
            });

        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAddItems){
                    if(isAdmin){
                        getQuantityDialog(item, true);
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

    private void removeItemFromBasket(BasketItem item) {
        FirebaseDatabase.getInstance().getReference("Baskets")
                .child(basketID).child("listToBye").child(item.getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        changeBasketTotalPrice();
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void changePersonalQuantity(BasketItem item, String newQuantity) {
        if(!newQuantity.isEmpty() && (Integer.parseInt(newQuantity) <= Integer.parseInt(item.getQuantity()))) {
            FirebaseDatabase.getInstance().getReference("Baskets")
                    .child(basketID).child("listToBye").child(item.getId()).child("buyersAndQuantities").child(userID).setValue(newQuantity)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, item.getItem().getName() +" changed", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if(newQuantity.equals("0")){
            removeItemFromBasket(item);
        }
        else  Toast.makeText(context, "quantity not changed", Toast.LENGTH_SHORT).show();
    }

    private void changeTotalQuantity(BasketItem item, String newQuantity) {
        if(!newQuantity.isEmpty()) {
            DatabaseReference basketRef = FirebaseDatabase.getInstance().getReference("Baskets");
            basketRef.child(basketID).child("listToBye").child(item.getId()).child("quantity").setValue(newQuantity)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            changeBasketTotalPrice();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if(newQuantity.equals("0")){
            removeItemFromBasket(item);
        }
        else  Toast.makeText(context, "quantity not changed", Toast.LENGTH_SHORT).show();
    }

    private void removeItemFromPersonalBasket(BasketItem item) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("Baskets")
                .child(basketID).child("listToBye").child(item.getId()).child("buyersAndQuantities").child(userID).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, item.getItem().getName() +" removed", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getQuantityDialog(BasketItem item, boolean isTotalChanges){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Set quantity:");
        final EditText quantity = new EditText(context);
        quantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(quantity);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newQuantity = "" + quantity.getText().toString();
                if(!newQuantity.isEmpty()){
                    if(isAddItems){
                        item.getItem().setCreator(basketID);
                        item.setQuantity(newQuantity);
                        addItemToBasket(item);
                    }
                    else{
                        if(isTotalChanges) changeTotalQuantity(item, newQuantity);
                        else changePersonalQuantity(item, newQuantity);
                    }
                }
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

    private void addItemToBasket(BasketItem item) {
        //ToDo add item to basket & delete from list view
        DatabaseReference newID = FirebaseDatabase.getInstance().getReference("Baskets").child(basketID).child("listToBye").push();
        item.setId(newID.getKey());
        newID.setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                changeBasketTotalPrice();
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

    private void changeBasketTotalPrice() {
        DatabaseReference basketRef = FirebaseDatabase.getInstance().getReference("Baskets");
        basketRef.child(basketID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Basket basket = dataSnapshot.getValue(Basket.class);
                    int total = 0;
                    if(basket.getListToBye() != null){
                        for (BasketItem item: basket.getListToBye().values()) {
                        total += Integer.parseInt(item.getQuantity()) * Integer.parseInt(item.getItem().getPrice());
                        }
                    }
                    else total = 0;
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
