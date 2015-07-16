package com.splice.rifatrashid.circlepong;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Reefer on 7/9/15.
 */
public class gameStartup extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GameSurfaceView(this));
    }
}
