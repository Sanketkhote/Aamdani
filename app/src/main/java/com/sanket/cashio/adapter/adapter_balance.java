package com.sanket.cashio.adapter;

import android.app.Dialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket.cashio.R;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class adapter_balance extends RecyclerView.Adapter<adapter_balance.ViewHolder>{
    private List<ListItem_balance> listItems;

    public adapter_balance(List<ListItem_balance> listItems) {
        this.listItems = listItems;

    }
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_balance, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        final ListItem_balance listItem = listItems.get(position);
        holder.bankName.setText(listItem.getBankName());
        holder.balance.setText("₹ " + listItem.getBalance());
        holder.accountNumber.setText(listItem.getAccountNumber());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        try {
            date = format.parse(listItem.getLastUpdated());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dayOfTheWeek = (String) DateFormat.format("EEE", date); // Thursday
        String day          = (String) DateFormat.format("dd",   date); // 20
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        holder.LastUpdated.setText(dayOfTheWeek+", "+day+" "+monthString);
        holder.balanceMaterialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), listItem.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView LastUpdated, balance, bankName, accountNumber;
        public CardView balanceMaterialCardView;


        public ViewHolder(View itemView) {
            super(itemView);
            balanceMaterialCardView=itemView.findViewById(R.id.balanceMaterialCardView);
            LastUpdated=itemView.findViewById(R.id.balance_lastupdated_textview);
            balance = (TextView) itemView.findViewById(R.id.balancetextview);
            bankName = (TextView) itemView.findViewById(R.id.balance_bank_textview);
            accountNumber = (TextView) itemView.findViewById(R.id.balance_acno_textview);

        }
    }
}
