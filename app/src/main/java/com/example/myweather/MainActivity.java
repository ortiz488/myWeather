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
        EditText city_ET = findViewById(R.id.editText_city);
        //String city = city_ET.getText().toString();

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
                    String city = city_ET.getText().toString();

                    String state = stateList.getText().toString();
                    Intent intent;
                    intent = new Intent(MainActivity.this, weatherActivity.class);

                    intent.putExtra("city_choice", city);
                    intent.putExtra("state_choice", state);

                    //Debug
                    Log.e("MainActivity", "City entered ---> " + city);
                    Log.e("MainActivity", "State selected ---> " + state);

                    //Toast.makeText(MainActivity.this, stateList.getText().toString(), Toast.LENGTH_SHORT).show();

                    sendRequest(city,state);
                    //startActivity(intent);
                }
            }
        });
    }

    public void sendRequest(String city, String state){
        //String url = "https://api.tomorrow.io/v4/weather/realtime?location=42.3478,-71.0466&apikey=WIKhfA7tBjUdHWiQ0ORZTX18Iau9vkd1";
        String url = "https://api.tomorrow.io/v4/weather/realtime?location=portland&apikey=WIKhfA7tBjUdHWiQ0ORZTX18Iau9vkd1";

        //New
        //Intent i = new Intent(MainActivity.this, weatherActivity.class);
        //i.putExtra("response", r.toString());

        //New1
        //MainActivity mainActivity = new MainActivity();
        //String response = mainActivity.run(url);


        //Saturday - 1-6-24
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //Log.e("ERROR", "From sendRequest - onFailure ---> " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //Works - 1-7-24
                if (response.isSuccessful()){
                    ResponseBody responseBody = response.body();
                    String body = responseBody.string();
                    Log.e("onResponse", "HEY ---> " + body);

                    Intent i = new Intent(MainActivity.this, weatherActivity.class);
                    //i.putExtra("response", response.body().string());
                    //i.putExtra("response", responseBody.string());

                    //i.putExtra("i", body);

                    //i.putExtra("response", responseBody.string());

                    //Log.e("MAIN", "FROM run() ---> " + responseBody.string());

                    Intent intent = new Intent(MainActivity.this, weatherActivity.class);
                    intent.putExtra("response", body);
                    startActivity(intent);
                }
            }
        });
    }
}