package com.example.cryptoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Portfolio extends AppCompatActivity implements PortfolioRVAdapter.OnEditListener {
    private PieChart pieChart;
    private RecyclerView currenciesRV;
    private TextView totalValueHolding;
    public ArrayList<PortfolioRVModel> portfolioRVModelArrayList;
    public PortfolioRVAdapter portfolioRVAdapter;
    public ArrayList<CurrencyRVModel> currencyRVModelArrayList;
    private static DecimalFormat df2 = new DecimalFormat("#.##");


    FirebaseUser mUser;
    FirebaseFirestore db;
    DocumentReference docRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        currencyRVModelArrayList= new ArrayList<>();
        loadCoinList();
        totalValueHolding = findViewById(R.id.totalHoldingValue);
        pieChart = findViewById(R.id.portfolioChart);
        currenciesRV = findViewById(R.id.idRVCurrencies);
        portfolioRVModelArrayList = new ArrayList<>();

        portfolioRVAdapter = new PortfolioRVAdapter(portfolioRVModelArrayList, Portfolio.this, this, this);
        currenciesRV.setLayoutManager(new LinearLayoutManager(this));
        currenciesRV.setAdapter(portfolioRVAdapter);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(mUser.getUid()).collection("crypto").document("portfolioList");



        Handler handler = new Handler();

        //Delay to allow price list to load
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getPortfolio();
            }
        }, 500);

        //Delay to allow portfolio data to load
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupPieChart();

            }
        }, 500);

        //Set delay to allow arraylist for portfolio to be instantiated

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPieChartData();
            }
        }, 1000);

    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setNoDataText("Loading Portfolio....");
        pieChart.setNoDataTextColor(Color.BLACK);
        pieChart.setCenterText("Portfolio");
        pieChart.setCenterTextSize(40);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(0,0,0,0);
        pieChart.getLegend().setEnabled(false);


    }

    //Load pie chart data
    private void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        double totalHolding = 0;

        //Gets total holding amount for portfolio
        for(int i = 0; i<portfolioRVModelArrayList.size(); i++){
            totalHolding = totalHolding + portfolioRVModelArrayList.get(i).getDollarTotal();

        }
        totalValueHolding.setText("$ " +String.valueOf(df2.format(totalHolding)));
        //Adds items to the pie chart, with their % of portfolio being their ratio of total to overall total
        for(int i = 0; i<portfolioRVModelArrayList.size(); i++){
            float mfloat = (float) (portfolioRVModelArrayList.get(i).getDollarTotal()/totalHolding);
            entries.add(new PieEntry(mfloat, portfolioRVModelArrayList.get(i).getName()));
        }


        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    //Get portfolio info from Firebase
    public void getPortfolio(){

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){

                            //loads data to json array to be used in activity
                            try {
                                //checks to see if there is any entries in the users portfolio
                                if(documentSnapshot.get("list") != null) {
                                    JSONArray json = new JSONArray(((List<?>) documentSnapshot.get("list")).toArray());

                                    for (int i = 0; i < json.length(); i++) {
                                        String name = json.getJSONObject(i).getString("name");
                                        double holding = Double.parseDouble(json.getJSONObject(i).getString("holdingAmount"));
                                        portfolioRVModelArrayList.add(new PortfolioRVModel(name, holding));
                                        //needs to set the holding value with new dollar amount every time activity is loaded
                                        portfolioRVModelArrayList.get(portfolioRVModelArrayList.size()-1)
                                                .setDollarTotal(searchCoinList(name)*holding);
                                    }
                                }else{
                                    //sends use to the editor if they dont have any entries in their portfolio
                                    Intent intent = new Intent(getApplicationContext(), PortfolioEditor.class);
                                    startActivity(intent);
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //Sort list in descending order
                            Collections.sort(portfolioRVModelArrayList, new Comparator<PortfolioRVModel>(){
                                @Override
                                public int compare(PortfolioRVModel p1, PortfolioRVModel p2){
                                    return Integer.valueOf((int) p2.getDollarTotal()).compareTo((int) p1.getDollarTotal());
                                }
                            });
                            portfolioRVAdapter.notifyDataSetChanged();



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
        portfolioRVAdapter.notifyDataSetChanged();

    }

    public double searchCoinList(String name){

        double searchedCoinPrice=0;
        for(int i = 0; i<currencyRVModelArrayList.size(); i++){
            if (currencyRVModelArrayList.get(i).getName().equals(name)) {
                searchedCoinPrice = currencyRVModelArrayList.get(i).getPrice();
            }
        }
        return searchedCoinPrice;
    }

    public void loadCoinList() {
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Extract data to List
                try {
                    JSONArray dataArray = response.getJSONArray("data");
                    //For loop gets the JSON object isolated so that can extract the value for ticket, name, price
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        String name = dataObj.getString("name");
                        String ticker = dataObj.getString("symbol");
                        //As price value is a value inside an object inside an object, need to iterate through data
                        JSONObject quote = dataObj.getJSONObject("quote");
                        JSONObject USD = quote.getJSONObject("USD");
                        double price = USD.getDouble("price");

                        currencyRVModelArrayList.add(new CurrencyRVModel(name,ticker, price));

                    }

                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            //To handle possible errors
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //Header for the GET request on CMC api - Key Name and Key Value
                headers.put("X-CMC_PRO_API_KEY", "59f04695-46d9-4ad1-b005-67538da42ddd");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }


    //Button action in recycler view
    @Override
    public void onEditClick(int position) {
        Intent intent = new Intent(this, PortfolioEditItem.class);
        intent.putExtra("holding", portfolioRVModelArrayList.get(position));
        startActivity(intent);
    }

    //Loads option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.portfolio_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();


        if(id==R.id.editButton){
            Intent intent = new Intent(this, PortfolioEditor.class);
            startActivity(intent);
        }


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