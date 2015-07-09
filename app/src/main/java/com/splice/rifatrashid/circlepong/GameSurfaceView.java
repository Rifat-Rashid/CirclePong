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
                Arena = new arena(550, 850, 350);
                ball = new Ball(Arena.getX(), Arena.getY(), 30);
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
                //Edited
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
                canvas.restore();
            }
        }
    }
}
