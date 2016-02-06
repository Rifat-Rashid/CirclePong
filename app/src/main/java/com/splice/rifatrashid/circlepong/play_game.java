package com.splice.rifatrashid.circlepong;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Random;
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
    private final Paint scorePaint = new Paint();
    private int ballRadius = 0;
    private final int FAKE_BALL_GROWTH_RATE = 11;
    private int gameScore = 0;
    private boolean finishedDrawing = false;
    private paddle Paddle;
    private Ball gameBall;
    private int numberOfScreenTaps = 0;
    private int countDown;
    private boolean countDownFinished = false;
    private int numberOfRuns = 0;
    private Paint counterPaint = new Paint();
    private boolean startGame = false;
    private static int degree = 75;
    private static final int paddleSpeed = 3;
    private static final double BALL_SPEED = 3;
    private int hit = 0;
    private int deltaX, deltaY = 0;
    private double shiftX = 3;
    private double shiftY = 3;
    private static final int centerOfCanvasX = 410;
    private static final int centerOfCanvasY = 410;
    Random r = new Random();
    public static int FRAMES_PER_SECOND = 61;
    public static double SKIP_TICKS = (1000 / FRAMES_PER_SECOND);
    public static double slope = 1;
    public static double b = 3;
    public static int chosen_sector = 4;
    public static int hits = 0;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
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

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                numberOfScreenTaps++;
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(play_game.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_left, R.anim.left_right);
    }

    class GameThread extends Thread {
        private int canvasWidth;
        private int canvasHeight;
        private boolean run = false;
        double MAX_FRAMESKIP = 5;

        public GameThread(SurfaceHolder surfaceHolder, Handler handler) {
            _surfaceHolder = surfaceHolder;
            handlerApplication = handler;
        }

        public void doStart() {

            synchronized (_surfaceHolder) {
                //Initialize stuff here!
                rightPaddle = new paddle(35, 35, 785, 785, 270, 0);
                leftPaddle = new paddle(35, 35, 785, 785, 270, 0);
                leftPaddleSmall = new paddle(35, 35, 785 + 7, 785 + 7, 90, 0);
                rightPaddleSmall = new paddle(35, 35, 785 + 7, 785 + 7, 90, 0);
                baseCirlce = new Circle(410, 410, 375);
                fakeGameBall = new Ball(410, 410, fakeGameBallRadius);
                Paddle = new paddle(35, 35, 785 + 7, 785 + 7, 75, 30);
                gameBall = new Ball(baseCirlce.getX(), baseCirlce.getY(), fakeGameBallRadius);
                //---------------------------------------------------------------------------------
                //right paddle paint
                rightPaddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                rightPaddlePaint.setAntiAlias(true);
                rightPaddlePaint.setColor(Color.parseColor("#191819"));
                rightPaddlePaint.setStyle(Paint.Style.STROKE);
                rightPaddlePaint.setStrokeWidth(3.5f);
                rightPaddle.setPaint(rightPaddlePaint);
                //Paddle2
                leftPaddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                leftPaddlePaint.setAntiAlias(true);
                leftPaddlePaint.setColor(Color.parseColor("#191819"));
                leftPaddlePaint.setStyle(Paint.Style.STROKE);
                leftPaddlePaint.setStrokeWidth(3.5f);
                leftPaddle.setPaint(leftPaddlePaint);
                //Paint for small paddles
                smallPaddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                smallPaddlePaint.setAntiAlias(true);
                smallPaddlePaint.setColor(Color.parseColor("#191819"));
                smallPaddlePaint.setStyle(Paint.Style.STROKE);
                smallPaddlePaint.setStrokeWidth(14.0f);
                leftPaddleSmall.setPaint(smallPaddlePaint);
                rightPaddleSmall.setPaint(smallPaddlePaint);
                Paddle.setPaint(smallPaddlePaint);
                //Paint for base circle
                baseCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                baseCirclePaint.setAntiAlias(true);
                baseCirclePaint.setStyle(Paint.Style.FILL);
                baseCirclePaint.setColor(Color.parseColor("#FFFFFF"));
                baseCirlce.setPaint(baseCirclePaint);
                //fake game ball paint
                fakeBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                fakeBallPaint.setAntiAlias(true);
                fakeBallPaint.setStyle(Paint.Style.FILL);
                fakeBallPaint.setColor(Color.parseColor("#2a2a2a"));
                fakeGameBall.setPaint(fakeBallPaint);
                gameBall.setPaint(fakeBallPaint);
                //paint for score text
                scorePaint.setColor(Color.parseColor("#b2b2b2"));
                scorePaint.setTextSize(290);
                //Counter paint
                counterPaint.setColor(Color.parseColor("#b2b2b2"));
                counterPaint.setTextSize(75);
                //---------------------------------------------------------------------------------
            }
        }

        /*
        public void run() {
            //begin of loop!
            long beginTime;
            long timeDiff;
            long sleepTime;
            long framesSkipped;
            sleepTime = 0;
            while (run) {
                Canvas c = null;
                beginTime = System.currentTimeMillis();
                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        framesSkipped = 0;
                        doDraw(c);
                    }
                } finally {
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (long) (SKIP_TICKS - timeDiff);
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);

                        } catch (Exception e) {

                        }
                    }
                }
            }
        }

        */


        public void run(){
            double next_game_tick = System.currentTimeMillis();
            int loops;
            while(run){
                loops = 0;
                while(System.currentTimeMillis() > next_game_tick && loops < MAX_FRAMESKIP){
                    Canvas c = null;
                    try{
                        c = _surfaceHolder.lockCanvas(null);
                        synchronized (_surfaceHolder){
                            doDraw(c);
                            next_game_tick += SKIP_TICKS;
                            loops++;
                        }
                    }finally {
                        if( c != null){
                            _surfaceHolder.unlockCanvasAndPost(c);
                        }
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

        //Any calculations in the game goes here...
        //--------------------------------------------------------------------------------------------
        private int getDistanceFromCenter() {
            int ballX = deltaX;
            int ballY = deltaY;
            int deltaX = (int) Math.pow((ballX), 2);
            int deltaY = (int) Math.pow((ballY), 2);
            int deltaXY = (int) (deltaX + deltaY);
            int distance = (int) Math.sqrt(deltaXY);
            return distance;
        }

        //Angle variable for the ball!
        int angle;

        private int getBallAngle() {
            int diffX = deltaX;
            int diffY = deltaY;
            try {
                angle = (int) (Math.atan2(-diffY, diffX) * 180 / Math.PI);
                if (angle < 0) {
                    angle = 360 + angle;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return angle;

        }

        //given 2 points create a line
        private void formulateNewLine(Point p1, Point p2) {
            //y = mx + b
            slope = (p1.y - p2.y) / (p1.x - p2.x);
            b = p1.y - (slope * p1.x);
        }

        private int newBallY(int x) {
            return (int) (slope * x + b);
        }
        //--------------------------------------------------------------------------------------------

        int ballAngle;

        private void doDraw(final Canvas canvas) {
            if (run) {
                canvas.save();
                canvas.drawColor(Color.parseColor("#e74c3c"));
                baseCirlce.Draw(canvas);
                if (gameScore < 10) {
                    String textString = "0" + gameScore;
                    int textWidth = (int) scorePaint.measureText(textString);
                    canvas.drawText("0" + gameScore, 410 - textWidth / 2, 410 + textWidth / 3, scorePaint);
                } else if (gameScore >= 10) {
                    String textString = String.valueOf(gameScore);
                    int textWidth = (int) scorePaint.measureText(textString);
                    canvas.drawText(String.valueOf(gameScore), 410 - textWidth / 2, 410 + textWidth / 3, scorePaint);
                }
                if (!finishedDrawing) {
                    rightPaddle.Draw(canvas);
                    leftPaddle.Draw(canvas);
                    leftPaddleSmall.Draw(canvas);
                    rightPaddleSmall.Draw(canvas);
                    fakeGameBall.Draw(canvas);
                } else if (finishedDrawing) {
                    switch (numberOfRuns) {
                        case 0:
                            startTimer();
                            numberOfRuns++;
                            break;
                        default:
                            //do nothing!
                            break;
                    }
                    if (!countDownFinished) {
                        int textWidth = (int) counterPaint.measureText(String.valueOf(countDown));
                        canvas.drawText(String.valueOf(countDown), 410 - textWidth / 2, 410 + 200, counterPaint);
                    }

                    //when game starts do...
                    //----------------------------------------------------------------------------------------------
                    int iX = 0;
                    int iY = 0;
                    int r1 = 0;
                    int r2 = 0;
                    if (startGame) {
                        //the 3.5 is the stroke width
                        int distanceFromCenter = getDistanceFromCenter();

                        if ((distanceFromCenter >= (baseCirlce.getRadius() - gameBall.getRadius()))) {
                            ballAngle = (360 - getBallAngle());
                            if (ballAngle >= Paddle.getMinDegree()  && ballAngle <= Paddle.getMaxDegree()) {
                                iX = deltaX;
                                iY = deltaY;
                                r1 = r.nextInt(5) - 2;
                                r2 = r.nextInt(5) - 2;

                                hit++;
                                gameScore++;
                            } else {
                                //Do nothing, ball missed paddle
                            }

                        }

                        if(startGame) {
                            switch (numberOfScreenTaps) {
                                case 0:
                                    break;
                                case 1:
                                    degree -= paddleSpeed;
                                    break;
                                case 2:
                                    degree += paddleSpeed;
                                    break;
                                case 3:
                                    //Reset counter!
                                    numberOfScreenTaps = 1;
                                    degree += paddleSpeed;
                                    break;
                            }
                        }
                        //System.out.println(gameBall.getX() + ", " + gameBall.getY());
                        //old detection system

                        if(iX > 0 && iY > 0) {
                            shiftX = -4+r1;
                            shiftY = -4+r2;
                        }
                        if(iX < 0 && iY > 0) {
                            shiftX = 4+r1;
                            shiftY = -4+r2;
                        }
                        if(iX < 0 && iY < 0) {
                            shiftX = 4+r1;
                            shiftY = 4+r2;
                        }
                        if(iX > 0 && iY < 0) {
                            shiftX = -4+r1;
                            shiftY = 4+r2;
                        }
                        deltaX += shiftX;
                        deltaY += shiftY;
                        gameBall.setX(centerOfCanvasX + deltaX);
                        gameBall.setY(centerOfCanvasY + deltaY);
                        System.out.println(gameBall.getX() + "," + gameBall.getY());
                        //System.out.println("Ball Y: " + newBallY(gameBall.getX()));
                        Log.d("", "Ball Y: " + newBallY(gameBall.getX()));
                        if (distanceFromCenter >= 375 + gameBall.getRadius()) {
                            resetGame();
                        }
                        if (degree > 360) {
                            degree = degree - 360;
                        }
                        if (degree < 360) {
                            degree = 360 + degree;
                        }
                    } else if (!startGame) {
                        //Start game = false...
                    }
                    //----------------------------------------------------------------------------------------------
                    rightPaddle.Draw(canvas);
                    leftPaddle.Draw(canvas);
                    gameBall.setRadius(ballRadius);
                    gameBall.Draw(canvas);
                    Paddle.setDegree(degree);
                    Paddle.Draw(canvas);
                }

                if (rightArcLength <= 180) {
                    rightArcLength += 4;
                    rightPaddle.setArcLength(rightArcLength);
                }

                if (leftArcLength <= 180) {
                    leftArcLength -= 4;
                    leftPaddle.setArcLength(leftArcLength);
                }

                if (rightArcLength >= 180) {
                    if (rightArcSmall >= -15) {
                        rightArcSmall -= 2;
                        rightPaddleSmall.setArcLength(rightArcSmall);
                    }
                }

                if (-leftArcLength >= 180) {
                    if (leftArcSmall <= 15) {
                        leftArcSmall += 2;
                        leftPaddleSmall.setArcLength(leftArcSmall);
                        if (ballRadius <= 22) {
                            ballRadius += FAKE_BALL_GROWTH_RATE;
                            fakeGameBall.setRadius(ballRadius);
                        } else {
                            finishedDrawing = true;
                        }
                    }
                }

                canvas.restore();
            }
        }

        private void resetGame() {
            gameBall.setX(baseCirlce.getX());
            deltaX = 0;
            deltaY = 0;
            b = 3;
            gameBall.setY(baseCirlce.getY());
            Paddle.setDegree(75);
            slope = 1;
            degree = 75;
            shiftX = 3;
            shiftY = 3;
            hits = 0;
            countDownFinished = false;
            startGame = false;
            gameScore = 0;
            startTimer();
        }

        private void startTimer() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new CountDownTimer(4100, 1000) {
                        public void onTick(long millisUntilFinished) {
                            countDown = (int) millisUntilFinished / 1000;
                        }

                        public void onFinish() {
                            countDownFinished = true;
                            startGame = true;
                            numberOfScreenTaps++;
                        }
                    }.start();
                }
            });
        }
    }
}

