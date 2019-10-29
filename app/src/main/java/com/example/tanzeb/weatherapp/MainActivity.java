package com.example.tanzeb.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView weatherTemp;
    TextView weatherImage;
    Typeface weatherFont;
    EditText zipcodeTxt;
    Button searchButton;
    TextView locationTxt;

    private static String ZIP_CODE= "zip_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         weatherTemp = findViewById(R.id.weatherTemp);
         weatherImage = findViewById(R.id.weatherImage);
         locationTxt = findViewById(R.id.locationText);

         weatherFont = Typeface.createFromAsset(getAssets(),"weathericons-regular-webfont.ttf");
         weatherImage.setTypeface(weatherFont);

         zipcodeTxt = findViewById(R.id.zipcodeTxt);
         searchButton = findViewById(R.id.searchButton);

         //Check for saved zipCode
        SharedPreferences prefs =getPreferences(Context.MODE_PRIVATE);
        String zipCode =prefs.getString(ZIP_CODE, null);
        if(zipCode != null){
            updateWeather(zipCode);
        }

         searchButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
               String zipCode=zipcodeTxt.getText().toString();
               updateWeather(zipCode);
             }
         });




    }
    String getIconForCode(int code){
        if(code==800){
            return "\uf00d";
        }else if(code==781){
            return "\uf056";
        }

        int hundreds = code /100;
        switch (hundreds){
            case 2:
                return "\uf01e";
            case 3:
                return "\uf01c";
            case 5:
                return "\uf019";
            case 6:
                return "\uf076";
            case 7:
                return "\uf014";
            default:
                return "\uf041";
        }
    }

    void updateWeather(final String zipCode) {
        String apiURL = String.format( "https://api.openweathermap.org/data/2.5/weather?zip=%1s,us&units=imperial&APPID=b90e62824838ec0f71c8212112d1d30e", zipCode);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //Displays weather on Screen
                try {

                   //Retrieves, formats ,and stores temp in the label
                    double temp =response.getJSONObject("main").getDouble("temp");
                    String tempFormatted=getString(R.string.temp_format, temp);

                    //Get Location
                    String location = response.getString("name");
                    locationTxt.setText(location);

                    //get the icon
                    int weathercode = response.getInt("cod");
                    String iconSymbol = getIconForCode(weathercode);
                    weatherImage.setText(iconSymbol);

                    SharedPreferences prefs= getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor=prefs.edit();
                    prefsEditor.putString(ZIP_CODE, zipCode);
                    prefsEditor.apply();


                    weatherTemp.setText(tempFormatted);
                } catch (JSONException e) {
                    Toast errorToast = Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG);
                    errorToast.show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast errorToast = Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG);
                errorToast.show();

            }
        });

        queue.add(request);
    }
}
