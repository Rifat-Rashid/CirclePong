package com.example.rifatrashid.circlepong;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.logging.Handler;

/**
 * Created by Reefer on 6/24/15.
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public static Context context;
    public static Handler handler;
    private SurfaceHolder surfaceHolder;

    public GameSurfaceView(Context context){

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
