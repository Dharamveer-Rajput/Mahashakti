package com.mahashakti.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.mahashakti.R;
import com.mahashakti.baseactivity.BaseActivity;
import com.mahashakti.sharedPreferences.UserDataUtility;

public class SplashActivity extends BaseActivity {


    // Splash screen timer
    private volatile boolean interrupt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.SplashTheme_Launcher);
        super.onCreate(savedInstanceState);


        // fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);




        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (!interrupt) {


                        if(UserDataUtility.getLogin(SplashActivity.this)){  //true

                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            startActivity(intent);
                            finish();

                            return;
                        }
                        else {  //false

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();

                        }

                    }
                }
            }
        };
        timer.start();


    }



    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        interrupt = true;
    }


}
