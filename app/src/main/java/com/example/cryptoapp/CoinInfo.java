package com.example.cryptoapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoinInfo extends AppCompatActivity {

    private TextView bigPrice, bigTKR, bigName, bigPriceChange;
    private TextView circSupply, totalSupply, mCap, totalMCap;
    private TextView day1Holder, day7Holder, day30Holder;
    private TextView pricePredictionHolder, upperBandHolder, lowerBandHolder;
    private String currencyName;
    private static DecimalFormat df6 = new DecimalFormat("###,###,###,###.######");
    private static DecimalFormat df2 = new DecimalFormat("###,###,###,###.##; -###,###,###,###.##");


    FirebaseFirestore db;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_info);
        bigPrice = findViewById(R.id.idBigPrice);
        bigTKR = findViewById(R.id.idBigTKR);
        bigName = findViewById(R.id.idBigName);
        bigPriceChange = findViewById(R.id.idBigPriceChange);
        circSupply = findViewById(R.id.circulatingSupplyHolder);
        totalSupply = findViewById(R.id.totalSupplyHolder);
        mCap = findViewById(R.id.marketCapHolder);
        totalMCap = findViewById(R.id.dilutedValHolder);
        day1Holder = findViewById(R.id.day1Holder);
        day7Holder= findViewById(R.id.day7Holder);
        day30Holder= findViewById(R.id.day30Holder);
        pricePredictionHolder = findViewById(R.id.idPricePredictionHolder);
        upperBandHolder = findViewById(R.id.idUpperBandHolder);
        lowerBandHolder = findViewById(R.id.idLowerBandHolder);

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("predictions").document("cryptos");

        loadPrediction();
        Intent incomingIntent = getIntent();
        currencyName = (String) incomingIntent.getSerializableExtra("currencyName");
        getCurrencyData();
    }

    private void loadPrediction(){

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){

                            //loads data to json array to be used in activity
                            try {
                                //checks to see if there is any entries in the users portfolio
                                if(documentSnapshot.get("cryptos") != null) {
                                    JSONArray json = new JSONArray(((List<?>) documentSnapshot.get("cryptos")).toArray());

                                    for (int i = 0; i < json.length(); i++) {

                                        String name = json.getJSONObject(i).getString("name");
                                        if(name.equalsIgnoreCase(currencyName)){
                                            pricePredictionHolder.setText("$ " + df6.format(json.getJSONObject(i).getDouble("Prediction")));
                                            upperBandHolder.setText("$ " + df6.format(json.getJSONObject(i).getDouble("Upper Band")));
                                            lowerBandHolder.setText("$ " + df6.format(json.getJSONObject(i).getDouble("Lower Band")));
                                        }


                                    }
                                }else{
                                    //if empty
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Intent intent = new Intent(getApplicationContext(), PortfolioEditor.class);
                            startActivity(intent);
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
    private void getCurrencyData(){

        String url ="https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //Extract data to List
                try {
                    JSONArray dataArray = response.getJSONArray("data");
                    //For loop gets the JSON object isolated so that can extract the value for ticket, name, price
                    for(int i = 0; i< dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        if(dataObj.getString("name").equalsIgnoreCase(currencyName)) {
                            bigName.setText(dataObj.getString("name"));
                            bigTKR.setText(dataObj.getString("symbol"));
                            circSupply.setText(dataObj.getString("circulating_supply"));
                            totalSupply.setText(dataObj.getString("max_supply"));
                            //As price value is a value inside an object inside an object, need to iterate through data
                            JSONObject quote = dataObj.getJSONObject("quote");
                            JSONObject USD = quote.getJSONObject("USD");
                            bigPrice.setText("$ " + df6.format(USD.getDouble("price")));
                            mCap.setText("$ " + df2.format(USD.getDouble("market_cap")));
                            totalMCap.setText("$ " + df2.format(USD.getDouble("fully_diluted_market_cap")));


                            bigPriceChange.setText(df2.format(USD.getDouble("percent_change_24h")) + "%");
                            day1Holder.setText(df2.format(USD.getDouble("percent_change_24h") )+ "%");
                            if(USD.getDouble("percent_change_24h")<0){
                                bigPriceChange.setTextColor(Color.RED);
                                day1Holder.setTextColor(Color.RED);
                            }


                            day7Holder.setText(df2.format(USD.getDouble("percent_change_7d")) + "%");
                            if(USD.getDouble("percent_change_7d")<0){
                                day7Holder.setTextColor(Color.RED);
                            }

                            day30Holder.setText(df2.format(USD.getDouble("percent_change_30d")) + "%");
                            if(USD.getDouble("percent_change_30d")<0){
                                day30Holder.setTextColor(Color.RED);
                            }


                        }


                    }
                    //Notify adapter that the data of array list has been updated

                }catch (JSONException e){
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            //To handle possible errors
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //Header for the GET request on CMC api - Key Name and Key Value
                headers.put("X-CMC_PRO_API_KEY","59f04695-46d9-4ad1-b005-67538da42ddd");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    //Loads option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();



        if(id==R.id.backButton){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        else if(id==R.id.portfolio){
            Intent intent = new Intent(this, Portfolio.class);
            startActivity(intent);
        }

        else if (id==R.id.news){
            Intent intent = new Intent(this, News.class);
            startActivity(intent);


        }

        else if(id == R.id.maps){
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }

        else if(id == R.id.fav){
            Intent intent = new Intent(this, FavList.class);
            startActivity(intent);
        }
        else if(id ==R.id.logout){

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();

        }

        return super.onOptionsItemSelected(item);
    }
}
