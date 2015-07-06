package com.example.rifatrashid.circlepong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Reefer on 7/5/15.
 */
public class Circle {
    private int x, y, radius;
    private Paint paint;

    public Circle(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#ecf0f1"));
    }

    public void Draw(Canvas canvas) {
        canvas.drawCircle(this.x, this.y, this.radius, this.paint);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setPaint(Paint paint){
        this.paint = paint;
    }
}
