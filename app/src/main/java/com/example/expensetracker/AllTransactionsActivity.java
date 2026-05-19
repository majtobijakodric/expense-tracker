package com.example.expensetracker;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class AllTransactionsActivity extends AppCompatActivity {

    DatabaseHelpers databaseHelpers;
    ListView allTransactionsListView;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_transactions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.allTransactionsMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelpers = new DatabaseHelpers(this);
        allTransactionsListView = findViewById(R.id.allTransactionsListView);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        ArrayList<String> transactions = databaseHelpers.getAllTransactions();
        TransactionAdapter adapter = new TransactionAdapter(this, transactions);
        allTransactionsListView.setAdapter(adapter);
    }
}
