package com.splice.rifatrashid.circlepong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Reefer on 6/14/15.
 */
public class arena {
    private int x, y, radius;
    private Paint paint;

    public arena(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#16a085"));
        paint.setStrokeWidth(3.5f);
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

    public void setRadius(int radius) {
        this.radius = radius;
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
