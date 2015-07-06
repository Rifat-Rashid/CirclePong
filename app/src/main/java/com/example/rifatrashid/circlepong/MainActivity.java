package com.example.rifatrashid.circlepong;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private TextView titleText;
    private Handler handlerApplication;
    private Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private SurfaceHolder _surfaceHolder;
    private GameThread thread;
    private SurfaceView _surfaceView;
    private paddle Paddle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        _surfaceHolder = _surfaceView.getHolder();
        _surfaceHolder.addCallback(this);
        //setupCanvas();
        /*
        //Create custom bitmap to draw on!
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Draw to bitmap!
                Bitmap tempBitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(tempBitmap);
                canvas.drawColor(Color.parseColor("#191919"));
                Arena.Draw(canvas);s
                ball.Draw(canvas);
                Paddle.Draw(canvas);
                imageView.setImageBitmap(tempBitmap);
            }
        }).start();
        */
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/Lato-Thin.ttf");
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setTypeface(lato);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new GameThread(_surfaceHolder, new Handler() {
            @Override
            public void close() {

            }

            @Override
            public void flush() {

            }

            @Override
            public void publish(LogRecord record) {

            }
        });
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while(retry){
            try{
                thread.join();
                retry = false;
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    class  GameThread extends Thread{
        private int canvasWidth;
        private int canvasHeight;
        private boolean run = false;

        public GameThread(SurfaceHolder surfaceHolder,Handler handler){
            _surfaceHolder = surfaceHolder;
            handlerApplication = handler;
        }

        public void doStart(){
            synchronized (_surfaceHolder){
                //Initialize stuff here!
                Paddle = new paddle(15,15 ,585, 585, 0, 360);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                paint.setColor(Color.parseColor("#FF2D55"));
                myPaint.setColor(Color.parseColor("#FFFFFF"));
                Paddle.setPaint(paint);
            }
        }

        public void run(){
            while(run){
                Canvas c = null;
                try{
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder){
                        doDraw(c);
                    }
                }finally {
                    if(c != null){
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        public void setRunning(boolean b){
            run = b;
        }

        public void setSurfaceSize(int width, int height){
            synchronized (_surfaceHolder){
                canvasHeight = height;
                canvasWidth = width;
                doStart();
            }
        }

        private void doDraw(final Canvas canvas){
            canvas.save();
            canvas.drawColor(Color.parseColor("#191919"));
            //Arenaa.Draw(canvas);
            Paddle.Draw(canvas);
            canvas.restore();
        }
    }
}
