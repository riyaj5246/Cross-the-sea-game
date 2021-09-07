package com.example.exploring_ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class end_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_game);
        String score = "Score: " + getIntent().getStringExtra("Score");
        TextView scoreText = (TextView) findViewById(R.id.score_view);
        scoreText.setText(score);

        Button rerunGame = (Button) findViewById(R.id.replay_button);
        rerunGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println(score);
                Intent myIntent = new Intent(end_activity.this, MainActivity.class);
                end_activity.this.startActivity(myIntent);
            }
        });

        Button mainPage = (Button) findViewById(R.id.mainPage_button);
        mainPage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent myIntent = new Intent(end_activity.this, intro_activity.class);
                end_activity.this.startActivity(myIntent);
            }
        });

    }


}