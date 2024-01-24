package com.example.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.Response;

public class weatherActivity extends AppCompatActivity {

    class Days{
        String time;
        String tempAvg;
        String tempMax;
        String tempMin;

        Days(String time, String tempAvg, String tempMax, String tempMin){
            this.time = time;
            this.tempAvg = tempAvg;
            this.tempMax = tempMax;
            this.tempMin = tempMin;
        }

        //This function is used for testing purposes.
        void printAll(){
            String info = time + " * " + tempAvg + " * " + tempMax + " * " + tempMin;
            Log.e("Days", "FROM Days - printAll - ----->  " + info);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //Parse the api response for the current weather and display its values
        parseJsonRealTime();

        //Parse the api response for the forecast weather and display its values
        parseJsonForecast();

        //setGuiForecast();

        Button back_btn = findViewById(R.id.back_button);
        Button newSearch_btn = findViewById(R.id.newSearch_button);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        newSearch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Use the flag "FLAG_ACTIVITY_CLEAR_TOP" to go straight to the main activity
                Intent i=new Intent(weatherActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
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

            String weatherCode = jsonObject3.get("weatherCode").toString();
            Log.e("Debug", "FROM parseJsonRealTime - Weather Code:  ---> " + weatherCode);

            JsonObject jsonObject4 = (JsonObject)jsonObject1.get("location");

            String location = removeQuotes(jsonObject4.get("name").toString());
            Log.e("Debug", "FROM parseJsonRealTime - LOCATION:  ---> " + location);


            //Set the textview text for temperature
            TextView temp_textView = findViewById(R.id.temp_textView);
            temp_textView.setText(temp+ "\u2109");

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

            //Get the first object "timelines"
            JsonObject jsonObject1 = (JsonObject)parser.parse(json);

            JsonObject jsonObject2 = (JsonObject)jsonObject1.get("timelines");

            JsonArray jsonArrayDaily = (JsonArray)jsonObject2.get("daily");

            ArrayList<Days> forecastDays = new ArrayList<Days>();

            String time;
            String tempAvg;
            String tempMax;
            String tempMin;

            for (int i = 0; i < jsonArrayDaily.size(); i++)
            {
                JsonObject jsonObject3 = (JsonObject) jsonArrayDaily.get(i);

                time = removeQuotes(String.valueOf(jsonObject3.get("time")));
                Log.e("Debug", "FROM parseJsonForecast - For loop - time: ----->  " + time);

                JsonObject jsonObject4 = (JsonObject)jsonObject3.get("values");

                tempAvg = String.valueOf(jsonObject4.get("temperatureApparentAvg"));
                Log.e("Debug", "FROM parseJsonForecast - For loop - temperatureApparentAvg: ----->  " + tempAvg);

                tempMax = String.valueOf(jsonObject4.get("temperatureApparentMax"));
                Log.e("Debug", "FROM parseJsonForecast - For loop - temperatureApparentMax: ----->  " + tempMax);

                tempMin = String.valueOf(jsonObject4.get("temperatureApparentMin"));
                Log.e("Debug", "FROM parseJsonForecast - For loop - temperatureApparentMin: ----->  " + tempMin);

                forecastDays.add(new Days(time, toFahrenheit(tempAvg), toFahrenheit(tempMax), toFahrenheit(tempMin)));
            }

            //Debug - testing that all class objects are correct
            for (int j = 0; j < forecastDays.size(); j++){
                forecastDays.get(j).printAll();
            }

            setGuiForecast(forecastDays);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void setGuiForecast(ArrayList<Days> forecastDays){
        Log.e("Debug", "FROM setGuiForecast - forecastDays arraylist size: ----->  " + forecastDays.size());

        //Start at index 1. This is because the first temperature displayed is the next day.
        String time1 = formatDate(forecastDays.get(1).time);

        TextView date1 = findViewById(R.id.date1_textView);
        date1.setText(time1);

        TextView date1Min = findViewById(R.id.date1_min_textView);
        date1Min.setText(forecastDays.get(1).tempMin + "\u2109");

        TextView date1Avg = findViewById(R.id.date1_avg_textView);
        date1Avg.setText(forecastDays.get(1).tempAvg+ "\u2109");

        TextView date1Max = findViewById(R.id.date1_max_textView);
        date1Max.setText(forecastDays.get(1).tempMax+ "\u2109");

        //------------------------------------------------------------
        String time2 = formatDate(forecastDays.get(2).time);

        TextView date2 = findViewById(R.id.date2_textView);
        date2.setText(time2);

        TextView date2Min = findViewById(R.id.date2_min_textView);
        date2Min.setText(forecastDays.get(2).tempMin + "\u2109");

        TextView date2Avg = findViewById(R.id.date2_avg_textView);
        date2Avg.setText(forecastDays.get(2).tempAvg+ "\u2109");

        TextView date2Max = findViewById(R.id.date2_max_textView);
        date2Max.setText(forecastDays.get(2).tempMax+ "\u2109");
        //------------------------------------------------------------

        //------------------------------------------------------------
        String time3 = formatDate(forecastDays.get(3).time);

        TextView date3 = findViewById(R.id.date3_textView);
        date3.setText(time3);

        TextView date3Min = findViewById(R.id.date3_min_textView);
        date3Min.setText(forecastDays.get(3).tempMin + "\u2109");

        TextView date3Avg = findViewById(R.id.date3_avg_textView);
        date3Avg.setText(forecastDays.get(3).tempAvg+ "\u2109");

        TextView date3Max = findViewById(R.id.date3_max_textView);
        date3Max.setText(forecastDays.get(3).tempMax+ "\u2109");
        //------------------------------------------------------------

        //------------------------------------------------------------
        String time4 = formatDate(forecastDays.get(4).time);

        TextView date4 = findViewById(R.id.date4_textView);
        date4.setText(time4);

        TextView date4Min = findViewById(R.id.date4_min_textView);
        date4Min.setText(forecastDays.get(4).tempMin + "\u2109");

        TextView date4Avg = findViewById(R.id.date4_avg_textView);
        date4Avg.setText(forecastDays.get(4).tempAvg+ "\u2109");

        TextView date4Max = findViewById(R.id.date4_max_textView);
        date4Max.setText(forecastDays.get(4).tempMax+ "\u2109");
        //------------------------------------------------------------

        //------------------------------------------------------------
        String time5 = formatDate(forecastDays.get(5).time);

        TextView date5 = findViewById(R.id.date5_textView);
        date5.setText(time5);

        TextView date5Min = findViewById(R.id.date5_min_textView);
        date5Min.setText(forecastDays.get(5).tempMin + "\u2109");

        TextView date5Avg = findViewById(R.id.date5_avg_textView);
        date5Avg.setText(forecastDays.get(5).tempAvg+ "\u2109");

        TextView date5Max = findViewById(R.id.date5_max_textView);
        date5Max.setText(forecastDays.get(5).tempMax+ "\u2109");
        //------------------------------------------------------------
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

    public String formatDate(String date){
        return date.substring(0, 10);
    }
}