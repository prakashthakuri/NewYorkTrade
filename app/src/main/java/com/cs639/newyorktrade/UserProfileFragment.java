package com.cs639.newyorktrade;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserProfileFragment extends Fragment {

    private ImageButton mSettingsDropDown;
    private TextView mName, mEmail, mGradYear, mPhoneNumber, mReputation, mItemNumber, mSalesNumber;
    private CircularImageView profileImage;
    //private ItemFragment itemFragment;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;

    public UserProfileFragment() {
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        //Initialize UI elements
        mSettingsDropDown = view.findViewById(R.id.edit);
        mName = view.findViewById(R.id.user_name);
        mEmail = view.findViewById(R.id.user_email);
        mGradYear = view.findViewById(R.id.user_grad_year);
        mPhoneNumber = view.findViewById(R.id.user_phone_number);
        profileImage = view.findViewById(R.id.imageview_account_profile);
        mReputation = view.findViewById(R.id.reputation);
        mItemNumber = view.findViewById(R.id.item_count);
        mSalesNumber = view.findViewById(R.id.sales_count);
        //Initialize firebase vars
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //Set email and name in profile
        mEmail.setText(user.getEmail());
        Linkify.addLinks(mEmail, Linkify.ALL);
        mName.setText(user.getDisplayName());
        //Set path to write user profile image
        mStorageRef = mStorageRef.child("Images").child("profile pictures");
        //Assign user image to image view
        mStorageRef.child(user.getUid()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profileImage));
        //Set user profile grad year and phone #

        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc882");
                emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{String.valueOf(mEmail)});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                emailIntent.putExtra(Intent.EXTRA_TEXT   , "body of email");


                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));

            }
        });

        mPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse(String.valueOf(mPhoneNumber)));

                startActivity(phoneIntent);
            }
        });


        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                mGradYear.setText(String.format(getString(R.string.grad_year), document.get("year")));
                mPhoneNumber.setText(document.getString("phone"));
                mReputation.setText(document.get("reputation").toString());
                mSalesNumber.setText(document.get("sales").toString());
                mItemNumber.setText(document.get("items").toString());

                Linkify.addLinks(mPhoneNumber, Linkify.ALL);
            }
        });
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addButtonClickListeners();
    }

    private void addButtonClickListeners() {
        mSettingsDropDown.setOnClickListener(v ->
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new EditUserProfileFragment()).commit());
    }
}
