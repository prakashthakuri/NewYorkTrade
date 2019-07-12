package com.cs639.newyorktrade;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class PurposeTrade extends AppCompatActivity {


    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purpose_trade);

        new ItemTradeDetailsActivity();
        new UserProfileFragment();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        Button mMessage = findViewById(R.id.message);
        Button viewProfile = findViewById(R.id.view_profile);
        Button getRating = findViewById(R.id.review);
        Button email = findViewById(R.id.user_email);
        Button callSeller = findViewById(R.id.user_phone_number);
        final RatingBar ratingBar = findViewById(R.id.rating);
        TextView Rating;
String userEmail = user.getEmail();
String userPhone = user.getPhoneNumber();


        getRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rating = " You gave a review of" + ratingBar.getRating() + "to the user "+ user.getDisplayName();
                Toast.makeText(PurposeTrade.this, rating, Toast.LENGTH_LONG).show();

                FirebaseFirestore.getInstance().collection("reputation").document();
                Map<String, Object> userItemsUpdateAgain = new HashMap<>();
                userItemsUpdateAgain.put("reputation", FieldValue.increment(+ratingBar.getRating()));
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(userItemsUpdateAgain);
            }
        });

        viewProfile.setOnClickListener(view ->
        {
            new UserProfileFragment();
        });

        mMessage.setOnClickListener(view -> {
            Toast.makeText(this, " Messaging Feature is Coming Soon on NewYork Trade App. \n Thank you for your Patience!", Toast.LENGTH_LONG).show();

        });

        email.setOnClickListener(view -> {

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc882");
            emailIntent.putExtra(Intent.EXTRA_EMAIL  , String.valueOf(userEmail));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
            emailIntent.putExtra(Intent.EXTRA_TEXT   , "body of email");


            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

        });
        callSeller.setOnClickListener(view -> {

            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse(String.valueOf(userPhone)));

            startActivity(phoneIntent);

        });



    }





}
