package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewPageActivity extends AppCompatActivity{
    private static final String TAG = "NewPageActivity";
    private Button finbtn,cameraBtn;
    private ImageButton photobtn;
    private EditText name,address,phone,detail;
    double tempx,tempy;
    long dataSize;
    ImageView selectedImage;
    private Uri imageUri;
    private File currentImageFile = null;
    private static final String FILENAME="New.txt";
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    Bitmap image;
    String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_page);
        image=null;
        tempx=getIntent().getExtras().getDouble("tempx");
        tempy=getIntent().getExtras().getDouble("tempy");
        name=(EditText)findViewById(R.id.editTextTextPersonName2);
        address=(EditText)findViewById(R.id.editTextTextPostalAddress);
        phone=(EditText)findViewById(R.id.editTextNumber);
        detail=(EditText)findViewById(R.id.editTextTextMultiLine3);
        selectedImage = (ImageView) findViewById(R.id.displayImageView);
        cameraBtn  = (Button) findViewById(R.id.cameraBtn);

        cameraBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                askCameraPermissions();
            }

        });

        finbtn = (Button) findViewById(R.id.button3);
        finbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(image!=null)
                    {
                        String JsonImage=bitmapToBase64(image);
                    }
                     JSONObject inf = new JSONObject();
                     inf.put("number", 1);
                     //JSONArray array = new JSONArray();
                     JSONObject arr_2=new JSONObject();
                     arr_2.put("PositionLon",tempx);
                     arr_2.put("PositionLat",tempy);
                     JSONObject arr_1 = new JSONObject();
                     arr_1.put("RestaurantName",name.getText().toString());
                     arr_1.put("Address",address.getText().toString());
                     arr_1.put("Phone", phone.getText().toString());
                     arr_1.put("Description",detail.getText().toString());
                     arr_1.put("Position",arr_2);
                    if(image!=null)
                    {
                        String JsonImage=bitmapToBase64(image);
                        arr_1.put("Picture",JsonImage);
                    }
                    //array.put(0, arr_1);

                    //inf.put("inf", array);
                    File data=new File("/data/data/com.example.myapplication/files/data");

                    String file=read();
                    Log.d(TAG,"fileread "+read());//Log.d(TAG, "file_string: ");
                    dataSize=data.length()-1;
                    Log.d(TAG,"data size: "+dataSize);
                    Log.d(TAG,"file size: "+file.length());
                    String newdata=arr_1.toString();
                    if(file.charAt((int) file.length() - 2) != '[')
                       newdata = ','+arr_1.toString();
                    insert("/data/data/com.example.myapplication/files/data",dataSize,newdata);
                    Log.d(TAG,"fileread AFTER "+read());
                   //  writeText.setText(inf.toString());
                  } catch (JSONException | IOException e) {
                     e.printStackTrace();
                    }


                finish();

            }
        });



    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            openCamera();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE){
            image = (Bitmap) data.getExtras().get("data");
            saveBitmap(image);

           // selectedImage.setImageBitmap( getBitmap(Environment.getExternalStorageDirectory().toString ()+"Image.png"));
            selectedImage.setImageBitmap(image);

        }
    }

    public void write(String inputText){
        FileOutputStream fos;
        BufferedWriter writer = null;//這裡要初始化一個null
        try{
            fos = openFileOutput("data", Context. MODE_APPEND);
            //fos.seek();
            writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(inputText);//這裡寫入引數
            // Log.d(TAG,"file"+inputText);
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(writer != null){
                    writer.close();//關閉字元緩衝輸出流
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public static void insert(String fileName, long pos, String insertContent)throws IOException{

        RandomAccessFile raf = null;

        File tmp = File.createTempFile("tmp", null);

        FileOutputStream tmpOut = null;

        FileInputStream tmpIn = null;

        tmp.deleteOnExit();

        try{

            raf = new RandomAccessFile(fileName, "rw");

            tmpOut = new FileOutputStream(tmp);

            tmpIn = new FileInputStream(tmp);

            raf.seek(pos);

            byte[] bbuf = new byte[64];

            int hasRead = 0;

            while ((hasRead = raf.read(bbuf)) > 0){

                tmpOut.write(bbuf, 0, hasRead);

            }

            raf.seek(pos);

            raf.write(insertContent.getBytes());

            while ((hasRead = tmpIn.read(bbuf)) > 0){

                raf.write(bbuf, 0, hasRead);

            }

        }finally{

            raf.close();

        }

    }


    public String read(){
        FileInputStream fis;
        BufferedReader reader = null;//這裡要初始化null
        StringBuilder builder = new StringBuilder();
        try{
            fis = openFileInput("data");
            reader = new BufferedReader(new InputStreamReader(fis));
            String lines="";
            while((lines = reader.readLine())!=null){
                builder.append(lines);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
    public void saveBitmap(Bitmap bitmap) {
       // FileOutputStream fOut;
        try {
            File dir = new File(Environment.getExternalStorageDirectory().toString ()+"/test");
            Log.d(TAG,"路徑"+Environment.getExternalStorageDirectory().toString ());
            if (!dir.exists()) {

                dir.mkdir();
                if (!dir.exists())
                {Log.d(TAG, "不存在不存在");}
            }

            File file = new File( Environment.getExternalStorageDirectory().toString ()+"/test", "abcdImage.png");
            // 開啟檔案串流
            FileOutputStream fOut = new FileOutputStream(file);

          //  String tmp = "/sdcard/demo/takepicture.jpg";
            //fOut = new FileOutputStream(tmp);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            try {
                fOut.flush();
                fOut.close();
                Log.d(TAG,"成功存檔");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            Log.w(TAG, "create error: "+ e.toString());
        }
    }
    public static Bitmap getBitmap(String path)
    {
        Bitmap bitmap=null;
        try{
            FileInputStream fis= new FileInputStream(path);
            bitmap= BitmapFactory.decodeStream(fis);

        }catch (Exception e){}
        return  bitmap;
    }

    private static String bitmapToBase64(Bitmap bitmap) {
        String result = null;

        ByteArrayOutputStream baos = null;

        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();

                baos.close();

                byte[] bitmapBytes = baos.toByteArray();

                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);

            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (baos != null) {
                    baos.flush();

                    baos.close();

                }

            } catch (IOException e) {
                e.printStackTrace();

            }

        }

        return result;

    }
};
