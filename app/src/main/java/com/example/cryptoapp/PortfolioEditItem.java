package com.example.cryptoapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

//Edit individual portfolio item
public class PortfolioEditItem extends AppCompatActivity {

    private Button submitBTN, removeBTN;
    private EditText editHolding;
    private TextView viewName;

    FirebaseUser mUser;
    FirebaseFirestore db;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_edit_item);

        Intent incomingIntent = getIntent();
        PortfolioRVModel mHolding = (PortfolioRVModel) incomingIntent.getSerializableExtra("holding");

        editHolding = findViewById(R.id.editHolding);
        editHolding.setText(String.valueOf( mHolding.getHoldingAmount()));
        viewName = findViewById(R.id.viewName);
        viewName.setText(mHolding.getName());
        submitBTN = findViewById(R.id.submitBTN);
        removeBTN = findViewById(R.id.removeBTN);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(mUser.getUid()).collection("crypto").document("portfolioList");

        //removes all holding
        removeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mHolding.setDollarTotal(0);
                        docRef.update("list", FieldValue.arrayRemove(mHolding))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Intent intent = new Intent(PortfolioEditItem.this, Portfolio.class);
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

        //Submit edit all holding
        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Error Handling
                if(TextUtils.isEmpty(editHolding.getText())){
                    editHolding.setError("Cannot Be Empty");
                    return;
                }
                if(Double.parseDouble(String.valueOf(editHolding.getText()))<0){
                    editHolding.setError("Must be 0 or greater");
                    return;
                }

                //Create new item to add to DB
                PortfolioRVModel newHolding = new PortfolioRVModel(viewName.getText().toString(),
                        Double.valueOf(editHolding.getText().toString()));

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mHolding.setDollarTotal(0);
                        docRef.update("list", FieldValue.arrayRemove(mHolding))
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

                //Write to portfolio
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        docRef.update("list", FieldValue.arrayUnion(newHolding))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Intent intent = new Intent(PortfolioEditItem.this, Portfolio.class);
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
