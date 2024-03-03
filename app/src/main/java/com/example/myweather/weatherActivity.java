package com.example.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

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
            String json = getIntent().getStringExtra("responseRealTime");

            Log.e("Debug", "FROM parseJsonRealTime - string json: ----->  " + json);

            JsonParser parser = new JsonParser();

            JsonObject jsonObject1 = (JsonObject)parser.parse(json);

            JsonObject jsonObject2 = (JsonObject)jsonObject1.get("data");


            String time = removeQuotes(jsonObject2.get("time").toString());
            Log.e("Debug", "FROM parseJsonRealTime - Time:  ---> " + time);

            time = toPst(time);
            Log.e("Debug", "FROM parseJsonRealTime - After toPst - Time:  ---> " + time);


            JsonObject jsonObject3 = (JsonObject)jsonObject2.get("values");

            String temp = toFahrenheit(jsonObject3.get("temperature").toString());
            Log.e("Debug", "FROM parseJsonRealTime - Temperature:  ---> " + temp);

            String weatherCode = jsonObject3.get("weatherCode").toString();
            Log.e("Debug", "FROM parseJsonRealTime - Weather Code:  ---> " + weatherCode);

            JsonObject jsonObject4 = (JsonObject)jsonObject1.get("location");

            String location = removeQuotes(jsonObject4.get("name").toString());
            Log.e("Debug", "FROM parseJsonRealTime - LOCATION:  ---> " + location);


            String weatherDescription = removeQuotes(getWeatherCode(weatherCode));
            Log.e("Debug", "FROM parseJsonRealTime - Weather Description:  ---> " + weatherDescription);

            setWeatherImage(weatherCode, time);

            //Set the textview text for temperature
            TextView temp_textView = findViewById(R.id.temp_textView);
            temp_textView.setText(temp+ "\u2109");

            //Set the textview text for the location
            TextView city_textView = findViewById(R.id.city_textView);
            city_textView.setText(location);

            TextView weatherDescription_textView = findViewById(R.id.weatherDescription_textView);
            //weatherDescription_textView.setText("Current Weather Description: " + weatherDescription);
            weatherDescription_textView.setText(weatherDescription);

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


    //Leave the first 10 characters of the string and cut off the remaining characters.
    public String formatDate(String date){
        return date.substring(0, 10);
    }


    //This function using the weather code from the realtime api response to read an external file called
    // "weatherCodes.json". The argument passed is a weather code that is the key. The function returns
    // the value of the weather code (key).
    public String getWeatherCode(String code){
        String weatherDescription= "ERROR";
        String weatherCodesJson = "";

        BufferedReader reader;

        //Open the external file found in the assets folder called "weatherCodes.json".
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("weatherCodes.json"), "UTF-8"));
            String newLine;

            //Read the file and assign the files contents to the string "newLine"
            while ((newLine = reader.readLine()) != null){
                weatherCodesJson += newLine;
            }

            //Close the file
            reader.close();
            //Debug
            Log.e("Debug", "FROM getWeatherCode - weatherCodes.json: ----->  " + weatherCodesJson);

        }catch (IOException e){
            e.printStackTrace();
        }

        //If the string containing the file contents is not null, then parse the json file
        // and get the value for the key (weather code).
        if(weatherCodesJson != null) {
            JsonParser parser = new JsonParser();

            JsonObject jsonObject1 = (JsonObject) parser.parse(weatherCodesJson);

            JsonObject jsonObject2 = (JsonObject) jsonObject1.get("weatherCode");

            weatherDescription = jsonObject2.get(code).toString();
        }

        return weatherDescription;
    }


    public String toPst(String utc){
        String pst = "";

        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date utcDate = utcFormat.parse(utc);

            SimpleDateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            pstFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

            pst = pstFormat.format(utcDate);

            //Debug
            Log.e("Debug", "FROM toPst - pst time: ----->  " + pst);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return pst;
    }


    public void setWeatherImage(String code, String time){
        ImageView weatherImage = findViewById(R.id.weather_imageView);

        String imagePath = "";
        String weatherCodesPng = "";
        BufferedReader reader;

        String testURL = "https://raw.githubusercontent.com/Tomorrow-IO-API/tomorrow-weather-codes/master/V2_icons/large/png/10000_clear_large%402x.png";

        //Open the external file found in the assets folder called ""weatherCodePng".json" and read the file contents into the string "weatherCodesPng"
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("weatherCodePng.json"), "UTF-8"));
            String newLine;

            //Read the file and assign the files contents to the string "newLine"
            while ((newLine = reader.readLine()) != null){
                weatherCodesPng += newLine;
            }
            //Close the file
            reader.close();

            //Debug
            Log.e("Debug", "FROM setWeatherImage - weatherCodePng.json: ----->  " + weatherCodesPng);
        }catch (IOException e){
            e.printStackTrace();
        }

        //If the string containing the file contents is not null, then parse the json file
        // and get the value for the key (weather code).
        if(weatherCodesPng != null) {

            //int hoursInt;
            String hoursStr = time.substring(11,13);
            int hoursInt = Integer.parseInt(hoursStr);
            Log.e("Debug", "FROM setWeatherImage - get hours: ----->  " + hoursStr);


            if(hoursInt >= 17 || hoursInt < 7) {
                switch (code)
                {
                    case "1000":
                        code = "10001";
                        break;
                    case "1102":
                        code = "11021";
                        break;
                    case "1101":
                        code = "11011";
                        break;
                    case "1100":
                        code = "11001";
                        break;
                }
                //Debug
                Log.e("Debug", "FROM setWeatherImage - if block - New Code: ----->  " + code);
            }

            JsonParser parser = new JsonParser();

            JsonObject jsonObject1 = (JsonObject) parser.parse(weatherCodesPng);

            JsonObject jsonObject2 = (JsonObject) jsonObject1.get("weatherCodePng");

            imagePath = removeQuotes(jsonObject2.get(code).toString());

            //Debug
            Log.e("Debug", "FROM setWeatherImage - image path: ----->  " + imagePath);
        }

        Picasso.get().load(imagePath).into(weatherImage);

        //Picasso.get().load(testURL).into(weatherImage);
    }

    //android:background="@drawable/weather5"

}