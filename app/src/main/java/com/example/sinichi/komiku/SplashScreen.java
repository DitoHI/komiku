package com.example.sinichi.komiku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class SplashScreen extends Activity{
    private  static  int progress;
    private ProgressBar progressBar;
    private  int progressStatus=0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);

        progress=0;
        //progressBar = (ProgressBar) findViewById(R.id.progressBar.essBar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus<5){
                    progressStatus = loading();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent exit =
                                new Intent(SplashScreen.this,MainActivity.class);
                        startActivity(exit);
                        finish();
                    }
                });
            }

            private int loading(){
                try {
                    Thread.sleep(500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                return ++progress;
            }
        }).start();

    }

}
