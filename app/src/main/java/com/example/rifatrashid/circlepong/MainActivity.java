package com.example.rifatrashid.circlepong;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MainActivity extends Activity implements SurfaceHolder.Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView titleText;
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
    private final int BASECIRCLE_GROWTH_RATE = 11;
    private Ball ball;
    private final int BALL_GROWTH_RATE = 4;
    private Paint ballPaint;
    private GoogleApiClient mGoogleApiClient;
    private Button achievments_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Games.SCOPE_GAMES)
                .build();
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addApi(Plus.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        _surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        _surfaceHolder = _surfaceView.getHolder();
        _surfaceHolder.addCallback(this);
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setTypeface(lato);
        achievments_button = (Button) findViewById(R.id.button);
        achievments_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Games.Achievements.getAchievementsIntent(mGoogleApiClient);
                startActivity(intent);
                //startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 1);
            }
        });
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
        mGoogleApiClient.disconnect();
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
                _surfaceView.setBackgroundColor(Color.parseColor("#191919"));
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

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){
            try{
                connectionResult.startResolutionForResult(this, 1);
            }catch (IntentSender.SendIntentException e){
                e.printStackTrace();
            }
        }
        mGoogleApiClient.connect();
        System.out.println(connectionResult);
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
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(8);
                paint.setColor(Color.parseColor("#FF2D55"));
                baseCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                baseCirclePaint.setAntiAlias(true);
                baseCirclePaint.setStyle(Paint.Style.FILL);
                baseCirclePaint.setColor(Color.parseColor("#2a2a2a"));
                ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                ballPaint.setAntiAlias(true);
                ballPaint.setStyle(Paint.Style.FILL);
                ballPaint.setColor(Color.parseColor("#FFFFFF"));
                Paddle.setPaint(paint);
                baseCircle.setPaint(baseCirclePaint);
                ball.setPaint(ballPaint);
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
                canvas.drawColor(Color.parseColor("#191919"));
                baseCircle.Draw(canvas);
                Paddle.Draw(canvas);
                ball.Draw(canvas);
                if (arc1Length <= 360) {
                    arc1Length += 3;
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
                canvas.restore();
            }
        }
    }
}
