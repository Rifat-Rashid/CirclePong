package com.splice.rifatrashid.circlepong;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Rifat Rashid on 7/20/2015.
 */
public class play_game extends Activity implements SurfaceHolder.Callback {
    private Handler handlerApplication;
    private SurfaceHolder _surfaceHolder;
    private GameThread thread;
    private SurfaceView _surfaceView;
    private paddle rightPaddle;
    private paddle leftPaddle;
    private Paint rightPaddlePaint;
    private Paint leftPaddlePaint;
    private paddle leftPaddleSmall;
    private paddle rightPaddleSmall;
    private Paint smallPaddlePaint;
    private int leftArcSmall = 0;
    private int rightArcSmall = 0;
    private int rightArcLength = 0;
    private int leftArcLength = 0;
    private Paint baseCirclePaint;
    private Circle baseCirlce;
    private Ball fakeGameBall;
    private Paint fakeBallPaint;
    private int fakeGameBallRadius = 0;
    private final Paint mainHeaderTextPaint = new Paint();
    private int ballRadius = 0;
    private final int FAKE_BALL_GROWTH_RATE = 11;
    private int gameScore = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_game_screen);
        _surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        _surfaceHolder = _surfaceView.getHolder();
        _surfaceHolder.addCallback(this);
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
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class GameThread extends Thread {
        private int canvasWidth;
        private int canvasHeight;
        private boolean run = false;

        public GameThread(SurfaceHolder surfaceHolder, Handler handler) {
            _surfaceHolder = surfaceHolder;
            handlerApplication = handler;
        }

        public void doStart() {
            synchronized (_surfaceHolder) {
                //Initialize stuff here!
                rightPaddle = new paddle(35, 35, 785, 785, 270, 0);
                leftPaddle = new paddle(35, 35, 785, 785, 270, 0);
                leftPaddleSmall = new paddle(35, 35, 785,785, 90, 0);
                rightPaddleSmall = new paddle(35, 35,785,785, 90, 0);
                baseCirlce = new Circle(410, 410, 375);
                fakeGameBall = new Ball(410, 410, fakeGameBallRadius);
                //---------------------------------------------------------------------------------
                //right paddle paint
                rightPaddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                rightPaddlePaint.setAntiAlias(true);
                rightPaddlePaint.setColor(Color.parseColor("#FF2D55"));
                rightPaddlePaint.setStyle(Paint.Style.STROKE);
                rightPaddlePaint.setStrokeWidth(3.5f);
                rightPaddle.setPaint(rightPaddlePaint);
                //Paddle2
                leftPaddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                leftPaddlePaint.setAntiAlias(true);
                leftPaddlePaint.setColor(Color.parseColor("#FF2D55"));
                leftPaddlePaint.setStyle(Paint.Style.STROKE);
                leftPaddlePaint.setStrokeWidth(3.5f);
                leftPaddle.setPaint(leftPaddlePaint);
                //Paint for small paddles
                smallPaddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                smallPaddlePaint.setAntiAlias(true);
                smallPaddlePaint.setColor(Color.parseColor("#FF2D55"));
                smallPaddlePaint.setStyle(Paint.Style.STROKE);
                smallPaddlePaint.setStrokeWidth(12.0f);
                leftPaddleSmall.setPaint(smallPaddlePaint);
                rightPaddleSmall.setPaint(smallPaddlePaint);
                //Paint for base circle
                baseCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                baseCirclePaint.setAntiAlias(true);
                baseCirclePaint.setStyle(Paint.Style.FILL);
                baseCirclePaint.setColor(Color.parseColor("#2a2a2a"));
                baseCirlce.setPaint(baseCirclePaint);
                //fake game ball paint
                fakeBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                fakeBallPaint.setAntiAlias(true);
                fakeBallPaint.setStyle(Paint.Style.FILL);
                fakeBallPaint.setColor(Color.parseColor("#FFFFFF"));
                fakeGameBall.setPaint(fakeBallPaint);
                //paint for score text
                mainHeaderTextPaint.setColor(Color.parseColor("#191819"));
                mainHeaderTextPaint.setTextSize(260);
                //---------------------------------------------------------------------------------
            }
        }

        public void run() {
            while (run) {
                Canvas c = null;
                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        doDraw(c);
                    }
                } finally {
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        public void setRunning(boolean b) {
            run = b;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (_surfaceHolder) {
                canvasHeight = height;
                canvasWidth = width;
                doStart();
            }
        }

        private void doDraw(final Canvas canvas) {
            if (run) {
                canvas.save();
                canvas.drawColor(Color.parseColor("#191819"));
                baseCirlce.Draw(canvas);
                if(gameScore < 10){
                    String textString = "0" + gameScore;
                    int textWidth = (int) mainHeaderTextPaint.measureText(textString);
                    canvas.drawText("0" + gameScore, 410 - textWidth/2, 410 + textWidth/2, mainHeaderTextPaint);
                }else if(gameScore >= 10){
                    String textString = String.valueOf(gameScore);
                    int textWidth = (int) mainHeaderTextPaint.measureText(textString);
                    canvas.drawText(String.valueOf(gameScore), 410 - textWidth/2, 410 + textWidth/2, mainHeaderTextPaint);//
                }
                mainHeaderTextPaint.measureText("00");
                rightPaddle.Draw(canvas);
                leftPaddle.Draw(canvas);
                leftPaddleSmall.Draw(canvas);
                rightPaddleSmall.Draw(canvas);
                fakeGameBall.Draw(canvas);
                if(rightArcLength <= 180){
                    rightArcLength += 4;
                    rightPaddle.setArcLength(rightArcLength);
                }
                if(leftArcLength <= 180){
                    leftArcLength -= 4;
                    leftPaddle.setArcLength(leftArcLength);
                }

                if(rightArcLength >= 180){
                    if(rightArcSmall >= -15){
                        rightArcSmall -= 2;
                        rightPaddleSmall.setArcLength(rightArcSmall);
                    }
                }

                if(-leftArcLength >= 180){
                    if(leftArcSmall <=15){
                        leftArcSmall += 2;
                        leftPaddleSmall.setArcLength(leftArcSmall);
                        if(ballRadius <= 22){
                            ballRadius += FAKE_BALL_GROWTH_RATE;
                        }
                    }
                }
                fakeGameBall.setRadius(ballRadius);
                canvas.restore();
            }
        }
    }
}

