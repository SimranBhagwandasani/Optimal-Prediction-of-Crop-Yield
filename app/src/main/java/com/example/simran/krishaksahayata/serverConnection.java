package com.example.simran.krishaksahayata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class serverConnection extends AppCompatActivity {


    String Area,Rainfall,Max_temp,Min_temp,Elevation,Precip,Humid,Solar,Season;
    TextView tvDisplay;
    ListView listView;
    Button btnChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_connection);

//        tvDisplay=findViewById(R.id.tvDisplay);
        listView = findViewById(R.id.listView);
        btnChart = findViewById(R.id.btnChart);
        Area=getIntent().getStringExtra("Area");
        Rainfall=getIntent().getStringExtra("rainfall");
        Max_temp=getIntent().getStringExtra("max_temp");
        Min_temp=getIntent().getStringExtra("min_temp");
        Elevation=getIntent().getStringExtra("elevation");
        Precip=getIntent().getStringExtra("precip");
        Humid=getIntent().getStringExtra("humi");
        Solar=getIntent().getStringExtra("solar");
        Season=getIntent().getStringExtra("season");

        try {
            getHttpResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(serverConnection.this,GraphActivity.class);
                startActivity(i);
            }
        });

    }
    public void getHttpResponse() throws IOException {

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "http://192.168.0.104:5000/output";


        JSONObject postdata = new JSONObject();
        try {
            postdata.put("Area", "100");
            postdata.put("Rainfall", "7.1333");
            postdata.put("Elevation", "589.0");
            postdata.put("Max Temperature", "33.00");
            postdata.put("Min Temperature", "33.00");
            postdata.put("Precipitation", "0.1944227");
            postdata.put("Relative Humidity", "0.34");
            postdata.put("Solar", "23.031844");
            postdata.put("Season", "Whole Year");

        } catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());


        Request request = new Request.Builder()
                .url(url).post(body)
                .build();
        Toast.makeText(this, request.toString(), Toast.LENGTH_SHORT).show();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String mMessage = response.body().string();
//                Log.i("check20", mMessage);
//                Log.i("check21", response.toString());
                JSONObject jsonObj = null;
                JSONObject jobj = null;
                List<CropYield> list = new ArrayList<>();
                CropYield cropYield;
                try {
                    jsonObj = new JSONObject(mMessage);
//                    Log.i("check22", jsonObj.toString());
                    jobj = jsonObj.getJSONObject("results");
//                    Log.i("check23", jobj.toString());

                    Iterator<?> keys = jobj.keys();
//                    List<CropYield> list = new ArrayList<>();
                    while(keys.hasNext()) {
                        String key = (String) keys.next();
                        cropYield = new CropYield(key, jobj.getDouble(key));
                        list.add(cropYield);
                    }

                    Collections.sort(list, new Comparator<CropYield>() {
                        @Override
                        public int compare(CropYield s1, CropYield s2) {
                            return Double.compare(s2.getYield(), s1.getYield());
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Log.i("check25", list.toString());
//                for(int i=0;i<list.size();i++){
//                    Log.i("check26",list.get(i).getCrop()+":"+list.get(i).getYield());
//                }

                final List<String> displayList = new ArrayList<>();
                for(int i=0;i<10;i++){
                    displayList.add(list.get(i).getCrop()+" : "+list.get(i).getYield());
                }


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ArrayAdapter adapter = new ArrayAdapter<String>(serverConnection.this, android.R.layout.simple_list_item_1, displayList);
                        listView.setAdapter(adapter);
                    }
                });

            }
        });
    }

}
