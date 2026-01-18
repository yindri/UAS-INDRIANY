package com.example.perpustakaan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvBooks;
    private BookAdapter adapter;
    private List<Object> displayList;
    private EditText etSearch;
    private Button btnSearch;
    private TextView tvLabelInput, tvLabelList;

    // Localization Variables
    private boolean isIndo = true;
    private String lang;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load Language
        SharedPreferences prefs = getSharedPreferences("AppSession", MODE_PRIVATE);
        lang = prefs.getString("language", "id");
        isIndo = "id".equalsIgnoreCase(lang);

        // Init Views
        tvLabelInput = findViewById(R.id.tv_label_input);
        tvLabelList = findViewById(R.id.tv_label_list);
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        rvBooks = findViewById(R.id.rv_books);

        // Apply Localization to Static UI
        // Global Layout Listener to ensure views are ready? No, strict null checks.
        if (isIndo) {
            if (tvLabelInput != null)
                tvLabelInput.setText("NAMA BUKU");
            if (tvLabelList != null)
                tvLabelList.setText("DAFTAR NAMA BUKU");
            if (etSearch != null)
                etSearch.setHint("Cari...");
            if (btnSearch != null)
                btnSearch.setText("Cari");
        } else {
            if (tvLabelInput != null)
                tvLabelInput.setText("BOOK TITLE");
            if (tvLabelList != null)
                tvLabelList.setText("BOOK LIST");
            if (etSearch != null)
                etSearch.setHint("Search...");
            if (btnSearch != null)
                btnSearch.setText("Search");
        }

        rvBooks.setLayoutManager(new LinearLayoutManager(this));

        displayList = new ArrayList<>();
        populateData();

        adapter = new BookAdapter(displayList);
        rvBooks.setAdapter(adapter);

        // Search Logic
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etSearch.getText().toString().trim();
                adapter.filter(query);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_user) {
            showUserDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUserDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_user_menu, null);
        builder.setView(view);

        final android.app.AlertDialog dialog = builder.create();

        TextView btnIndo = view.findViewById(R.id.btn_lang_id);
        TextView btnEng = view.findViewById(R.id.btn_lang_en);
        TextView btnLogout = view.findViewById(R.id.btn_logout);

        // Highlight active lang
        if (isIndo) {
            btnIndo.setBackgroundColor(getResources().getColor(R.color.purple_500));
            btnIndo.setTextColor(getResources().getColor(R.color.white));

            btnEng.setBackgroundColor(getResources().getColor(R.color.white));
            btnEng.setTextColor(getResources().getColor(R.color.black));
        } else {
            btnEng.setBackgroundColor(getResources().getColor(R.color.purple_500));
            btnEng.setTextColor(getResources().getColor(R.color.white));

            btnIndo.setBackgroundColor(getResources().getColor(R.color.white));
            btnIndo.setTextColor(getResources().getColor(R.color.black));
        }

        // Listeners
        btnIndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage("id");
                dialog.dismiss();
            }
        });

        btnEng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage("en");
                dialog.dismiss();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                logout();
            }
        });

        dialog.show();
    }

    private void changeLanguage(String newLang) {
        if (!lang.equals(newLang)) {
            SharedPreferences prefs = getSharedPreferences("AppSession", MODE_PRIVATE);
            prefs.edit().putString("language", newLang).commit();
            recreate(); // Restart activity to apply changes
        }
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("AppSession", MODE_PRIVATE);
        prefs.edit().clear().apply(); // Clear session

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        String message = isIndo ? "Tekan sekali lagi untuk keluar" : "Press back again to exit";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void populateData() {
        // Strings based on Lang
        String cat1 = isIndo ? "Dasar-dasar Pemrograman & Algoritma" : "Programming Basics & Algorithms";
        String cat2 = isIndo ? "Rekayasa Perangkat Lunak" : "Software Engineering";

        // Raw Data - We can technically translate titles too but titles usually stay as
        // is.
        // Let's assume Book Titles are universal (English mostly) but Categories need
        // translation.

        List<Book> allBooks = new ArrayList<>();
        allBooks.add(new Book("Introduction to Algorithms", "Thomas H. Cormen", 2009, cat1));
        allBooks.add(
                new Book("Clean Code: A Handbook of Agile Software Craftsmanship", "Robert C. Martin", 2008, cat1));
        allBooks.add(new Book("The Pragmatic Programmer", "Andrew Hunt & David Thomas", 1999, cat1));
        allBooks.add(new Book("Structure and Interpretation of Computer Programs",
                "Harold Abelson & Gerald Jay Sussman", 1996, cat1));

        allBooks.add(new Book("Software Engineering", "Ian Sommerville", 2015, cat2));
        allBooks.add(new Book("Design Patterns: Elements of Reusable Object-Oriented Software",
                "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", 1994, cat2));
        allBooks.add(new Book("Refactoring: Improving the Design of Existing Code", "Martin Fowler", 1999, cat2));

        // Grouping
        Map<String, List<Book>> grouped = new HashMap<>();
        for (Book b : allBooks) {
            String cat = b.getKategori();
            if (cat == null)
                cat = "Other";
            if (!grouped.containsKey(cat)) {
                grouped.put(cat, new ArrayList<>());
            }
            grouped.get(cat).add(b);
        }

        // Flatten to Display List with Headers
        if (grouped.containsKey(cat1)) {
            displayList.add("1. " + cat1);
            displayList.addAll(grouped.get(cat1));
        }

        if (grouped.containsKey(cat2)) {
            displayList.add("2. " + cat2);
            displayList.addAll(grouped.get(cat2));
        }
    }
}
