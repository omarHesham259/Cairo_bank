package com.example.cairo_bank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactionList = transactions;
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView date, amount, service;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_date);
            amount = itemView.findViewById(R.id.tv_amount_value);
            service = itemView.findViewById(R.id.tv_service_value);
        }
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction t = transactionList.get(position);
        holder.date.setText(new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(t.getTimestamp()));
        holder.amount.setText("Amount: " + t.getAmount());
        holder.service.setText("Service: " + t.getType());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}
