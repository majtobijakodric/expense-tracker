package com.example.expensetracker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TransactionAdapter extends ArrayAdapter<String> {

    public TransactionAdapter(Context context, ArrayList<String> transactions) {
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

        switch (category) {
            case "Food":
                return R.drawable.ic_food;
            case "School":
                return R.drawable.ic_school;
            case "Travel":
                return R.drawable.ic_travel;
            case "Subscriptions":
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
