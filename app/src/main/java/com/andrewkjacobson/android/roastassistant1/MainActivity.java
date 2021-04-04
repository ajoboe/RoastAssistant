package com.andrewkjacobson.android.roastassistant1;

// TODO RESTRUCTURE ARCHITECTURE
//          https://developer.android.com/jetpack/guide

// todo save RoastReadingsSparceArray in DB
// todo PreviousRoastsActivity (cards)
// todo ViewPreviousRoastActivity (combine with MainActivity?...fragments?)--shows current roast when finishes
// todo export to Google Sheets
// todo continue to refine layout

// todo hide First Crack info until relevant
// todo hide suggestions section until relevant
// todo 1c time and temp on graph only
// todo end temp on graph only
// todo make sure roast is running before denying a temp check
// todo dev time under suggestions section
// todo something turns red when roast goes overtime (and yellow a few seconds before...)
// todo desired development percentage range in settings (default 80-82% / 18-20%)

// todo refine roast details
//      roast metadata:
//          - need label roast level spinner
//          - switch to autocomplete for:
//              - bean type
//              - roaster
//          - add date picker
//              https://developer.android.com/codelabs/android-training-menus-and-pickers?index=..%2F..%2Fandroid-training#7
//          - add postfixes for ambient temp, batch size and yield
//          - ambient temp (autofilled...how? setting? prev value? other? outdoor temp?)

// todo social aspect
// todo add graph temp labels at beginning, end, and peak
// todo set init temp before start roast
// todo suggested end roast window (on graph?)
// todo audio end roast que
// todo batch size -> profile suggestions
// todo export/share to Google Sheets
// todo find another graph implementation
// todo full screen on roast start
// todo convert graph to actual time in min:seconds
// todo add graph update interval to settings
// todo if time=zero+addend, overwrite the roast reading list (temp and pow) for any reading before the clock starts
// todo only allow 1c to happen once? or at least say "are you sure?"
// todo clear everything on "Start Roast"...just call newRoast()
// todo add previous roast graph overlay! (from LJ and SM)
// todo have an upload area for users to upload their roasts
// todo add color customization to settings
// todo add drum speed (enable in settings)
// todo roast starting power setting should have limited options

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;
import java.util.Locale;

import static android.os.SystemClock.elapsedRealtime;




/**
 * @author      Andrew Jacobson <ajoboe@gmail.com>
 * @version     1.0
 * @since       1.0          (the version of the package this class was first added to)
 */
public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_TEMPERATURE = 10;
    public static final int REQUEST_CODE_1C = 20;
    public static final int REQUEST_CODE_ROAST_DETAILS_ACTIVITY = 30;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // keys for saving instance state
    private final String SECONDS_ELAPSED_KEY = "seconds elapsed";
    private final String FIRST_CRACK_TIME_KEY = "first crack time";
    private final String READINGS_KEY = "readings";
    private final String ROAST_RUNNING_KEY = "roast running";
    private final String CHRONO_BASE_KEY = "chronometer base";
    private final String CURRENT_READING_KEY = "current reading";


    // settings
    private int mTemperatureCheckFrequency;
    private int mAllowedTempChange;
    private int mStartingTemperature;
    private int mStartingPower;
    private int mRoastTimeInSecAddend;
    private int mExpectedRoastLength = 60 * 12; // use
    private final int mMaxGraphTemperature = 400;

    // controls
    Chronometer mChronometerRoastTime;
    Button mButtonStartEndRoast;
    Button mButton1C;
    Button mButtonRecordTemp;
    TextView mTextCurrentTemperature;
    GraphView mGraph;
    LineGraphSeries<DataPoint> mGraphSeriesTemperature;
    LineGraphSeries<DataPoint> mGraphSeriesPower;

    // fields
    RoastDetailsViewModel mRoastDetailsViewModel;
    int mSecondsElapsed;
    int m1cTimeInSeconds = -1;
    RoastReading mCurrentReading;
    boolean mRoastIsRunning = false;
    SparseArray<RoastReading> mReadings;


    /**
     * Short one line description.                           (1)
     * <p>
     * Longer description. If there were any, it would be    (2)
     * here.
     * <p>
     * And even more explanations to follow in consecutive
     * paragraphs separated by HTML paragraph breaks.
     *
     * @param  savedInstanceState Description text text text.          (3)
     * @return Description text text text.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRoastDetailsViewModel = new ViewModelProvider(this).get(RoastDetailsViewModel.class);
        mRoastDetailsViewModel.getAllRoasts().observe(this, new Observer<List<RoastDetails>>() {
            @Override
            public void onChanged(List<RoastDetails> roastDetails) {

            }
        });

        loadSettings();

        // controls
        mChronometerRoastTime = (Chronometer) findViewById(R.id.chrono_roast_time);
        mButtonStartEndRoast = (Button) findViewById(R.id.button_start_end_roast);
        mButton1C = (Button) findViewById(R.id.button_first_crack);
        mButtonRecordTemp = (Button) findViewById(R.id.button_record_temperature);
        mTextCurrentTemperature = (TextView) findViewById(R.id.text_current_temperature);
        setPowerRadioButton(mStartingPower);

        mCurrentReading = new RoastReading(0, mStartingTemperature, mStartingPower);
        mReadings = new SparseArray<>();
        mGraph = (GraphView) findViewById(R.id.graph);
        initGraph();

        recordReading(mStartingTemperature, mStartingPower);

        // set tick listener
        mChronometerRoastTime.setOnChronometerTickListener(chronometer -> {
            if(firstCrackOccurred()) update1cPercent();
            if((mSecondsElapsed + 5) % mTemperatureCheckFrequency == 0) { // fire five seconds before each time increment
                queryTemperature(REQUEST_CODE_TEMPERATURE);
            }
            mSecondsElapsed++;
        });

        // Restore saved instance
        if(savedInstanceState != null) {
            mSecondsElapsed = savedInstanceState.getInt(SECONDS_ELAPSED_KEY);

            mReadings = savedInstanceState.getSparseParcelableArray(READINGS_KEY);

            m1cTimeInSeconds = savedInstanceState.getInt(FIRST_CRACK_TIME_KEY);
            if(m1cTimeInSeconds != -1 && mReadings.get(m1cTimeInSeconds) != null) {
                ((TextView) findViewById(R.id.text_1c_time)).setText(Integer.toString(m1cTimeInSeconds));
                ((TextView) findViewById(R.id.text_1c_temperature))
                        .setText(Integer.toString(mReadings.get(m1cTimeInSeconds).getTemperature()));
            }

            mCurrentReading = savedInstanceState.getParcelable(CURRENT_READING_KEY);
            mTextCurrentTemperature.setText(Integer.toString(getCurrentReading().getTemperature()));

            mRoastIsRunning = savedInstanceState.getBoolean(ROAST_RUNNING_KEY);

            if(mSecondsElapsed > 0) {
                mChronometerRoastTime.setBase(savedInstanceState.getLong(CHRONO_BASE_KEY));
            }
            if(mRoastIsRunning) {
                ((Button) findViewById(R.id.button_start_end_roast)).setText(R.string.string_button_end_roast);
                mChronometerRoastTime.start();
            } else {
                ((Button) findViewById(R.id.button_start_end_roast)).setText(R.string.string_button_start_roast);
            }
        }
    }

    private void loadSettings() {
        // settings and preferences
        androidx.preference.PreferenceManager
                .setDefaultValues(this, R.xml.root_preferences, false);
        SharedPreferences sharedPreferences =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        mTemperatureCheckFrequency = Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_TEMP_CHECK_FREQ, "60"));
        mAllowedTempChange = Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_ALLOWED_TEMP_CHANGE, "50"));
        mStartingTemperature = Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_STARTING_TEMPERATURE, "68"));
        mStartingPower = Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_STARTING_POWER, "100"));
        mRoastTimeInSecAddend = Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_ROAST_TIME_ADDEND, "0"));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
        // todo save graph state???

        // fields to save
        outState.putInt(SECONDS_ELAPSED_KEY, mSecondsElapsed);
        outState.putInt(FIRST_CRACK_TIME_KEY, m1cTimeInSeconds);
        outState.putSparseParcelableArray(READINGS_KEY, mReadings);
        outState.putBoolean(ROAST_RUNNING_KEY, mRoastIsRunning);
        outState.putLong(CHRONO_BASE_KEY, mChronometerRoastTime.getBase());
        outState.putParcelable(CURRENT_READING_KEY, mCurrentReading);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_share:
                Toast.makeText(getApplicationContext(), R.string.string_coming_soon, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_new_roast:
                showNewRoastDialog();
                return true;
            case R.id.action_roast_details:
                showRoastDetails();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void showRoastDetails() {
        Intent roastDetailsIntent = new Intent(this, RoastDetailsActivity.class);
        startActivityForResult(roastDetailsIntent, REQUEST_CODE_ROAST_DETAILS_ACTIVITY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void showNewRoastDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
//        alertBuilder.setTitle("Clear current roast?");
        alertBuilder.setMessage("Clear the current roast and start a new one?");
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newRoast();
            }
        });
        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "New roast canceled.", Toast.LENGTH_SHORT).show();
            }
        });
        alertBuilder.show();
    }
    public void newRoast() {
        if (Build.VERSION.SDK_INT >= 11) {
            mChronometerRoastTime.setBase(elapsedRealtime());
            recreate();
        } else {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);

            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    public void startRoast(View view) {
//        Toast toast = Toast.makeText(this, R.string.string_roast_started_message,
//                Toast.LENGTH_SHORT);
//        toast.show();
        mSecondsElapsed = 0 + mRoastTimeInSecAddend;
        int chronoAddend =  -(mRoastTimeInSecAddend * 1000);
        mChronometerRoastTime.setBase(elapsedRealtime() + chronoAddend);
        mChronometerRoastTime.start();
        mRoastIsRunning = true;
        mButtonStartEndRoast.setText(R.string.string_button_end_roast);
        mButton1C.setVisibility(View.VISIBLE);
        mButtonRecordTemp.setVisibility(View.VISIBLE);
    }

    public boolean firstCrackOccurred() {
        return m1cTimeInSeconds != -1;
    }

    public void endRoast() {
//        Toast toast = Toast.makeText(this, R.string.string_roast_ended_message,
//                Toast.LENGTH_SHORT);
//        toast.show();
        mChronometerRoastTime.stop();
        mButtonStartEndRoast.setText(R.string.string_button_start_roast);
        mButton1C.setVisibility(View.GONE);
        mButtonRecordTemp.setVisibility(View.GONE);
        mRoastIsRunning = false;

        storeRoast();
    }

    private void storeRoast() {
        // save time, temp, power info
        // save 1c info
        // save bitmap of graph
        // HOW TO GET BITMAP OF THE GRAPH
        Bitmap bitmap = mGraph.takeSnapshot();
        //The runtime permission WRITE_EXTERNAL_STORAGE is needed!
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
        switch (requestCode) {
            case REQUEST_CODE_1C:
                if(resultCode == RESULT_OK && data != null) {
                    processRecognizerResults(data, true);
                    after1cLogged();
                }
                break;
            case REQUEST_CODE_TEMPERATURE:
                if(resultCode == RESULT_OK && data != null) processRecognizerResults(data, false);
                break;
            case REQUEST_CODE_ROAST_DETAILS_ACTIVITY:
                if(resultCode == RESULT_OK && data != null)
                    storeRoastDetails(data);
                break;
        }
    }

    private void after1cLogged() {
        // todo add time and temp labels
        BarGraphSeries<DataPoint> series = new BarGraphSeries();
        series.appendData(new DataPoint(m1cTimeInSeconds, mMaxGraphTemperature), false, 99);
        series.setDataWidth(5);
        series.setColor(Color.GREEN);
        mGraph.addSeries(series);
    }

    private void storeRoastDetails(Intent data) {
            RoastDetails details = data.getParcelableExtra(RoastDetailsActivity.EXTRA_REPLY);
            mRoastDetailsViewModel.insert(details);
    }


    private void processRecognizerResults(Intent data, boolean isFirstCrack) {
        String stringCurrTemp = stringTempFromResult(data);
        if (isValidTemperature(stringCurrTemp)) {
            recordTemperature(Integer.parseInt(stringCurrTemp));
            if(isFirstCrack) record1cInfo();
        } else {
//            Toast.makeText(getApplicationContext(), "These aren't the numbers we're looking for...try that again", Toast.LENGTH_LONG);
            if (isFirstCrack) {
                queryTemperature(REQUEST_CODE_1C);
            } else {
                queryTemperature(REQUEST_CODE_TEMPERATURE);
            }
        }
    }

    private String stringTempFromResult(Intent data) {
        return data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                .get(0).replaceAll("[^0-9]", "");
    }

    private void record1cInfo() {
        ((TextView) findViewById(R.id.text_1c_temperature)).setText(getCurrentReading().getTemperature() + "°");
        ((TextView) findViewById(R.id.text_1c_time)).setText(mChronometerRoastTime.getText());
        m1cTimeInSeconds = mSecondsElapsed;
        update1cPercent();
    }

    private RoastReading getCurrentReading() {
        return mCurrentReading;
    }

    public void recordTemperature(int temperature) {
        recordReading(temperature, getCurrentReading().getPowerPercentage());
    }

    /** records power and updates graph **/
    public void recordPower(int power) {
        recordReading(getCurrentReading().getTemperature(), power);
    }

    /**
     * Records the current time, temperature and power
     *
     * @param temperature
     * @param power
     */
    public void recordReading(int temperature, int power) {
        mCurrentReading = new RoastReading(mSecondsElapsed, temperature, power);
        mReadings.put(mSecondsElapsed, mCurrentReading);
        mTextCurrentTemperature.setText(Integer.toString(temperature) + "°");
        setPowerRadioButton(power);
        Log.d(LOG_TAG, "New reading recorded..." + mCurrentReading);
        updateGraph(mCurrentReading);
    }

    private void setPowerRadioButton(int power) {
        switch (power) {
            case 0:
                ((RadioButton)findViewById(R.id.radio_button_0)).setChecked(true);
                break;
            case 25:
                ((RadioButton)findViewById(R.id.radio_button_25)).setChecked(true);
                break;
            case 50:
                ((RadioButton)findViewById(R.id.radio_button_50)).setChecked(true);
                break;
            case 75:
                ((RadioButton)findViewById(R.id.radio_button_75)).setChecked(true);
                break;
            case 100:
                ((RadioButton)findViewById(R.id.radio_button_100)).setChecked(true);
                break;
            default:
                // do nothing
                break;
        }
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
        // SHOW TIME FOR X VALUES todo Doesn't work!!
        mGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return String.format("%d:%02d",  (int)value/60, (int)value%60);
                } else {
                    // convert seconds to minutes:seconds
//                    return super.formatLabel(value, isValueX) + " €";
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        try {

            // GRAPH SETTINGS
            mGraph.setVisibility(View.VISIBLE);
            mGraph.getViewport().setScalable(false);
            mGraph.getViewport().setScrollable(false);
//            mGraph.getGridLabelRenderer().setVerticalLabelsVisible(true);
            mGraph.getGridLabelRenderer().setHorizontalLabelsVisible(true);

            // TEMPERATURE SERIES
            mGraphSeriesTemperature = new LineGraphSeries<>();
            mGraphSeriesTemperature.setColor(Color.BLUE);
            mGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLUE);
            mGraph.getGridLabelRenderer().setLabelVerticalWidth(50);
            mGraph.addSeries(mGraphSeriesTemperature);
            mGraph.getViewport().setYAxisBoundsManual(true);
            mGraph.getViewport().setMinY(mStartingTemperature - 20);
            mGraph.getViewport().setMaxY(mMaxGraphTemperature);
            mGraph.getViewport().setXAxisBoundsManual(true);
            mGraph.getViewport().setMinX(0);
            mGraph.getViewport().setMaxX(getExpectedRoastLength());
//            mGraph.getViewport().setScalable(false); // if true, messes with the time


            // POWER SERIES
            mGraphSeriesPower = new LineGraphSeries<>();
            mGraphSeriesPower.setColor(Color.RED);
            mGraph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);
            mGraph.getGridLabelRenderer().setSecondScaleLabelVerticalWidth(50); // todo doesn't do anything
            mGraph.getSecondScale().addSeries(mGraphSeriesPower);
            mGraph.getSecondScale().setMinY(0);
            mGraph.getSecondScale().setMaxY(100);



            // feed initial values to the graph
            updateGraph(new RoastReading(0, mStartingTemperature, mStartingPower));
        } catch (IllegalArgumentException e) {
//            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            System.err.println("graph failed to initialize.");
        }
    }

    public void updateGraph(RoastReading reading) {
        try {
            mGraphSeriesTemperature.appendData(new DataPoint(
                    reading.getTimeStamp(),
                    reading.getTemperature()),
                    false, 99999);
            mGraphSeriesPower.appendData(new DataPoint(
                    reading.getTimeStamp(),
                    reading.getPowerPercentage()),
                    false, 99999); // if scrollToEnd is true, shows negatives in the beginning
            if(mSecondsElapsed > getExpectedRoastLength()) mGraph.getViewport().scrollToEnd(); // todo should be expectedRoastLen
        } catch (IllegalArgumentException e) {
//            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            System.err.println("failed to update graph.");
        }
    }

    private int getExpectedRoastLength() {
        return mExpectedRoastLength;
    }

    public void buttonRecordTemperatureClicked(View view) {
        queryTemperature(REQUEST_CODE_TEMPERATURE);
    }

    public void buttonFirstCrackClicked(View view) {
        queryTemperature(REQUEST_CODE_1C); // updates 1c related TextViews
    }

    public RoastReading get1cReading() {
        return mReadings.get(m1cTimeInSeconds);
    }

    private boolean isValidTemperature(String temperature) {
        return temperature.length() > 0
                && Integer.valueOf(temperature) < getCurrentReading().getTemperature() + mAllowedTempChange
                && Integer.valueOf(temperature) > getCurrentReading().getTemperature() - mAllowedTempChange;
    }

    public void onRadioButtonPowerClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int power =0;
        switch (view.getId()) {
            case R.id.radio_button_0:
                if(checked) power = 0;
                break;
            case R.id.radio_button_25:
                if(checked) power = 25;
                break;
            case R.id.radio_button_50:
                if(checked) power = 50;
                break;
            case R.id.radio_button_75:
                if(checked) power = 75;
                break;
            case R.id.radio_button_100:
                if(checked) power = 100;
                break;
            default:
                // do nothing
                break;
        }
        Log.d(LOG_TAG, "Power changed to " + Integer.toString(power));
        // save power change time / power
        recordPower(power);
    }
}