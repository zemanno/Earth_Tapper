package com.games.norbert.myownclicker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity
{
    // variables
    TextView tvTime, tvClicks, tvHighScore;
    Button startButton, resetButton;
    ImageButton clickButton;
    CountDownTimer timer;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MediaPlayer mediaPlayer;
    int length, highScore, time = 30, clicks = 0, milliseconds = time * 1000;
    static final int COUNTDOWN_INTERVAL = 1000;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // associate variables to connect to the widgets within the activity
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvClicks = (TextView) findViewById(R.id.tvClicks);
        tvHighScore = (TextView) findViewById(R.id.tvHighScore);
        startButton = (Button) findViewById(R.id.btnStart);
        clickButton = (ImageButton) findViewById(R.id.btnClick);
        resetButton = (Button) findViewById(R.id.btnReset);

        // allocate memory in SharedPreferences to hold the high score and store the value of the
        // high score in the highScore int variable
        sharedPreferences = getSharedPreferences("highScores", Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt("highScore", 0);

        // assign the song to the Mediaplayer object
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.edx_rememberhouse);
        //mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.all_i_want_dosem_remix);
        mediaPlayer.start();

        // when the game loads, have the start button enabled and not the the Click button, which
        // will be activated when the game will start
        startButton.setEnabled(true);
        clickButton.setEnabled(false);

        // show info in the TextViews
        tvClicks.setText("Clicks: " + clicks);
        tvTime.setText(time + " seconds");
        tvHighScore.setText("High Score: " + highScore + " clicks");

        // create the timer and tell it what to do as it's ticking and what to do when it's done
        timer = new CountDownTimer(milliseconds, COUNTDOWN_INTERVAL)
        {
            @Override public void onTick(long l) { tick(); }
            @Override public void onFinish() { finished(); }
        };

        // assign OnClickListeners to the 3 buttons in the activity
        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view) { startButton(); }
        });
        clickButton.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view) { clickButton(); }
        });
        resetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view) { resetButton(); }
        });
    }

    @Override public void onPause()
    {
        super.onPause();
        mediaPlayer.pause();
        length = mediaPlayer.getCurrentPosition();
    }

    @Override public void onPostResume()
    {
        super.onPostResume();
        mediaPlayer.start();
        mediaPlayer.seekTo(length);
    }

    private void resetButton()
    {
        sharedPreferences = getSharedPreferences("highScores", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("highScore", 0);
        editor.apply();
        highScore = sharedPreferences.getInt("highScore", 0);
        clicks = 0;
        time = 30;
        tvClicks.setText("Clicks: " + clicks);
        tvTime.setText(time + " seconds");
        tvHighScore.setText("High Score: 0 clicks");
    }

    private void tick()
    {
        time--;
        tvTime.setText(time + " seconds");
    }

    private void finished()
    {
        tvTime.setText("Time\'s up!");

        startButton.setEnabled(true);
        clickButton.setEnabled(false);
        resetButton.setEnabled(true);

        sharedPreferences = getSharedPreferences("highScores", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        highScore = sharedPreferences.getInt("highScore", 0);

        if(clicks >= highScore)
        {
            highScore = clicks;
            editor.putInt("highScore", highScore);
            editor.apply();
        }

        // editor.putInt("highScore", getHigherScore(clicks, highScore));
        editor.apply();
        tvHighScore.setText("High Score: " + highScore + " clicks");
    }

    private void startButton()
    {
        timer.start();
        startButton.setEnabled(false);
        resetButton.setEnabled(false);
        clickButton.setEnabled(true);
        clicks = 0;
        time = 30;
        tvTime.setText("30 seconds");
        tvClicks.setText("Clicks: 0");
    }

    private void clickButton()
    {
        clicks++;
        tvClicks.setText("Clicks: " + clicks);
    }

    private int getHigherScore(int a, int b) { return a > b ? a : b; }
}