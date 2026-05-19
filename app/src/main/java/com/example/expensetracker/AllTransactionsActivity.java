package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
    Button clearAllButton;

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
        clearAllButton = findViewById(R.id.clearAllButton);

        backButton.setOnClickListener(v -> finish());

        clearAllButton.setOnClickListener(v -> showClearConfirmation());

        refreshList();
    }

    private void refreshList() {
        ArrayList<String> transactions = databaseHelpers.getAllTransactions();
        TransactionAdapter adapter = new TransactionAdapter(this, transactions);
        allTransactionsListView.setAdapter(adapter);
    }

    private void showClearConfirmation() {
        // alert box
        new AlertDialog.Builder(this)
                .setTitle("Clear All Transactions")
                .setMessage("Are you sure you want to delete all transactions? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelpers.deleteAllTransactions();
                    refreshList();
                    Toast.makeText(this, "All transactions cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
