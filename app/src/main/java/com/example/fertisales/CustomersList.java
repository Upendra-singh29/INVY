package com.example.fertisales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fertisales.databinding.ActivityCustomersListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomersList extends AppCompatActivity {
    ActivityCustomersListBinding customersListBinding;
    DatabaseReference myRef;
    ArrayList<String> customersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customersListBinding = ActivityCustomersListBinding.inflate(getLayoutInflater());
        setContentView(customersListBinding.getRoot());
        myRef = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        customersList = new ArrayList<>();
        customersListBinding.customersListRV.setLayoutManager(new LinearLayoutManager(this));
        myRef.orderByChild("customers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    for (DataSnapshot data1 : data.child("customers").getChildren()){
                            customersList.add(data1.child("name").getValue().toString());
                        }
                }
                customersListBinding.customersListRV.setAdapter(new CustomersAdapter(customersList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomersList.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}