package com.sanket.cashio;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.internal.GmsClientEventManager;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class smsReciever extends BroadcastReceiver {
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    public static class ExpenseData {
        public String expenseName,catagory;
        public int expense;
        public String created;
        public int ignored;
        public ExpenseData(String expenseName,String catagory,int expense,String created,int ignored) {
            this.expenseName = expenseName;
            this.catagory=catagory;
            this.expense=expense;
            this.created=created;
            this.ignored=ignored;
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("android.provider.Telephony.SMS_RECEIVED")){
           // Toast.makeText(context,"sms Recieved sanket",Toast.LENGTH_LONG).show();
            String data="";
            data=getsms(context,intent);
            validatSms(context,data);
           // createNotification(context,data);

        }
    }


        public void createNotification (Context context,String data) {
            int NOTIFICATION_ID = ( int ) System. currentTimeMillis () ;
            PendingIntent pendingIntent = PendingIntent. getActivity ( context, 0 , new Intent() , 0 ) ;
            Intent buttonIntent = new Intent( context, NotificationBroadcastReceiver.class ) ;
            buttonIntent.putExtra( "notificationId" , NOTIFICATION_ID) ;
            buttonIntent.putExtra( "message" , data) ;
            PendingIntent btPendingIntent = PendingIntent. getBroadcast ( context, 0 , buttonIntent , 0 ) ;
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService( NOTIFICATION_SERVICE ) ;
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context , default_notification_channel_id ) ;
            mBuilder.setContentTitle( "My Notification" ) ;
            mBuilder.setContentIntent(pendingIntent) ;
            mBuilder.addAction(R.drawable. ic_launcher_foreground , "Cancel" , btPendingIntent) ;
            mBuilder.setContentText( "Notification Listener Service Example" ) ;
            mBuilder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
            mBuilder.setAutoCancel( true ) ;
            mBuilder.setDeleteIntent(getDeleteIntent(context)) ;
            if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
                int importance = NotificationManager. IMPORTANCE_HIGH ;
                NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
                mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel) ;
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(NOTIFICATION_ID , mBuilder.build()) ;
        }
    protected PendingIntent getDeleteIntent (Context context) {
        Intent intent = new Intent(context,
                NotificationBroadcastReceiver. class ) ;
        intent.setAction( "notification_cancelled" ) ;
        return PendingIntent. getBroadcast (context, 0 , intent , PendingIntent. FLAG_CANCEL_CURRENT ) ;
    }
    public String getsms(Context context,Intent intent){
        String data="";
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return "";
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());


                }
                String message = sb.toString();
                data=message;
               // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            }

        }
        return data;
    }
    public SQLiteDatabase balanceDB(Context context){
        SQLiteDatabase mydatabase = context.openOrCreateDatabase("expenseDB",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Balance(LastUpdated DATETIME NOT NULL PRIMARY KEY,Balance INT,AccountNo TEXT,BankName TEXT);");
        return mydatabase;
    }
    public void validatSms(Context context,String data){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date result = new Date();

        SQLiteDatabase balanceDB=balanceDB(context);
        MainActivity.readBalance( balanceDB ,data, dateFormat.format(result));
        if (data==""){
           return;
        }
        MainActivity.ExpenseData expense ;
        String smallData=data.toLowerCase();

          Log.e("yes",data);

        if ((smallData.contains("bank")|| smallData.contains("sbi")) &&(smallData.contains("transaction")||smallData.contains("debited")) && (smallData.contains("rs")||smallData.contains("rs.")||smallData.contains("inr")) && !(smallData.contains("will")||smallData.contains("due")) ) {


            Log.e("yes2","got it");
        //extract price

        int expensePrice=0;
        String expenseName;
            String regex = "(\\$|rs|inr|inr |inr. |inr.|rs.|rs. )(\\s?[0-9,]+)";
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(smallData);

            while (matcher.find()) {
                String test=matcher.group(2);
                test=test.replace(",","");
                test=test.trim();
                System.out.println("Full match: " + test);
                expensePrice= Integer.parseInt(test);
                break;
            }


        if (expensePrice==0){
            Log.e("hi",data);
        }

        if (data.contains("UPI")){
            expenseName="UPI Transfer";
        } else{
            expenseName="Net Banking";
        }

        expense=new MainActivity.ExpenseData(expenseName,expenseName,expensePrice, dateFormat.format(result),0,data);
        SQLiteDatabase mydatabase = context.openOrCreateDatabase("expenseDB", MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Records(created DATETIME NOT NULL PRIMARY KEY,ExpenseName TEXT,Catagory TEXT,Expense INTEGER,Ignored INTEGER);");
        if (expensePrice!=0){
            MainActivity.saveToDB(expense,mydatabase);
        }

        }

    }

}
