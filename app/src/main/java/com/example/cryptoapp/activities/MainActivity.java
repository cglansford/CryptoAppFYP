package com.example.cryptoapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptoapp.R;
import com.example.cryptoapp.adapters.CurrencyRVAdapter;
import com.example.cryptoapp.models.CurrencyRVModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Search CryptoCurrencies, add to favourites list
public class MainActivity extends AppCompatActivity implements CurrencyRVAdapter.OnEditListener{

    private EditText searchEdit;
    private RecyclerView currenciesRV;
    private ImageButton starBTN;
    private ProgressBar loadingPB;
    public ArrayList<CurrencyRVModel> currencyRVModelArrayList;
    public ArrayList<String> favListCurrencies;
    public CurrencyRVAdapter currencyRVAdapter;

    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        searchEdit = findViewById(R.id.idEditSearch);
        currenciesRV = findViewById(R.id.idRVCurrencies);
        loadingPB = findViewById(R.id.idPBLoading);
        starBTN = findViewById(R.id.star);
        currencyRVModelArrayList = new ArrayList<>();
        favListCurrencies = new ArrayList<>();
        currencyRVAdapter = new CurrencyRVAdapter(currencyRVModelArrayList, favListCurrencies, MainActivity.this, this, this);
        currenciesRV.setLayoutManager(new LinearLayoutManager(this));
        currenciesRV.setAdapter(currencyRVAdapter);

        //TouchHelper for swipe action
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(currenciesRV);

        getFavList();
        getCurrencyData();

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //Once text has been entered into the search bar the filter currencies method
            //is called with the text passed through to search for
            @Override
            public void afterTextChanged(Editable s) {
                filterCurrencies(s.toString());
            }
        });


    }


    //Swipe Action
    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        //On swipe, get position of coin swiped then create a temp object
        //Add it to the users favourites list
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();
            Intent intent = new Intent(getApplicationContext(), CoinInfo.class);
            intent.putExtra("currencyName", currencyRVAdapter.getList().get(pos).getName());
            startActivity(intent);
            }


    };

    //Create filter function to be able to search for a particular coin
    private void filterCurrencies(String currency){
        ArrayList<CurrencyRVModel> filteredList = new ArrayList<>();
        for(CurrencyRVModel item: currencyRVModelArrayList){
            //If searched currency is in the list, adds to list
            if(item.getName().toLowerCase().startsWith(currency.toLowerCase())){
                filteredList.add(item);
            }
        }
        //returns filtered list
        currencyRVAdapter.filterList(filteredList);

    }

    //Loads crypto prices from api and adds to array list for currency model
    private void getCurrencyData(){
        loadingPB.setVisibility(View.VISIBLE);
        String url ="https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                //Extract data to List
                try {
                    JSONArray dataArray = response.getJSONArray("data");
                    //For loop gets the JSON object isolated so that can extract the value for ticket, name, price
                    for(int i = 0; i< dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        String name = dataObj.getString("name");
                        String ticker = dataObj.getString("symbol");
                        //As price value is a value inside an object inside an object, need to iterate through data
                        JSONObject quote = dataObj.getJSONObject("quote");
                        JSONObject USD = quote.getJSONObject("USD");
                        double price = USD.getDouble("price");

                        currencyRVModelArrayList.add(new CurrencyRVModel(name,ticker, price));
                    }
                    //Notify adapter that the data of array list has been updated
                    currencyRVAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed to extract json data..", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            //To handle possible errors
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Failed to get the data..", Toast.LENGTH_SHORT).show();
            }
        }){
            //Headers for api call
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

    //Star Button action in recycler view
    @Override
    public void onEditClick(int position) {

        CurrencyRVModel favTemp = currencyRVAdapter.getList().get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(mUser.getUid()).collection("crypto").document("favlist");

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    //If existing favlist exists
                    docRef.update("cryptos", FieldValue.arrayUnion(favTemp))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                //If no list exists, create one
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    docRef.set(favTemp);
                                    docRef.update("cryptos", FieldValue.arrayUnion(favTemp))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                }
                            });
                }
            });
    }

    //Get favlist info from Firebase
    public void getFavList(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(mUser.getUid()).collection("crypto").document("favlist");

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
                                        favListCurrencies.add(name);
                                        currencyRVAdapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    //Loads option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    //To change to new activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if (id==R.id.news){
            Intent intent = new Intent(this, News.class);
            startActivity(intent);
        }

        else if(id==R.id.portfolio){
            Intent intent = new Intent(this, Portfolio.class);
            startActivity(intent);
        }
        else if(id == R.id.maps){
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.forum){
            Intent intent = new Intent(this, Forum.class);
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