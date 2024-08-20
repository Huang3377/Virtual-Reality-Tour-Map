package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
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
import retrofit2.http.GET;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    ArrayList<HashMap<String, String>> receiveDATA = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File data=new File("/data/data/com.example.myapplication/files/data");

        if(!data.exists())
        {
           // Log.d(TAG,"不存在");
            write("[]");
        }
       Log.d(TAG,"readfile: "+read());
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(getAssets().open("data.json"), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            JSONArray jsonArray1 = new JSONArray(stringBuilder.toString()); //原本的
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jsonObject = jsonArray1.getJSONObject(i);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Name", jsonObject.getString("RestaurantName"));
                hashMap.put("Address", jsonObject.getString("Address"));
                hashMap.put("Locationlon",jsonObject.getJSONObject("Position").getString("PositionLon"));
                hashMap.put("Locationlat",jsonObject.getJSONObject("Position").getString("PositionLat"));
                hashMap.put("Phone",jsonObject.getString("Phone"));
                hashMap.put("Description",jsonObject.getString("Description"));
                //hashMap.put("Picture", jsonObject.getString("Picture"));
                receiveDATA.add(hashMap);
            }
            JSONArray jsonArray2 = new JSONArray(read().toString());    //新加的
            for (int i = 0; i < jsonArray2.length(); i++) {
                JSONObject jsonObject = jsonArray2.getJSONObject(i);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Name", jsonObject.getString("RestaurantName"));
                hashMap.put("Address", jsonObject.getString("Address"));
                hashMap.put("Locationlon",jsonObject.getJSONObject("Position").getString("PositionLon"));
                hashMap.put("Locationlat",jsonObject.getJSONObject("Position").getString("PositionLat"));
                hashMap.put("Phone",jsonObject.getString("Phone"));
                hashMap.put("Description",jsonObject.getString("Description"));
                hashMap.put("Picture", jsonObject.getString("Picture"));
                receiveDATA.add(hashMap);
            }
            Log.d(TAG, "receiveDATA "+receiveDATA);

            Intent info = new Intent(SplashActivity.this,MainActivity.class);

            info.putExtra("data",receiveDATA);
            info.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(info);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
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
    public void write(String inputText){
        FileOutputStream fos;
        BufferedWriter writer = null;//這裡要初始化一個null
        try{
            fos = openFileOutput("data", Context. MODE_APPEND);
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



}