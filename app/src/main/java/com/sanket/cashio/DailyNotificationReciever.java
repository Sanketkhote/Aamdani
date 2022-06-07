package com.sanket.cashio;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class DailyNotificationReciever extends BroadcastReceiver {
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        int expense=getTodaysExpense(context);
        String notificationTitle="You've Spent "+expense+" Rs Today!";
        createNotification(context,notificationTitle);
    }
    public void createNotification (Context context,String data) {
        int NOTIFICATION_ID = ( int ) System. currentTimeMillis () ;
        PendingIntent pendingIntent = PendingIntent.getActivity ( context, 0 , new Intent() , PendingIntent.FLAG_MUTABLE ) ;
        Intent buttonIntent = new Intent( context, NotificationBroadcastReceiver.class ) ;
        buttonIntent.putExtra( "notificationId" , NOTIFICATION_ID) ;
        buttonIntent.putExtra( "message" , data) ;
        PendingIntent btPendingIntent = PendingIntent. getBroadcast ( context, 0 , buttonIntent , PendingIntent.FLAG_MUTABLE ) ;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService( NOTIFICATION_SERVICE ) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context , default_notification_channel_id ) ;
        mBuilder.setContentTitle( data ) ;
        mBuilder.setContentIntent(pendingIntent) ;
        mBuilder.addAction(R.drawable.ic_baseline_timeline_24 , "Cancel" , btPendingIntent) ;

        mBuilder.setContentText( "Please Add Your Cash Expenses" ) ;
        mBuilder.setSmallIcon(R.drawable. cashio ) ;
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
        return PendingIntent. getBroadcast (context, 0 , intent , PendingIntent.FLAG_MUTABLE ) ;
    }

    public int getTodaysExpense(Context context) {

        SQLiteDatabase mydatabase = context.openOrCreateDatabase("expenseDB", context.MODE_PRIVATE, null);

        String selectQuery = "SELECT SUM(Expense) FROM Records WHERE Date(Created)=Date('now','localtime') AND ignored=0 AND investment=0";

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
}
