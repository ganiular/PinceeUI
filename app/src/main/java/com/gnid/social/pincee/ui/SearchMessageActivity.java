package com.gnid.social.pincee.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.gnid.social.pinceeui.R;

// Implementation guide: developer.android.com/develop/ui/views/search/search-dialog
public class SearchMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchMessage(query);
        }
    }

    private void searchMessage(String query) {
        TextView t = findViewById(R.id.text_view);
        t.setText(query);
        Toast.makeText(this, "Search for "+query, Toast.LENGTH_SHORT).show();
    }
}