package com.example.myapplication;

import static com.example.myapplication.MainActivity.drawableToBitmap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.R;

import java.security.DomainLoadStoreParameter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class LocationActivity extends AppCompatActivity {

    private static final String TAG = "LocationActivity";

    TextView tv_show;
    private ImageView iv_canvas;
    private Paint p;
    private Bitmap baseBitmap;
    private Bitmap bitmapwalk,bitmap_destination;
    private Canvas canvas;
    int initial;
    double tempy,testy,ratio;
    double tempx,testx;
    ImageButton btnclose;
    int range,index;
    private ArrayList<HashMap<String, String>> receiveDATA;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        iv_canvas = (ImageView)findViewById(R.id.tv_show);
        receiveDATA = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("data");
        range=getIntent().getExtras().getInt("range");
        index = getIntent().getExtras().getInt("click");
        tempx=getIntent().getExtras().getDouble("tempx");
        tempy=getIntent().getExtras().getDouble("tempy");
        Log.d(TAG,"range"+range);

        //tempy=tempx=5000;
        //initial=3;
        //Draw_map(location);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
            //
        }
        btnclose = (ImageButton) findViewById(R.id.end);
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent();
                intent.setClass(LocationActivity.this, MainActivity.class);
                startActivity(intent);*/
                finish();
            }
        });



        p=new Paint();
        p.setColor(Color.WHITE);
        baseBitmap = Bitmap.createBitmap(550,900, Bitmap.Config.ARGB_8888);//iv_canvas.getWidth(),iv_canvas.getHeight(), Bitmap.Config.ARGB_8888//650
        canvas = new Canvas(baseBitmap);
        Drawable d = getResources().getDrawable(R.drawable.ic_walk);
        Drawable l=getResources().getDrawable(R.drawable.ic_dest);
        bitmap_destination=MainActivity.drawableToBitmap(l);
        bitmapwalk=MainActivity.drawableToBitmap(d);
      /*  canvas.drawBitmap(bitmap_destination,(float)countX(tempx,Double.parseDouble(receiveDATA.get(index).get("Locationlon")), canvas.getWidth(),range),
                (float) countY(tempy,Double.parseDouble(receiveDATA.get(index).get("Locationlat")), canvas.getHeight(),range),new Paint());
        iv_canvas.setImageBitmap(baseBitmap);
        canvas.drawBitmap(bitmapwalk,(float) countX( tempx,tempx, canvas.getWidth(),range),(float) countY(tempy,tempy, canvas.getHeight(),range),new Paint());
        iv_canvas.setImageBitmap(baseBitmap);*/
        canvas.translate(canvas.getWidth()/ 2, canvas.getHeight()/ 2);//座標原點一中間

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getLocal();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocal() {
        /**沒有權限則返回*/
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String localProvider = "";
        /**知道位置後..*/
        Location location = manager.getLastKnownLocation(localProvider);
        if (location != null){

           /* if(initial ==3 && Intaiwan(location.getLongitude(),location.getLatitude()) == true)
            {
                tempx=location.getLongitude();
                tempy=location.getLatitude();
                initial--;
            }*/
            Draw_map(location);



        }else{
            Log.d(TAG, "getLocal: ");
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mListener);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mListener);
        }
    }

    /**監聽位置變化*/
    LocationListener mListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onLocationChanged(Location location) {
          /*  if(initial ==3 && Intaiwan(location.getLongitude(),location.getLatitude()) == true)
            {
                tempx=location.getLongitude();
                tempy=location.getLatitude();
initial--;
            }*/
            Draw_map(location);


        }
    };
    void Draw_map(Location location)
    {
        Log.d(TAG,"tempx"+tempx);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        int index = getIntent().getExtras().getInt("click");
        // Log.d(TAG, "onLocationChanged: index: " +index);
        // Log.d(TAG, "onLocationChanged: "+receiveDATA.get(index).get("Locationlon"));
        p.setColor(Color.WHITE);
        canvas.drawRect(-250,390,250,420,p);
        p.setTextAlign(Paint.Align.CENTER);
        p.setColor(Color.BLACK);
        p.setTextSize(20);
        canvas.drawText("50%",0,445,p);
        canvas.drawText("即將抵達",235,445,p);
        canvas.drawText("0%",-240,445,p);
        p.setTextSize(50);
        canvas.drawText("元智大學",0,-360,p);
        p.setTextSize(35);
        canvas.drawText(receiveDATA.get(getIntent().getExtras().getInt("click")).get("Name"),0,-300,p);
        canvas.drawBitmap(bitmap_destination,(float)countX(tempx,Double.parseDouble(receiveDATA.get(index).get("Locationlon")), canvas.getWidth(),range),
                (float) countY(tempy,Double.parseDouble(receiveDATA.get(index).get("Locationlat")), canvas.getHeight(),range),new Paint());
        iv_canvas.setImageBitmap(baseBitmap);
        canvas.drawBitmap(bitmapwalk,(float) countX( tempx,location.getLongitude(), canvas.getWidth(),range),(float) countY(tempy,location.getLatitude(), canvas.getHeight(),range),new Paint());
        iv_canvas.setImageBitmap(baseBitmap);
        double ratio=Draw_Bar(location.getLatitude(),location.getLongitude(),
                Double.parseDouble(receiveDATA.get(getIntent().getExtras().getInt("click")).get("Locationlat")),
                Double.parseDouble(receiveDATA.get(getIntent().getExtras().getInt("click")).get("Locationlon")),tempy,tempx);
        //Log.d(TAG,"ratio"+ratio);
        Log.d(TAG, "restx"+Double.parseDouble(receiveDATA.get(getIntent().getExtras().getInt("click")).get("Locationlon")));
        Log.d(TAG, "resty"+Double.parseDouble(receiveDATA.get(getIntent().getExtras().getInt("click")).get("Locationlat")));
        Log.d(TAG, "GETtempy"+location.getLongitude());
        Log.d(TAG, "GETtempy"+location.getLatitude());
        if(tempy!=5000&&tempx!=5000)
        {       p.setColor(Color.RED);
            ratio=Draw_Bar(location.getLatitude(),location.getLongitude(),
                    Double.parseDouble(receiveDATA.get(getIntent().getExtras().getInt("click")).get("Locationlat")),
                    Double.parseDouble(receiveDATA.get(getIntent().getExtras().getInt("click")).get("Locationlon")),tempy,tempx);
            Log.d(TAG,"ratio"+ratio);
            if(ratio>=-250)
            {canvas.drawRect(-250,390,(float) ratio,420,p);}
            if(ratio>=170)
            {
                canvas.drawRect(-250,390,250,420,p);
            }
        }
    }
    boolean Intaiwan(double x,double y)
    {
        if(x>=120&&x<=122)
        {
            if(y>=22&&y<=25)
                return true;
        }
            return false;
    }
    double countX(double tempx, double restx, double width,int range)//目前座標 要畫店家 canvas總寬
    {   double distance;
        double val;//要化的座標
        width=width/2;
        distance=restx-tempx;
        val=(width*distance)*100000/range;
        return val;
    }
    double countY(double tempy, double resty, double Height,int range)//目前座標(初始 未走) 要畫店家 canvas總寬
    {   double distance;
        double val;//要化的座標
        Height=Height/2;
        distance=resty-tempy;
        val=(Height*distance)*100000/range;
        return val*(-1);
    }
    double Draw_Bar(double y1,double x1,double y2,double x2,double y3,double x3)
    {
        double dist =MainActivity.GetDistance(y1,x1,y2,x2);
        double total=MainActivity.GetDistance(y3,x3,y2,x2);
        Log.d(TAG,"total:"+total);
        Log.d(TAG,"dist:"+dist);
        return 500-((500*dist)/total)-250;


    }

}