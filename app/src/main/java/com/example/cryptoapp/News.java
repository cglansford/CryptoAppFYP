package com.example.cryptoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;


import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class News extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RecyclerViewInterface{

    private RecyclerView newsRV;

    private ArrayList<NewsRVModel>newsRVModelArrayList;
    private NewsRVAdapter newsRVAdapter;
    private Spinner dropdownSpinner;
    private static final String[] newsSources = {"All","Abcnews", "Coindesk",
                                    "Cointelegraph","Cryptonews", "Economictimes","Yahoo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);

        newsRV =  findViewById(R.id.idRVNewsItem);
        dropdownSpinner = findViewById(R.id.sourceSpinner);




        newsRVModelArrayList = new ArrayList<>();
        newsRVAdapter = new NewsRVAdapter(newsRVModelArrayList, this, this);
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        newsRV.setAdapter(newsRVAdapter);

        getNewsData();

       ArrayAdapter<String>dropdownAdapter = new ArrayAdapter<String>(News.this,
                android.R.layout.simple_spinner_item, newsSources);
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownSpinner.setAdapter(dropdownAdapter);
        dropdownSpinner.setSelection(0);
        dropdownSpinner.setOnItemSelectedListener(this);
        filterNewsSource("All");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id){
        filterNewsSource((String) parent.getItemAtPosition(position));
        Toast.makeText(News.this, "Test", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //Create filter function to be able to search for a particular coin
    private void filterNewsSource(String source){

            ArrayList<NewsRVModel> filteredList = new ArrayList<>();
            if(source.equals("All")) {

                filteredList.addAll(newsRVModelArrayList);
            }else{
                for (NewsRVModel item : newsRVModelArrayList) {
                    //If searched currency is in the list, adds to list
                    if (item.getSourceName().equalsIgnoreCase(source)) {
                        filteredList.add(item);
                    }
                }
            }
            //returns filtered list
            newsRVAdapter.filterList(filteredList);
        }



    //Loads news data and adds to array list of news stories to be displayed
    private void getNewsData(){
        String url ="https://crypto-news6.p.rapidapi.com/news";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //To handle possible errors
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
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
            }catch (JSONException e){
               getNewsData();
            }
        }, error -> Toast.makeText(News.this, "Failed to get the data..", Toast.LENGTH_SHORT).show()){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                //Header for the GET request on CMC api - Key Name and Key Value
                headers.put("X-RapidAPI-Key","b78c6a4786msha14f0241d475bc8p13cd39jsn651802d67073");
                return headers;
            }
        };
        //Notify adapter that the data of array list has been updated
        newsRVAdapter.notifyDataSetChanged();
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

        else if(id==R.id.forum){
            Intent intent = new Intent(this, Forum.class);
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

    @Override
    public void onItemLongClick(int position) {
        Intent intent = new Intent(getApplicationContext(), AddPost.class);
        intent.putExtra("newsArticle", newsRVModelArrayList.get(position));
        startActivity(intent);

    }
}