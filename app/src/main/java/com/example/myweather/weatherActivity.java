package com.example.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import okhttp3.Response;

public class weatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        parseJson();
    }

    public void parseJson(){
        try {
            /*InputStream inputStream = new ByteArrayInputStream((getIntent().getStringExtra("response").getBytes()));
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();*/

            Log.e("parseJson", "FROM parseJson - try block 1:  ----->  ");

            //String json = new String(buffer, StandardCharsets.UTF_8);
            String json = getIntent().getStringExtra("response");

            Log.e("parseJson", "FROM parseJson - string json: ----->  " + json);

            TextView temp_textView = findViewById(R.id.temp_textView);
            temp_textView.setText(json);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}