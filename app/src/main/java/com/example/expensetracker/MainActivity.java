package com.example.expensetracker;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNumber;
    private EditText whatDidYouSpendOnInput;
    private TextView totalTextView;
    private ArrayList<String> expenseList;
    private ArrayAdapter<String> adapter;
    // this is our database class
    private DatabaseStuff db;

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

        // connect to the database
        db = new DatabaseStuff(this);

        // get all the GUI items
        editTextNumber = findViewById(R.id.howMuchDidYouSpend);
        whatDidYouSpendOnInput = findViewById(R.id.whatDidYouSpendOnInput);
        totalTextView = findViewById(R.id.totalTextView);
        ListView listView = findViewById(R.id.listView);
        Button button = findViewById(R.id.button);
        Button clearButton = findViewById(R.id.clearButton);

        // list items are stored here
        expenseList = new ArrayList<>();

        // this is the connection between the ArrayList and the GUI list
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenseList);
        // set the connection
        listView.setAdapter(adapter);

        // load data from database when app starts
        refreshUI();

        button.setOnClickListener(v -> addExpense());

        // this button erases everything so we can debug
        clearButton.setOnClickListener(v -> {
            db.deleteAllExpenses();
            refreshUI();
            Toast.makeText(this, "All data erased!", Toast.LENGTH_SHORT).show();
        });
    }

    // this method updates the list and the total price from the database
    private void refreshUI() {
        expenseList.clear();
        expenseList.addAll(db.getAllExpenses());
        adapter.notifyDataSetChanged();

        double totalExpense = db.getTotalExpenses();
        totalTextView.setText(String.format("%s€", totalExpense));
    }

    private void addExpense() {
        String amountInput = editTextNumber.getText().toString();
        String itemInput = whatDidYouSpendOnInput.getText().toString();

        if (!amountInput.isEmpty() && !itemInput.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountInput);

                // Add to database
                db.addExpense(itemInput, amount);

                // Update the UI
                refreshUI();

                // Clear input fields
                editTextNumber.setText("");
                whatDidYouSpendOnInput.setText("");
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter both amount and description", Toast.LENGTH_SHORT).show();
        }

    }
}
