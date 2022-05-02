package com.example.cryptoapp.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptoapp.R;
import com.example.cryptoapp.models.PortfolioRVModel;
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
import java.util.Map;

public class PortfolioEditor extends AppCompatActivity {


    FirebaseUser mUser;

    private EditText amtHolding;
    private Button addHolding;
    private TextView cryptoTV;
    Dialog dialog;
    private ArrayList coinList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_editor);
        loadCoinList();
        cryptoTV = findViewById(R.id.cryptoTV);
        amtHolding = findViewById(R.id.amountHolding);
        addHolding = findViewById(R.id.addHolding);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        cryptoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Shows list of possible cryptocurrency entries based of price api
                dialog = new Dialog(PortfolioEditor.this);
                dialog.setContentView(R.layout.dialog_spinner);
                dialog.getWindow().setLayout(650, 800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(PortfolioEditor.this,
                        android.R.layout.simple_list_item_1, coinList);
                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        cryptoTV.setText(adapter.getItem(position));

                        // Dismiss dialog
                        dialog.dismiss();
                    }
                });
            }
        });


        addHolding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHolding();
            }
        });


    }


    public void addHolding() {

        //Error Handling
        if(TextUtils.isEmpty(amtHolding.getText())){
            amtHolding.setError("Cannot Be Empty");
            return;
        }
        try{
            Double.parseDouble(amtHolding.getText().toString());
        }catch(NumberFormatException e){
            amtHolding.setError("Please enter a number");
            return;
        }
        if(Double.parseDouble(String.valueOf(amtHolding.getText()))<0){
            amtHolding.setError("Must be 0 or greater");
            return;
        }

        //Assign values for portfolio entry
        String coinName = cryptoTV.getText().toString();
        double coinHolding = Double.parseDouble(amtHolding.getText().toString());


        PortfolioRVModel pv = new PortfolioRVModel(coinName, coinHolding);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(mUser.getUid()).collection("crypto").document("portfolioList");

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                docRef.update("list", FieldValue.arrayUnion(pv))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Intent intent = new Intent(PortfolioEditor.this, Portfolio.class);
                                startActivity(intent);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Creates list if first time
                                docRef.set(pv);

                                //Then adds entry to the created list
                                docRef.update("list", FieldValue.arrayUnion(pv))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Intent intent = new Intent(PortfolioEditor.this, Portfolio.class);
                                                startActivity(intent);

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

    //Load coin data from api
    public void loadCoinList() {
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //JSON Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Extract data to json array
                try {
                    JSONArray dataArray = response.getJSONArray("data");
                    //For loop gets the JSON object isolated so that can extract the value for ticket, name, price
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        String name = dataObj.getString("name");
                        coinList.add(name);
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


    //Loads option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.portfolio_edit_menu, menu);

        return true;
    }

    //To change to new activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.backButton) {
            Intent intent = new Intent(this, Portfolio.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}

