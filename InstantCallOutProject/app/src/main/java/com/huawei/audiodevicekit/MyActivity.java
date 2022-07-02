package com.huawei.audiodevicekit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.audiodevicekit.bluetoothsample.view.SampleBtActivity;

public class MyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my);
        EditText editup=(EditText) findViewById(R.id.edit1);
        EditText editdown=(EditText) findViewById(R.id.edit2);
        EditText editleft=(EditText) findViewById(R.id.edit3);
        EditText editright=(EditText) findViewById(R.id.edit4);
        Button buttonback=(Button) findViewById(R.id.buttonback);
        buttonback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sup=editup.getText().toString();
                String sdown=editdown.getText().toString();
                String sleft=editleft.getText().toString();
                String sright=editright.getText().toString();
                if(sup!=null&&sup.length()!=0) {
                    SampleBtActivity.strup=sup;
                }
                if(sdown!=null&&sdown.length()!=0) {
                    SampleBtActivity.strdown=sdown;
                }
                if(sleft!=null&&sleft.length()!=0) {
                    SampleBtActivity.strleft=sleft;
                }
                if(sright!=null&&sright.length()!=0) {
                    SampleBtActivity.strright=sright;
                }
                finish();
            }
        });
    }

}