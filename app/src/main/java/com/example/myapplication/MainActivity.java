package com.example.myapplication;

import static java.sql.DriverManager.println;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.webkit.SafeBrowsingResponse;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageButton btnguide,btnnew, btnswitch, btninfo, btncall, btnrange,btntest;
    private RelativeLayout iv_canvas;
    // private RelativeLayout relativeLayout;
    private Canvas canvas;
    private Paint p;
    private Bitmap baseBitmap;
    protected static LocationManager lm;
    private static TextView tv_show;
    ArrayList<HashMap<String, String>> receiveDATA;
    private ImageButton[] r;
    private Handler mainHandler = new Handler();
    private Bitmap bitmapwalk;
    private int count;
    private int click;
    private int pick_range = 100;
    Intent info;
    Intent intent_click = new Intent();
    Boolean status = false;
    private Switch swPlay;
    private ArrayList<String> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private NumberPicker mNumberPicker;
    private TextView textView;
    double tempx,tempy;
    Dialog dialog;
    boolean disable_check = false;
    View viewdialog;
    int[] imgId={R.drawable.im01, R.drawable.im02, R.drawable.im03, R.drawable.im04, R.drawable.im05,R.drawable.im06,R.drawable.im07,R.drawable.im08,R.drawable.im09,R.drawable.im10,R.drawable.im11,R.drawable.im12,R.drawable.im13,R.drawable.im14,R.drawable.im15,R.drawable.im16,R.drawable.im17};
    //private OptionsPickerView pvOptions;
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_canvas = (RelativeLayout) findViewById(R.id.relativeLayout);

        Log.d(TAG,"Location"+tempx);
        info = getIntent();
        receiveDATA = (ArrayList<HashMap<String, String>>) info.getSerializableExtra("data");
textView=(TextView)findViewById(R.id.textView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        btnswitch = (ImageButton) findViewById(R.id.btnSwitch);
        btnswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculate();
                info.setClass(MainActivity.this, ListActivity.class);
                startActivity(info);
            }
        });



        btnrange = (ImageButton) findViewById(R.id.btnRange);
        btnrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_distance();
            }
        });



        btnnew = (ImageButton) findViewById(R.id.btnNew);
        btnnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewPageActivity.class);
                intent.putExtra("tempx",tempx);
                intent.putExtra("tempy",tempy);
                startActivity(intent);
            }
        });


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
            tempx=location.getLongitude();
            tempy=location.getLatitude();
            Log.d(TAG,"已定位");
            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
            DrawButton_on_canvas();
            p.setColor(Color.BLACK);
            canvas.drawCircle( countX(tempx, tempx, 1050, pick_range),countX(tempy, tempy, 1050, pick_range),50, new Paint());
        }else{
            Log.d(TAG,"location = null");

            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mListener);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mListener);

            Toast.makeText(MainActivity.this, "定位中請稍", Toast.LENGTH_SHORT).show();
            DrawButton_on_canvas();
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
                tempx=location.getLongitude();
                tempy=location.getLatitude();Log.d(TAG,"lon"+tempx);
                DrawButton_on_canvas();
            Log.d(TAG,"已更新定位");
            Toast.makeText(MainActivity.this, "已更新定位", Toast.LENGTH_SHORT).show();
        }
    };
    private void getDialog() {
        int i=0;
        //後面的R.style.dialognsq是我自己自定義的style讓對話框變成圓弧，也可以不添加
        dialog = new Dialog(MainActivity.this );

        //綁定自定義的dialog.xml
        viewdialog = getLayoutInflater().inflate(R.layout.info , null);

        //然後把綁好的xml連接到dialog上面
        dialog.setContentView(viewdialog);

        //因為是自定義的子元件，後面綁定id記得都要綁上面的view
        // account = viewdialog.findViewById(R.id.account);
        //password = viewdialog.findViewById(R.id.password);
        // ok = viewdialog.findViewById(R.id.ok);
        //cancel = viewdialog.findViewById(R.id.cancle);
        String temp;
        String phone ="";
        Log.d(TAG,"click"+click);
        temp = receiveDATA.get(click).get("Phone");
        Log.d(TAG,"tmp"+temp);
        for (int j = 0; j <temp.length(); j++) {
            if (j >= 6)
                phone = phone + temp.charAt(j);
            Log.d(TAG,"phone"+phone);
        }
        Button btncall=viewdialog.findViewById(R.id.call);
        //Button guide=viewdialog.findViewById(R.id.guide);
        btncall.setText(phone);
        btncall.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent call = new Intent();
                String temp;
                String phone ="";
                Log.d(TAG,"click"+click);
                temp = receiveDATA.get(click).get("Phone");
                Log.d(TAG,"tmp"+temp);
                for (int j = 0; j < temp.length(); j++) {
                    if (j >= 6)
                        phone = phone + temp.charAt(j);
                    Log.d(TAG,"phone"+phone);
                }
                if (view ==btncall) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        call.setAction(Intent.ACTION_CALL);
                        call.setData(Uri.parse("tel:" + phone));
                        startActivity(call);
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                        call.setAction(Intent.ACTION_CALL);
                        call.setData(Uri.parse("tel:" + phone));
                        startActivity(call);
                    }
                }
                return true;
            }
        });

        ImageView picture=viewdialog.findViewById(R.id.picture);
        if(click>=17)
        {
            Bitmap pict = base64ToBitmap(receiveDATA.get(click).get("Picture"));
            picture.setImageBitmap(pict);
        }
        else
        {
            picture.setImageResource(imgId[click]);
        }


        Button btnguide=viewdialog.findViewById(R.id.guide);
        btnguide.setText( receiveDATA.get(click).get("Address"));
        btnguide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: click: " + click);
                info.putExtra("range", pick_range);
                info.putExtra("click", click);
                info.putExtra("data", receiveDATA);
                info.putExtra("tempx",tempx);
                info.putExtra("tempy",tempy);
                //info.setClass(MainActivity.this,ListActivity.class);
                //startActivity(info);
                info.setClass(MainActivity.this, LocationActivity.class); //GuideActivity
                startActivity(info);

            }
        });
        TextView name=viewdialog.findViewById(R.id.name);
        name.setText(receiveDATA.get(click).get("Name"));
        TextView detail=viewdialog.findViewById(R.id.txv_detail);
        detail.setText(receiveDATA.get(click).get("Description"));

        dialog.show();
       }
    public void DrawButton_on_canvas() {
        iv_canvas = (RelativeLayout) findViewById(R.id.relativeLayout);
        p = new Paint();
        p.setColor(Color.RED);

        baseBitmap = Bitmap.createBitmap(550, 650, Bitmap.Config.ARGB_8888);//iv_canvas.getWidth(),iv_canvas.getHeight(), Bitmap.Config.ARGB_8888
        canvas = new Canvas(baseBitmap);
        if(disable_check==true)
        {
            for(int i=0;i<count;i++)
            {
                r[i].setVisibility(View.GONE);
            }
        }
        Drawable d = getResources().getDrawable(R.drawable.ic_walk);
        bitmapwalk = drawableToBitmap(d);
        canvas.translate(canvas.getWidth()/ 2, canvas.getHeight()/ 2);//座標原點一
        canvas.drawBitmap(bitmapwalk, -bitmapwalk.getWidth() / 2, -bitmapwalk.getHeight() / 2, new Paint());//目前位置，在Main中不動

        r = new ImageButton[30];
        count = receiveDATA.size();
        double lon, lat;
        for (int i = 0; i < count; i++) {
            r[i] = new ImageButton(this);
            r[i].setBackground(getResources().getDrawable(R.drawable.circle));
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams
                    (
                            //RelativeLayout.LayoutParams.FILL_PARENT,
                            //  // RelativeLayout.LayoutParams.WRAP_CONTENT
                            40, 40//大小
                    );

            lon = Double.parseDouble(receiveDATA.get(i).get("Locationlon"));
//            Log.d(TAG, "onCreate: lon: "+lon);
            lat = Double.parseDouble(receiveDATA.get(i).get("Locationlat"));
//          Log.d(TAG, "onCreate: lat: "+lat);
            p.setMargins(countX(tempx, lon, 1050, pick_range), countY(tempy, lat, 1500, pick_range), 0, 0);//left[i],top[i],0,0
            iv_canvas.addView(r[i], p);
        }
        disable_check=true;
        for (int i = 0; i < count; i++) {
            int j = i;
            r[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    click = j;
                    getDialog();

                }
            });
        }

    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    static int countX(double tempx, double restx, int width, int range)//目前座標 要畫店家 canvas總寬
    {
        double distance;
        int val;//要化的座標
        width = width / 2;
        distance = restx - tempx;

        val = (int) ((width * distance) * 100000 / range);
        val = width + val;

        return val;
    }

    static int countY(double tempy, double resty, int Height, int range)//目前座標 要畫店家 canvas總寬
    {
        double distance;
        int val;//要化的座標
        Height = Height / 2;
        distance = resty - tempy;
        val = (int) ((Height * distance) * 100000 / range) * -1;
        val = Height + val;//Log.d(TAG,"value"+val);
        return val;

    }

    static double GetDistance(double lat1, double lng1, double lat2, double lng2) {
        double EARTH_RADIUS = 6371.393;
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 1000);
        return s;
    }
    void calculate(){
        double distance = 0, lat = 0, lng = 0;
        ArrayList<Double> dis_array = new ArrayList<Double>();
        for(int i = 0; i < count; i++ ){
            lng = Double.parseDouble(receiveDATA.get(i).get("Locationlon"));
            lat = Double.parseDouble(receiveDATA.get(i).get("Locationlat"));
            distance = GetDistance(lat, lng, tempy, tempx);
            dis_array.add(i,distance);
        }
        info.putExtra("distance",dis_array);
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public void select_distance() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog);
        Button setBtn = dialog.findViewById(R.id.button1);
        Button cancelBtn = dialog.findViewById(R.id.button2);


        final NumberPicker minPicker = dialog.findViewById(R.id.min_picker);
        int NUMBER_OF_VALUES = 20; //num of values in the picker
        int PICKER_RANGE = 100;//每個選項差多少

        String[] displayedValues = new String[NUMBER_OF_VALUES];

        for (int i = 0; i < NUMBER_OF_VALUES; i++)
            displayedValues[i] = String.valueOf(PICKER_RANGE * (i + 1));

        minPicker.setMinValue(0);
        minPicker.setMaxValue(20 - 1);
        minPicker.setDisplayedValues(displayedValues);
        Log.d(TAG, "receiveDATA.size(): " + receiveDATA.size());

        minPicker.setWrapSelectorWheel(false);
        minPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker minPicker, int i, int i1) {

            }
        });


        setBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                minPicker.setDisplayedValues(displayedValues);

                pick_range = Integer.parseInt(displayedValues[minPicker.getValue()]);

                for (int i = 0; i < receiveDATA.size(); i++)
                    r[i].setVisibility(View.GONE);
                DrawButton_on_canvas();
                textView.setText("全部("+Integer.toString(pick_range)+"公尺)");
                Log.d(TAG, "Picker: " + pick_range);
                //textView.setText(hourPicker.getValue() + ":" + minPicker.getValue() + ":" + secPicker.getValue());
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }

}





