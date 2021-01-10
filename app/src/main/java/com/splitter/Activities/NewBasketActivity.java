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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.splitter.Model.Basket;
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
    HashMap<Product , Integer> items;
    List<String> adminsID;
    //ToDo work on search view
    SearchView searchView;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_basket);
        initView();
    }
    private void initView() {
        items = new HashMap<>();
        title = findViewById(R.id.basket_titleIV);
        /*date = findViewById(R.id.basket_dateIv);
        time = findViewById(R.id.basket_timeIv);*/
        saveBtn = findViewById(R.id.basket_saveBtn);
        addItemBtn = findViewById(R.id.basket_addItem);
        recyclerView = findViewById(R.id.basket_recyclerView);

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NewBasketActivity.this, NewProductActivity.class);
                startActivityForResult(i, 1);
                //ToDo:add item to HashMap
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bTitle = title.getText().toString();
                //ToDo: get date and time
                //String bDateTime = date + " " + time;
                //ToDo: check date and time to be future
                if (TextUtils.isEmpty(bTitle)) {
                    title.setError("Title is Required");
                    title.setFocusable(true);
                    return;
                }
                saveBasketToFirebase(bTitle, items);
                //Important: You can change this to create products from other activities besides Basket startActivity(new Intent(getApplicationContext(), NewBasketActivity.class));
            }
        });
    }
    //ToDo:finish it
    private void saveBasketToFirebase(String bTitle, HashMap<Product, Integer> items) {
        //ToDo: Do We want to save the creator of item or save "items by types" in users data?
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Baskets" );
        DatabaseReference newID = dbRef.push();
        Intent intent = getIntent();
        String parentID = intent.getStringExtra("parentID");
        String userID = intent.getStringExtra("userID");
        adminsID = new ArrayList<>();
        adminsID.add(userID);
        Basket basket = new Basket(newID.getKey(), parentID, bTitle, adminsID, items);
        newID.setValue(basket).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent=new Intent();
                intent.putExtra("basketID", newID.getKey());
                intent.putExtra("basketTitle", basket.getTitle());
                setResult(1,intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewBasketActivity.this, "basket is not added" , Toast.LENGTH_LONG).show();
            }
        });
    }
    //To get item
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK){
                String itemID = data.getStringExtra("item");
                //ToDo: find item in dataBase and put it to recyclerView + get numOfItems to buy
            }
            else{
                Toast.makeText(NewBasketActivity.this,"Item not saved. Please try again", Toast.LENGTH_LONG).show();
            }
        }
    }

}
