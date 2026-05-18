package com.example.expensetracker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

        ArrayAdapter<String> adapter = new TransactionAdapter(this, transactions);

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

    private static class TransactionAdapter extends ArrayAdapter<String> {

        TransactionAdapter(Context context, ArrayList<String> transactions) {
            super(context, R.layout.transaction_item, transactions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.transaction_item, parent, false);
            }

            String transaction = getItem(position);
            String title = "";
            String amount = "";

            if (transaction != null) {
                String[] parts = transaction.split("\\n", 2);
                title = parts[0];
                if (parts.length > 1) {
                    amount = parts[1];
                }
            }

            String category = getCategoryFromTitle(title);
            boolean isIncome = amount.startsWith("+") || category.equals("Income");

            ImageView icon = view.findViewById(R.id.transactionIcon);
            TextView titleText = view.findViewById(R.id.transactionTitle);
            TextView subtitleText = view.findViewById(R.id.transactionSubtitle);
            TextView amountText = view.findViewById(R.id.transactionAmount);

            titleText.setText(title);
            subtitleText.setText(category);
            amountText.setText(amount);
            amountText.setTextColor(isIncome ? Color.parseColor("#2E7D32") : Color.parseColor("#1A1A1A"));

            int iconColor = getIconColor(category, isIncome);
            icon.setImageResource(getIconResource(category, isIncome));
            icon.setImageTintList(ColorStateList.valueOf(iconColor));
            icon.setBackground(createCircle(getCircleColor(category, isIncome)));

            return view;
        }

        private static String getCategoryFromTitle(String title) {
            int divider = title.indexOf(":");
            if (divider > 0) {
                return title.substring(0, divider);
            }
            return title;
        }

        private static int getIconResource(String category, boolean isIncome) {
            if (isIncome) {
                return R.drawable.ic_trending_up;
            }
            if (category.equals("Food")) {
                return R.drawable.ic_food;
            }
            if (category.equals("School")) {
                return R.drawable.ic_school;
            }
            if (category.equals("Travel")) {
                return R.drawable.ic_travel;
            }
            if (category.equals("Subscriptions")) {
                return R.drawable.ic_subscriptions;
            }
            return R.drawable.ic_other;
        }

        private static int getCircleColor(String category, boolean isIncome) {
            if (isIncome || category.equals("Food")) {
                return Color.parseColor("#E8F5E9");
            }
            if (category.equals("School")) {
                return Color.parseColor("#EDE9FF");
            }
            if (category.equals("Travel")) {
                return Color.parseColor("#E3F2FD");
            }
            return Color.parseColor("#FFF3E0");
        }

        private static int getIconColor(String category, boolean isIncome) {
            if (isIncome || category.equals("Food")) {
                return Color.parseColor("#2E7D32");
            }
            if (category.equals("School")) {
                return Color.parseColor("#4B2FD4");
            }
            if (category.equals("Travel")) {
                return Color.parseColor("#1565C0");
            }
            return Color.parseColor("#E65100");
        }

        private static GradientDrawable createCircle(int color) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(color);
            return drawable;
        }
    }

}
