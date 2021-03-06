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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.sanket.cashio.MainActivity;
import com.sanket.cashio.R;
import com.sanket.cashio.adapter.ListItem_dailyExpense;
import com.sanket.cashio.adapter.adapter_dailyExpense;

import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class ReportFragment extends Fragment {
    View v;
    RecyclerView monthlyExpenseRecycler;
    List<ListItem_dailyExpense> listitems;
    RecyclerView.Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v=inflater.inflate(R.layout.fragment_report,container,false);
        String selectedMonth = getArguments().getString("month");
        String selectedYear=getArguments().getString("year");

        catagoryExpenseTrend(selectedMonth,selectedYear);
        getMonthlyExpense(selectedMonth,selectedYear);
        return v;

    }
    public MainActivity.Catagories[] getCatagoryWiseData(String selectedMonth,String selectedYear){

        String selectedDate=selectedYear+"-"+selectedMonth+"-01";
        SQLiteDatabase mydatabase = getActivity().openOrCreateDatabase("expenseDB", Context.MODE_PRIVATE,null);
        String selectQuery = "SELECT Catagory,sum(Expense) FROM Records WHERE strftime('%m %Y',created) = strftime('%m %Y','"+selectedDate+"') AND ignored=0 GROUP by Catagory ORDER by Catagory;";

        Cursor cursor      = mydatabase.rawQuery(selectQuery, null);
        MainActivity.Catagories[] data= new MainActivity.Catagories[cursor.getCount()];
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                // get the data into array, or class variable

                data[i]=new MainActivity.Catagories(cursor.getString(0),cursor.getInt(1));
                i++;


            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }
    public void catagoryExpenseTrend(String selectedMonth,String selectedYear) {
        PieChart catagoryExpenseChart = v.findViewById(R.id.monthlycatagorypiechart);
        ArrayList<PieEntry> catagoryExpenses = new ArrayList<>();

        MainActivity.Catagories[] catagories;

        catagories = getCatagoryWiseData(selectedMonth, selectedYear);

        for (int i = 0; i < catagories.length; i++) {
            catagoryExpenses.add(new PieEntry(catagories[i].expense, catagories[i].catagory));
        }
        final int[] MATERIAL_COLORS = {
                rgb("#03DAC5"), rgb("#BB86FC"), rgb("#eaad43"), rgb("#2ecc71"), rgb("#f1c40f"), rgb("#e74c3c"), rgb("#3498db")
        };


        PieDataSet pieDataSet = new PieDataSet(catagoryExpenses, "Catagorywise Expense");
        pieDataSet.setColors(MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);

        pieDataSet.setValueTextSize(16f);
        PieData pieData = new PieData(pieDataSet);
        catagoryExpenseChart.setData(pieData);
        catagoryExpenseChart.getDescription().setEnabled(false);
        catagoryExpenseChart.setCenterText("Catagories");
        catagoryExpenseChart.getLegend().setTextColor(Color.BLACK);
        catagoryExpenseChart.setEntryLabelColor(Color.BLACK);
        catagoryExpenseChart.setBackgroundColor(Color.parseColor("#FFFFFF"));
        catagoryExpenseChart.setTransparentCircleColor(Color.parseColor("#FFFFFF"));
        catagoryExpenseChart.animate();

    }

    public MainActivity.ExpenseData[] getMonthlyExpenseData(String selectedMonth,String selectedYear){
        String selectedDate=selectedYear+"-"+selectedMonth+"-01";
        SQLiteDatabase mydatabase =  getActivity().openOrCreateDatabase("expenseDB",Context.MODE_PRIVATE,null);
        final String TABLE_NAME = "Records";
        //String selectQuery = "SELECT  * FROM " + TABLE_NAME+" where Date(Created)=Date('now')";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME+" WHERE strftime('%m %Y',created) = strftime('%m %Y','"+selectedDate+"') AND Ignored=0 ORDER BY date(Created) DESC";

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
    public void getMonthlyExpense(String selectedMonth,String selectedYear){
        monthlyExpenseRecycler = v.findViewById(R.id.monthlyExpenseRecycler);
        monthlyExpenseRecycler.setBackgroundColor(Color.WHITE);
        monthlyExpenseRecycler.setHasFixedSize(true);
        monthlyExpenseRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        MainActivity.ExpenseData[] dailyExpenses;

        dailyExpenses=getMonthlyExpenseData(selectedMonth,selectedYear);

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
        monthlyExpenseRecycler.setAdapter(adapter);

    }

}
