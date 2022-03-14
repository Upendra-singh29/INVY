package com.example.fertisales;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class SupplierListAdapter extends RecyclerView.Adapter<SupplierListAdapter.SupplierViewHolder> {

    ArrayList<String> supplierName,phoneNo;
    Context myContext;
    public SupplierListAdapter(Context context ,ArrayList<String> supplierName,ArrayList<String> phoneNo) {
        this.supplierName = supplierName;
        this.phoneNo = phoneNo;
        this.myContext = context;
    }

    @NonNull
    @Override
    public SupplierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.supplier_row,parent,false);
        return new SupplierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierViewHolder holder, final int position) {
        String full = supplierName.get(position) + " : " + phoneNo.get(position);
        holder.textView.setText(full);

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + phoneNo.get(position);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                myContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return supplierName.size();
    }

    public class SupplierViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageButton imageButton;
        public SupplierViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.supplierName_ROW_TV);
            imageButton = itemView.findViewById(R.id.supplier_row_call);
        }
    }
}
