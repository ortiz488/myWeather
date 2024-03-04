package com.example.myweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    final OkHttpClient client = new OkHttpClient();
    boolean isRequestSuccessful1 = false;
    boolean isRequestSuccessful2 = false;
    String realTimeStr;
    String forecastStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set text view instructions.
        TextView welcome_TV = findViewById(R.id.textView_welcome);
        welcome_TV.setText("Welcome to myWeather!");
        TextView instructions_TV= findViewById(R.id.textView_Instructions);
        instructions_TV.setText("Enter Your city and state");

        //Get the user entered city.
        EditText city_editText = findViewById(R.id.editText_city);

        TextInputLayout stateSelect_tv = findViewById(R.id.stateSelect);

        //list of states for the drop down menu.
        String[] stateArray = {"Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "District Of Columbia",
                "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts",
                "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York",
                "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas",
                "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"};

        //Add the states to the drop down menu.
        MaterialAutoCompleteTextView stateList = findViewById(R.id.stateInput);
        stateList.setSimpleItems(stateArray);

        //If the go button is pressed then execute the following code.
        Button go_btn = findViewById(R.id.button_go);
        go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If the no state is selected from the drop down menu then show a error message.
                //Else if no city entered then show a error message.
                //Else, call the functions sendRequestRealtime and sendRequestForecast
                if(stateList.getText().toString().isEmpty()){
                    stateSelect_tv.setError("Please Select a State");
                } else if (city_editText.getText().toString().equals("Enter a City")) {
                    //Show error Toast message
                    Toast.makeText(MainActivity.this, "Error, please enter city name", Toast.LENGTH_LONG).show();
                } else {
                    //Get the user input.
                    String city = city_editText.getText().toString();
                    String state = stateList.getText().toString();

                    //Debug
                    Log.i("MainActivity", "City entered ---> " + city);
                    Log.i("MainActivity", "State selected ---> " + state);

                    //Call the following functions with the arguments city and state.
                    sendRequestRealtime(city,state);
                    sendRequestForecast(city,state);
                }
            }
        });
    }

    //-------------------------------------------------------------------------
    // This function creates a HTTP request to the Tomorrow.io weather realtime
    // api.
    //-------------------------------------------------------------------------
    public void sendRequestRealtime(String city, String state) {
        //Create url request
        String urlRealTime = "https://api.tomorrow.io/v4/weather/realtime?"
                + "location=" + city + " " + state
                + "&apikey=" + getString(R.string.api_key);

        //Debug
        Log.i("DEBUG", "From sendRequest - urlRealTime ---> " + urlRealTime);

        //Create a HTTP request.
        Request request = new Request.Builder().url(urlRealTime).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //If call creation fails print the stack trace.
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //If the api sends a response then pass the response to the weatherActivity class and start the activity.
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    //Store the api response into a string.
                    String body = responseBody.string();
                    Log.i("Debug", "sendRequestRealtime - RESPONSE ---> " + body);

                    //Create an intent for the weatherActivity class.
                    Intent intent = new Intent(MainActivity.this, weatherActivity.class);

                    //Store the api response body into a global string.
                    realTimeStr = body;
                    isRequestSuccessful1 = true;

                    //if the realtime api call has a response and the forecast api call has a response then start weatherActivity activity.
                    if(isRequestSuccessful1 && isRequestSuccessful2){
                        Log.i("Debug", "sendRequestRealtime if block - true");

                        //Pass the response body date to the weatherActivity class.
                        intent.putExtra("responseForecast", forecastStr);
                        intent.putExtra("responseRealTime", realTimeStr);

                        //Start weatherActivity
                        startActivity(intent);
                    }
                }
                else {
                    //Show error message if the response fails.
                    String errorBodyStr = response.body().string();
                    Log.e("ERROR", "From sendRequestRealtime - onResponse - else block ---> " + errorBodyStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Show error Toast message, asking user to verify their input.
                            Toast.makeText(MainActivity.this, "Error, please check your city and or state are correct!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    //-------------------------------------------------------------------------
    // This function creates a HTTP request to the Tomorrow.io weather forecast
    // api.
    //-------------------------------------------------------------------------
    public void sendRequestForecast(String city, String state){
        //Create url request
        String urlForecast = "https://api.tomorrow.io/v4/weather/forecast?"
                + "location=" + city + " " + state
                + "&timesteps=daily"
                + "&apikey=" + getString(R.string.api_key);

        //Debug
        Log.e("DEBUG", "From sendRequest - urlForecast ---> " + urlForecast);

        //Create a HTTP request.
        Request request = new Request.Builder().url(urlForecast).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //If call creation fails print the stack trace.
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //If the api sends a response then pass the response to the weatherActivity class and start the activity.
                if (response.isSuccessful()){
                    ResponseBody responseBody = response.body();
                    //Store the api response into a string.
                    String body = responseBody.string();
                    //Debug
                    Log.i("Debug", "sendRequestForecast - RESPONSE ---> " + body);

                    //Create an intent for the weatherActivity class.
                    Intent intent = new Intent(MainActivity.this, weatherActivity.class);

                    isRequestSuccessful2 = true;
                    //Store the api response body into a global string.
                    forecastStr = body;

                    //If the realtime api call has a response and the forecast api call has a response then start weatherActivity activity.
                    if(isRequestSuccessful1 && isRequestSuccessful2){
                        //Debug
                        Log.e("Debug", "sendRequestForecast if block - true");

                        //Pass the response body date to the weatherActivity class.
                        intent.putExtra("responseForecast", forecastStr);
                        intent.putExtra("responseRealTime", realTimeStr);

                        //Start weatherActivity
                        startActivity(intent);
                    }
                }
            }
        });
    }
}