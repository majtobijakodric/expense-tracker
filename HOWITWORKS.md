# ExpenseTracker

ExpenseTracker is a simple Android app for recording personal income and expenses on the device. It helps users track what they earn, what they spend, and the current balance without needing an online account or external service.

## Purpose

The app is meant for anyone who wants a lightweight way to monitor day-to-day money flow. It is useful because all data is saved locally on the phone, the main screen shows a quick financial overview, and the full history is only one tap away.

## Main Features

- Add a transaction as either an expense or income.
- Enter the amount and a short description for each transaction.
- Choose a predefined category for expense entries such as Food, Travel, School, Subscriptions, Shopping, or Other.
- View total income, total expenses, and the current balance on the home screen.
- See up to five of the most recent transactions on the home screen.
- Open a separate screen with the full transaction list.
- Clear all saved transactions from the full history screen after confirmation.

## How the Application Works

1. The user opens the main screen and selects whether the entry is an expense or income.
2. The user enters an amount and a description. If the entry is an expense, a category is selected from a dropdown.
3. When the user taps Save Transaction, the app validates the input and stores the record in a local SQLite database.
4. The home screen refreshes its summary cards and the five-item recent list from the database.
5. Tapping See all opens the full transaction history screen, where every saved entry is shown.
6. On the history screen, the user can clear all transactions, which triggers a confirmation dialog before deleting the data.

## Project Structure

- `app/src/main/java/com/example/expensetracker/MainActivity.java` - main screen. Handles input, validation, saving transactions, and refreshing the summary.
- `app/src/main/java/com/example/expensetracker/AllTransactionsActivity.java` - full history screen. Loads all saved transactions and provides the clear-all action.
- `app/src/main/java/com/example/expensetracker/DatabaseHelpers.java` - SQLite database helper. Creates the table, inserts transactions, calculates totals, and deletes all rows.
- `app/src/main/java/com/example/expensetracker/TransactionAdapter.java` - adapts transaction strings into list rows with icons, colors, and formatted text.
- `app/src/main/res/layout/activity_main.xml` - layout for the home screen with the form, summary cards, and recent transactions list.
- `app/src/main/res/layout/activity_all_transactions.xml` - layout for the history screen with the list and clear button.
- `app/src/main/res/layout/transaction_item.xml` - row layout used by the transaction list.
- `app/src/main/AndroidManifest.xml` - declares the app entry point and the second activity.
- `app/src/main/res/values/strings.xml`, `colors.xml`, `themes.xml` - shared labels, colors, and the Material theme.

## Connection Between UI, Logic, and Data

The UI is built in the XML layout files and displayed by the two activities. `MainActivity` connects the form controls, summary text, and recent transaction list to the underlying logic. `AllTransactionsActivity` does the same for the full history view.

`DatabaseHelpers` is the data layer. It stores everything in a local SQLite database file named `ExpenseTracker.db` in a single `transactions` table. Each record contains an id, amount, type, category, description, and creation time.

`TransactionAdapter` sits between the raw data and the list UI. It takes the transaction text returned from the database helper and turns it into a styled list row with the correct icon and color.

In short: the user interacts with the screen, the activity validates and passes the data to the database helper, and the adapter formats stored data for display.

## Technologies Used

- Java 11 - application logic.
- Android SDK - activities, views, intents, and lifecycle handling.
- AndroidX AppCompat - backward-compatible activity support.
- Material Components - text fields, buttons, cards, and radio buttons.
- ConstraintLayout and LinearLayout - screen layout structure.
- SQLiteOpenHelper - local persistent storage.
- ListView and ArrayAdapter - displaying transaction lists.
- Edge-to-edge APIs - drawing content behind system bars.

## Running the Project

1. Open the project in Android Studio.
2. Let Gradle sync finish.
3. Run the `app` module on an Android device or emulator.
4. The project targets Android 13+ because the app module uses `minSdk 33`.

## Possible Improvements

- Show the stored creation date in the transaction history, since the database already saves it.
- Add editing or deleting for individual transactions instead of only clearing everything.
- Add search or filtering by type, category, or date.
- Allow custom categories instead of a fixed expense list.
- Store amounts and display formatting with locale-aware currency handling.