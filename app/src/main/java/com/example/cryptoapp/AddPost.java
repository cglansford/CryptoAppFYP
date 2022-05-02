package com.example.cryptoapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

//Add post to forum
public class AddPost extends AppCompatActivity {


    private TextInputEditText postTitle;
    private TextInputEditText postDesc;
    private FloatingActionButton submitBTN;
    FirebaseUser mUser;
    FirebaseFirestore db;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        postTitle = findViewById(R.id.idInputPostTitle);
        postDesc = findViewById(R.id.idInputPostDesc);
        submitBTN = findViewById(R.id.postSubmitBTN);

        Intent incomingIntent = getIntent();
        NewsRVModel intentPost = (NewsRVModel) incomingIntent.getSerializableExtra("newsArticle");

        if(intentPost!=null) {
            postTitle.setText(intentPost.getHeadline());
            postDesc.setText(intentPost.getSourceURL());
        }

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("posts").document();

        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postTitleContent = postTitle.getText().toString();
                String postDescContent = postDesc.getText().toString();
                String postCreator = mUser.getEmail();

                if(TextUtils.isEmpty(postTitleContent)){
                    postTitle.setError("Title is required");
                    return;
                }
                if(TextUtils.isEmpty(postDescContent)){
                    postDesc.setError("Required");
                    return;
                }

                PostRVModel aPost = new PostRVModel(postTitleContent, postDescContent, postCreator);

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        docRef.set(aPost);
                        Intent intent = new Intent(AddPost.this, Forum.class);
                        startActivity(intent);

                    }
                });

            }
        });
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
            Intent intent = new Intent(this, Forum.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
