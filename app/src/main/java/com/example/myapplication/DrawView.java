package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawView extends View {
    private static final String TAG = "DrawView";
    public DrawView(Context context){
        super(context);
    }
    @Override
    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);


        Paint p = new Paint();// 創建畫筆
        p.setAntiAlias(true);									// 設置畫筆的鋸齒效果。 true是去除。
        p.setColor(Color.RED);
        canvas.drawText("直線：",110,20,p);			// 寫一段文字
        canvas.drawLine(160, 20, 200, 20, p);
        canvas.drawPoint(160,20,p);


    }

}

