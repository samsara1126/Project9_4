package com.cookandroid.project9_4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final static int LINE = 1, CIRCLE = 2, RECT = 3;
    static int curShape = LINE;

    static List<Shape> shapeList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyGraphicView(this));
        setTitle("간단 그림판 - 객체 리스트 관리");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "선 추가");
        menu.add(0, 2, 0, "원 추가");
        menu.add(0, 3, 0, "사각형 추가");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                curShape = LINE;
                return true;
            case 2:
                curShape = CIRCLE;
                return true;
            case 3:
                curShape = RECT;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    abstract static class Shape {
        int startX, startY, stopX, stopY;
        Shape(int startX, int startY, int stopX, int stopY) {
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
        }
        abstract void draw(Canvas canvas, Paint paint);
    }

    static class Line extends Shape {
        Line(int startX, int startY, int stopX, int stopY) { super(startX, startY, stopX, stopY); }
        @Override
        void draw(Canvas canvas, Paint paint) {
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }

    static class Circle extends Shape {
        Circle(int startX, int startY, int stopX, int stopY) { super(startX, startY, stopX, stopY); }
        @Override
        void draw(Canvas canvas, Paint paint) {
            int radius = (int) Math.sqrt(Math.pow(stopX - startX, 2) + Math.pow(stopY - startY, 2));
            canvas.drawCircle(startX, startY, radius, paint);
        }
    }

    static class MyRect extends Shape {
        MyRect(int startX, int startY, int stopX, int stopY) { super(startX, startY, stopX, stopY); }
        @Override
        void draw(Canvas canvas, Paint paint) {
            android.graphics.Rect rect = new android.graphics.Rect(
                    Math.min(startX, stopX), Math.min(startY, stopY),
                    Math.max(startX, stopX), Math.max(startY, stopY)
            );
            canvas.drawRect(rect, paint);
        }
    }

    private static class MyGraphicView extends View {
        int startX = -1, startY = -1, stopX = -1, stopY = -1;
        boolean isDrawing = false;

        public MyGraphicView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    isDrawing = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    stopX = (int) event.getX();
                    stopY = (int) event.getY();
                    this.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    stopX = (int) event.getX();
                    stopY = (int) event.getY();
                    isDrawing = false;

                    Shape newShape = null;
                    switch (curShape) {
                        case LINE:
                            newShape = new Line(startX, startY, stopX, stopY);
                            break;
                        case CIRCLE:
                            newShape = new Circle(startX, startY, stopX, stopY);
                            break;
                        case RECT:
                            newShape = new MyRect(startX, startY, stopX, stopY);
                            break;
                    }
                    if (newShape != null) {
                        shapeList.add(newShape);
                    }
                    this.invalidate();
                    break;
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);

            for (Shape shape : shapeList) {
                shape.draw(canvas, paint);
            }

            if (isDrawing && startX != -1) {
                switch (curShape) {
                    case LINE:
                        canvas.drawLine(startX, startY, stopX, stopY, paint);
                        break;
                    case CIRCLE:
                        int radius = (int) Math.sqrt(Math.pow(stopX - startX, 2) + Math.pow(stopY - startY, 2));
                        canvas.drawCircle(startX, startY, radius, paint);
                        break;
                    case RECT:
                        android.graphics.Rect rect = new android.graphics.Rect(
                                Math.min(startX, stopX), Math.min(startY, stopY),
                                Math.max(startX, stopX), Math.max(startY, stopY)
                        );
                        canvas.drawRect(rect, paint);
                        break;
                }
            }
        }
    }
}