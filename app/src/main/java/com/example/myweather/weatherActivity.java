package com.example.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

        //Parse the api response for the current weather and display its values
        parseJsonRealTime();

        //Parse the api response for the forecast weather and display its values
        parseJsonForecast();
    }

    public void parseJsonRealTime(){
        try {
            String json = getIntent().getStringExtra("1");

            Log.e("Debug", "FROM parseJsonRealTime - string json: ----->  " + json);

            JsonParser parser = new JsonParser();

            JsonObject jsonObject1 = (JsonObject)parser.parse(json);

            JsonObject jsonObject2 = (JsonObject)jsonObject1.get("data");

            JsonObject jsonObject3 = (JsonObject)jsonObject2.get("values");

            String temp = toFahrenheit(jsonObject3.get("temperature").toString());
            Log.e("Debug", "FROM parseJsonRealTime - Temperature:  ---> " + temp);

            JsonObject jsonObject4 = (JsonObject)jsonObject1.get("location");

            String location = removeQuotes(jsonObject4.get("name").toString());
            Log.e("Debug", "FROM parseJsonRealTime - LOCATION:  ---> " + location);

            //Set the textview text for temperature
            TextView temp_textView = findViewById(R.id.temp_textView);
            temp_textView.setText(temp);

            //Set the textview text for the location
            TextView city_textView = findViewById(R.id.city_textView);
            city_textView.setText(location);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void parseJsonForecast(){
        try {
            String json = getIntent().getStringExtra("responseForecast");

            Log.e("Debug", "FROM parseJsonForecast - string json: ----->  " + json);

            JsonParser parser = new JsonParser();

            JsonObject jsonObject1 = (JsonObject)parser.parse(json);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //This function removes the beginning and ending quotation marks from the json key-values.
    public String removeQuotes(String s){
        String newString = "";
        newString = s.replace("\"", "");

        return newString;
    }

    //This function converts celsius to fahrenheit.
    public String toFahrenheit(String s){
        double celsius = Double.valueOf(s);

        double fahrenheit =  celsius * 1.8 + 32;

        return String.format("%.2f", fahrenheit);
    }
}