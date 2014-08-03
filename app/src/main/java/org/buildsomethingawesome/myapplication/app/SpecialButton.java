package org.buildsomethingawesome.myapplication.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.Button;

public class SpecialButton extends Button {

    public SpecialButton(Context context) {
        this(context, null, 0);
    }

    public SpecialButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setText("AAA");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint thePaint = new Paint();
        thePaint.setColor(Color.BLUE);
        RectF rectangle1 = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rectangle1, 10.0f, 10.0f, thePaint);
    }
}
