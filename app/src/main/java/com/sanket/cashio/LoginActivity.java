package com.sanket.cashio;

import android.Manifest;

import androidx.fragment.app.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;


import android.widget.Toast;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;


public class LoginActivity extends AppIntro {

    SharedPreferences sharedpreferences;
    String[] permissions = {Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        sharedpreferences= getSharedPreferences("appData", Context.MODE_PRIVATE);

        boolean first=sharedpreferences.getBoolean("first", true);
       if (!first){
           Intent intent=new Intent(this,MainActivity.class);
           startActivity(intent);
           finish();
           return;
       }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("first", true);
        editor.commit();

        addSlide(AppIntroFragment.newInstance("Hey...","Let us track your daily transactions",R.drawable.appintroone,Color.RED,Color.BLACK,Color.BLACK, R.font.opensans ,R.font.opensans,R.drawable.background ));
        addSlide(AppIntroFragment.newInstance("Safe & Secure","No bank passwords \n or A/C numbers",R.drawable.appintrotwo,Color.RED,Color.BLACK,Color.BLACK, R.font.opensans ,R.font.opensans,R.drawable.background ));
        addSlide(AppIntroFragment.newInstance("Your Money\n Made Simple","Just need to add cash expense manually\n all online payments will track by us",R.drawable.appintrothree,Color.RED,Color.BLACK,Color.BLACK, R.font.opensans ,R.font.opensans,R.drawable.background ));




        setProgressIndicator();
        // Change Indicator Color
        setBarColor(Color.parseColor("#121212"));
        setSeparatorColor(Color.parseColor("#121212"));

        setTransformer(AppIntroPageTransformerType.Flow.INSTANCE);
        setWizardMode(true);
        setImmersiveMode();

        askForPermissions(permissions,3);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Toast.makeText(this,"skip pressed",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Toast.makeText(this,"Done pressed",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}