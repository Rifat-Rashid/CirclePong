package com.example.rifatrashid.circlepong;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextView titleText;
    private arena Arena;
    private Ball ball;
    private paddle Paddle;
    private ImageView imageView;
    private Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int ADDITIONAL_WIDTH = 10;
    private final int ADDITIONAL_PADDLEARC = 25;
    private Bitmap bitmap, tempBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        myPaint.setAntiAlias(true);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        myPaint.setColor(Color.parseColor("#FF2D55"));
        ballPaint.setAntiAlias(true);
        ballPaint.setStyle(Paint.Style.FILL);
        ballPaint.setColor(Color.parseColor("#FFFFFF"));
        Arena = new arena(60, 60, 50);
        Arena.setPaint(myPaint);
        ball = new Ball(Arena.getX(), Arena.getY(), 5);
        ball.setPaint(ballPaint);
        Paddle = new paddle(Arena.getX() - Arena.getRadius(), Arena.getY() - Arena.getRadius(), Arena.getRadius() * 2 + ADDITIONAL_WIDTH, Arena.getRadius() * 2 + ADDITIONAL_WIDTH, ADDITIONAL_PADDLEARC, 30);
        Paddle.setStroke(3);
        //Create custom bitmap to draw on!
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Draw to bitmap!
                Bitmap tempBitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(tempBitmap);
                canvas.drawColor(Color.parseColor("#191919"));
                Arena.Draw(canvas);
                ball.Draw(canvas);
                Paddle.Draw(canvas);
                imageView.setImageBitmap(tempBitmap);
            }
        }).start();
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/Lato-Thin.ttf");
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setTypeface(lato);

    }
}
