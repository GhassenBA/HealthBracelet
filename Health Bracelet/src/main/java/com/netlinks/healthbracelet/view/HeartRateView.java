package com.netlinks.healthbracelet.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
/*
 * Copyright (C) 2014 Health Bracelet Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Saif Chaouachi(ch.saiff35@gmail.com)
 *         2/21/14
 */
public class HeartRateView extends View {
    public HeartRateView(Context context) {
        super(context);
    }

    public HeartRateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HeartRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
// Get the size of the control based on the last call to onMeasure.
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
// Find the center
        int px = width / 2;
        int py = height / 2;
// Create the new paint brushes.
// NOTE: For efficiency this should be done in
// the views’s constructor
        Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
// Define the string.
        String displayText = "Heart Rate Custom View!";
// Measure the width of the text string.
        float textWidth = mTextPaint.measureText(displayText);
// Draw the text string in the center of the control.
        canvas.drawText(displayText, px - textWidth / 2, py, mTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredHeight = measureHeight(heightMeasureSpec);
        int measuredWidth = measureWidth(widthMeasureSpec);
        setMeasuredDimension(measuredHeight, measuredWidth);
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
// Default size if no limits are specified.
        int result = 250;
        if (specMode == MeasureSpec.AT_MOST) {
// Calculate the ideal size of your
// control within this maximum size.
// If your control fills the available
// space return the outer bound.
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
// If your control can fit within these bounds return that value.
            result = specSize;
        }
        return result;
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
// Default size if no limits are specified.
        int result = 250;
        if (specMode == MeasureSpec.AT_MOST) {
// Calculate the ideal size of your control
// within this maximum size.
// If your control fills the available space
// return the outer bound.
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
// If your control can fit within these bounds return that value.
            result = specSize;
        }
        return result;
    }
}
