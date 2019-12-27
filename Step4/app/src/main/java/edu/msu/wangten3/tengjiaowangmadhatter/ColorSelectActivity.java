package edu.msu.wangten3.tengjiaowangmadhatter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ColorSelectActivity extends AppCompatActivity {
    public static final String COLOR = " edu.msu.wangten3.tengjiaowangmadhatter.COLOR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_select);
    }



    public void selectColor(int color) {
        Intent result = new Intent();
        result.putExtra(COLOR, color);
        setResult(Activity.RESULT_OK, result);
        finish();

    }
}
