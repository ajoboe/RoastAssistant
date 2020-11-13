    package com.andrewkjacobson.android.roastassistant1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import static android.os.SystemClock.*;

public class MainActivity extends AppCompatActivity {
    Chronometer mChronometerRoastTime;
    Button mButtonStartStopRoast;
    int mSecondsElapsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChronometerRoastTime = (Chronometer) findViewById(R.id.chrono_roast_time);
        mButtonStartStopRoast = (Button) findViewById(R.id.button_start_end_roast);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case 10:
                    ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getApplicationContext(), res.toString(), Toast.LENGTH_LONG).show();
//                    int intFound = getNumberFromResult(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
//                    if (intFound != -1) {
//                        FIRST_NUMBER = intFound;
//                        firstNumTextView.setText(intFound);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Sorry, I didn't catch that! Please try again", Toast.LENGTH_LONG).show();
//                    }
                    break;
                case 20:

                case 30:

            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to recognize speech!", Toast.LENGTH_LONG).show();
        }
    }
    public void startRoast(View view) {
        Toast toast = Toast.makeText(this, "Roast started.",
                Toast.LENGTH_SHORT);
        toast.show();
        mChronometerRoastTime.setBase(elapsedRealtime());
        mChronometerRoastTime.start();
        mButtonStartStopRoast.setText(R.string.string_button_end_roast);
        mSecondsElapsed = 0;

        mChronometerRoastTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if(mSecondsElapsed % 10 == 0) {
                    Toast.makeText(view.getContext(),"Elapsed time: " + Integer.toString(mSecondsElapsed), Toast.LENGTH_SHORT).show();
                }
                mSecondsElapsed++;
            }
        });
    }

    public void endRoast(View view) {
        Toast toast = Toast.makeText(this, "Roast ended.",
                Toast.LENGTH_SHORT);
        toast.show();
        mChronometerRoastTime.stop();
        mButtonStartStopRoast.setText(R.string.string_button_start_roast);

    }

    public void recordTemperature(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        startActivityForResult(intent, 10);
    }
}