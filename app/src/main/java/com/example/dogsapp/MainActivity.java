package com.example.dogsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private ImageView imageView;
    private Button button;
    private ProgressBar progressBar;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.loadDogImage();
        viewModel.getIsError()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isError) {
                        if (isError) Toast.makeText(
                                MainActivity.this,
                                "Ошибка при загрузке",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        viewModel.getIsLoading()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean loading) {
                        if (loading) progressBar.setVisibility(View.VISIBLE);
                        else progressBar.setVisibility(View.GONE);
                    }
                });
        viewModel.getDogImage().observe(this, new Observer<DogImage>() {
            @Override
            public void onChanged(DogImage dogImage) {
                Glide.with(MainActivity.this)
                        .load(dogImage.getMessage())
                        .into(imageView);
            }
        });

        initViews();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.loadDogImage();
            }
        });
    }


    private void initViews() {
        imageView = findViewById(R.id.ivDog);
        progressBar = findViewById(R.id.pbLoading);
        button = findViewById(R.id.button);
    }
}