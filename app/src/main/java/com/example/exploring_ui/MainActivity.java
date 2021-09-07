package com.example.exploring_ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    //Screen size
    private int screenWidth;
    private int screenHeight;

    //Images
    private ImageView arrowUp;
    private ImageView arrowDown;
    private ImageView arrowRight;
    private ImageView arrowLeft;
    private ImageView ball;
    private ImageView star;
    private ImageView moveUp;
    private ImageView moveDown;
    private ImageView moveLeft;
    private ImageView moveRight;
    private ImageView portal;
    private ImageView speed;
    private ImageView freeze;

    //Textview
    private TextView lives_text;
    private TextView restart_text;
    private TextView points_text;

    //Position arrows
    private float arrowUpX;
    private float arrowUpY;
    private float arrowDownX;
    private float arrowDownY;
    private float arrowRightX;
    private float arrowRightY;
    private float arrowLeftX;
    private float arrowLeftY;
    ArrayList<ImageView> arrows = new ArrayList<>();

    //game variables
    private int lives = 3;
    private int points = 0;
    private float ballPrevX = 0;
    private float ballPrevY = 0;
    private boolean arrowMovementOn = true;
    private boolean disableBallMovement = false;
    private float starPosX;
    private float starPosY;
    private int ballSpeed = 40;
    private Powerups[] powerupArray = new Powerups[3];
    private boolean freezeArrows = false;


    //Initialize Class
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpImageviews();

        //Get Screen size
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

            //timer
            timer.schedule(new TimerTask(){
                @Override
                public void run() {
                    handler.post(new Runnable(){
                        @Override
                        public void run(){
                            if(isBallOutsideMargin() && arrowMovementOn && !freezeArrows){
                                arrowRight.setVisibility(View.VISIBLE);
                                arrowLeft.setVisibility(View.VISIBLE);
                                arrowUp.setVisibility(View.VISIBLE);
                                arrowDown.setVisibility(View.VISIBLE);

                                changePos();
                            }
                            for(ImageView a: arrows){
                                if(detectCollision(a, ball) && arrowMovementOn && isBallOutsideMargin()){
                                    System.out.println("collision");
                                    disableBallMovement = true;
                                    ball.setX(ballPrevX);
                                    ball.setY(ballPrevY);
                                    updatePowerupTimeLeft();

                                    if(lives > 0){
                                        lives -= 1;
                                        arrowMovementOn = false;
                                        String text = "Lives: " + lives;
                                        lives_text.setText(text);
                                        restart_text.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        Intent myIntent = new Intent(MainActivity.this, end_activity.class);
                                        myIntent.putExtra("Score", points +" ");
                                        MainActivity.this.startActivity(myIntent);
                                        //setContentView(R.layout.end_game);
                                    }
                                }
                            }
                            if(detectCollision(star, ball)){
                                ballPrevX = starPosX;
                                ballPrevY = starPosY;
                                ball.setX(starPosX);
                                ball.setY(starPosY);
                                updatePowerupTimeLeft();

                                if(starPosX == 950){
                                    ball.setImageResource(R.drawable.fish_flipped);
                                }else{
                                    ball.setImageResource(R.drawable.fish);
                                }
                                points += 1000;
                                String text = "Points: " + points;
                                points_text.setText(text);
                                addPowerup();
                                generateStarPosition();
                            }
                        }
                    });
                }
            }, 0 ,20);

        //setting ball movement control
            arrowUp.setX(-80.0f);
            arrowUp.setY(-80.0f);
            arrowDown.setX(-80.0f);
            arrowDown.setY(screenHeight + 80.0f);
            arrowRight.setX(screenWidth + 80.0f);
            arrowRight.setY(-80.0f);
            arrowLeft.setX(-80.0f);
            arrowLeft.setY(-80.0f);
            generateStarPosition();

            //up
            moveUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //System.out.println("up button was clicked");
                    if(ball.getY() > 0 && !disableBallMovement){
                        ball.setY(ball.getY() - ballSpeed);
                        System.out.println("ball y " + ball.getY());
                    }
                }
            });

            //down
            moveDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ball.getY() + ball.getHeight() < screenHeight && !disableBallMovement){
                        ball.setY(ball.getY() + ballSpeed);
                        System.out.println("ball y" + ball.getY());
                    }
                }
            });

            //left
            moveLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ball.getX() > 0 && !disableBallMovement){
                        ball.setX(ball.getX() - ballSpeed);
                        System.out.println("ball x" + ball.getX());
                    }
                }
            });

            //right
            moveRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ball.getX() + ball.getWidth() < screenWidth && !disableBallMovement){
                        ball.setX(ball.getX() + ballSpeed);
                        System.out.println("ball x " + ball.getX());
                    }
                }
            });
            clickPowerup();
    }

    private void setUpImageviews() {
        //red arrow imageviews - call and add to arraylist
        arrowUp = (ImageView) findViewById(R.id.arrowUp);
        arrowDown = (ImageView) findViewById(R.id.arrowDown);
        arrowLeft = (ImageView) findViewById(R.id.arrowLeft);
        arrowRight = (ImageView) findViewById(R.id.arrowRight);
        ball = (ImageView) findViewById(R.id.ball_imgView);
        star = (ImageView) findViewById(R.id.star);

        arrows.add(arrowUp);
        arrows.add(arrowDown);
        arrows.add(arrowLeft);
        arrows.add(arrowRight);

        //textviews
        lives_text = (TextView) findViewById(R.id.lives_text);
        restart_text = (TextView) findViewById(R.id.swipe_to_restart_text);
        points_text = (TextView) findViewById(R.id.points_text);

        //move control arrow
        moveUp = (ImageView)findViewById(R.id.up_button);
        moveDown = (ImageView) findViewById(R.id.down_button);
        moveLeft = (ImageView) findViewById(R.id.left_button);
        moveRight = (ImageView) findViewById(R.id.right_button);

        //powerups
        portal = (ImageView) findViewById(R.id.portal);
        speed = (ImageView) findViewById(R.id.speed);
        freeze = (ImageView) findViewById(R.id.freeze);
    }

    float x1, x2;
    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                if(x1 < x2){
                    disableBallMovement = false;
                    restart_text.setVisibility(View.INVISIBLE);
                    arrowMovementOn = true;
                }
                break;
        }
        return false;
    }

    //move arrows
    public void changePos(){
        //Up
        arrowUpY -= 5;
        if (arrowUp.getY() + arrowUp.getHeight() < 0){
            arrowUpX = (float) Math.floor(Math.random() * (screenWidth - arrowUp.getWidth()));
            arrowUpY = screenHeight + 100.0f;
        }
        arrowUp.setX(arrowUpX);
        arrowUp.setY(arrowUpY);

        //Down
        arrowDownY += 5;
        if(arrowDown.getY() > screenHeight){
            arrowDownX = (float)Math.floor(Math.random() *(screenWidth -arrowDown.getWidth()));
            arrowDownY = 0.0f;
        }
        arrowDown.setX(arrowDownX);
        arrowDown.setY(arrowDownY);

        //Right
        arrowRightX += 5;
        if (arrowRight.getX() > screenWidth){
            arrowRightX = -100.0f;
            arrowRightY = (float)Math.floor(Math.random() * (screenHeight - arrowRight.getHeight()));
        }
        arrowRight.setX(arrowRightX);
        arrowRight.setY(arrowRightY);

        //Left
        arrowLeftX -= 5;
        if (arrowLeft.getX() + arrowLeft.getWidth() < 0){
            arrowLeftX = screenWidth + 100.0f;
            arrowLeftY = (float)Math.floor(Math.random()*(screenHeight - arrowLeft.getHeight()));
        }
        arrowLeft.setX(arrowLeftX);
        arrowLeft.setY(arrowLeftY);
    }

    //detects collisions between object (arrow, star) and ball
    public boolean detectCollision(ImageView object, ImageView ball)
    {
        Rect BallRect = new Rect();
        ball.getHitRect(BallRect);
        Rect objectRect = new Rect();
        object.getHitRect(objectRect);
        return BallRect.intersect(objectRect);
    }

    //true if ball is outside of margin
    private boolean isBallOutsideMargin(){
       // System.out.println(ball.getX() + ", " +ball.getY());
        if(ball.getX() < 75 || (ball.getX() + ball.getWidth()) > screenWidth - 75) {
            //|| ball.getY() < 25 || (ball.getY() + ball.getHeight()) > screenHeight - 50
                return false;
        }
        return true;
    }

    //random star position
    private void generateStarPosition(){
        System.out.println("got here2");

        //x position
        if(ballPrevX <=100){
            System.out.println("picked right");
            starPosX = 950;
        }
        else{
            starPosX = 10;
        }

        //y position
        starPosY = (float) Math.random()* (1460);

        star.setX(starPosX);
        star.setY(starPosY);
    }

    //check for and add powerups
    private void addPowerup(){
        int random = (int) (Math.random() * 6);
        if (random >= 3){
            System.out.println("no powerup");
        }
        else if(random == 0){
            System.out.println("freeze");
            if(powerupArray[0] == null || !powerupArray[0].getEnabled()){
                powerupArray[0] = new Powerups(0);
                freeze.setAlpha(1.0F);
            }
        }
        else if(random == 1){
            System.out.println("speed");
            if(powerupArray[1] == null || !powerupArray[1].getEnabled()){
                powerupArray[1] = new Powerups(1);
                speed.setAlpha(1.0F);
            }
        }
        else{
            System.out.println("portal");
            if(powerupArray[2] == null || !powerupArray[2].getEnabled()){
                powerupArray[2] = new Powerups(2);
                portal.setAlpha(1.0F);
            }
        }
    }

    //click on powerups
    private void clickPowerup(){
        freeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(powerupArray[0] != null){
                    if(powerupArray[0].getEnabled()){
                        freezeArrows = true;
                        freeze.setAlpha(0.5F);
                    }
                }
            }
        });

        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(powerupArray[1] != null){
                    if(powerupArray[1].getEnabled()){
                        ballSpeed = 60;
                        speed.setAlpha(0.5F);
                    }
                }
            }
        });

        portal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(powerupArray[2] != null){
                    if(powerupArray[2].getEnabled()){
                        ball.setX(star.getX());
                        ball.setY(star.getY());
                        powerupArray[2].disable();
                        portal.setAlpha(0.5F);
                    }
                }
            }
        });
    }

    //updates + checks how much time left for powerup
    private void updatePowerupTimeLeft(){
        for(Powerups p: powerupArray){
            if(p != null){
                if(p.getEnabled()){
                    p.addToTurnsEnabled();
                    if(p.getTurnsEnabled() > 3){
                        p.disable();
                        int x = p.getType();
                        switch(x){
                            case 0:
                                freeze.setAlpha(0.5F);
                                freezeArrows = false;
                                break;
                            case 1:
                                speed.setAlpha(0.5F);
                                ballSpeed = 40;
                                break;
                            case 2:
                                portal.setAlpha(0.5F);
                                break;
                        }
                    }
                }
            }
        }
    }
}