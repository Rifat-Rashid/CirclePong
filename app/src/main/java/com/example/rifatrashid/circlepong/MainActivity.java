package com.example.rifatrashid.circlepong;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/Lato-Thin.ttf");
    }
}
