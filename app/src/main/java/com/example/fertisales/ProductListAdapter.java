package com.example.fertisales;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {
    ArrayList<String> products, quantity, price;
    Context myContext;
    private DatabaseReference myRef;
    public ProductListAdapter(Context myContext, String userUID, ArrayList<String> products, ArrayList<String> quantity, ArrayList<String> price) {
        this.products = products;
        this.quantity = quantity;
        this.price = price;
        this.myContext = myContext;
        myRef = FirebaseDatabase.getInstance().getReference(userUID);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View view = li.inflate(R.layout.product_row, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, final int position) {
        holder.textView1.setText(products.get(position));
        String txt = quantity.get(position) + "  :  " + price.get(position);
        holder.textView2.setText(txt);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(myContext);

                final EditText editText = new EditText(myContext);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setHint("Enter New Price");
                builder.setTitle("Edit Price of " + products.get(position));
                builder.setView(editText);

                builder.setPositiveButton("Edit It", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int editedPrice = Integer.parseInt(editText.getText().toString());
                        price.set(position, Integer.toString(editedPrice));
                        notifyDataSetChanged();
                        myRef.orderByChild("productName").equalTo(products.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    if (data.child("quantity").getValue().toString().equals(quantity.get(position))) {
                                        myRef.child(data.getKey() + "/sellPrice").setValue(editedPrice);
                                        break;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(myContext, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textView1;
        TextView textView2;
        LinearLayout layout;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.productName_ROW_TV);
            textView2 = itemView.findViewById(R.id.productQuantity_ROW_TV);
            layout = itemView.findViewById(R.id.product_row_layout);
        }
    }
}
