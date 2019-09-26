package com.example.android.githubreposearch;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {


    private MainActivityViewModel mainActivityViewModel;
    private EditText searchInput;
    private Button searchButton;
    private RecyclerView recyclerView;
    LiveData<List<Repository>> repositories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchInput = findViewById(R.id.editText);
        searchButton = findViewById(R.id.button);
        recyclerView = findViewById(R.id.recycler_view);
        final MainActivityRecyclerViewAdapter adapter = new MainActivityRecyclerViewAdapter(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        repositories = mainActivityViewModel.getResult();
        repositories.observe(this, new Observer<List<Repository>>() {
            @Override
            public void onChanged(List<Repository> repositories) {
                adapter.setNotes(repositories);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityViewModel.generateResult(searchInput.getText().toString());
            }
        });
    }
}
