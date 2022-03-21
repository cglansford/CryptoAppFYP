package com.example.cryptoapp;

import static com.android.volley.VolleyLog.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Forum extends AppCompatActivity implements PostRVAdapter.OnEditListener {

    private Button addPostBTN;
    private RecyclerView postRV;
    public ArrayList<PostRVModel> postRVModelArrayList;
    public PostRVAdapter postRVAdapter;

    FirebaseUser mUser;
    FirebaseFirestore db;
    CollectionReference collectionRef;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        addPostBTN = findViewById(R.id.idAddPostBTN);

        postRV = findViewById(R.id.idPostRV);
        postRVModelArrayList = new ArrayList<>();
        postRVAdapter = new PostRVAdapter(postRVModelArrayList, Forum.this, this, this);
        postRV.setLayoutManager(new LinearLayoutManager(this));
        postRV.setAdapter(postRVAdapter);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        collectionRef = db.collection("posts");

        addPostBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Forum.this,AddPost.class);
                startActivity(intent);
            }
        });
        loadPosts();

    }

    public void loadPosts() {
        collectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               String postTitle = document.getString("postTitle");
                               String postDesc = document.getString("postDesc");
                               String uidOfCreator = document.getString("uidOfCreator");
                               postRVModelArrayList.add(new PostRVModel(postTitle, postDesc, uidOfCreator));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        postRVAdapter.notifyDataSetChanged();
                    }
                });

    }

    @Override
    public void onEditClick(int position) {

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
}
