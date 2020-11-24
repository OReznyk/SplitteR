package com.splitter.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.splitter.R;

import java.util.HashMap;

public class NewProductActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    EditText type, title, price, description;
    Button saveBtn;
    ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        initView();
    }

    private void initView() {
        //ToDo: save img for product
        img = findViewById(R.id.item_imgIv);
        type = findViewById(R.id.item_type);
        title = findViewById(R.id.item_titleIv);
        price = findViewById(R.id.item_priceIv);
        description = findViewById(R.id.item_descriptionIv);
        saveBtn = findViewById(R.id.item_saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDO: set imgListener
                final String pImg = "";
                final String pType = type.getText().toString();
                final String pTitle = title.getText().toString();
                String pPrice = price.getText().toString();
                final String pDescription = description.getText().toString();
                //ToDo: check type with regex! only chars allowed
                if (TextUtils.isEmpty(pType)) {
                    type.setError("Type of item is Required in one word");
                    type.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(pTitle)) {
                    title.setError("Title is Required");
                    title.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(pPrice)) {
                    price.setError("Setting price to 0");
                    pPrice = "0";
                    return;
                }
                saveProductToFirebase(pImg, pType, pTitle, pPrice, pDescription);
                //Important: You can change this to create products from other activities besides Basket
                startActivity(new Intent(getApplicationContext(), NewBasketActivity.class));
            }
        });
    }

    private void saveProductToFirebase(String img, String pType, String pTitle, String pPrice, String pDescription) {
        //ToDo: Do We want to save the creator of item or save "items by types" in users data?
        FirebaseDatabase fDb = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = fDb.getReference("ItemsByTypes" + pType);
        DatabaseReference newItemID = dbRef.push();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", newItemID.toString());
        hashMap.put("name", title);
        hashMap.put("price", price);
        hashMap.put("description", "noOne");
        hashMap.put("img", img);
        hashMap.put("buyer", "");
        newItemID.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //to log failure
                //Log.d(TAG, "Item is not created for "+ "id "+ e.getMessage());
            }
        });
    }
}
