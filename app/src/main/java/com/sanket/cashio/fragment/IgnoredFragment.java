package com.sanket.cashio.fragment;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.sanket.cashio.MainActivity;
import com.sanket.cashio.R;
import com.sanket.cashio.adapter.ListItem_dailyExpense;
import com.sanket.cashio.adapter.adapter_dailyExpense;

import java.util.ArrayList;
import java.util.List;

public class IgnoredFragment extends Fragment {
    View v;
    RecyclerView monthlyInvestmentRecycler;
    List<ListItem_dailyExpense> listitems;
    RecyclerView.Adapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.fragment_ignored,container,false);
        String selectedMonth = getArguments().getString("month");
        String selectedYear=getArguments().getString("year");

        catagoryInvestmentTrend(selectedMonth,selectedYear);
        getMonthlyInvestment(selectedMonth,selectedYear);
        return v;

    }
    public MainActivity.Catagories[] getCatagoryWiseData(String selectedMonth,String selectedYear){

        String selectedDate=selectedYear+"-"+selectedMonth+"-01";
        SQLiteDatabase mydatabase = getActivity().openOrCreateDatabase("expenseDB", Context.MODE_PRIVATE,null);
        String selectQuery = "SELECT Catagory,sum(Expense) FROM Records WHERE strftime('%m %Y',created) = strftime('%m %Y','"+selectedDate+"') AND ignored=0 AND Investment=1 GROUP by Catagory ORDER by Catagory;";

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
    public void catagoryInvestmentTrend(String selectedMonth,String selectedYear) {
        PieChart catagoryExpenseChart = v.findViewById(R.id.monthlyinvestmentpiechart);
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
        pieDataSet.setValueLinePart1OffsetPercentage(90.f);
        pieDataSet.setValueLinePart1Length(1f);
        pieDataSet.setValueLinePart2Length(.2f);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1OffsetPercentage(90.f);
        pieDataSet.setValueLinePart1Length(.5f);
        pieDataSet.setValueLinePart2Length(.2f);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //catagoryExpenseChart.setDrawSliceText(false);

        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        catagoryExpenseChart.setMinAngleForSlices(10);
        pieDataSet.setValueTextSize(10f);
        catagoryExpenseChart.setEntryLabelTextSize(10f);
        PieData pieData = new PieData(pieDataSet);
        catagoryExpenseChart.setData(pieData);

        catagoryExpenseChart.getDescription().setEnabled(false);
        catagoryExpenseChart.setCenterText("Catagories");
        catagoryExpenseChart.getLegend().setTextColor(Color.BLACK);
        catagoryExpenseChart.setEntryLabelColor(Color.BLACK);
        catagoryExpenseChart.setBackgroundColor(Color.parseColor("#FFFFFF"));
        catagoryExpenseChart.setTransparentCircleColor(Color.parseColor("#FFFFFF"));
        // catagoryExpenseChart.setExtraBottomOffset(20f);
        catagoryExpenseChart.setExtraLeftOffset(15f);
        // catagoryExpenseChart.setExtraRightOffset(20f);
        Legend legend = catagoryExpenseChart.getLegend();
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        catagoryExpenseChart.animate();

    }
    public MainActivity.ExpenseData[] getMonthlyInvestmentData(String selectedMonth,String selectedYear){
        String selectedDate=selectedYear+"-"+selectedMonth+"-01";
        SQLiteDatabase mydatabase =  getActivity().openOrCreateDatabase("expenseDB",Context.MODE_PRIVATE,null);
        final String TABLE_NAME = "Records";
        //String selectQuery = "SELECT  * FROM " + TABLE_NAME+" where Date(Created)=Date('now')";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME+" WHERE strftime('%m %Y',created) = strftime('%m %Y','"+selectedDate+"') AND Ignored=0 AND Investment=1 ORDER BY date(Created) DESC";

        Cursor cursor      = mydatabase.rawQuery(selectQuery, null);
        MainActivity.ExpenseData[] dailyexpenses= new MainActivity.ExpenseData[cursor.getCount()];
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                // get the data into array, or class variable

                dailyexpenses[i]=new MainActivity.ExpenseData(cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getString(0),cursor.getInt(4),cursor.getString(5),cursor.getInt(6),cursor.getInt(7),cursor.getInt(8));
                i++;

            } while (cursor.moveToNext());
        }
        cursor.close();
        return dailyexpenses;
    }
    public void getMonthlyInvestment(String selectedMonth,String selectedYear){
        monthlyInvestmentRecycler = v.findViewById(R.id.monthlyInvestmentRecycler);
        monthlyInvestmentRecycler.setBackgroundColor(Color.WHITE);
        monthlyInvestmentRecycler.setHasFixedSize(true);
        monthlyInvestmentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        MainActivity.ExpenseData[] dailyExpenses;

        dailyExpenses=getMonthlyInvestmentData(selectedMonth,selectedYear);

        listitems=new ArrayList<>();


        for(int i=0;i<dailyExpenses.length;i++){
            Log.e(String.valueOf(i),dailyExpenses[i].created+" "+ dailyExpenses[i].expenseName+" "+dailyExpenses[i].catagory+" "+dailyExpenses[i].expense);
            ListItem_dailyExpense item= new ListItem_dailyExpense(
                    dailyExpenses[i].created,
                    dailyExpenses[i].expense,
                    dailyExpenses[i].expenseName,
                    dailyExpenses[i].catagory,
                    dailyExpenses[i].ignored,
                    dailyExpenses[i].detail,
                    dailyExpenses[i].investment,
                    dailyExpenses[i].lend,
                    dailyExpenses[i].loan
            );
            listitems.add(item);
        }
        adapter=new adapter_dailyExpense(listitems,getContext());
        monthlyInvestmentRecycler.setAdapter(adapter);

    }

}
