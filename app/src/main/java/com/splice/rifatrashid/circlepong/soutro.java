package com.splice.rifatrashid.circlepong;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

/**
 * Created by Reefer on 1/12/16.
 */
public class soutro extends Activity {
    private TextView logo_text;
    public Handler handler;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        //;)
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new CountDownTimer(1500, 100){
                    public void onTick(long mill){

                    }
                    public void onFinish(){
                        Intent i = new Intent(soutro.this, MainActivity.class);
                        startActivity(i);
                    }
                }.start();
            }
        });
    }
}
