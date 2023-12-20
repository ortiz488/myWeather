package com.example.myweather;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

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
        String city = city_ET.getText().toString();

        Log.e("LOG_CHECK", city);

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
                    //Toast.makeText(MainActivity.this, stateList.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}