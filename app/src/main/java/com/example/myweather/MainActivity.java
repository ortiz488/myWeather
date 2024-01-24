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

import com.google.android.material.button.MaterialButton;
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
    String forecasrStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set instructions
        TextView welcome_TV = findViewById(R.id.textView_welcome);
        welcome_TV.setText("Welcome to myWeather!");

        TextView instructions_TV= findViewById(R.id.textView_Instructions);
        instructions_TV.setText("Enter Your city and state");

        //Get the user entered city
        EditText city_editText = findViewById(R.id.editText_city);

        TextInputLayout stateSelect_tv = findViewById(R.id.stateSelect);

        //list of states for the drop down menu
        String[] stateArray = {"Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "District Of Columbia",
                "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts",
                "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York",
                "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas",
                "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"};

        MaterialAutoCompleteTextView stateList = findViewById(R.id.stateInput);
        stateList.setSimpleItems(stateArray);

        Button go_btn = findViewById(R.id.button_go);

        go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stateList.getText().toString().isEmpty()){
                    stateSelect_tv.setError("Please Select a State");
                }
                else{
                    String city = city_editText.getText().toString();

                    String state = stateList.getText().toString();
                    Intent intent;
                    intent = new Intent(MainActivity.this, weatherActivity.class);

                    //intent.putExtra("city_choice", city);
                    //intent.putExtra("state_choice", state);

                    //Debug
                    Log.e("MainActivity", "City entered ---> " + city);
                    Log.e("MainActivity", "State selected ---> " + state);

                    sendRequestRealtime(city,state);
                    sendRequestForecast(city,state);

                    Log.e("MainActivity", "From - MainActivity");

                    /*if(isRequestSuccessful1 && isRequestSuccessful2){
                        Log.e("MainActivity", "HEYYY ");
                        startActivity(intent);
                    }*/
                }
            }
        });
    }

    public void sendRequestRealtime(String city, String state){
        String urlRealTime = "https://api.tomorrow.io/v4/weather/realtime?"
                + "location=" + city + " " + state
                + "&apikey=" + getString(R.string.api_key);

        //Debug
        Log.e("DEBUG", "From sendRequest - urlRealTime ---> " + urlRealTime);

        /*String urlForecast = "https://api.tomorrow.io/v4/weather/forecast?"
                + "location=" + city + " " + state
                + "&timesteps=daily"
                + "&apikey=" + getString(R.string.api_key);

        //Debug
        Log.e("DEBUG", "From sendRequest - urlForecast ---> " + urlForecast);*/

        //New 1/15/24
        Request request = new Request.Builder().url(urlRealTime).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    ResponseBody responseBody = response.body();
                    String body = responseBody.string();
                    Log.e("Debug", "sendRequestRealtime - RESPONSE ---> " + body);

                    Intent intent = new Intent(MainActivity.this, weatherActivity.class);
                    //intent.putExtra("1", body);

                    realTimeStr = body;
                    isRequestSuccessful1 = true;

                    if(isRequestSuccessful1 && isRequestSuccessful2){
                        Log.e("Debug", "sendRequestRealtime if block - true");

                        intent.putExtra("responseForecast", forecasrStr);
                        intent.putExtra("1", realTimeStr);

                        startActivity(intent);
                    }
                }
                else {
                    String errorBodyStr = response.body().string();
                    Log.e("ERROR", "From sendRequestRealtime - onResponse - else block ---> " + errorBodyStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Show error Toast message
                            Toast.makeText(MainActivity.this, "Error, please check your city and or state are correct!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    public void sendRequestForecast(String city, String state){
        String urlForecast = "https://api.tomorrow.io/v4/weather/forecast?"
                + "location=" + city + " " + state
                + "&timesteps=daily"
                + "&apikey=" + getString(R.string.api_key);

        //Debug
        Log.e("DEBUG", "From sendRequest - urlForecast ---> " + urlForecast);

        //New 1/15/24
        Request request = new Request.Builder().url(urlForecast).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    ResponseBody responseBody = response.body();
                    String body = responseBody.string();
                    Log.e("Debug", "sendRequestForecast - RESPONSE ---> " + body);

                    Intent intent = new Intent(MainActivity.this, weatherActivity.class);
                    //intent.putExtra("responseForecast", body);

                    isRequestSuccessful2 = true;
                    //startActivity(intent);

                    forecasrStr = body;

                    if(isRequestSuccessful1 && isRequestSuccessful2){
                        //Debug
                        Log.e("Debug", "sendRequestForecast if block - true");

                        intent.putExtra("responseForecast", forecasrStr);
                        intent.putExtra("1", realTimeStr);

                        startActivity(intent);
                    }
                }
                //Only one of the else blocks needs to catch the invalid request.
               /* else {
                    String errorBodyStr = response.body().string();
                    Log.e("ERROR", "From sendRequestForecast - onResponse - else block ---> " + errorBodyStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Show error Toast message
                            Toast.makeText(MainActivity.this, "Error, please check your city and or state are correct!", Toast.LENGTH_LONG).show();
                        }
                    });
                }*/
            }
        });
    }
}