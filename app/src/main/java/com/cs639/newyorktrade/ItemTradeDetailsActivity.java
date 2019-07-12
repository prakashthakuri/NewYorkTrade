package com.cs639.newyorktrade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ItemTradeDetailsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_trade_details);
        DisplaySellerData();
        setButtonClickListeners();
    }
    private void DisplaySellerData()
    {
        Intent startingIntent = getIntent();
        TextView ItemNameTextView = findViewById(R.id.seller_name);
        TextView itemTypeTextView = findViewById(R.id.item_type_name);
        TextView estimatedValueTextView = findViewById(R.id.estimated_value);
        itemTypeTextView.setText(startingIntent.getStringExtra("category"));
        estimatedValueTextView.setText(startingIntent.getStringExtra("cost"));
        ItemNameTextView.setText(startingIntent.getStringExtra("userName"));

    }
    private void setButtonClickListeners()
    {
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
        Button proposeTradeButton = findViewById(R.id.propose_trade_button);
        proposeTradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent TradeIntent = new Intent(ItemTradeDetailsActivity.this, PurposeTrade.class);
                startActivity(TradeIntent);

            }
        });




    }


}

