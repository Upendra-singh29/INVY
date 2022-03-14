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
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.fertisales.databinding.ActivityAddStockBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Add_Stock extends AppCompatActivity {

    ActivityAddStockBinding addStockBinding;
    private DatabaseReference myRef;
    private FirebaseAuth firebaseAuth;
    private ArrayList<String> chemicalName,supplierName,supplierPhone;
    private int quantity = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__stock);

        addStockBinding = ActivityAddStockBinding.inflate(getLayoutInflater());
        View view = addStockBinding.getRoot();
        setContentView(view);      //todo : when supplier name inserted auto add phone no.
        //todo : when product name inserted auto add chemical name

        chemicalName = new ArrayList<>();
        supplierName = new ArrayList<>();
        supplierPhone = new ArrayList<>();

        addStockBinding.PPPPET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String tempPPPPET = s.toString();
                    if (!tempPPPPET.equals("")) {
                        if (!TextUtils.isEmpty(addStockBinding.quantityET.getText().toString())) {
                            int tempFinalAmount = Integer.parseInt(tempPPPPET) * quantity;
                            addStockBinding.finalAmountET.setText(Integer.toString(tempFinalAmount));

                            int tempPPPPETint = Integer.parseInt(tempPPPPET);
                            int price10 = (tempPPPPETint / 10) + tempPPPPETint;
                            int price20 = (tempPPPPETint / 5) + tempPPPPETint;
                            int price30 = ((tempPPPPETint * 3) / 10) + tempPPPPETint;
                            addStockBinding.PTSPPLayout.setHelperText(
                                    "At 10%-" + price10 + "(TOPO-" + (price10 - tempPPPPETint) * quantity + ")"
                                            + ". 20%-" + price20 + "(TOPO-" + (price20 - tempPPPPETint) * quantity + ")"
                                            + ". 30%-" + price30 + "(TOPO-" + (price30 - tempPPPPETint) * quantity + ")");
                        } else {
                            addStockBinding.quantityET.setError("Put some quantity for recommendation.");
                            //addStockBinding.quantityET.setFocusable(true);
                            addStockBinding.quantityET.requestFocus();
                        }
                    }
                }
            });

        addStockBinding.quantityET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("0"))
                    addStockBinding.quantityET.setError("Quantity can't be zero.");
                else if (!s.toString().equals(""))
                    quantity = Integer.parseInt(s.toString());

            }
        });

        addStockBinding.supplierNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (supplierName.contains(s.toString())) {
                    addStockBinding.supplierNumberET.setText(supplierPhone.get(supplierName.indexOf(s.toString())));
                }
                else{
                    addStockBinding.supplierNumberET.setText(null);
                }
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String supplierNumber = bundle.get("suppNum").toString();
            String supplierName = bundle.get("suppName").toString();
            addStockBinding.supplierNameET.setText(supplierName);
            addStockBinding.supplierNumberET.setText(supplierNumber);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (isConnected()) {
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            updateUI(currentUser);
        } else {
            Toast.makeText(this, "Connect to Internet", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            myRef = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
            myRef.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (!chemicalName.contains(data.child("chemicalName").getValue().toString())) {
                            chemicalName.add(data.child("chemicalName").getValue().toString());
                        }
                        if (!supplierName.contains(data.child("supplierName").getValue().toString())) {
                            supplierName.add(data.child("supplierName").getValue().toString());
                        }
                        if (!supplierPhone.contains(data.child("supplierNumber").getValue().toString())) {
                            supplierPhone.add(data.child("supplierNumber").getValue().toString());
                        }
                    }
                    chemicalList();
                    supplierList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Add_Stock.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void supplierList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, supplierName);
        addStockBinding.supplierNameET.setAdapter(adapter);
    }

    private void chemicalList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chemicalName);
        addStockBinding.chemicalNameET.setAdapter(adapter);
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void addingStock(View view) {

        final String tempSupplierName = addStockBinding.supplierNameET.getText().toString().trim();
        final String tempSupplierNumber = addStockBinding.supplierNumberET.getText().toString().trim();
        final String tempProductName = addStockBinding.productNameET.getText().toString().trim();
        final String tempChemicalName = addStockBinding.chemicalNameET.getText().toString().trim();
        final String tempQuantity = addStockBinding.quantityET.getText().toString().trim();
        final String tempPPPPET = addStockBinding.PPPPET.getText().toString().trim();
        final String tempPTSPPET = addStockBinding.PTSPPET.getText().toString().trim();
        final String tempMRPET = addStockBinding.MRPET.getText().toString().trim();
        final String tempFinalAmount = addStockBinding.finalAmountET.getText().toString().trim();
        if (!tempSupplierName.equals("") && !tempSupplierNumber.equals("") && !tempProductName.equals("")
                && !tempChemicalName.equals("") && !tempQuantity.equals("") && !tempPPPPET.equals("")
                && !tempPTSPPET.equals("") && !tempMRPET.equals("")) {
            Stock stock = new Stock(
                    tempSupplierName
                    , Integer.parseInt(tempSupplierNumber)
                    , tempProductName
                    , tempChemicalName
                    , Integer.parseInt(tempQuantity)
                    , Integer.parseInt(tempPPPPET)
                    , Integer.parseInt(tempPTSPPET)
                    , Integer.parseInt(tempMRPET)
                    , Integer.parseInt(tempFinalAmount)
                    , ServerValue.TIMESTAMP);

            myRef.push().setValue(stock);

            Toast.makeText(this, "Stock Added", Toast.LENGTH_SHORT).show();

            finish();
        } else {
            Toast.makeText(this, "Fill All Details", Toast.LENGTH_SHORT).show();
        }
    }

    public void addingMoreStock(View view) {
        final String tempSupplierName = addStockBinding.supplierNameET.getText().toString().trim();
        final String tempSupplierNumber = addStockBinding.supplierNumberET.getText().toString().trim();
        final String tempProductName = addStockBinding.productNameET.getText().toString().trim();
        final String tempChemicalName = addStockBinding.chemicalNameET.getText().toString().trim();
        final String tempQuantity = addStockBinding.quantityET.getText().toString().trim();
        final String tempPPPPET = addStockBinding.PPPPET.getText().toString().trim();
        final String tempPTSPPET = addStockBinding.PTSPPET.getText().toString().trim();
        final String tempMRPET = addStockBinding.MRPET.getText().toString().trim();
        final String tempFinalAmount = addStockBinding.finalAmountET.getText().toString().trim();
        if (!tempSupplierName.equals("") && !tempSupplierNumber.equals("") && !tempProductName.equals("")
                && !tempChemicalName.equals("") && !tempQuantity.equals("") && !tempPPPPET.equals("")
                && !tempPTSPPET.equals("") && !tempMRPET.equals("")) {
            Stock stock = new Stock(
                    tempSupplierName
                    , Integer.parseInt(tempSupplierNumber)
                    , tempProductName
                    , tempChemicalName
                    , Integer.parseInt(tempQuantity)
                    , Integer.parseInt(tempPPPPET)
                    , Integer.parseInt(tempPTSPPET)
                    , Integer.parseInt(tempMRPET)
                    , Integer.parseInt(tempFinalAmount)
                    , ServerValue.TIMESTAMP);

            myRef.push().setValue(stock);

            Toast.makeText(this, "Stock Added", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Add_Stock.this, Add_Stock.class);
            intent.putExtra("suppName", tempSupplierName).putExtra("suppNum", tempSupplierNumber);
            startActivity(intent);

            finish(); //
        } else {
            Toast.makeText(this, "Fill All Details", Toast.LENGTH_SHORT).show();
        }
    }

    public void editPrevious(View view) {
        myRef.orderByChild("timeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (final DataSnapshot data : snapshot.getChildren()) {
                    i++;
                    if (i == snapshot.getChildrenCount()) {

                        final String tempSaleNumber = data.child("numSales").getValue().toString();

                        if (Integer.parseInt(tempSaleNumber) > 0) {
                            final String tempSupplierName = data.child("supplierName").getValue().toString();
                            final String tempSupplierNumber = data.child("supplierNumber").getValue().toString();
                            final String tempProductName = data.child("productName").getValue().toString();
                            final String tempChemicalName = data.child("chemicalName").getValue().toString();
                            final String tempQuantity = data.child("quantity").getValue().toString();
                            final String tempPPPPET = data.child("paidPrice").getValue().toString();
                            final String tempPTSPPET = data.child("sellPrice").getValue().toString();
                            final String tempMRPET = data.child("mrp").getValue().toString();

                            AlertDialog.Builder builder = new AlertDialog.Builder(Add_Stock.this);
                            builder.setTitle("Edit Last Record");
                            builder.setMessage("Are you sure you want edit previous record, when some items are already sold?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    addStockBinding.supplierNameET.setText(tempSupplierName);
                                    addStockBinding.supplierNumberET.setText(tempSupplierNumber);
                                    addStockBinding.productNameET.setText(tempProductName);
                                    addStockBinding.chemicalNameET.setText(tempChemicalName);
                                    addStockBinding.quantityET.setText(tempQuantity);
                                    addStockBinding.PPPPET.setText(tempPPPPET);
                                    addStockBinding.PTSPPET.setText(tempPTSPPET);
                                    addStockBinding.MRPET.setText(tempMRPET);

                                    myRef.child(data.getKey()).removeValue();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                            builder.show();
                        } else {
                            final String tempSupplierName = data.child("supplierName").getValue().toString();
                            final String tempSupplierNumber = data.child("supplierNumber").getValue().toString();
                            final String tempProductName = data.child("productName").getValue().toString();
                            final String tempChemicalName = data.child("chemicalName").getValue().toString();
                            final String tempQuantity = data.child("quantity").getValue().toString();
                            final String tempPPPPET = data.child("paidPrice").getValue().toString();
                            final String tempPTSPPET = data.child("sellPrice").getValue().toString();
                            final String tempMRPET = data.child("mrp").getValue().toString();

                            AlertDialog.Builder builder = new AlertDialog.Builder(Add_Stock.this);
                            builder.setTitle("Edit Last Record");
                            builder.setMessage("Are you sure you want edit previous record?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    addStockBinding.supplierNameET.setText(tempSupplierName);
                                    addStockBinding.supplierNumberET.setText(tempSupplierNumber);
                                    addStockBinding.productNameET.setText(tempProductName);
                                    addStockBinding.chemicalNameET.setText(tempChemicalName);
                                    addStockBinding.quantityET.setText(tempQuantity);
                                    addStockBinding.PPPPET.setText(tempPPPPET);
                                    addStockBinding.PTSPPET.setText(tempPTSPPET);
                                    addStockBinding.MRPET.setText(tempMRPET);

                                    myRef.child(data.getKey()).removeValue();
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Add_Stock.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}