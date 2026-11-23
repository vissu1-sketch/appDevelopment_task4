package com.example.textprint;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText etNum1, etNum2;
    private TextView tvResult, tvApiResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNum1 = findViewById(R.id.etNum1);
        etNum2 = findViewById(R.id.etNum2);
        tvResult = findViewById(R.id.tvResult);
        tvApiResponse = findViewById(R.id.tvApiResponse);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnSub = findViewById(R.id.btnSub);
        Button btnMul = findViewById(R.id.btnMul);
        Button btnDiv = findViewById(R.id.btnDiv);
        Button btnHistory = findViewById(R.id.btnHistory);
        Button btnGetApi = findViewById(R.id.btnGetApi);
        Button btnPostApi = findViewById(R.id.btnPostApi);

        // Calculator Operations
        btnAdd.setOnClickListener(v -> calculate("+"));
        btnSub.setOnClickListener(v -> calculate("-"));
        btnMul.setOnClickListener(v -> calculate("*"));
        btnDiv.setOnClickListener(v -> calculate("/"));

        // History Dialog
        btnHistory.setOnClickListener(v -> showHistoryDialog());

        // REST API Calls (Task 3 + Task 4)
        btnGetApi.setOnClickListener(v -> fetchSampleData());
        btnPostApi.setOnClickListener(v -> postCalculationToApi());
    }

    private void calculate(String op) {
        String s1 = etNum1.getText().toString().trim();
        String s2 = etNum2.getText().toString().trim();

        if (s1.isEmpty() || s2.isEmpty()) {
            Toast.makeText(this, "Enter both numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        double n1 = Double.parseDouble(s1);
        double n2 = Double.parseDouble(s2);
        double res = 0;

        switch (op) {
            case "+":
                res = n1 + n2;
                break;
            case "-":
                res = n1 - n2;
                break;
            case "*":
                res = n1 * n2;
                break;
            case "/":
                if (n2 == 0) {
                    Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show();
                    return;
                }
                res = n1 / n2;
                break;
        }

        String record = n1 + " " + op + " " + n2 + " = " + res;
        tvResult.setText("Result: " + res);
        saveHistory(record);
    }

    // Save calculation to SharedPreferences
    private void saveHistory(String entry) {
        SharedPreferences prefs = getSharedPreferences("CalcPrefs", MODE_PRIVATE);
        String existing = prefs.getString("history", "");
        prefs.edit().putString("history", entry + "\n" + existing).apply();
    }

    // Show History Popup Dialog
    private void showHistoryDialog() {
        SharedPreferences prefs = getSharedPreferences("CalcPrefs", MODE_PRIVATE);
        String history = prefs.getString("history", "No calculations yet.");

        new AlertDialog.Builder(this)
                .setTitle("Calculation History")
                .setMessage(history)
                .setPositiveButton("Close", null)
                .setNegativeButton("Clear", (dialog, which) -> {
                    prefs.edit().remove("history").apply();
                    Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // HTTP GET Request
    private void fetchSampleData() {
        tvApiResponse.setText("Fetching data...");
        new Thread(() -> {
            try {
                URL url = new URL("https://jsonplaceholder.typicode.com/posts/1");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(sb.toString());
                String title = json.getString("title");

                runOnUiThread(() -> tvApiResponse.setText("GET Success: " + title));
            } catch (Exception e) {
                runOnUiThread(() -> tvApiResponse.setText("GET Error: " + e.getMessage()));
            }
        }).start();
    }

    // HTTP POST Request
    private void postCalculationToApi() {
        String currentResult = tvResult.getText().toString();
        tvApiResponse.setText("Syncing result...");

        new Thread(() -> {
            try {
                URL url = new URL("https://jsonplaceholder.typicode.com/posts");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("title", "Calculator Sync");
                payload.put("body", currentResult);
                payload.put("userId", 1);

                OutputStream os = conn.getOutputStream();
                os.write(payload.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> tvApiResponse.setText("POST Status: " + responseCode + " (" + currentResult + ")"));
            } catch (Exception e) {
                runOnUiThread(() -> tvApiResponse.setText("POST Error: " + e.getMessage()));
            }
        }).start();
    }
}