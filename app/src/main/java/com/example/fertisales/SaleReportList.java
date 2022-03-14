package com.example.fertisales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fertisales.databinding.ActivitySaleReportListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class SaleReportList extends AppCompatActivity {

    ActivitySaleReportListBinding reportListBinding;
    long milliF, milliT;
    DatabaseReference myRef;
    ArrayList<String> customerName, quantity, finalPrice, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportListBinding = ActivitySaleReportListBinding.inflate(getLayoutInflater());
        setContentView(reportListBinding.getRoot());

        customerName = new ArrayList<>();
        quantity = new ArrayList<>();
        finalPrice = new ArrayList<>();
        date = new ArrayList<>();

        customerName.add(" Name  ");
        quantity.add(":  Quan.  :  ");
        finalPrice.add("Trans.  :  ");
        date.add("Date-Time ");
        myRef = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            milliF = (long) bundle.get("dateF");
            milliT = (long) bundle.get("dateT");
        }

        reportListBinding.saleReportRV.setLayoutManager(new LinearLayoutManager(this));

        myRef.orderByChild("customers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    for (DataSnapshot data1 : data.child("customers").getChildren()) {

                        long tempMilli = (long) data1.child("soldAt").getValue();

                        if (tempMilli <= milliT && tempMilli >= milliF) {
                            customerName.add(" " + data1.child("name").getValue().toString());
                            quantity.add("  :  " + data1.child("quantity").getValue().toString() + "  :  ");
                            finalPrice.add(data1.child("finalPrice").getValue().toString() + "  :  ");

                            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(tempMilli);
                            String temp = formatter.format(calendar.getTime());

                            date.add(temp + " ");
                        }
                    }
                }
                reportListBinding.saleReportRV.setAdapter(new SaleReportAdapter(customerName, quantity, finalPrice, date));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SaleReportList.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}