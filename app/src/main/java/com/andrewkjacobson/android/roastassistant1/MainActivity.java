package com.andrewkjacobson.android.roastassistant1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.os.SystemClock.*;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_TEMPERATURE = 10;
    public static final int REQUEST_CODE_1C = 20;
    final int mTemperatureCheckFrequency = 15;
    final int mAllowedTempChange = 50;
    final int mStartingTemperature = 68;
    final int mStartingPower = 0;

    Chronometer mChronometerRoastTime;
    Button mButtonStartEndRoast;
    TextView mTextCurrentTemperature;

    int mSecondsElapsed;
    int m1cTimeInSeconds = -1;
    //    List<Integer> mTemperatures;
    List<RoastReading> mReadings;

    boolean mRoastIsRunning = false;
    boolean m1cOccurred = false;


    GraphView mGraph;
    LineGraphSeries<DataPoint> mGraphSeriesTemperature;
    LineGraphSeries<DataPoint> mGraphSeriesPower;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChronometerRoastTime = (Chronometer) findViewById(R.id.chrono_roast_time);
        mButtonStartEndRoast = (Button) findViewById(R.id.button_start_end_roast);
        mTextCurrentTemperature = (TextView) findViewById(R.id.text_current_temperature);
//        mTemperatures = new ArrayList<>();
        mReadings = new ArrayList<>();
//        mTemperatures.add(mStartingTemperature);
        mGraph = (GraphView) findViewById(R.id.graph);
        initGraph();

        RadioGroup rgPower = ((RadioGroup) findViewById(R.id.radio_group_powers));
        recordReading(mStartingTemperature, mStartingPower);
        rgPower.setOnCheckedChangeListener((group, checkedId) -> {
            // checkedId is the RadioButton selected
            int power =0;
            switch (checkedId) {
                case R.id.radio_button_0:
                    power = 0;
                    break;
                case R.id.radio_button_25:
                    power = 25;
                    break;
                case R.id.radio_button_50:
                    power = 50;
                    break;
                case R.id.radio_button_75:
                    power = 75;
                    break;
                case R.id.radio_button_100:
                    power = 100;
                    break;
            }
            // save power change time / power
            recordPower(power);

            // update graph series
        });
    }

    public int getLastRecordedTemperature() {
        return mReadings.get(mReadings.size() - 1).getTemperature();
    }

    private int getLastRecordedPower() {
        return mReadings.get(mReadings.size() - 1).getPowerPercentage();
    }

    public void startRoast(View view) {
        Toast toast = Toast.makeText(this, R.string.string_roast_started_message,
                Toast.LENGTH_SHORT);
        toast.show();
        mChronometerRoastTime.setBase(elapsedRealtime());
        mChronometerRoastTime.start();
        mButtonStartEndRoast.setText(R.string.string_button_end_roast);
        mSecondsElapsed = 0;
        mRoastIsRunning = true;

        // todo move to method chronometerTickListener()
        mChronometerRoastTime.setOnChronometerTickListener(chronometer -> {
            if (m1cOccurred) update1cPercent();

            if ((mSecondsElapsed + 5) % mTemperatureCheckFrequency == 0) { // fire five seconds before each time increment
                queryTemperature(REQUEST_CODE_TEMPERATURE);
            }
            mSecondsElapsed++;
        });
    }

    public void chronometerTickListener() {

    }

    public void endRoast() {
        Toast toast = Toast.makeText(this, R.string.string_roast_ended_message,
                Toast.LENGTH_SHORT);
        toast.show();
        mChronometerRoastTime.stop();
        mButtonStartEndRoast.setText(R.string.string_button_start_roast);
        mRoastIsRunning = false;
    }

    public void queryTemperature(int requestCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        startActivityForResult(intent, requestCode); // callback is onActivityResult()
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            boolean isFirstCrack = false;
            switch (requestCode) {
                case REQUEST_CODE_1C:
                    isFirstCrack = true;
                    m1cOccurred = true;
                    // fall through
                case REQUEST_CODE_TEMPERATURE:
                    String stringCurrTemp = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0).replaceAll("[^0-9]", "");
                    if(isValidTemperature(stringCurrTemp)) {
                        recordTemperature(Integer.parseInt(stringCurrTemp));
                        if (isFirstCrack) {
                            record1cInfo();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "These aren't the numbers we're looking for...try that again", Toast.LENGTH_LONG);
                        if (isFirstCrack) {
                            queryTemperature(REQUEST_CODE_1C);
                        } else {
                            queryTemperature(REQUEST_CODE_TEMPERATURE);
                        }
                    }
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to recognize speech!", Toast.LENGTH_LONG).show();
        }
    }

    private void record1cInfo() {
        ((TextView) findViewById(R.id.text_1c_temperature)).setText(getLastRecordedTemperature() + " degrees");
        ((TextView) findViewById(R.id.text_1c_time)).setText(mChronometerRoastTime.getText());
        m1cTimeInSeconds = mSecondsElapsed;
        update1cPercent();
    }

    public void recordTemperature(int temperature) {
        recordReading(temperature, getLastRecordedPower());
    }

    public void recordPower(int power) {
        recordReading(getLastRecordedTemperature(), power);
    }

    public void recordReading(int temperature, int power) {
        mReadings.add(new RoastReading(mSecondsElapsed, temperature, power));
        mTextCurrentTemperature.setText(Integer.toString(temperature));
        updateGraph(mSecondsElapsed, temperature, power);
    }

    public void update1cPercent() {
        float fPercentage = ((float) m1cTimeInSeconds) / ((float) mSecondsElapsed) * 100;
        String percentage = String.format("%.2f", fPercentage) + "%";
        ((TextView) findViewById(R.id.text_1c_percent)).setText(percentage);
    }

    public void toggleRoast(View view) {
        if (!mRoastIsRunning) {
            startRoast(view);
        } else {
            endRoast();
        }
    }

    public void initGraph() {
        mGraph.setVisibility(View.VISIBLE);
        try {
            mGraphSeriesTemperature = new LineGraphSeries<>(
                    new DataPoint[]{
                            new DataPoint(0, mStartingTemperature)
                    });
            mGraphSeriesTemperature.setColor(Color.BLUE);
            mGraphSeriesTemperature.setTitle("Temperature");
            mGraph.addSeries(mGraphSeriesTemperature);
            mGraphSeriesPower = new LineGraphSeries<>(
                    new DataPoint[]{
                            new DataPoint(0, mStartingPower)
                    });
            mGraphSeriesPower.setColor(Color.RED);
            mGraphSeriesPower.setTitle("Power");
            mGraph.addSeries(mGraphSeriesPower);
            updateGraph(0, mStartingTemperature, mStartingPower);

            mGraph.getViewport().setXAxisBoundsManual(true);
            mGraph.getViewport().setMinX(0);
            mGraph.getViewport().setMaxX(2);
            mGraph.getViewport().setYAxisBoundsManual(true);
            mGraph.getViewport().setMinY(mStartingTemperature - 20);
            mGraph.getViewport().setMaxY(400);
            mGraph.getViewport().setScalable(true);
            mGraph.getViewport().setScrollable(true);
            mGraph.getLegendRenderer().setVisible(true);
            mGraph.getSecondScale().addSeries(mGraphSeriesPower);
            mGraph.getSecondScale()
        } catch (IllegalArgumentException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            System.err.println("graph failed to initialize.");
        }
    }

    public void updateGraph(int time, int temperature, int power) {
        try {
            mGraphSeriesTemperature.appendData(new DataPoint(time, temperature), true, 40);
            mGraphSeriesPower.appendData(new DataPoint(time, power), true, 40);
            mGraph.getViewport().setMaxX(mGraph.getViewport().getMaxX(true) + 1);
            mGraph.getViewport().scrollToEnd();
        } catch (IllegalArgumentException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            System.err.println("failed to update graph.");
        }
    }

    public void buttonRecordTemperatureClicked(View view) {
        queryTemperature(REQUEST_CODE_TEMPERATURE);
    }

    public void buttonFirstCrackClicked(View view) {
        queryTemperature(REQUEST_CODE_1C); // updates 1c related TextViews
    }

}

    private boolean isValidTemperature(String temperature) {
        return temperature.length() > 0
                && Integer.valueOf(temperature) < getLastRecordedTemperature() + mAllowedTempChange
                && Integer.valueOf(temperature) > getLastRecordedTemperature() - mAllowedTempChange;
    }
}