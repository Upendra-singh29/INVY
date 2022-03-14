package com.example.fertisales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fertisales.databinding.ActivitySupplierListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SupplierList extends AppCompatActivity {
    ArrayList<String> supplierName,supplierPhone;
    ActivitySupplierListBinding supplierListBinding;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supplierListBinding = ActivitySupplierListBinding.inflate(getLayoutInflater());
        View view = supplierListBinding.getRoot();
        setContentView(view);
        supplierName = new ArrayList<>();
        supplierPhone = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        supplierListBinding.supplierListRV.setLayoutManager(new LinearLayoutManager(this));

        myRef.orderByChild("supplierName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    supplierName.add(data.child("supplierName").getValue().toString());
                    supplierPhone.add(data.child("supplierNumber").getValue().toString());
                }
                supplierListBinding.supplierListRV.setAdapter(new SupplierListAdapter(SupplierList.this ,supplierName, supplierPhone));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SupplierList.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}