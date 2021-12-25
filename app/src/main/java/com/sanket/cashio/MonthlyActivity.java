package com.sanket.cashio;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sanket.cashio.fragment.IgnoredFragment;
import com.sanket.cashio.fragment.ReportFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MonthlyActivity extends AppCompatActivity {
BottomNavigationView bottomNavigationView;
    private ActionBar actionBar;

    MaterialToolbar toolbar;
    Spinner spinner;
    String selectedMonth,selectedYear;
    int fragmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly);
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        spinner =  findViewById(R.id.monthlySpinner);



        SimpleDateFormat format = new SimpleDateFormat("MMMM");
        SimpleDateFormat formatyear = new SimpleDateFormat("YYYY");



        Date date = null;

            date = new Date();

        Calendar cal2 = Calendar.getInstance();

        cal2.setTime(date);
        int month=(cal2.get(Calendar.MONTH)+1);

        selectedMonth=String.valueOf(month);
        if (month>0 && month<10){
            selectedMonth="0"+month;
        }



       selectedYear= String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        ArrayList<String> arrayList = new ArrayList<>();
        final ArrayList<String> arrayListYear = new ArrayList<>();
        for(int i=0;i<12;i++){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -i);
            arrayList.add(format.format(cal.getTime()));
            arrayListYear.add(String.valueOf(cal.get(Calendar.YEAR)));
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedYear=arrayListYear.get(position);
                Toast.makeText(parent.getContext(), "Selected: " + selectedYear,Toast.LENGTH_LONG).show();
                Date date = null;
                try {
                    date = new SimpleDateFormat("MMMM").parse(parent.getItemAtPosition(position).toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int month=(cal.get(Calendar.MONTH)+1);

                selectedMonth=String.valueOf(month);
                if (month>0 && month<10){
                    selectedMonth="0"+month;
                }
                Fragment selectedFragment=null;
                switch (fragmentID){
                    case R.id.reportpage:
                        selectedFragment=new ReportFragment();
                        break;
                    case R.id.ignoredpage:
                        selectedFragment=new IgnoredFragment();
                        break;

                }
                Bundle bundle = new Bundle();
                bundle.putString("month", selectedMonth);
                bundle.putString("year", selectedYear);
                selectedFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer,selectedFragment).commit();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });
        Bundle bundle = new Bundle();

        bundle.putString("month", selectedMonth);
        bundle.putString("year",selectedYear);
        fragmentID=R.id.reportpage;
        Fragment reportFragment = new ReportFragment();
        reportFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer,reportFragment).commit();

    }



    BottomNavigationView.OnNavigationItemSelectedListener navListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment=null;
                    fragmentID=item.getItemId();
                    switch (item.getItemId()){
                        case R.id.reportpage:
                            selectedFragment=new ReportFragment();
                            break;
                        case R.id.ignoredpage:
                            selectedFragment=new IgnoredFragment();
                            break;


                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("month", selectedMonth);
                    bundle.putString("year", selectedYear);
                    selectedFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, selectedFragment).commit();

                    return true;
                }
            };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MonthlyActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
