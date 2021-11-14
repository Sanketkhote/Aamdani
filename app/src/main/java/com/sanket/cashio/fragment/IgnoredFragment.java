package com.sanket.cashio.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket.cashio.MainActivity;
import com.sanket.cashio.R;
import com.sanket.cashio.adapter.ListItem_dailyExpense;
import com.sanket.cashio.adapter.adapter_dailyExpense;

import java.util.ArrayList;
import java.util.List;

public class IgnoredFragment extends Fragment {
View v;
    RecyclerView ignoredyExpenseRecycler;
    List<ListItem_dailyExpense> listitems;
    RecyclerView.Adapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.fragment_ignored,container,false);
        String selectedMonth = getArguments().getString("month");
        String selectedYear=getArguments().getString("year");

        getIgnoredExpense(selectedMonth,selectedYear);
        return v;

    }

    public MainActivity.ExpenseData[] getIgnoredExpenseData(String selectedMonth,String selectedYear){

        String selectedDate=selectedYear+"-"+selectedMonth+"-01";
        SQLiteDatabase mydatabase =  getActivity().openOrCreateDatabase("expenseDB", Context.MODE_PRIVATE,null);
        final String TABLE_NAME = "Records";
        //String selectQuery = "SELECT  * FROM " + TABLE_NAME+" where Date(Created)=Date('now')";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME+" WHERE strftime('%m %Y',created) = strftime('%m %Y','"+selectedDate+"') AND Ignored=1 ORDER BY date(Created) DESC";

        Cursor cursor      = mydatabase.rawQuery(selectQuery, null);
        MainActivity.ExpenseData[] dailyexpenses= new MainActivity.ExpenseData[cursor.getCount()];
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                // get the data into array, or class variable

                dailyexpenses[i]=new MainActivity.ExpenseData(cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getString(0),cursor.getInt(4),cursor.getString(5));
                i++;


            } while (cursor.moveToNext());
        }
        cursor.close();
        return dailyexpenses;
    }
    public void getIgnoredExpense(String selectedMonth,String selectedYear){
        ignoredyExpenseRecycler = v.findViewById(R.id.ignoredExpenseRecycler);
        ignoredyExpenseRecycler.setBackgroundColor(Color.BLACK);
        ignoredyExpenseRecycler.setHasFixedSize(true);
        ignoredyExpenseRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        MainActivity.ExpenseData[] dailyExpenses;

        dailyExpenses=getIgnoredExpenseData(selectedMonth,selectedYear);

        listitems=new ArrayList<>();


        for(int i=0;i<dailyExpenses.length;i++){
            Log.e(String.valueOf(i),dailyExpenses[i].created+" "+ dailyExpenses[i].expenseName+" "+dailyExpenses[i].catagory+" "+dailyExpenses[i].expense);
            ListItem_dailyExpense item= new ListItem_dailyExpense(
                    dailyExpenses[i].created,
                    dailyExpenses[i].expense,
                    dailyExpenses[i].expenseName,
                    dailyExpenses[i].catagory,
                    dailyExpenses[i].ignored,
                    dailyExpenses[i].detail
            );
            listitems.add(item);
        }
        adapter=new adapter_dailyExpense(listitems,getContext());
        ignoredyExpenseRecycler.setAdapter(adapter);

    }
}
