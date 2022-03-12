package com.example.cryptoapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavList extends AppCompatActivity implements FavRVAdapter.OnEditListener{



    private RecyclerView favRV;
    private ProgressBar loadingPB;
    public ArrayList<FavRVModel> favRVModelArrayList;

    public FavRVAdapter favRVAdapter;
    public ArrayList<String> favNames;

    FirebaseUser mUser;
    FirebaseFirestore db;
    DocumentReference docRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        favRVModelArrayList = new ArrayList<>();
        favNames = new ArrayList<>();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(mUser.getUid()).collection("crypto").document("favlist");

         favRV = findViewById(R.id.idRVFavourites);
         loadingPB = findViewById(R.id.idPBLoading);




        favRVAdapter = new FavRVAdapter(favRVModelArrayList, FavList.this, this, this);
        favRV.setLayoutManager(new LinearLayoutManager(this));
        favRV.setAdapter(favRVAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(favRV);


        //getCurrencyData();
        getFavList();



}

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



            FavRVModel favTemp = favRVAdapter.getList().get(pos);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                        docRef.update("cryptos", FieldValue.arrayRemove(favTemp))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //upon removing the entry from the DB, need to update the Adapter and list
                                        favRVModelArrayList.remove(pos);
                                        favRVAdapter.notifyItemRemoved(pos);
                                        favRVAdapter.notifyItemRangeChanged(pos, favRVModelArrayList.size());
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
    };



    public void getFavList(){

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){

                            try {
                                JSONArray json = new JSONArray(((List<?>) documentSnapshot.get("cryptos")).toArray());

                                for(int i = 0; i< json.length(); i++) {
                                    String name = json.getJSONObject(i).getString("name");
                                    String ticker = json.getJSONObject(i).getString("ticker");
                                    double price = Double.parseDouble(json.getJSONObject(i).getString("price"));
                                    favRVModelArrayList.add(new FavRVModel(name, ticker ,price));
                                }
                                favRVAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }else{
                            Toast.makeText(FavList.this, "No existing document", Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        favRVAdapter.notifyDataSetChanged();


    }


    //Button action in recycler view
    @Override
    public void onEditClick(int position) {
        FavRVModel favTemp = favRVAdapter.getList().get(position);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                docRef.update("cryptos", FieldValue.arrayRemove(favTemp))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //upon removing the entry from the DB, need to update the Adapter and list
                                favRVModelArrayList.remove(position);
                                favRVAdapter.notifyItemRemoved(position);
                                favRVAdapter.notifyItemRangeChanged(position, favRVModelArrayList.size());
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



    //Loads option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);

        return true;
    }

    //To change to new activity
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
