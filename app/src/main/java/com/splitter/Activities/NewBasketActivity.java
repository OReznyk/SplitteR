package com.splitter.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.splitter.Model.Product;
import com.splitter.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewBasketActivity  extends AppCompatActivity {
    private static final String TAG = "TAG";
    EditText title;
    DatePicker date;
    TimePicker time;
    Button saveBtn;
    FloatingActionButton addItemBtn;
    List<Product> items;
    SearchView searchView;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_basket);
        initView();
    }
    private void initView() {
        items = new ArrayList();
        title = findViewById(R.id.basket_titleIV);
        date = findViewById(R.id.basket_dateIv);
        time = findViewById(R.id.basket_timeIv);
        saveBtn = findViewById(R.id.basket_saveBtn);
        addItemBtn = findViewById(R.id.basket_addItem);

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewProductActivity.class));
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bTitle = title.getText().toString();
                //ToDo: get date and time
                String bDateTime = date + " " + time;
                //ToDo: check date and time to be future
                if (TextUtils.isEmpty(bTitle)) {
                    title.setError("Title is Required");
                    title.setFocusable(true);
                    return;
                }
                saveBasketToFirebase(bTitle, bDateTime, items);
                //Important: You can change this to create products from other activities besides Basket
                startActivity(new Intent(getApplicationContext(), NewBasketActivity.class));
            }
        });
    }
    //ToDo:finish it
    private void saveBasketToFirebase(String bTitle, String bDateTime, List<Product> items) {
        //ToDo: Do We want to save the creator of item or save "items by types" in users data?
        FirebaseDatabase fDb = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = fDb.getReference("Baskets" );
        DatabaseReference newID = dbRef.push();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", newID.toString());
        hashMap.put("title", title);
        hashMap.put("date", date);
        hashMap.put("time", time);
        newID.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
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
