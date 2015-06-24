package com.example.rifatrashid.circlepong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.logging.Handler;

/**
 * Created by Reefer on 6/24/15.
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public static Context context;
    public static Handler handler;
    private SurfaceHolder surfaceHolder;private static int degree = 30;
    private final Paint mainHeaderTextPaint = new Paint();
    private static final int paddleSpeed = 2;
    private static int counter = 0;
    private static final int BALL_SPEED = 2;
    private paddle gamePaddle;
    private arena Arena;
    private Ball ball;
    private int deltaX, deltaY = 0;
    private int shift = 1;
    private int hit = 0;


    public GameSurfaceView(Context context){
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = surfaceHolder.lockCanvas();
        surfaceHolder.unlockCanvasAndPost(canvas);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
