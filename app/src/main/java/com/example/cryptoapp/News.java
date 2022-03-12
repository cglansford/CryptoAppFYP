package com.example.cryptoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class News extends AppCompatActivity {

    private RecyclerView newsRV;
    private ProgressBar loadingPB;
    private ArrayList<NewsRVModel>newsRVModelArrayList;
    private NewsRVAdapter newsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);
        loadingPB = findViewById(R.id.idPBLoading);
        newsRV =  findViewById(R.id.idRVNewsItem);
        newsRVModelArrayList = new ArrayList<>();
        newsRVAdapter = new NewsRVAdapter(newsRVModelArrayList, this);
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        newsRV.setAdapter(newsRVAdapter);
        getNewsData();

    }



    //Loads news data and adds to array list of news stories to be displayed
    private void getNewsData(){
        loadingPB.setVisibility(View.VISIBLE);
        String url ="https://crypto-news6.p.rapidapi.com/news";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingPB.setVisibility(View.GONE);
                //Extract data to List
                try {
                    JSONArray dataArray =  new JSONArray(response);
                    //For loop gets the JSON object isolated so that can extract the value for ticket, name, price
                    for(int i = 0; i< dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        String headline = dataObj.getString("title");
                        String sourceUrl = dataObj.getString("url");
                        String source = dataObj.getString("source");

                        newsRVModelArrayList.add(new NewsRVModel(headline,sourceUrl, source ));
                    }
                    //Notify adapter that the data of array list has been updated
                    newsRVAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(News.this, "Failed to extract json data..", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            //To handle possible errors
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(News.this, "Failed to get the data..", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //Header for the GET request on CMC api - Key Name and Key Value
                headers.put("X-RapidAPI-Key","b78c6a4786msha14f0241d475bc8p13cd39jsn651802d67073");
                return headers;
            }
        };
        requestQueue.add(request);
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