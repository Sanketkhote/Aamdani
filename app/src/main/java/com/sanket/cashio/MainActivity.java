package com.sanket.cashio;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sanket.cashio.adapter.ListItem_balance;
import com.sanket.cashio.adapter.ListItem_dailyExpense;
import com.sanket.cashio.adapter.adapter_balance;
import com.sanket.cashio.adapter.adapter_dailyExpense;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class MainActivity extends AppCompatActivity {
    Dialog create, monthpopup;
    EditText catagoryEdit;
    Button addButton;
    PieChart monthlyTrend;
    LinearLayout changeMonthlyTarget, monthlyData;
    TextView monthTarget, DailyTarget, Available, TotalSavedTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    SharedPreferences sharedpreferences;

    public static class ExpenseData {
        public String expenseName, catagory;
        public int expense;
        public String created;
        public int ignored;
        public String detail;

        public ExpenseData(String expenseName, String catagory, int expense, String created, int ignored, String details) {
            this.expenseName = expenseName;
            this.catagory = catagory;
            this.expense = expense;
            this.created = created;
            this.ignored = ignored;
            this.detail = details;
        }
    }

    public static class Catagories {
        public String catagory;
        public int expense;

        public Catagories(String catagory, int expense) {
            this.catagory = catagory;
            this.expense = expense;
        }
    }

    public static class AvailableBalance {
        public String lastUpdated, accountNo, bankName, message;
        public int availableBalance;

        public AvailableBalance(String lastUpdated, String accountNo, String bankName, int availableBalance, String message) {
            this.lastUpdated = lastUpdated;
            this.accountNo = accountNo;
            this.bankName = bankName;
            this.availableBalance = availableBalance;
            this.message = message;


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        getSupportActionBar().hide();

        monthTarget = findViewById(R.id.monthlytargettext);
        DailyTarget = findViewById(R.id.dailyTargetTextview);
        Available = findViewById(R.id.availableTextview);
        TotalSavedTextView = findViewById(R.id.totalSavedTextView);
        sharedpreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        create = new Dialog(this);
        monthpopup = new Dialog(this);
        CardView dailyCard = findViewById(R.id.dailycard);
        monthlyData = findViewById(R.id.monthlydatalinear);


        // /You will setup the action bar with pull to refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container);

        mSwipeRefreshLayout.setColorScheme(R.color.bluelight,
                R.color.green, R.color.orange, R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(getClass().getSimpleName(), "refresh");
                FreshData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        dailyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int todayExpense = getTodaysExpense();
                if (todayExpense <= 0) {
                    Toast.makeText(MainActivity.this, "No expense Today", Toast.LENGTH_LONG).show();
                }
            }
        });

        monthlyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int monthlyExpense = getDBmonthlyExpense();
                if (monthlyExpense <= 0) {
                    Toast.makeText(MainActivity.this, "No expense in this month", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, MonthlyActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


        boolean first = sharedpreferences.getBoolean("first", false);
        Log.e("first_flag", String.valueOf(first));
        if (first) {
            monthlypopup();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("first", false);
            editor.commit();
            //To process all SMS
            try {
                process();
            } catch (ParseException e) {
                // e.printStackTrace();
            }

        }
        monthTarget.setText(String.valueOf(sharedpreferences.getInt("monthlyTarget", 0)));
        if (sharedpreferences.getInt("dailyTarget", 0) == 0) {
            int dailyTarget;
            dailyTarget = sharedpreferences.getInt("monthlyTarget", 0) / 30;
            DailyTarget.setText("₹ " + String.valueOf(dailyTarget));
        } else {
            DailyTarget.setText("₹ " + String.valueOf(sharedpreferences.getInt("dailyTarget", 0)));
        }

        updateMonthlyTarget();

        FreshData();
        FloatingActionButton fab = findViewById(R.id.addExpense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreatePopup();
            }
        });


    }

    public void FreshData() {
        CardView catagaoryCardView;

        catagaoryCardView=findViewById(R.id.catagryMaterialCardView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                catagaoryCardView.getLayoutParams();

        dailyExpense();
        monthlyTrendData();
        //dailyExpenseTrend();
        if (getTodaysExpense()!=0){
            layoutParams.height=1000;
            catagoryExpenseTrend();

        }else{
            layoutParams.height = 0;

        }
        getDaiyExpense();
        showAvailableBalance();
    }

    public void updateMonthlyTarget() {
        changeMonthlyTarget = findViewById(R.id.changemonthlytarget);


        changeMonthlyTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthlypopup();

            }
        });
    }

    public void monthlyTrendData() {
        monthlyTrend = findViewById(R.id.monthlyTrendchart);
        monthlyTrend.setBackgroundColor(Color.WHITE);
        moveOffScreen();
        ArrayList<PieEntry> catagoryExpenses = new ArrayList<>();
        int monthlyExpense = getDBmonthlyExpense();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd", Locale.getDefault());
        String formattedDate = df.format(c);
        System.out.println(formattedDate);
        int totalSaved;
        totalSaved = (Integer.parseInt(formattedDate) * sharedpreferences.getInt("dailyTarget", 0)) - monthlyExpense;
        TotalSavedTextView.setText("₹ " + String.valueOf(totalSaved));
        int monthlyTarget = sharedpreferences.getInt("monthlyTarget", 0);

        if (monthlyExpense > monthlyTarget) {
            monthlyTrend.setCenterText("Target\nreached limit");
            catagoryExpenses.add(new PieEntry(monthlyExpense, "Spents"));
            catagoryExpenses.add(new PieEntry(0, ""));
        } else {
            monthlyTrend.setCenterText("Target");
            catagoryExpenses.add(new PieEntry(monthlyExpense, "Spent"));
            catagoryExpenses.add(new PieEntry(sharedpreferences.getInt("monthlyTarget", 0) - monthlyExpense, "Remaining"));
        }


        final int[] colors = {
                rgb("#CF6679"), rgb("#03DAC5")
        };
        PieDataSet pieDataSet = new PieDataSet(catagoryExpenses, "Catagorywise Expense");

        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);
        PieData pieData = new PieData(pieDataSet);
        monthlyTrend.setData(pieData);
        monthlyTrend.setRotationEnabled(false);
        monthlyTrend.getDescription().setEnabled(false);
        monthlyTrend.setDrawHoleEnabled(true);
        monthlyTrend.setEntryLabelColor(Color.BLACK);
        monthlyTrend.setBackgroundColor(Color.parseColor("#FFFFFF"));
        monthlyTrend.setHoleColor(Color.parseColor("#FFFFFF"));
        monthlyTrend.setTransparentCircleColor(Color.parseColor("#FFFFFF"));
        monthlyTrend.setCenterTextColor(Color.BLACK);
        monthlyTrend.setMaxAngle(180);
        monthlyTrend.setRotationAngle(180);
        monthlyTrend.setCenterTextOffset(0, -20);
        monthlyTrend.animateY(1000, Easing.EaseInOutCubic);
        monthlyTrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "hi", Toast.LENGTH_SHORT).show();
            }
        });


        monthlyTrend.animate();

    }

    public void moveOffScreen() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int offset = (int) (height * 0.12);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) monthlyTrend.getLayoutParams();
        params.setMargins(0, 0, 0, -offset);
        monthlyTrend.setLayoutParams(params);
    }

    public void monthlypopup() {
        final EditText monthlyTarget;
        final Button doneMontlyTarget;
        monthpopup.setContentView(R.layout.monthlytargetpopup);
        doneMontlyTarget = monthpopup.findViewById(R.id.donemonthlytarget);
        monthlyTarget = monthpopup.findViewById(R.id.monthtargetedit);


        monthpopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        monthpopup.setCancelable(false);
        monthpopup.show();
        doneMontlyTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (monthlyTarget.getText().toString().matches("") || monthlyTarget.getText().toString().matches("0")) {
                    monthlyTarget.setError("Enter Amount");
                    return;
                }
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("monthlyTarget", Integer.parseInt(monthlyTarget.getText().toString()));
                int dailyTarget;
                dailyTarget = Integer.parseInt(monthlyTarget.getText().toString()) / 30;
                editor.putInt("dailyTarget", dailyTarget);
                editor.commit();
                monthTarget.setText(String.valueOf(sharedpreferences.getInt("monthlyTarget", 0)));
                DailyTarget.setText(String.valueOf(sharedpreferences.getInt("dailyTarget", 0)));
                monthlyTrendData();
                monthpopup.dismiss();
            }
        });

    }

    public void showCreatePopup() {
        TextView textClose;
        final EditText dateAndTime;
        final EditText expenseNameEdit;
        final EditText expenseAmountEdit;


        create.setContentView(R.layout.custompopup);


        dateAndTime = create.findViewById(R.id.datepicker);
        expenseNameEdit = create.findViewById(R.id.expnsenametext);
        expenseAmountEdit = create.findViewById(R.id.expenseamounttext);
        catagoryEdit = create.findViewById(R.id.expensecatagorytext);

        expenseAmountEdit.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        textClose = create.findViewById(R.id.textclose);
        addButton = create.findViewById(R.id.addButton);

        catagoryEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //   catagoryEdit.setCursorVisible(false);
                    addButton.performClick();


                    return true;
                }

                return false;
            }

        });

        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create.dismiss();
            }
        });
        create.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        create.show();
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateAndTime.setText(simpleDateFormat.format(new Date().getTime()));
        dateAndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);

                        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                calendar.set(Calendar.HOUR_OF_DAY, i);
                                calendar.set(Calendar.MINUTE, i1);


                                dateAndTime.setText(simpleDateFormat.format(calendar.getTime()));
                            }
                        };
                        new TimePickerDialog(create.getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(create.getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        setTag(getCatagories());
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean errorFlag = false;
                SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Records(created DATETIME NOT NULL PRIMARY KEY,ExpenseName TEXT,Catagory TEXT,Expense INTEGER);");


                if (catagoryEdit.getText().toString().matches("")) {
                    catagoryEdit.setError("Enter Catagory");
                    errorFlag = true;
                }
                if (expenseAmountEdit.getText().toString().matches("")) {
                    expenseAmountEdit.setError("Enter Amount");
                    errorFlag = true;
                }
                if (errorFlag) {
                    return;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put("Created", simpleDateFormat2.format(calendar.getTime()));
                contentValues.put("ExpenseName", expenseNameEdit.getText().toString());
                contentValues.put("Catagory", catagoryEdit.getText().toString());
                contentValues.put("Ignored", 0);
                insertCatagories(catagoryEdit.getText().toString());
                contentValues.put("Expense", expenseAmountEdit.getText().toString());
                mydatabase.insert("Records", null, contentValues);

                //just to hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
               FreshData();



                create.dismiss();
            }
        });

    }

    public void insertCatagories(String catagory) {
        SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Catagories(Catagory TEXT NOT NULL PRIMARY KEY);");
        ContentValues contentValues = new ContentValues();
        contentValues.put("Catagory", catagory);
        mydatabase.insert("Catagories", null, contentValues);

    }

    public void deleteCatagories(String catagory) {
        SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Catagories(Catagory TEXT NOT NULL PRIMARY KEY);");
        ContentValues contentValues = new ContentValues();
        contentValues.put("Catagory", catagory);
        mydatabase.delete("Catagories", "Catagory=\"" + catagory + "\"", null);

    }

    public ArrayList<String> getCatagories() {
        ArrayList<String> catagories = new ArrayList<String>();
        SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Catagories(Catagory TEXT NOT NULL PRIMARY KEY);");
        ContentValues contentValues = new ContentValues();
        contentValues.put("Catagory", "Food");
        mydatabase.insert("Catagories", null, contentValues);
        contentValues.put("Catagory", "Groceries");
        mydatabase.insert("Catagories", null, contentValues);
        contentValues.put("Catagory", "Hospital");
        mydatabase.insert("Catagories", null, contentValues);
        String selectQuery = "SELECT  * FROM Catagories";

        Cursor cursor = mydatabase.rawQuery(selectQuery, null);

        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                // get the data into array, or class variable
                catagories.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return catagories;

    }

    private void setTag(final List<String> tagList) {

        final ChipGroup chipGroup = create.findViewById(R.id.catagoryChips);
        for (int index = 0; index < tagList.size(); index++) {
            final String tagName = tagList.get(index);
            final Chip chip = new Chip(this);
            int paddingDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics()
            );
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
            chip.setText(tagName);
            chip.setCloseIconResource(R.drawable.ic_close_black_24dp);
            chip.setCloseIconEnabled(true);
            //Added click listener on close icon to remove tag from ChipGroup
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tagList.remove(tagName);
                    chipGroup.removeView(chip);
                    deleteCatagories(chip.getText().toString());
                }
            });
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    catagoryEdit.setText(chip.getText());
                }
            });

            chipGroup.addView(chip);
        }
    }


    public void dailyExpense() {
        TextView todaysExpense = findViewById(R.id.todayExpenseTextView);
        int todayExpense;
        todayExpense = getTodaysExpense();
        int available;
        int dailyTarget;
        if (sharedpreferences.getInt("dailyTarget", 0) == 0) {
            dailyTarget = sharedpreferences.getInt("monthlyTarget", 0) / 30;
        } else {
            dailyTarget = sharedpreferences.getInt("dailyTarget", 0);
        }
        available = dailyTarget - todayExpense;
        Available.setText("₹ " + String.valueOf(available));
        todaysExpense.setText("₹ " + String.valueOf(todayExpense));

    }

    public int getTodaysExpense() {

        SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);

        String selectQuery = "SELECT SUM(Expense) FROM Records WHERE Date(Created)=Date('now','localtime') AND ignored=0";

        Cursor cursor = mydatabase.rawQuery(selectQuery, null);

        int todaysExpense = 0;
        if (cursor.moveToFirst()) {
            do {
                // get the data into array, or class variable
                todaysExpense = cursor.getInt(0);


            } while (cursor.moveToNext());
        }
        cursor.close();
        return todaysExpense;
    }

    public int getDBmonthlyExpense() {

        SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);

        String selectQuery = "SELECT sum(Expense) FROM Records WHERE strftime('%m %Y',created) = strftime('%m %Y','now','localtime') AND ignored=0";

        Cursor cursor = mydatabase.rawQuery(selectQuery, null);

        int monthlyExpense = 0;
        if (cursor.moveToFirst()) {
            do {
                // get the data into array, or class variable
                monthlyExpense = cursor.getInt(0);


            } while (cursor.moveToNext());
        }
        cursor.close();
        return monthlyExpense;
    }


    public Catagories[] getCatagoryWiseData() {

        SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);
        String selectQuery = "SELECT Catagory,sum(Expense) FROM Records WHERE strftime('%d %m %Y',created) = strftime('%d %m %Y','now','localtime') AND ignored=0 GROUP by Catagory ORDER by Catagory;";

        Cursor cursor = mydatabase.rawQuery(selectQuery, null);
        Catagories[] data = new Catagories[cursor.getCount()];
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                // get the data into array, or class variable

                data[i] = new Catagories(cursor.getString(0), cursor.getInt(1));
                i++;


            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    public void catagoryExpenseTrend() {
        PieChart catagoryExpenseChart = findViewById(R.id.catagoryExpenseChart);
        ArrayList<PieEntry> catagoryExpenses = new ArrayList<>();

        Catagories[] catagories;

        catagories = getCatagoryWiseData();

        for (int i = 0; i < catagories.length; i++) {
            catagoryExpenses.add(new PieEntry(catagories[i].expense, catagories[i].catagory));
        }
        final int[] MATERIAL_COLORS = {
                rgb("#03DAC5"), rgb("#BB86FC"), rgb("#eaad43"), rgb("#2ecc71"), rgb("#f1c40f"), rgb("#e74c3c"), rgb("#3498db")
        };
        PieDataSet pieDataSet = new PieDataSet(catagoryExpenses, "Catagorywise Expense");
        pieDataSet.setColors(MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueLineColor(Color.BLACK);
        pieDataSet.setSliceSpace(2f);

        pieDataSet.setValueTextSize(16f);
        PieData pieData = new PieData(pieDataSet);
        catagoryExpenseChart.setNoDataText("No Spends in this Month \uD83D\uDE03");
        if (catagoryExpenses.size() == 0) {
            catagoryExpenseChart.setData(null);
        } else {
            catagoryExpenseChart.setData(pieData);
        }

        catagoryExpenseChart.getLegend().setTextColor(Color.BLACK);
        catagoryExpenseChart.getDescription().setEnabled(false);
        catagoryExpenseChart.setCenterText("Catagories");
        catagoryExpenseChart.setEntryLabelColor(Color.BLACK);
        catagoryExpenseChart.animate();

    }

    public String[] getDayString() {
        String[] days = new String[5];
        DateFormat formatter = new SimpleDateFormat("EEE", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        String reportDate = sdf.format(date);
        System.out.println("reportDate :" + reportDate);
        for (int i = 0; i < 5; i++) {

            date = cal.getTime();
            reportDate = sdf.format(date);
            System.out.println("reportDate :" + reportDate);
            days[i] = formatter.format(date);
            Log.e("day1s", days[i]);
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        return days;
    }

    public int[] getWeeklyTrendFromDB() {
        int[] expenses = new int[5];
        SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);

        String[] selectQuerys = new String[]{"SELECT SUM(Expense) FROM Records WHERE Date(Created)=Date('now','localtime') AND ignored=0",
                "SELECT SUM(Expense) FROM Records WHERE Date(Created)=Date('now','localtime','-1 day') AND ignored=0",
                "SELECT SUM(Expense) FROM Records WHERE Date(Created)=Date('now','localtime','-2 day') AND ignored=0",
                "SELECT SUM(Expense) FROM Records WHERE Date(Created)=Date('now','localtime','-3 day') AND ignored=0",
                "SELECT SUM(Expense) FROM Records WHERE Date(Created)=Date('now','localtime','-4 day') AND ignored=0"};

        int j = 0;
        for (j = 0; j < 5; j++) {
            Cursor cursor = mydatabase.rawQuery(selectQuerys[j], null);
            if (cursor.moveToFirst()) {
                do {
                    // get the data into array, or class variable

                    expenses[j] = cursor.getInt(0);


                } while (cursor.moveToNext());
            }
            cursor.close();
        }


        return expenses;

    }

    static String[] reverse(String a[], int n) {
        String[] b = new String[n];
        int j = n;
        for (int i = 0; i < n; i++) {
            b[j - 1] = a[i];
            j = j - 1;
        }

        /*printing the reversed array*/
        System.out.println("Reversed array is: \n");
        return b;
    }

    public void dailyExpenseTrend() {
        String[] days = getDayString();
        days = reverse(days, days.length);
        Log.e("day1s", String.valueOf(days));
        BarChart dailyTrendChart = findViewById(R.id.dailyTrendChart);
        int[] weeklyData = getWeeklyTrendFromDB();

        ArrayList<BarEntry> dailyExpenses = new ArrayList<>();
        dailyExpenses.add(new BarEntry(0, weeklyData[4]));
        dailyExpenses.add(new BarEntry(1, weeklyData[3]));
        dailyExpenses.add(new BarEntry(2, weeklyData[2]));
        dailyExpenses.add(new BarEntry(3, weeklyData[1]));
        dailyExpenses.add(new BarEntry(4, weeklyData[0]));


        BarDataSet barDataSet = new BarDataSet(dailyExpenses, "daily Expense");
        barDataSet.setColor(Color.parseColor("#BB86FC"));
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setValueTextSize(16f);
        barDataSet.setHighlightEnabled(true);
        barDataSet.setDrawValues(true);
        barDataSet.setValueTextSize(13);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) {
                    return "";
                }
                int a = (int) value;
                return String.valueOf(a);
            }
        });
        barDataSet.setDrawIcons(true);
        ;


        BarData barData = new BarData(barDataSet);
        XAxis xAxis = dailyTrendChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setDrawGridLines(false); // disable grid lines for the XAxis
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGridColor(Color.WHITE);
        xAxis.setLabelCount(7);


        dailyTrendChart.setFitBars(true);

        dailyTrendChart.setTouchEnabled(true);
        dailyTrendChart.setDrawBarShadow(false);
        dailyTrendChart.setDrawValueAboveBar(true);
        dailyTrendChart.setNoDataText("No spends in last 5 days \uD83D\uDE03");

        dailyTrendChart.getDescription().setEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        dailyTrendChart.setPinchZoom(false);
        int temp = 0;
        for (int i = 0; i < 5; i++) {
            if (weeklyData[i] > 0) {
                temp = 1;
                break;
            }
        }
        if (temp == 1) {
            dailyTrendChart.setData(barData);
        } else {
            dailyTrendChart.setData(null);

        }

        dailyTrendChart.setDrawBorders(false);

        YAxis leftAxis = dailyTrendChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // disable grid lines for the left YAxis
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = dailyTrendChart.getAxisRight();
        rightAxis.setDrawGridLines(false); // disable grid lines for the right YAxis
        rightAxis.setDrawAxisLine(false);
        rightAxis.setZeroLineColor(Color.WHITE);
        rightAxis.setAxisLineColor(Color.WHITE);
        rightAxis.setTextColor(Color.WHITE);

        rightAxis.setEnabled(false);


        Legend l = dailyTrendChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setTextColor(Color.WHITE);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(9f);

        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        dailyTrendChart.getDescription().setText("Daily Expense");

        dailyTrendChart.setBackgroundColor(Color.TRANSPARENT);

        dailyTrendChart.setDrawGridBackground(false);
        dailyTrendChart.animateY(2000);
    }

    public void process() throws ParseException {
        SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Records(created DATETIME NOT NULL PRIMARY KEY,ExpenseName TEXT,Catagory TEXT,Expense INTEGER,Ignored INTEGER,Detail TEXT);");
        readSMS(mydatabase);
        // mydatabase.close();
    }

    private String getDateTime(String date) {
        long dateAsLong = Long.parseLong(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date result = new Date(dateAsLong);
        return dateFormat.format(result);
    }

    public static void saveToDB(ExpenseData ed, SQLiteDatabase myDatabase) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("Created", ed.created);
        contentValues.put("ExpenseName", ed.expenseName);
        contentValues.put("Catagory", ed.catagory);
        contentValues.put("Expense", ed.expense);
        contentValues.put("Ignored", ed.ignored);
        contentValues.put("Detail", ed.detail);
        try {

            myDatabase.insertOrThrow("Records", null, contentValues);
        } catch (SQLException exception) {
            Log.i("error la inserare child", "on the next line");
            //  exception.printStackTrace();
        }


    }

    public void readSMS(SQLiteDatabase myDatabase) throws ParseException {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);


        if (cursor.moveToFirst() && cursor != null) { // must check the result to prevent exception
            do {
                String messageBody = cursor.getString(cursor.getColumnIndex("body"));
                if (messageBody == null) {
                    continue;
                }
                ExpenseData expense;
                SQLiteDatabase balanceDB = balanceDB();
                readBalance(balanceDB, messageBody, getDateTime(cursor.getString(4)));
                //  Log.e("yes",messageBody);
                String smallMessageBody = messageBody.toLowerCase();
                if ((smallMessageBody.contains("bank")|| smallMessageBody.contains("sbi")) && (smallMessageBody.contains("transaction") || smallMessageBody.contains("debited")) && (smallMessageBody.contains("rs") || smallMessageBody.contains("inr")) && !(smallMessageBody.contains("will") || smallMessageBody.contains("due"))) {

                    Log.e("yes2", "got it");
                    //extract price


                    String regex = "(\\$|rs|inr|inr |inr. |inr.|rs.|rs. )(\\s?[0-9,]+)";
                    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                    final Matcher matcher = pattern.matcher(messageBody.toLowerCase());
                    int expensePrice = 0;
                    String expenseName;
                    while (matcher.find()) {
                        String test = matcher.group(2);


                        test = test.replace(",", "");
                        test = test.trim();


                        System.out.println("Full match: " + test);

                        expensePrice = Integer.parseInt(test);
                        break;
                    }
                    if (expensePrice == 0) {
                        Log.e("hi", messageBody);
                    }

                    if (messageBody.contains("UPI")) {
                        expenseName = "UPI Transfer";
                    } else {
                        expenseName = "Net Banking";
                    }
                    //  Log.e("time123",cursor.getString(4));

                    expense = new ExpenseData(expenseName, expenseName, expensePrice, getDateTime(cursor.getString(4)), 0, messageBody);
                    if (expensePrice!=0){
                        saveToDB(expense, myDatabase);
                    }

                }

            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
            Log.e("hi", "no messages");
        }
    }

    public static int extractPrice(String messageBody) {
        String regex = "(\\$|rs|inr|inr |inr. |inr.|rs.|rs. )(\\s?[0-9,]+)";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(messageBody.toLowerCase());
        int expensePrice = 0;
        while (matcher.find()) {
            String test = matcher.group(2);
            test = test.replace(",", "");
            test = test.trim();
            System.out.println("Full match: " + test);
            expensePrice = Integer.parseInt(test);

        }
        return expensePrice;
    }

    public static void readBalance(SQLiteDatabase mydatabase, String messageBody, String timestamp) {
        boolean recievedACNumber = false;
        boolean recievedBankName = false;
        if (messageBody.contains("Dear Customer, your ICICI Bank Account XX824 has been credited with INR 1,00,943.00")){
            Log.e("hi","hi");
        }
        if ((messageBody.toLowerCase().contains("bank") || messageBody.toLowerCase().contains("sbi")) && (messageBody.toLowerCase().contains("balance") || messageBody.toLowerCase().contains("bal")) && (messageBody.toLowerCase().contains("inr") || messageBody.toLowerCase().contains("rs") || messageBody.toLowerCase().contains("rs.")) && (messageBody.toLowerCase().contains("account") || messageBody.toLowerCase().contains("a/c")) && !(messageBody.toLowerCase().contains("policy")||messageBody.toLowerCase().contains("interest"))) {
            String[] tokens = stringToToken(messageBody);
            String bankName = new String(), accountNumber = new String();
            for (int k = 0; k < tokens.length; k++) {
                System.out.println(tokens[k].toLowerCase());
                if (!recievedBankName) {
                    if (tokens[k].toLowerCase().contains("bank")) {
                        bankName = tokens[k - 1] + " " + tokens[k];
                        recievedBankName = true;
                    }
                }
                if (!recievedACNumber) {
                    if (tokens[k].toLowerCase().contains("a/c")) {
                        accountNumber = tokens[k]+" "+tokens[k+1];
                        recievedACNumber = true;
                    }
                    if (tokens[k].toLowerCase().contains("xx")) {
                        accountNumber = tokens[k];
                        recievedACNumber = true;
                    }
                }
            }
            int availableBalance = extractPrice(messageBody);
            Log.e("available balance", String.valueOf(availableBalance) + " " + accountNumber + " " + bankName + " " + timestamp);
            AvailableBalance availableBalancedata;
            availableBalancedata = new AvailableBalance(timestamp, accountNumber, bankName, availableBalance, messageBody);
            if((availableBalance!=0)||accountNumber!=""){

                addAvailableBalance(mydatabase, availableBalancedata);
            }

        }

    }


    public boolean isValideAccountNumber(String accountNumber){

        return false;

    }
    public static String[] stringToToken(String messageBody) {
        StringTokenizer st = new StringTokenizer(messageBody, " ");
        String[] tokens = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            tokens[i] = st.nextToken();
            i++;
        }
        return tokens;
    }

    public SQLiteDatabase balanceDB() {
        SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Balance(LastUpdated DATETIME NOT NULL PRIMARY KEY,Balance INT,AccountNo TEXT,BankName TEXT,Message TEXT);");
        return mydatabase;
    }

    public static void addAvailableBalance(SQLiteDatabase mydatabase, AvailableBalance data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("LastUpdated", data.lastUpdated);
        contentValues.put("Balance", data.availableBalance);
        contentValues.put("AccountNo", data.accountNo);
        contentValues.put("BankName", data.bankName);
        contentValues.put("Message", data.message);
        try {

            mydatabase.insertOrThrow("Balance", null, contentValues);
        } catch (SQLException exception) {
            Log.i("error la inserare child", "on the next line");
            //  exception.printStackTrace();
        }

    }

    RecyclerView balanceRecycler;
    RecyclerView.Adapter balanceAdapter;
    List<ListItem_balance> listitems;

    public void showAvailableBalance() {
        balanceRecycler = findViewById(R.id.balanceRecycler);
        balanceRecycler.setBackgroundColor(Color.BLACK);
        balanceRecycler.setHasFixedSize(true);
        balanceRecycler.setLayoutManager(new LinearLayoutManager(this));
        AvailableBalance[] availableBalances;

        availableBalances = getAvailblebalance();

        listitems = new ArrayList<>();


        for (int i = 0; i < availableBalances.length; i++) {
            Log.e(String.valueOf(i), availableBalances[i].lastUpdated + " " + availableBalances[i].bankName + " " + availableBalances[i].accountNo + " " + availableBalances[i].message);
            ListItem_balance item = new ListItem_balance(
                    availableBalances[i].lastUpdated,
                    availableBalances[i].availableBalance,
                    availableBalances[i].bankName,
                    availableBalances[i].accountNo,
                    availableBalances[i].message
            );
            listitems.add(item);
        }
        balanceAdapter = new adapter_balance(listitems);
        balanceRecycler.setAdapter(balanceAdapter);
    }

    public AvailableBalance[] getAvailblebalance() {
        SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);
        //String selectQuery = "SELECT  * FROM " + TABLE_NAME+" where Date(Created)=Date('now')";
        String selectQuery = "SELECT LastUpdated, Balance,AccountNo,BankName,Message \n" +
                "FROM (\n" +
                "    SELECT LastUpdated,Balance,AccountNo,BankName,Message FROM Balance\n" +
                "    ORDER BY LastUpdated DESC\n" +
                ") AS sub\n" +
                "GROUP BY AccountNo;";

        Cursor cursor = mydatabase.rawQuery(selectQuery, null);
        AvailableBalance[] availableBalances = new AvailableBalance[cursor.getCount()];
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                // get the data into array, or class variable

                availableBalances[i] = new AvailableBalance(cursor.getString(0), cursor.getString(3), cursor.getString(2), cursor.getInt(1), cursor.getString(4));
                i++;


            } while (cursor.moveToNext());
        }
        cursor.close();
        return availableBalances;
    }


//--------------------------Daily Expense ----------------------

    public MainActivity.ExpenseData[] getDailyExpenseData() {
        SQLiteDatabase mydatabase = openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);
        final String TABLE_NAME = "Records";
        //String selectQuery = "SELECT  * FROM " + TABLE_NAME+" where Date(Created)=Date('now')";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE Date(Created)=Date('now','localtime') AND ignored=0 ORDER BY date(Created) DESC";

        Cursor cursor = mydatabase.rawQuery(selectQuery, null);
        MainActivity.ExpenseData[] dailyexpenses = new MainActivity.ExpenseData[cursor.getCount()];
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                // get the data into array, or class variable

                dailyexpenses[i] = new MainActivity.ExpenseData(cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(0), cursor.getInt(4), cursor.getString(5));
                i++;


            } while (cursor.moveToNext());
        }
        cursor.close();
        return dailyexpenses;
    }

    public void getDaiyExpense() {
        RecyclerView dailyExpenseRecycler;
        List<ListItem_dailyExpense> DailyListItems;
        RecyclerView.Adapter dailyExpenseAdapter;
        dailyExpenseRecycler = findViewById(R.id.dailyExpenseRecycler);
        dailyExpenseRecycler.setBackgroundColor(Color.BLACK);
        dailyExpenseRecycler.setHasFixedSize(true);
        dailyExpenseRecycler.setLayoutManager(new LinearLayoutManager(this));
        MainActivity.ExpenseData[] dailyExpenses;

        dailyExpenses = getDailyExpenseData();

        DailyListItems = new ArrayList<>();


        for (int i = 0; i < dailyExpenses.length; i++) {
            Log.e(String.valueOf(i), dailyExpenses[i].created + " " + dailyExpenses[i].expenseName + " " + dailyExpenses[i].catagory + " " + dailyExpenses[i].expense);
            ListItem_dailyExpense item = new ListItem_dailyExpense(
                    dailyExpenses[i].created,
                    dailyExpenses[i].expense,
                    dailyExpenses[i].expenseName,
                    dailyExpenses[i].catagory,
                    dailyExpenses[i].ignored,
                    dailyExpenses[i].detail
            );
            DailyListItems.add(item);
        }
        dailyExpenseAdapter = new adapter_dailyExpense(DailyListItems, this);
        dailyExpenseRecycler.setAdapter(dailyExpenseAdapter);

    }


}