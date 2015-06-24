package com.example.rifatrashid.circlepong;

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
    private SurfaceHolder surfaceHolder;
    private static int degree = 30;
    private final Paint mainHeaderTextPaint = new Paint();
    private final Paint counterTextPaint = new Paint();
    private static final int paddleSpeed = 2;
    private static int counter = 0;
    private static final int BALL_SPEED = 2;
    private paddle gamePaddle;
    private arena Arena;
    private Ball ball;
    private int deltaX, deltaY = 0;
    private int shift = 1;
    private int hit = 0;
    private GameThread thread;


    public GameSurfaceView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        mainHeaderTextPaint.setColor(Color.WHITE);
        mainHeaderTextPaint.setTextSize(130);
        counterTextPaint.setColor(Color.WHITE);
        counterTextPaint.setTextSize(130);
    }

    public GameThread getThread() {
        return thread;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = surfaceHolder.lockCanvas();
        surfaceHolder.unlockCanvasAndPost(canvas);
        thread = new GameThread(surfaceHolder, getContext(), new Handler() {
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
                counter++;
                break;
        }
        return false;
    }

    //Gamethread
    class GameThread extends Thread {
        private int canvasWidth = 600;
        private int canvasHeight = 600;
        private boolean run = false;
        private boolean startDrawing = false;
        private boolean isCounting = true;

        public GameThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            surfaceHolder = surfaceHolder;
            handlerApplication = handler;
            contextApplication = context;
        }

        public void doStart() {
            synchronized (surfaceHolder) {
                gamePaddle = new paddle(200, 400, 900, 1100, degree, 30);
                Arena = new arena(550, 750, 350);
                ball = new Ball(Arena.getX(), Arena.getY(), 30);
            }
        }

        public void run() {
            while (run) {
                Canvas c = null;
                try {
                    c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        doDraw(c);
                    }
                } finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
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
            synchronized (surfaceHolder) {
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
                if (isCounting) {
                    try {
                        new CountDownTimer(4000, 1000) {
                            public void onTick(long timeUntilFinished) {
                                canvas.drawText(String.valueOf((timeUntilFinished / 1000)), Arena.getX(), Arena.getY(), counterTextPaint);
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
                if (startDrawing) {
                    int distanceFromCenter = getDistanceFromCenter();
                    if (distanceFromCenter >= (Arena.getRadius() - ball.getRadius()) && distanceFromCenter <= ((Arena.getRadius() - ball.getRadius()) + 2)) {
                        ballAngle = (360 - getBallAngle());
                        if (ballAngle >= gamePaddle.getMinDegree() && ballAngle <= gamePaddle.getMaxDegree()) {
                            hit++;
                        } else {
                            //Do nothing, ball missed paddle
                        }
                    }
                    switch (counter) {
                        case 0:
                            degree += paddleSpeed;
                            break;
                        case 1:
                            degree -= paddleSpeed;
                            break;
                        case 2:
                            //Reset counter!
                            counter = 0;
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
                    deltaX += shift;
                    deltaY += shift;
                    ball.setX(Arena.getX() + deltaX);
                    ball.setY(Arena.getY() + deltaY);
                    //fix paddle angle calculations
                    if (degree > 360) {
                        degree = degree - 360;
                    }
                    if (degree < 360) {
                        degree = 360 + degree;
                    }
                    gamePaddle.setDegree(degree);
                    canvas.drawColor(Color.parseColor("#1abc9c"));
                    gamePaddle.Draw(canvas);
                    Arena.Draw(canvas);
                    ball.Draw(canvas);
                    canvas.drawText("Circle Pong", 235, 150, mainHeaderTextPaint);
                }
                canvas.restore();
            }
        }
    }
}
