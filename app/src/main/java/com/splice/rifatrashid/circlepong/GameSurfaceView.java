package com.splice.rifatrashid.circlepong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Reefer on 6/24/15.
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public static Context contextApplication;
    public static Handler handlerApplication;
    private SurfaceHolder surfaceHolderApplication;
    //Variable to keep track of the number of times the user touches the screen
    private int numberOfScreenTaps = 0;
    private boolean startDrawing = true;
    private boolean isCounting = false;
    private static int degree = 75;
    private final Paint mainHeaderTextPaint = new Paint();
    private final Paint counterTextPaint = new Paint();
    private final Paint subHeaderText = new Paint();
    private static final int paddleSpeed = 3;
    private static int counter = 0;
    private static final int BALL_SPEED  = 5;
    private paddle gamePaddle;
    private arena Arena;
    private Ball ball;
    private int deltaX, deltaY = 0;
    private int shift = 1;
    private int hit = 0;
    private boolean moveBall = false;
    private GameThread thread;
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
    private int ballRadius = 0;
    private final int FAKE_BALL_GROWTH_RATE = 11;

    public GameSurfaceView(Context context) {
        super(context);
        surfaceHolderApplication = getHolder();
        surfaceHolderApplication.addCallback(this);
        mainHeaderTextPaint.setColor(Color.WHITE);
        mainHeaderTextPaint.setTextSize(130);
        counterTextPaint.setColor(Color.WHITE);
        counterTextPaint.setTextSize(130);
        subHeaderText.setColor(Color.WHITE);
        subHeaderText.setTextSize(65);
    }

    public GameThread getThread() {
        return thread;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = surfaceHolderApplication.lockCanvas();
        surfaceHolderApplication.unlockCanvasAndPost(canvas);
        thread = new GameThread(surfaceHolderApplication, getContext(), new Handler() {
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
        //Currently being unused due to trouble with the countdown...(attempt fixing later)
        if (isCounting) {
            try {
                new CountDownTimer(4000, 1000) {
                    public void onTick(long timeUntilFinished) {
                        //unused code, uncommenting causes an error, fix later...
                        //counterTime = timeUntilFinished;
                    }

                    @Override
                    public void onFinish() {
                        //Stop the counter, start drawing objects on Canvas!
                        isCounting = false;
                        startDrawing = true;
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                counter++;
                numberOfScreenTaps++;
                break;
        }
        return false;
    }

    //Gamethread
    class GameThread extends Thread {
        private int canvasWidth = 600;
        private int canvasHeight = 600;
        private boolean run = false;

        public GameThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            surfaceHolderApplication = surfaceHolder;
            handlerApplication = handler;
            contextApplication = context;
        }

        //750
        public void doStart() {
            synchronized (surfaceHolderApplication) {
                gamePaddle = new paddle(200, 500, 900, 1200, degree, 30);
                rightPaddle = new paddle(200, 500, 900, 1200, 270, 0);
                leftPaddle = new paddle(200, 500, 900, 1200, 270, 0);
                leftPaddleSmall = new paddle(200, 500, 900, 1200, 90, 0);
                rightPaddleSmall = new paddle(200, 500, 900, 1200, 90, 0);
                baseCirlce = new Circle(550, 850, 350);
                Arena = new arena(550, 850, 350);
                ball = new Ball(Arena.getX(), Arena.getY(), 30);
                fakeGameBall = new Ball(Arena.getX(), Arena.getY(), fakeGameBallRadius);
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
                //---------------------------------------------------------------------------------
            }
        }

        public void run() {
            while (run) {
                Canvas c = null;
                try {
                    c = surfaceHolderApplication.lockCanvas(null);
                    synchronized (surfaceHolderApplication) {
                        doDraw(c);
                    }
                } finally {
                    if (c != null) {
                        surfaceHolderApplication.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        public void setRunning(boolean b) {
            run = b;
        }

        //Set startDrawing!
        public void setStartDrawing(boolean b) {
            startDrawing = b;
        }

        //Set wether timer is counting down or not!
        public void setIsCounting(boolean b) {
            isCounting = b;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (surfaceHolderApplication) {
                canvasWidth = width;
                canvasHeight = height;
                doStart();
            }
        }

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

        int ballAngle;

        private void doDraw(final Canvas canvas) {
            if (run) {
                canvas.save();
                canvas.drawColor(Color.parseColor("#191819"));
                baseCirlce.Draw(canvas);
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

                //Edited
                /*
                if (startDrawing) {
                    int distanceFromCenter = getDistanceFromCenter();
                    if (distanceFromCenter >= (Arena.getRadius() - ball.getRadius()) && distanceFromCenter <= ((Arena.getRadius() - ball.getRadius()) + 4)) {
                        ballAngle = (360 - getBallAngle());
                        if (ballAngle >= gamePaddle.getMinDegree() && ballAngle <= gamePaddle.getMaxDegree()) {
                            hit++;
                        } else {
                            //Do nothing, ball missed paddle
                        }
                    }
                    switch (counter) {
                        case 0:
                            break;
                        case 1:
                            degree += paddleSpeed;
                            break;
                        case 2:
                            degree -= paddleSpeed;
                             break;
                        case 3:
                            //Reset counter!
                            counter = 1;
                            degree += paddleSpeed;
                            break;
                    }
                    switch (hit) {
                        case 0:
                            shift = BALL_SPEED;
                            break;
                        case 1:
                            shift = -BALL_SPEED;
                            break;
                        case 2:
                            hit = 0;
                            shift = BALL_SPEED;
                            break;
                    }
                    if(moveBall) {
                        deltaX += shift;
                        deltaY += shift;
                    }
                    if(!moveBall){
                        deltaX = 0;
                        deltaY = 0;
                    }
                    ball.setX(Arena.getX() + deltaX);
                    ball.setY(Arena.getY() + deltaY);
                    //fix paddle angle calculations
                    if (degree > 360) {
                        degree = degree - 360;
                    }
                    if (degree < 360) {
                        degree = 360 + degree;
                    }
                    canvas.drawColor(Color.parseColor("#1abc9c"));
                    gamePaddle.setDegree(degree);
                    rightPaddle.Draw(canvas);
                    ball.Draw(canvas);
                    gamePaddle.Draw(canvas);
                    Arena.Draw(canvas);
                    canvas.drawText("Circle Pong", 235, 200, mainHeaderTextPaint);
                    switch (numberOfScreenTaps) {
                        case 0:
                            canvas.drawText("Tap to steady paddle", Arena.getX() - 275, Arena.getY() + Arena.getRadius() + 150, subHeaderText);
                            break;
                        case 1:
                            canvas.drawText("Tap to start", Arena.getX() - 150, Arena.getY() + Arena.getRadius() + 150, subHeaderText);
                            break;
                        case 2:
                            moveBall = true;
                            break;
                    }
                }
                */
                canvas.restore();
            }
        }
    }
}
