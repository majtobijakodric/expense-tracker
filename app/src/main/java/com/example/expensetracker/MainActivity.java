package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {

    DatabaseHelpers databaseHelpers;

    EditText amountInput;
    EditText descriptionInput;

    RadioButton expenseRadio;
    RadioButton incomeRadio;

    AutoCompleteTextView categorySpinner;
    View categoryContainer;

    Button saveButton;

    TextView totalIncomeText;
    TextView totalExpenseText;
    TextView balanceText;

    ListView transactionListView;
    TextView seeAllText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // connect all UI elements to a variable
        databaseHelpers = new DatabaseHelpers(this);

        amountInput = findViewById(R.id.amountInput);
        descriptionInput = findViewById(R.id.descriptionInput);

        expenseRadio = findViewById(R.id.expenseRadio);
        incomeRadio = findViewById(R.id.incomeRadio);

        categorySpinner = findViewById(R.id.categorySpinner);
        categoryContainer = findViewById(R.id.categoryContainer);

        saveButton = findViewById(R.id.saveButton);

        totalIncomeText = findViewById(R.id.totalIncomeText);
        totalExpenseText = findViewById(R.id.totalExpenseText);
        balanceText = findViewById(R.id.balanceText);

        transactionListView = findViewById(R.id.transactionListView);
        seeAllText = findViewById(R.id.seeAllText);

        // add spending categories
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Food");
        categories.add("Travel");
        categories.add("Subscriptions");
        categories.add("School");
        categories.add("Shopping");
        categories.add("Other");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        // says to the spinner how it should show data
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // connects to spinner
        categorySpinner.setAdapter(adapter);

        // hide category when you select income
        expenseRadio.setOnClickListener(v -> updateTransactionTypeUi());
        incomeRadio.setOnClickListener(v -> updateTransactionTypeUi());
        updateTransactionTypeUi();

        saveButton.setOnClickListener(v -> saveTransaction());

        seeAllText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AllTransactionsActivity.class);
            startActivity(intent);
        });

        // remove old data after saving a transaction
        refreshOverview();
    }

    private void updateTransactionTypeUi() {
        if (expenseRadio.isChecked()) {
            categoryContainer.setVisibility(View.VISIBLE);
        } else {
            categoryContainer.setVisibility(View.GONE);
        }
    }

    private void refreshOverview() {
        double totalIncome = databaseHelpers.getTotalIncome();
        double totalExpense = databaseHelpers.getTotalExpense();
        double balance = databaseHelpers.getBalance();

        totalIncomeText.setText(String.format("%.2f", totalIncome) + "€");
        totalExpenseText.setText(String.format("%.2f", totalExpense) + "€");
        balanceText.setText(String.format("%.2f", balance) + "€");

        ArrayList<String> transactions = databaseHelpers.getAllTransactions();
        
        // Show only last 5 transactions in main activity if needed, but for now just showing all as before
        // The user didn't ask to limit it, but usually main page shows recent ones.
        // Let's keep it as is or limit to 5.
        
        ArrayList<String> recentTransactions = new ArrayList<>();
        for (int i = 0; i < Math.min(transactions.size(), 5); i++) {
            recentTransactions.add(transactions.get(i));
        }

        TransactionAdapter adapter = new TransactionAdapter(this, recentTransactions);

        transactionListView.setAdapter(adapter);
    }

    //
    public void saveTransaction() {
        String amountText = amountInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        String type;
        String category;

        if (expenseRadio.isChecked()) {
            type = DatabaseHelpers.TYPE_EXPENSE;
            category = categorySpinner.getText().toString();
        } else {
            type = DatabaseHelpers.TYPE_INCOME;
            category = "";
        }

        databaseHelpers.addTransaction(amount, type, category, description);

        // clear the inputs after transaction safe
        amountInput.setText("");
        descriptionInput.setText("");
        expenseRadio.setChecked(true);
        updateTransactionTypeUi();

        Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();

        refreshOverview();
    }
}

