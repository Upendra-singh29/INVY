package com.example.fertisales;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SaleReportAdapter extends RecyclerView.Adapter<SaleReportAdapter.SaleViewHolder> {

    ArrayList<String> customerName, quantity, finalPrice, date;

    public SaleReportAdapter(ArrayList<String> customerName, ArrayList<String> quantity, ArrayList<String> finalPrice, ArrayList<String> date) {
        this.customerName = customerName;
        this.quantity = quantity;
        this.finalPrice = finalPrice;
        this.date = date;
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.sale_row, parent, false);
        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
        String name = customerName.get(position);
        String quan = quantity.get(position);
        String amount = finalPrice.get(position);
        String dte = date.get(position);

        holder.textViewName.setText(name);
        holder.textViewQuantity.setText(quan);
        holder.textViewAmount.setText(amount);
        holder.textViewDate.setText(dte);
    }

    @Override
    public int getItemCount() {
        return customerName.size();
    }

    public class SaleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewQuantity, textViewAmount, textViewDate;

        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.customerName_TV);
            textViewQuantity = itemView.findViewById(R.id.quantity_TV);
            textViewAmount = itemView.findViewById(R.id.FinalAmount_TV);
            textViewDate = itemView.findViewById(R.id.DateTime_TV);
        }
    }
}
