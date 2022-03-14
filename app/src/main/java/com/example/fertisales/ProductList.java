package com.example.fertisales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.fertisales.databinding.ActivityProductListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductList extends AppCompatActivity {
    private ArrayList<String> productList, quantityList, price;
    ActivityProductListBinding productListBinding;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productListBinding = ActivityProductListBinding.inflate(getLayoutInflater());
        setContentView(productListBinding.getRoot());
        productList = new ArrayList<>();
        quantityList = new ArrayList<>();
        price = new ArrayList<>();

        final String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference(userUID);
        productListBinding.productListRV.setLayoutManager(new LinearLayoutManager(this));
        myRef.orderByChild("productName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {
                    productList.add(data.child("productName").getValue().toString());
                    quantityList.add(data.child("quantity").getValue().toString());
                    price.add(data.child("sellPrice").getValue().toString());
                }
                productListBinding.productListRV.setAdapter(new ProductListAdapter(ProductList.this, userUID, productList, quantityList, price));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductList.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}