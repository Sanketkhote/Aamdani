package com.sanket.cashio.adapter;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sanket.cashio.R;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class adapter_dailyExpense extends RecyclerView.Adapter<adapter_dailyExpense.ViewHolder> {
    Dialog update;
    EditText catagoryEdit;
    private List<ListItem_dailyExpense> listItems;
    private Context context;

    public adapter_dailyExpense(List<ListItem_dailyExpense> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public adapter_dailyExpense.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dailyexpenses, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final adapter_dailyExpense.ViewHolder holder, final int position) {

        final ListItem_dailyExpense listItem = listItems.get(position);
        holder.expenseName.setText(listItem.getExpenseName());
        holder.expense.setText("₹ " + listItem.getExpense());
        holder.expenseCatagory.setText(listItem.getCatagory());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();

        try {
            date = format.parse(listItem.getCreated());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dayOfTheWeek = (String) DateFormat.format("EEE", date); // Thursday
        String day          = (String) DateFormat.format("dd",   date); // 20
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        holder.expenseDate.setText(dayOfTheWeek+", "+day+" "+monthString);
        String letter = String.valueOf(listItem.getCatagory().charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
//        Create a new TextDrawable for our image's background
        int color = generator.getColor(listItem.getCatagory());
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(letter, color);

        holder.letter.setImageDrawable(drawable);

        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listItem.getIgnored()==1){
                    SQLiteDatabase db = context.openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);
                    ContentValues cv = new ContentValues();
                    cv.put("Ignored", 1); //These Fields should be your String values of actual column names
                    db.delete("Records",  "Created=" + "\"" + listItem.getCreated() + "\"", null);

                    listItems.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, listItems.size());
                }
                else{
                    SQLiteDatabase db = context.openOrCreateDatabase("expenseDB", MODE_PRIVATE, null);
                    ContentValues cv = new ContentValues();
                    cv.put("Ignored", 1); //These Fields should be your String values of actual column names
                    db.update("Records", cv, "Created=" + "\"" + listItem.getCreated() + "\"", null);
                    listItems.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, listItems.size());
                }


            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("clicked","clicked");
                showUpdatePopup(listItem.getExpenseName(),listItem.getCatagory(),listItem.getCreated(),listItem.getExpense(),listItem.getIgnored(),position,listItem.getDetails());



            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView expenseName, expense, expenseCatagory, close,expenseDate;
        public ImageView letter;

        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            letter=itemView.findViewById(R.id.letterimage);

            expenseDate=itemView.findViewById(R.id.datetextview);
            expenseName = (TextView) itemView.findViewById(R.id.expenseNameTextView);
            expense = (TextView) itemView.findViewById(R.id.expensePriceTextview);
            expenseCatagory = (TextView) itemView.findViewById(R.id.expenseCatagoryTextView);

            close = itemView.findViewById(R.id.closeTextView);

            linearLayout = itemView.findViewById(R.id.linear_click);
        }
    }

    public void showUpdatePopup(String expenseName, String expenseCatagory, final String created, final int expenseAmount,final int ignored, final int position,final String Details) {
        TextView textClose;

        final EditText expenseNameEdit;
        final TextView detailEdit;


        final Button updateButton;



        update = new Dialog(context);
        update.setContentView(R.layout.updatepopup);


        expenseNameEdit = update.findViewById(R.id.updateexpnsenametext);
        catagoryEdit = update.findViewById(R.id.updateexpensecatagorytext);

        textClose = update.findViewById(R.id.updatetextclose);
        updateButton = update.findViewById(R.id.updateButton);
        detailEdit=update.findViewById(R.id.expensedetail);

        detailEdit.setText(Details);

        expenseNameEdit.setText(expenseName);
        catagoryEdit.setText(expenseCatagory);
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update.dismiss();
            }
        });
        update.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        update.show();
        setTag(getCatagories());
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean errorFlag=false;
                SQLiteDatabase mydatabase = context.openOrCreateDatabase("expenseDB",MODE_PRIVATE,null);
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Records(created DATETIME NOT NULL PRIMARY KEY,ExpenseName TEXT,Catagory TEXT,Expense INTEGER,Ignored INTEGER);");


                if (expenseNameEdit.getText().toString().matches("")) {
                    expenseNameEdit.setError("Enter Expense");
                    errorFlag=true;
                }
                if (catagoryEdit.getText().toString().matches("")) {
                    catagoryEdit.setError("Enter Catagory");
                    errorFlag=true;
                }

                if (errorFlag){
                    return;
                }






                ContentValues contentValues = new ContentValues();
                contentValues.put("ExpenseName", expenseNameEdit.getText().toString());
                contentValues.put("Catagory", catagoryEdit.getText().toString());
                insertCatagories(catagoryEdit.getText().toString());

                mydatabase.update("Records", contentValues, "Created=" + "\"" + created + "\"", null);

                ListItem_dailyExpense item= new ListItem_dailyExpense(
                        created,
                        expenseAmount,
                        expenseNameEdit.getText().toString(),
                        catagoryEdit.getText().toString(),
                        ignored,
                        Details
                );
                listItems.set(position, item);

               notifyItemChanged(position);
                update.dismiss();
            }
        });



    }
    private void setTag(final List<String> tagList ) {

        final ChipGroup chipGroup = update.findViewById(R.id.catagoryChips);
        for (int index = 0; index < tagList.size(); index++) {
            final String tagName = tagList.get(index);
            final Chip chip = new Chip(context);
            int paddingDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10,
                    context.getResources().getDisplayMetrics()
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
    public void insertCatagories(String catagory){
        SQLiteDatabase mydatabase = context.openOrCreateDatabase("expenseDB",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Catagories(Catagory TEXT NOT NULL PRIMARY KEY);");
        ContentValues contentValues = new ContentValues();
        contentValues.put("Catagory", catagory);
        mydatabase.insert("Catagories", null, contentValues);

    }
    public void deleteCatagories(String catagory){
        SQLiteDatabase mydatabase = context.openOrCreateDatabase("expenseDB",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Catagories(Catagory TEXT NOT NULL PRIMARY KEY);");
        ContentValues contentValues = new ContentValues();
        contentValues.put("Catagory", catagory);
        mydatabase.insert("Catagories", null, contentValues);
        mydatabase.delete("Catagories","Catagory=\""+catagory+"\"",null );

    }
    public ArrayList<String> getCatagories(){
        ArrayList<String> catagories = new ArrayList<String>();
        SQLiteDatabase mydatabase = context.openOrCreateDatabase("expenseDB",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Catagories(Catagory TEXT NOT NULL PRIMARY KEY);");
        ContentValues contentValues = new ContentValues();
        contentValues.put("Catagory", "Food");
        mydatabase.insert("Catagories", null, contentValues);
        contentValues.put("Catagory", "Groceries");
        mydatabase.insert("Catagories", null, contentValues);
        contentValues.put("Catagory", "Hospital");
        mydatabase.insert("Catagories", null, contentValues);
        String selectQuery = "SELECT  * FROM Catagories";

        Cursor cursor      = mydatabase.rawQuery(selectQuery, null);

        int i=0;
        if (cursor.moveToFirst()) {
            do {
                // get the data into array, or class variable
                catagories.add( cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return catagories;

    }

}