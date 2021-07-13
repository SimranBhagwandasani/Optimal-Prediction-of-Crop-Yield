package com.example.simran.krishaksahayata;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class FetchDetailsActivity extends AppCompatActivity {

    TextView tvLocation, tvTemperature, tvWind, tvHumidity, tvPressure;
    EditText etArea;
    Button btnPredict;
    ProgressDialog progressDialog;
    String latitude1, longitude1;
    String temperature,humidity,pressure,windspeed,city,temp_max,temp_min;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_details);

        tvTemperature = findViewById(R.id.tvTemperature);
        tvWind = findViewById(R.id.tvWind);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvPressure = findViewById(R.id.tvPressure);
        etArea = findViewById(R.id.etArea);
        tvLocation = findViewById(R.id.tvLocation);
        btnPredict = findViewById(R.id.btnPredict);
        latitude1 = getIntent().getStringExtra("latitude");
        longitude1 = getIntent().getStringExtra("longitude");

        //progressDialog = new ProgressDialog(FetchDetailsActivity.this);
        //progressDialog.setCancelable(false);
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressDialog.setTitle("Fetching App List");
        //progressDialog.setMessage("Please Wait...");

        /*tvLocation.setText("Chembur");
        tvWind.setText("2.1 m/s");
        tvTemperature.setText("29 deg C");
        tvPressure.setText("1014 hPa");
        tvHumidity.setText("34%");*/

        find_weather();
        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a=etArea.getText().toString();
                if (etArea.length() == 0) {
                    etArea.setError("Area field is Empty.");
                    etArea.requestFocus();
                    return;
                }
                Intent i=new Intent(FetchDetailsActivity.this,serverConnection.class);
                i.putExtra("Area",a);
                i.putExtra("temp_min",temp_min);
                i.putExtra("temp_max",temp_max);
                i.putExtra("humi",humidity);
                i.putExtra("rainfall","7.13333");
                i.putExtra("elevation","589.00");
                i.putExtra("solar","23.031");
                i.putExtra("season","Whole Year");
                i.putExtra("precip","0.194");
                startActivity(i);
            }
        });
    }

    public void find_weather()
    {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+latitude1+"&lon="+longitude1+"&appid=1a3ca7900eebc64a517a486e8c5c75c0";
        Log.d("test",url);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    Log.d("msg",response.toString());
                    JSONObject jsonObject = response.getJSONObject("main");
                    JSONObject jsonObject1 = response.getJSONObject("wind");
                    temperature = String.valueOf(jsonObject.getDouble("temp"));
                    temp_max=String.valueOf(jsonObject.getDouble("temp_min"));
                    temp_min=String.valueOf(jsonObject.getDouble("temp_min"));
                    humidity = String.valueOf(jsonObject.getInt("humidity"));
                    pressure = String.valueOf(jsonObject.getInt("pressure"));
                    windspeed = String.valueOf(jsonObject1.getDouble("speed"));
                    city = response.getString("name");
                    tvLocation.setText(city);
                    tvPressure.setText(pressure + " hPa");
                    tvHumidity.setText(humidity + " %");
                    tvWind.setText(windspeed+ " m/s");
                    Log.d("temp",temperature);

                    double temperature_int = Double.parseDouble(temperature);
                    double centigrate = (temperature_int - 273.15);
                    centigrate = Math.round(centigrate);
                    int i = (int)centigrate;
                    tvTemperature.setText(String.valueOf(i)+"°C");
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.d("error2",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("error1",error.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

        /*private void prod() {
            progressDialog.show();
        }*/


}
