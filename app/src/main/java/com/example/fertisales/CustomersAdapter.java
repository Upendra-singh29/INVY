package com.example.fertisales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder> {

    ArrayList<String> customerName;

    public CustomersAdapter(ArrayList<String> customerName) {
        this.customerName = customerName;
    }

    @NonNull
    @Override
    public CustomersAdapter.CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.customer_row, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomersAdapter.CustomerViewHolder holder, int position) {
        String name = customerName.get(position);
        holder.textView.setText(name);
    }

    @Override
    public int getItemCount() {
        return customerName.size();
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.customers_row);
        }
    }
}
