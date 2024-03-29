package com.bitcoin.juwan.butterknife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bitcoin.juwan.annotations.BindView;
import com.bitcoin.juwan.api3.MyBufferknife;
import com.bitcoin.juwan.butterknife.test.SecondActivity;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.first_view)
    TextView textView;

    @BindView(R.id.second_view)
    TextView secondView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyBufferknife.init(this);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
    }
}
