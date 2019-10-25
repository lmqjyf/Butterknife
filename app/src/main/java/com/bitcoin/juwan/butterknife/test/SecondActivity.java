package com.bitcoin.juwan.butterknife.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bitcoin.juwan.annotations.BindView;
import com.bitcoin.juwan.api3.MyBufferknife;
import com.bitcoin.juwan.butterknife.R;

import org.w3c.dom.Text;

public class SecondActivity extends AppCompatActivity {

    @BindView(R.id.first_view)
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        MyBufferknife.init(this);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("-----", "-----");
            }
        });
    }
}
