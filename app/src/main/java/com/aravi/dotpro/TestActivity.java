package com.aravi.dotpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import soup.neumorphism.NeumorphButton;

public class TestActivity extends AppCompatActivity {

    private boolean isSafeDotTurnedOn = false;

    NeumorphButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        button = findViewById(R.id.safeDotModeButton);

        ScrollView scrollView = findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSafeDotTurnedOn){
                    button.setBackgroundColor(getResources().getColor(R.color.background));
                    isSafeDotTurnedOn = false;
                }
                else {
                    button.setBackgroundColor(getResources().getColor(R.color.blue_100));
                    isSafeDotTurnedOn = true;
                }
            }
        });
    }

}