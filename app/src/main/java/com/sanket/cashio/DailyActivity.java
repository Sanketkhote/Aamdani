package com.sanket.cashio;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket.cashio.adapter.ListItem_dailyExpense;
import com.sanket.cashio.adapter.adapter_dailyExpense;

import java.util.ArrayList;
import java.util.List;

public class DailyActivity extends AppCompatActivity {
    RecyclerView dailyExpenseRecycler;
    List<ListItem_dailyExpense> listitems;
    RecyclerView.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);
        getDaiyExpense();
    }
    public MainActivity.ExpenseData[] getDailyExpenseData(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB",MODE_PRIVATE,null);
        final String TABLE_NAME = "Records";
        //String selectQuery = "SELECT  * FROM " + TABLE_NAME+" where Date(Created)=Date('now')";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME+" WHERE Date(Created)=Date('now') AND ignored=0 AND investment=0 ORDER BY date(Created) DESC";

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

    public void getDaiyExpense(){
        dailyExpenseRecycler = findViewById(R.id.dailyExpenseRecycler);
        dailyExpenseRecycler.setBackgroundColor(Color.BLACK);
        dailyExpenseRecycler.setHasFixedSize(true);
        dailyExpenseRecycler.setLayoutManager(new LinearLayoutManager(this));
        MainActivity.ExpenseData[] dailyExpenses;

        dailyExpenses=getDailyExpenseData();

        listitems=new ArrayList<>();


        for(int i = 0; i<dailyExpenses.length; i++){
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
        adapter = new adapter_dailyExpense(listitems, this);
        dailyExpenseRecycler.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DailyActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
