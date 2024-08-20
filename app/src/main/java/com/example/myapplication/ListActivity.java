package com.example.myapplication;

import static com.example.myapplication.MainActivity.base64ToBitmap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = "ListActivity";
    private ImageButton btnguide, btnswitch, btnnew,btnrange;
    private RecyclerView recycle_View;
    private Cardview_adapter cardview_adapter;
    private ArrayList<HashMap<String, String>> receiveDATA;// = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("data");
    private ArrayList<HashMap<String, String>> Data_Inrange;
    private Handler mainHandler = new Handler();
    private  int index;//傳
    private double tempx,tempy;
    private int click = 100;
    Intent info;
    ArrayList<Double> dis_array;
    Dialog dialog;
    View viewdialog;
    TextView textView;
    int[] imgId={R.drawable.im01, R.drawable.im02, R.drawable.im03, R.drawable.im04, R.drawable.im05,R.drawable.im06,R.drawable.im07,R.drawable.im08,R.drawable.im09,R.drawable.im10,R.drawable.im11,R.drawable.im12,R.drawable.im13,R.drawable.im14,R.drawable.im15,R.drawable.im16,R.drawable.im17};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
       // Data_Inrange= new ArrayList<>();
        //收資料
        info=getIntent();
        Data_Inrange=receiveDATA = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("data");
        click=getIntent().getExtras().getInt("range");
        index = getIntent().getExtras().getInt("click");
        tempx=getIntent().getExtras().getDouble("tempx");
        tempy=getIntent().getExtras().getDouble("tempy");
        dis_array=( ArrayList<Double>)getIntent().getSerializableExtra("distance");

        textView=(TextView)findViewById(R.id.textView);
        btnrange = (ImageButton) findViewById(R.id.btnRange);
        btnrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_distance();
            }
        });


        btnswitch = (ImageButton) findViewById(R.id.btnSwitch);
        btnswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnnew = (ImageButton) findViewById(R.id.btnNew);
        btnnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, NewPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                intent.putExtra("tempx",tempx);
                intent.putExtra("tempy",tempy);
                startActivity(intent);
                //intent.setClass(MainActivity.this, NewPageActivity.class);
                startActivity(intent);
            }
        });


        recycle_View = findViewById(R.id.recycle_View);

        recycle_View.setLayoutManager(new LinearLayoutManager(this));
        recycle_View.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        cardview_adapter = new Cardview_adapter();
        recycle_View.setAdapter(cardview_adapter);
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
            Log.d(TAG,"location != null");
          ///  Toast.makeText(ListActivity.this, "location != null", Toast.LENGTH_SHORT).show();


        }else{
            Log.d(TAG,"location = null");

            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mListener);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mListener);

           // Toast.makeText(ListActivity.this, "location = null", Toast.LENGTH_SHORT).show();

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

          //  Log.d(TAG,"onlocationchange");
          //  Toast.makeText(MainActivity.this, "location change", Toast.LENGTH_SHORT).show();
        }
    };
    //實作出一個Recycleview的Class，其中以下方法是必須存在的，不可缺少

    private class Cardview_adapter extends RecyclerView.Adapter<Cardview_adapter.ViewHolder> {

        //此Class是先讓Recycleview知道稍後item當中的元件是誰，類似在Activity之下的findviewbyid
        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView cardview_item_photo;
            private TextView cardview_item_name, cardview_item_address;

            private ImageView getCardview_item_photo;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                cardview_item_photo = itemView.findViewById(R.id.cardview_item_photo);
                cardview_item_name = itemView.findViewById(R.id.cardview_item_name);
                cardview_item_address = itemView.findViewById(R.id.cardview_item_address);
                cardview_item_photo=itemView.findViewById(R.id.cardview_item_photo);
                //mView = itemView;
            }
        }

        //這邊是讓activity_list.xml之下的recycle_View這個Component知道他的item長怎樣，所以使用了Layoutinflator
        //告知他

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item, parent, false);
            return new ViewHolder(view);
        }

        //以下是載入Recycleview的時候要做的事情，底下例子就是把recieveDATA當中的資料一一送給各個item的name和address和下載photo
        //而recieveDATA就是從GET_DATA當中串接API得到的資料，以Arraylist結構結合Hashmap放入資料
        //而底下的position的話，Recycleview會自動從0、1、2...往後帶入，所以無需特別設定初值，
        //終值的話則是最底下getItemCount()此方法來決定return的數字為何。
        //如果要對個別item做點擊事件的處理的話在底下新增

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.cardview_item_name.setText("");
            holder.cardview_item_address.setText("");
            holder.cardview_item_name.setText("名稱：\n" +Data_Inrange.get(position).get("Name"));
            holder.cardview_item_address.setText("地址：\n" + Data_Inrange.get(position).get("Address"));
            //holder.cardview_item_address.set
            if(position>=17)
            {
                Bitmap pict = base64ToBitmap(receiveDATA.get(position).get("Picture"));
                holder.cardview_item_photo.setImageBitmap(pict);
            }
            else
            {
                holder.cardview_item_photo.setImageResource(imgId[position]);
            }

            holder.cardview_item_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    click=position;
                    getDialog();
                    Toast.makeText(ListActivity.this, "第"+(position+1)+"個", Toast.LENGTH_SHORT).show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return Data_Inrange.size();
        }
    }


    void dataInRange()
    { Data_Inrange=new ArrayList<>();
        Log.d(TAG, "dataInRange: dis_array.size() " + dis_array.size());
        Log.d(TAG, "dataInRange: range " + click);
        for(int k=0;k<dis_array.size();k++)
        {
            int i=0;
            if(dis_array.get(k) <= click)
            {
                HashMap<String, String> hashMap = new HashMap<>();
                Data_Inrange.add(receiveDATA.get(k));
                Log.d(TAG,"datainrange "+Data_Inrange.get(i).get("Name"));
                i++;
            }

        }
    }
    public void select_distance() {
        final Dialog dialog = new Dialog(ListActivity.this);
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
        Log.d(TAG, "Picker" + displayedValues[minPicker.getValue()]);

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

                click = Integer.parseInt(displayedValues[minPicker.getValue()]);
                dataInRange();
                Log.d(TAG, "onClick: ");
                textView.setText("全部("+Integer.toString(click)+"公尺)");
                for (int i=0;i<Data_Inrange.size();i++)
               {
                   Log.d(TAG,"datainrange"+Data_Inrange.get(i).get("Name"));
               }
                Log.d(TAG, "Picker" + click);
                recycle_View.setAdapter(cardview_adapter);
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
    private void getDialog() {
        int i=0;

        dialog = new Dialog(ListActivity.this );

        //綁定自定義的dialog.xml
        viewdialog = getLayoutInflater().inflate(R.layout.info , null);

        //然後把綁好的xml連接到dialog上面
        dialog.setContentView(viewdialog);

        //因為是自定義的子元件，後面綁定id都要綁上面的view

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
                    if (ContextCompat.checkSelfPermission(ListActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        call.setAction(Intent.ACTION_CALL);
                        call.setData(Uri.parse("tel:" + phone));
                        startActivity(call);
                    } else {
                        ActivityCompat.requestPermissions(ListActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
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
            picture.setImageResource(imgId[0]);
        }

        Button btnguide=viewdialog.findViewById(R.id.guide);
        btnguide.setText( Data_Inrange.get(click).get("Address"));
        btnguide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: click: " + click);
                info.putExtra("range", click);
                info.putExtra("click", click);
                info.putExtra("data", receiveDATA);
                info.putExtra("tempx",tempx);
                info.putExtra("tempy",tempy);
                //info.setClass(MainActivity.this,ListActivity.class);
                //startActivity(info);
                info.setClass(ListActivity.this, LocationActivity.class); //GuideActivity
                startActivity(info);

            }
        });
        TextView name=viewdialog.findViewById(R.id.name);
        name.setText(Data_Inrange.get(click).get("Name"));
        TextView detail=viewdialog.findViewById(R.id.txv_detail);
        detail.setText(Data_Inrange.get(click).get("Description"));
        dialog.show();


        }

}