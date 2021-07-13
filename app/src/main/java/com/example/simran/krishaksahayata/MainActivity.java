package com.example.simran.krishaksahayata;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    TextView tvLatitude,tvLongitude;
    Button btnFetchDetails,btnLogout;
    GoogleApiClient gac;
    Location loc;
    double lat;
    double log;
    String latitude_value,longitude_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        btnFetchDetails = findViewById(R.id.btnFetchDetails);
        btnLogout = findViewById(R.id.btnLogout);
        tvLatitude.setText("19.03");
        tvLongitude.setText("73.03");

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addApi(LocationServices.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        gac = builder.build();
        btnFetchDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //double lat = loc.getLatitude();
                //double log = loc.getLongitude();
                lat = 19.03;
                log = 73.03;
                Intent i = new Intent(MainActivity.this, FetchDetailsActivity.class);

                Bundle b = new Bundle();
                b.putString("latitude",String.valueOf(lat));
                b.putString("longitude",String.valueOf(log));
                i.putExtras(b);
                startActivity(i);

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i1);
                finish();
            }
        });


    }

    class MyTask extends AsyncTask<String, Void, String>
    {
        String temp;
        @Override
        protected String doInBackground(String... strings) {
            String json = "";
            String line = "";

            try
            {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStreamReader isr = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader br = new BufferedReader(isr);

                while((line = br.readLine()) != null)
                    json = json + line + "\n";

                JSONObject jsonObject = new JSONObject(json);
                JSONObject p = jsonObject.getJSONObject("main");
                temp = p.getString("temp");
                Log.d("test4",String.valueOf(p));
            }
            catch (Exception e)
            {
                Log.d("SS123",""+e);
            }

            return temp;
        }

        @Override
        protected void onPostExecute(String aDouble) {
            super.onPostExecute(aDouble);

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(gac != null)
            gac.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(gac != null)
        {
            gac.disconnect();

        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        loc = LocationServices.FusedLocationApi.getLastLocation(gac);
        if(loc != null)
        {
            lat = loc.getLatitude();
            log = loc.getLongitude();
            //latitude_value = String.format("%.2f",lat);
            //longitude_value = String.format("%.2f",log);
            tvLatitude.setText("19.03");
            tvLongitude.setText("73.03");
            Log.d("Hello",String.valueOf(lat));

            MyTask t1 = new MyTask();
            String web = "http://api.openweathermap.org/data/2.5/weather?lat="+String.valueOf(lat)+"&lon="+String.valueOf(log);
            String api = "&appid=2dd78c1ebfd0be1a3dedb2e75843e0a2\n";
            String info = web + api;
            Log.d("testtttttt",info);
            t1.execute(info);
        }
        else
        {
            //tvShowWhenNoNetwork.setText("Please Enable GPS/ Come in Open Area.");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed!", Toast.LENGTH_LONG).show();
    }

}
