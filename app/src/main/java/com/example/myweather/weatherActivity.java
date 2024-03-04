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
    //Nested class used for holding attributes from the forecast api.
    class Days{
       String time, tempAvg, tempMax, tempMin;

        //Class constructor
        Days(String time, String tempAvg, String tempMax, String tempMin){
            this.time = time;
            this.tempAvg = tempAvg;
            this.tempMax = tempMax;
            this.tempMin = tempMin;
        }

        //This function is used for testing purposes.
        void printAll(){
            String info = time + " * " + tempAvg + " * " + tempMax + " * " + tempMin;
            Log.i("Days", "FROM Days - printAll - ----->  " + info);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //Parse the api response for the current weather and display its values.
        parseJsonRealTime();

        //Parse the api response for the forecast weather and display its values.
        parseJsonForecast();

        //If the back button is pressed then go back to the MainActivity class.
        Button back_btn = findViewById(R.id.back_button);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        //If the new search button is pressed then go to the MainActivity class.
        Button newSearch_btn = findViewById(R.id.newSearch_button);
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


    //-------------------------------------------------------------------------
    // This function will parse the response from the realtime api.
    //-------------------------------------------------------------------------
    public void parseJsonRealTime(){
        try {
            //Get realtime api response from the main activity.
            String json = getIntent().getStringExtra("responseRealTime");
            //Debug
            Log.i("Debug", "FROM parseJsonRealTime - string json: ----->  " + json);

            JsonParser parser = new JsonParser();
            //Get the first object from the realtime json.
            JsonObject jsonObject1 = (JsonObject)parser.parse(json);
            //Using the first json object get the nested object called "data".
            JsonObject jsonObject2 = (JsonObject)jsonObject1.get("data");

            //From the data object get the key and value for "time".
            String time = removeQuotes(jsonObject2.get("time").toString());
            //Debug
            Log.i("Debug", "FROM parseJsonRealTime - Time:  ---> " + time);

            //Convert the time to pst.
            time = toPst(time);
            //Debug
            Log.i("Debug", "FROM parseJsonRealTime - After toPst - Time:  ---> " + time);

            //In the "data" object get the nested object call "values".
            JsonObject jsonObject3 = (JsonObject)jsonObject2.get("values");
            //From the "values" object get the key and value for "temperature".
            String temp = toFahrenheit(jsonObject3.get("temperature").toString());
            //Debug
            Log.i("Debug", "FROM parseJsonRealTime - Temperature:  ---> " + temp);
            //From the "values" object get the key and value for "weatherCode".
            String weatherCode = jsonObject3.get("weatherCode").toString();
            //Debug
            Log.i("Debug", "FROM parseJsonRealTime - Weather Code:  ---> " + weatherCode);

            //get the "location" object
            JsonObject jsonObject4 = (JsonObject)jsonObject1.get("location");
            //From the "location" object get the key and value for "name".
            String location = removeQuotes(jsonObject4.get("name").toString());
            //Debug
            Log.i("Debug", "FROM parseJsonRealTime - LOCATION:  ---> " + location);

            //call the function getWeatherCode function, passing the string weather code and call the removeQuotes function.
            String weatherDescription = removeQuotes(getWeatherCode(weatherCode));
            //Debug
            Log.i("Debug", "FROM parseJsonRealTime - Weather Description:  ---> " + weatherDescription);

            //Call the function setWeather, passing the string arguments weathercode and time.
            setWeatherImage(weatherCode, time);

            //Set the textview text for temperature.
            TextView temp_textView = findViewById(R.id.temp_textView);
            //\u2109 is code for the fahrenheit sign.
            temp_textView.setText(temp+ "\u2109");

            //Set the textview text for the location.
            TextView city_textView = findViewById(R.id.city_textView);
            city_textView.setText(location);

            //Set the textview text for weatherDescription.
            TextView weatherDescription_textView = findViewById(R.id.weatherDescription_textView);
            weatherDescription_textView.setText(weatherDescription);

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    //-------------------------------------------------------------------------
    // This function will parse the response from the forecast api.
    //-------------------------------------------------------------------------
    public void parseJsonForecast(){
        try {
            //Get forecast api response from the main activity.
            String json = getIntent().getStringExtra("responseForecast");
            //Debug
            Log.i("Debug", "FROM parseJsonForecast - string json: ----->  " + json);

            JsonParser parser = new JsonParser();
            //Get the first object from the forecast json.
            JsonObject jsonObject1 = (JsonObject)parser.parse(json);
            //Using the first json object get the nested object called "timelines".
            JsonObject jsonObject2 = (JsonObject)jsonObject1.get("timelines");

            //Using the first json object get the nested json array called "daily".
            JsonArray jsonArrayDaily = (JsonArray)jsonObject2.get("daily");

            //Create a arraylist of Days type.
            ArrayList<Days> forecastDays = new ArrayList<Days>();

            String time, tempAvg, tempMax, tempMin;

            //Iterate through the "daily" json array.
            for (int i = 0; i < jsonArrayDaily.size(); i++)
            {
                //Get the first object inside the "daily" json array.
                JsonObject jsonObject3 = (JsonObject) jsonArrayDaily.get(i);

                //Get the key and value for "time" and call the function removeQuotes.
                time = removeQuotes(String.valueOf(jsonObject3.get("time")));
                //Debug
                Log.i("Debug", "FROM parseJsonForecast - For loop - time: ----->  " + time);

                //Using the first json object get the nested object called "values".
                JsonObject jsonObject4 = (JsonObject)jsonObject3.get("values");

                //From the "values" object get the key and value for "temperatureApparentAvg".
                tempAvg = String.valueOf(jsonObject4.get("temperatureApparentAvg"));
                //Debug
                Log.i("Debug", "FROM parseJsonForecast - For loop - temperatureApparentAvg: ----->  " + tempAvg);
                //From the "values" object get the key and value for "temperatureApparentMax".
                tempMax = String.valueOf(jsonObject4.get("temperatureApparentMax"));
                //Debug
                Log.i("Debug", "FROM parseJsonForecast - For loop - temperatureApparentMax: ----->  " + tempMax);
                //From the "values" object get the key and value for "temperatureApparentMin".
                tempMin = String.valueOf(jsonObject4.get("temperatureApparentMin"));
                //Debug
                Log.i("Debug", "FROM parseJsonForecast - For loop - temperatureApparentMin: ----->  " + tempMin);

                //Add a new Day object to the forecastDays array list.
                forecastDays.add(new Days(time, toFahrenheit(tempAvg), toFahrenheit(tempMax), toFahrenheit(tempMin)));
            }

            //Debug - testing that all class objects are correct
            for (int j = 0; j < forecastDays.size(); j++){
                forecastDays.get(j).printAll();
            }

            //Call the setGuiForecast function passing the argument forecastDays.
            setGuiForecast(forecastDays);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //-------------------------------------------------------------------------
    // This function uses the forecastDays data to populate the weatherActivity GUI
    // attributes. The function iterates through the forecastDays arraylist populating
    // the textviews of date, tempAvg, tempMin, and tempMax.
    //-------------------------------------------------------------------------
    public void setGuiForecast(ArrayList<Days> forecastDays) {
        //Debug
        Log.i("Debug", "FROM setGuiForecast - forecastDays arraylist size: ----->  " + forecastDays.size());

        //------------------------------------------------------------
        //Start at index 1 (tomorrow). This is because the first day temperature is already displayed from the realtime api.
        String time1 = formatDate(forecastDays.get(1).time);

        //Set the text for textViews date1, date1Min, date1Avg, and date1Max.
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

        //Set the text for textViews date2, date2Min, date2Avg, and date2Max.
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

        //Set the text for textViews date3, date3Min, date3Avg, and date3Max.
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

        //Set the text for textViews date4, date4Min, date4Avg, and date4Max.
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

        //Set the text for textViews date5, date5Min, date5Avg, and date5Max.
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

    //-------------------------------------------------------------------------
    // This function removes the beginning and ending quotation marks from the json key-values.
    //-------------------------------------------------------------------------
    public String removeQuotes(String s){
        String newString = "";
        newString = s.replace("\"", "");

        return newString;
    }

    //-------------------------------------------------------------------------
    // This function converts celsius to fahrenheit.
    //-------------------------------------------------------------------------
    public String toFahrenheit(String s){
        double celsius = Double.valueOf(s);

        double fahrenheit =  celsius * 1.8 + 32;    //Formula for celsius to fahrenheit.

        return String.format("%.2f", fahrenheit);
    }


    //-------------------------------------------------------------------------
    // This function formats the date to "00:00:00", leaving the first 10 characters
    // of the string and cuts off the remaining characters.
    //-------------------------------------------------------------------------
    public String formatDate(String date){
        return date.substring(0, 10);
    }

    //-------------------------------------------------------------------------
    // This function uses the weather code from the realtime api response and read
    // an external file called "weatherCodes.json". The argument passed is a weather
    // code that is the key and its value is the weather description. The function
    // returns the value of the weather code (key).
    //-------------------------------------------------------------------------
    public String getWeatherCode(String code){
        String weatherDescription= "ERROR";
        String weatherCodesJson = "";
        BufferedReader reader;

        //Open the external file found in the assets folder called "weatherCodes.json".
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("weatherCodes.json"), "UTF-8"));
            String newLine;

            //Read the file and assign the files contents to the string "newLine".
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

        //If the string containing the file contents is not null, then parse the json file and get the value for the key (weather code).
        if(weatherCodesJson != null) {
            JsonParser parser = new JsonParser();
            //Get the first json object.
            JsonObject jsonObject1 = (JsonObject) parser.parse(weatherCodesJson);
            //Using jsonObject1 get the nested object call "weatherCode".
            JsonObject jsonObject2 = (JsonObject) jsonObject1.get("weatherCode");
            //From the "weatherCode" object get the key and value for the weather code(the weather code is the argument of the function).
            weatherDescription = jsonObject2.get(code).toString();
        }
        //Return the weather description for the given weather code.
        return weatherDescription;
    }


    //-------------------------------------------------------------------------
    // This function converts the time from UTC to PST.
    //-------------------------------------------------------------------------
    public String toPst(String utc) {
        String pst = "";

        try {
            //Format the time format to "yyyy-MM-dd'T'HH:mm:ss'Z'".
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date utcDate = utcFormat.parse(utc);

            //Convert the time from UTC to PST.
            SimpleDateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            pstFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            pst = pstFormat.format(utcDate);
            //Debug
            Log.i("Debug", "FROM toPst - pst time: ----->  " + pst);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        //Return the PST time.
        return pst;
    }


    //-------------------------------------------------------------------------
    // This function
    //-------------------------------------------------------------------------
    public void setWeatherImage(String code, String time){
        //Get the ID for the weather_imageView.
        ImageView weatherImage = findViewById(R.id.weather_imageView);

        String imagePath = "";
        String weatherCodesPng = "";
        BufferedReader reader;

        //Open the external file found in the assets folder called ""weatherCodePng".json" and read the file contents into the string "weatherCodesPng".
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("weatherCodePng.json"), "UTF-8"));
            String newLine;

            //Read the file and assign the files contents to the string "newLine".
            while ((newLine = reader.readLine()) != null){
                weatherCodesPng += newLine;
            }
            //Close the file
            reader.close();
            //Debug
            Log.i("Debug", "FROM setWeatherImage - weatherCodePng.json: ----->  " + weatherCodesPng);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //If the string containing the file contents is not null, then parse the json file
        // and get the value for the key (weather code).
        if(weatherCodesPng != null) {
            //Get the current hour.
            String hoursStr = time.substring(11,13);
            int hoursInt = Integer.parseInt(hoursStr);
            //Debug
            Log.i("Debug", "FROM setWeatherImage - get hours: ----->  " + hoursStr);

            //If the hour is at night or early morning change the weather code to a night weather code.
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
                Log.i("Debug", "FROM setWeatherImage - if block - New Code: ----->  " + code);
            }

            JsonParser parser = new JsonParser();
            //Get the first json object.
            JsonObject jsonObject1 = (JsonObject) parser.parse(weatherCodesPng);
            //Using jsonObject1 get the nested object call "weatherCodePng".
            JsonObject jsonObject2 = (JsonObject) jsonObject1.get("weatherCodePng");

            //Get the image url path from the key value pair.
            imagePath = removeQuotes(jsonObject2.get(code).toString());
            //Debug
            Log.i("Debug", "FROM setWeatherImage - image path: ----->  " + imagePath);
        }

        //Use Picasso to upload the image to the ImageView call "weather_imageView".
        Picasso.get().load(imagePath).into(weatherImage);
    }
}