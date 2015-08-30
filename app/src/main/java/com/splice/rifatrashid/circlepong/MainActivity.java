package com.splice.rifatrashid.circlepong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MainActivity extends Activity implements SurfaceHolder.Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView titleText, titleText2;
    private Handler handlerApplication;
    private SurfaceHolder _surfaceHolder;
    private GameThread thread;
    private SurfaceView _surfaceView;
    private paddle Paddle;
    private final int DEGREE_ARC_1 = 270;
    private int arc1Length = 0;
    private Circle baseCircle;
    //Starting radius for baseCircle
    private int baseCircleRadius = 0;
    private int ballRadius = 0;
    private Paint baseCirclePaint;
    private final int BASECIRCLE_GROWTH_RATE = 13;
    private Ball ball;
    private final int BALL_GROWTH_RATE = 4;
    private Paint ballPaint;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private boolean ballObjectDrawn = false;
    private ImageButton achievementButton, leaderboard_btn, play_btn;
    private paddle padLeft, padRight;
    private int deltaX, deltaY = 0;
    private boolean getBallXY = false;
    protected PowerManager.WakeLock wakeLock;
    private int dX, dY = 1;
    private int hits = 0;
    private Random random = new Random();
    private int max = 3;
    private int min = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Lock");
        this.wakeLock.acquire();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Games.SCOPE_GAMES)
                .build();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        play_btn = (ImageButton) findViewById(R.id.playButton);
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(MainActivity.this, play_game.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.animation, R.anim.animation2);
                } catch (Exception e) {
                    //Error occured with starting single player game variant!
                    e.printStackTrace();
                }
            }
        });

        leaderboard_btn = (ImageButton) findViewById(R.id.leaderboard_btn);
        leaderboard_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mGoogleApiClient.isConnected()) {
                        startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), 1);
                    } else if (!mGoogleApiClient.isConnected()) {
                        //Error with connecting to leaderboards
                        System.out.println("Could not start leaderboards Intent");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        _surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        _surfaceHolder = _surfaceView.getHolder();
        _surfaceHolder.addCallback(this);
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
        Typeface lato2 = Typeface.createFromAsset(getAssets(), "fonts/Lato-Thin.ttf");
        titleText2 = (TextView) findViewById(R.id.titleText2);
        titleText2.setTypeface(lato);
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setTypeface(lato2);

        achievementButton = (ImageButton) findViewById(R.id.achievement_btn);
        achievementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start ahcievments activity!
                //@Provided by google
                try {
                    if (mGoogleApiClient.isConnected()) {
                        startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 1);
                    } else if (!mGoogleApiClient.isConnected()) {
                        System.out.println("Could not start achievements Intent");
                    }
                } catch (Exception e) {
                    //Error with starting achievments activity!
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.wakeLock.release();
    }

    @Override
    public void onResume(){
        super.onResume();
        this.wakeLock.acquire();
    }

    @Override
    public void onPause(){
        super.onPause();
        this.wakeLock.release();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
        new CountDownTimer(300, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                //Do Nothing!
                _surfaceView.setBackgroundColor(Color.parseColor("#e74c3c"));
            }

            @Override
            public void onFinish() {
                _surfaceView.setBackgroundColor(Color.parseColor("#00000000"));
                thread.setRunning(true);
                thread.start();
            }
        }.start();
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
    public void onConnected(Bundle bundle) {
        System.out.println("Connected!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingError) {
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                //If theres an error try to connect again
                mGoogleApiClient.connect();
            }
        } else {
            mResolvingError = true;
        }
        System.out.println(connectionResult);
    }

    //Override default onActivityResult...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
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
                Paddle = new paddle(15, 15, 585, 585, DEGREE_ARC_1, arc1Length);
                baseCircle = new Circle(300, 300, baseCircleRadius);
                ball = new Ball(300, 300, ballRadius);
                padRight = new paddle(15, 15, 585, 585, 90, 20);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setColor(Color.parseColor("#2a2a2a"));
                Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint1.setStrokeWidth(20);
                paint1.setColor(Color.parseColor("#2a2a2a"));
                padRight.setPaint(paint1);
                baseCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                baseCirclePaint.setAntiAlias(true);
                baseCirclePaint.setStyle(Paint.Style.FILL);
                baseCirclePaint.setColor(Color.parseColor("#d3d3d3"));
                ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                ballPaint.setAntiAlias(true);
                ballPaint.setStyle(Paint.Style.FILL);
                ballPaint.setColor(Color.parseColor("#2a2a2a"));
                Paddle.setPaint(paint);
                baseCircle.setPaint(baseCirclePaint);
                ball.setPaint(ballPaint);
                //Set deltaX and deltaY
                deltaX = ball.getX();
                deltaY = ball.getY();
            }
        }

        private int getDistanceFromCenter() {
            int centerX = baseCircle.getX();
            int centerY = baseCircle.getY();
            int ballX = deltaX;
            int ballY = deltaY;
            int deltaX = (int) Math.abs(centerX - ballX);
            int deltaY = (int) Math.abs(centerY - ballY);
            int deltaXs = (int) Math.pow(deltaX, 2);
            int deltaYs = (int) Math.pow(deltaY, 2);
            int deltaXY = (int) (deltaXs + deltaYs);
            int distance = (int) Math.sqrt(deltaXY);
            System.out.println(distance);
            return distance;
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
                canvas.drawColor(Color.parseColor("#e74c3c"));
                baseCircle.Draw(canvas);
                Paddle.Draw(canvas);
                ball.Draw(canvas);
                if (arc1Length <= 360) {
                    arc1Length += 7;
                    Paddle.setArcLength(arc1Length);
                }
                if (arc1Length >= 360) {
                    if (baseCircleRadius <= 280) {
                        baseCircleRadius += BASECIRCLE_GROWTH_RATE;
                        baseCircle.setRadius(baseCircleRadius);
                    }
                }
                if (baseCircleRadius >= 255) {
                    if (ballRadius <= 22) {
                        ballRadius += BALL_GROWTH_RATE;
                        ball.setRadius(ballRadius);
                    }
                }
                if(ball.getRadius() >= 22){
                    //Last object to be drawn: stop the thread
                    ballObjectDrawn = true;
                }
                if(ballObjectDrawn){
                    //Allow ball to move
                    if(getDistanceFromCenter() >= 300 - (ball.getRadius()/2 + ball.getRadius())) {
                        hits++;
                    }
                    switch (hits){
                        case 0:
                            dX = random.nextInt(max - min + 1) + min;
                            dY = random.nextInt(max - min + 1) + min;
                            break;
                        case 1:
                            int random2 = random.nextInt(max - min + 1) + min;
                            dX = -random2;
                            dY = -random2;
                            break;
                        case 2:
                            int random3 = random.nextInt(max - min + 1) + min;
                            hits = 0;
                            dX = random3;
                            dY = random3;
                            break;
                    }
                    deltaX = ball.getX() + dX;
                    deltaY = ball.getY() + dY;
                    ball.setX(deltaX);
                    ball.setY(deltaY);
                }
                canvas.restore();
            }
        }
    }
}
