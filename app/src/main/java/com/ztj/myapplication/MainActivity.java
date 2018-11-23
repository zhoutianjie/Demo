package com.ztj.myapplication;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button = findViewById(R.id.btn);
        final SelfView selfView = findViewById(R.id.self);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfView.reset();
            }
        });


    }

}
