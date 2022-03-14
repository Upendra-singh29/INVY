package com.example.fertisales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.fertisales.databinding.ActivitySaleToFarmerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sale_To_Farmer extends AppCompatActivity {
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private ActivitySaleToFarmerBinding saleToFarmerBinding;
    private String price;
    private int quanFinal;
    private int discFinal;
    private int quanAvailable = 0;
    private ArrayList<String> product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale__to__farmer);

        saleToFarmerBinding = ActivitySaleToFarmerBinding.inflate(getLayoutInflater());
        View view = saleToFarmerBinding.getRoot();
        setContentView(view);
        saleToFarmerBinding.discountET.setText("0");
        product = new ArrayList<>();

        saleToFarmerBinding.productNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().equals("")) {
                    if (product.contains(s.toString())) {
                        saleToFarmerBinding.productNameLayout.setError("");
                        myRef.orderByChild("productName")
                                .equalTo(s.toString())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                            quanAvailable = Integer.parseInt(data.child("quantity").getValue().toString());
                                                price = data.child("sellPrice").getValue().toString();

                                                saleToFarmerBinding.priceET.setText(price);
                                                saleToFarmerBinding.discountET.setText("0");
                                                if (!TextUtils.isEmpty(saleToFarmerBinding.quantityET.getText().toString())) {
                                                    saleToFarmerBinding.quantityET.setText("0");
                                                }
                                            }
                                            saleToFarmerBinding.quantityLayout.setHelperText("Quantity Available : " + quanAvailable);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(Sale_To_Farmer.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        saleToFarmerBinding.productNameLayout.setError("Select from List");
                    }
                }
            }
        });

        saleToFarmerBinding.quantityET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    quanFinal = Integer.parseInt(price) * Integer.parseInt(s.toString());//todo : add option for selecting per piece or from total
                    saleToFarmerBinding.finalPriceET.setText(Integer.toString(quanFinal - Integer.parseInt(saleToFarmerBinding.discountET.getText().toString())));
                    if (Integer.parseInt(s.toString()) > quanAvailable)
                        saleToFarmerBinding.quantityLayout.setError("limit exceeded");
                    else
                        saleToFarmerBinding.quantityLayout.setError("");
                } else
                    saleToFarmerBinding.finalPriceET.setText("0");
            }
        });

        saleToFarmerBinding.discountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    discFinal = quanFinal - Integer.parseInt(s.toString());
                    //price = Integer.toString(fullFinal);
                    saleToFarmerBinding.finalPriceET.setText(Integer.toString(discFinal));
                } else
                    saleToFarmerBinding.discountET.setText("0");//todo : do something creative
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String cust = bundle.get("custName").toString();
            if (!cust.equals(""))
                saleToFarmerBinding.customerNameET.setText(cust);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (isConnected()) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        } else {
            Toast.makeText(this, "Connect to Internet", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //finish();
        } else {
            //mAuth = FirebaseAuth.getInstance();
            myRef = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()); //todo remove Error

            myRef.orderByChild("productName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        int tempQuantity = Integer.parseInt(data.child("quantity").getValue().toString());
                        if (tempQuantity > 0) {
                            product.add(data.child("productName").getValue().toString());
                        }

                    }
                    addListPN();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Sale_To_Farmer.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    public void addListPN() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, product);
        saleToFarmerBinding.productNameET.setAdapter(adapter);
    }

    public void Sold(View view) {
        final String tempProductName = saleToFarmerBinding.productNameET.getText().toString().trim();
        final String tempCustomerName = saleToFarmerBinding.customerNameET.getText().toString().trim();
        final String tempQuantity = saleToFarmerBinding.quantityET.getText().toString().trim();
        final String tempDiscount = saleToFarmerBinding.discountET.getText().toString().trim();
        final String tempFinalPrice = saleToFarmerBinding.finalPriceET.getText().toString().trim();
        final String tempPrice = saleToFarmerBinding.priceET.getText().toString();
        if (!tempCustomerName.equals("") && !tempProductName.equals("") && !tempQuantity.equals(""))
            myRef.orderByChild("productName").equalTo(tempProductName).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                if (tempPrice.equals(data.child("sellPrice").getValue().toString())) {

                                    Map<String, Object> stock = (Map<String, Object>) data.getValue();
                                    Map<String, Object> userData = new HashMap<>();
                                    Map<String, Object> customer = new HashMap<>();
                                    Map<String, String> time = ServerValue.TIMESTAMP;

                                    int lc = Integer.parseInt(stock.get("numSales").toString()) + 1;

                                    customer.put("name", tempCustomerName);
                                    customer.put("quantity", tempQuantity);
                                    customer.put("discount", tempDiscount);
                                    customer.put("finalPrice", tempFinalPrice);
                                    customer.put("soldAt", time);

                                    userData.put(data.getKey() + "/quantity", Integer.parseInt(stock.get("quantity").toString()) - Integer.parseInt(tempQuantity));
                                    userData.put(data.getKey() + "/numSales", lc);
                                    userData.put(data.getKey() + "/customers/" + lc, customer);

                                    myRef.updateChildren(userData);
                                    Toast.makeText(Sale_To_Farmer.this, "Sold", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Sale_To_Farmer.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        else
            Toast.makeText(this, "Fill All Details", Toast.LENGTH_SHORT).show();
    }

    public void moreToSell(View view) {
        final String tempProductName = saleToFarmerBinding.productNameET.getText().toString();
        final String tempCustomerName = saleToFarmerBinding.customerNameET.getText().toString();
        final String tempQuantity = saleToFarmerBinding.quantityET.getText().toString();
        final String tempDiscount = saleToFarmerBinding.discountET.getText().toString();
        final String tempFinalPrice = saleToFarmerBinding.finalPriceET.getText().toString();
        if (!tempCustomerName.equals("") && !tempProductName.equals("") && !tempQuantity.equals(""))
            myRef.orderByChild("productName").equalTo(tempProductName).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {

                                Map<String, Object> stock = (Map<String, Object>) data.getValue();
                                Map<String, Object> userData = new HashMap<>();
                                Map<String, Object> customer = new HashMap<>();
                                Map<String, String> time = ServerValue.TIMESTAMP;

                                int lc = Integer.parseInt(stock.get("numSales").toString()) + 1;

                                customer.put("name", tempCustomerName);
                                customer.put("quantity", tempQuantity);
                                customer.put("discount", tempDiscount);
                                customer.put("finalPrice", tempFinalPrice);
                                customer.put("soldAt", time);
                                userData.put(data.getKey() + "/quantity", Integer.parseInt(stock.get("quantity").toString()) - Integer.parseInt(tempQuantity));
                                userData.put(data.getKey() + "/numSales", lc);
                                userData.put(data.getKey() + "/customers/" + lc, customer);

                                myRef.updateChildren(userData);

                                Toast.makeText(Sale_To_Farmer.this, "Sold", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(Sale_To_Farmer.this, Sale_To_Farmer.class);
                                intent.putExtra("custName", tempCustomerName);
                                startActivity(intent);
                                finish();
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Sale_To_Farmer.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        else
            Toast.makeText(this, "Fill All Details", Toast.LENGTH_SHORT).show();
    }

    public void editLast(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Sale_To_Farmer.this);
        builder.setTitle("Edit Previous Sale");
        builder.setMessage("Are you sure you want to edit your last sold item?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myRef.orderByChild("timeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String key = null, key1 = null;
                        long tempele = 0;
                        List<Long> longList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String tempNumSales = data.child("numSales").getValue().toString();
                            if (!tempNumSales.equals("0")) {
                                tempele = (long) data.child("customers/" + tempNumSales + "/soldAt").getValue();
                                longList.add(tempele);

                                if (tempele == Collections.max(longList)) {
                                    key = data.getKey();
                                    key1 = tempNumSales;
                                }
                            }
                        }
                        String tempCustomerName = snapshot.child(key + "/customers/" + key1 + "/name").getValue().toString();
                        String tempProductName = snapshot.child(key + "/productName").getValue().toString();

                        int tempQuantity = Integer.parseInt(snapshot.child(key + "/customers/" + key1 + "/quantity").getValue().toString());
                        int tempProductQuantity = Integer.parseInt(snapshot.child(key + "/quantity").getValue().toString());

                        saleToFarmerBinding.customerNameET.setText(tempCustomerName);
                        saleToFarmerBinding.productNameET.setText(tempProductName);

                        myRef.child(key + "/quantity").setValue(Integer.toString(tempProductQuantity + tempQuantity));

                        myRef.child(key + "/numSales").setValue(Integer.toString(Integer.parseInt(key1) - 1));

                        myRef.child(key + "/customers/" + key1).removeValue();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Sale_To_Farmer.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }
}