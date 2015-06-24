package com.example.rifatrashid.circlepong;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Reefer on 6/24/15.
 */
public class initializeGame extends Activity {
    //Start GameSurfaceView Class...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GameSurfaceView(this));
    }
}
