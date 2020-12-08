package com.andrewkjacobson.android.roastassistant1;
// todo end roast dialog with extra data
//      roast metadata:
//          - need labels for everything
//          - bean type (text...autocomplete/combobox)
//          - roast degree (spinner...other->listener->text) <--- NEEDS FILLING
//          - date (autofilled date widget) <--- start with curr
//          - batch size in grams (text) <--- put a g at the end (put batch size, yield, and weight loss on same line)
//          - yield in grams (text) <--- put a g at the end
//          - roast notes (multi-line text) <--- doesn't look like multi
//          - tasting notes (multi-line text) <--- "
//          - roaster (autofilled text)
//          - ambient temp (autofilled text)
//          - preheat time <--- needs adding still

// todo save roast (locally AND to Goog Drive)
// todo apply new settings immediately
// todo set init temp before start roast
// todo date should be current
// todo toolbar on roast details
// todo toolbar on settings page
// todo done button on roast details
// todo suggested end roast window (on graph?)
// todo audio end roast que
// todo batch size -> profile suggestions
// todo export/share to Google Sheets
// todo find another graph implementation
// todo full screen on roast start
// todo convert graph to actual time in min:seconds
// todo update graph every second
// todo if time=zero, overwrite the roast reading list (temp and pow)
// todo require that a power be entered before starting
// todo     or just a default starting power
// todo only allow 1c to happen once? or at least say "are you sure?"
// todo clear everything on "Start Roast"...just call newRoast()
// todo deal with rotations (onSaveInstanceState?)
// todo add previous roast graph overlay! (from LJ and SM)
// todo have an upload area for users to upload their roasts
// todo add color customization to settings
// todo add drum speed

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Locale;

import static android.os.SystemClock.*;




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
    int mTemperatureCheckFrequency;
    int mAllowedTempChange;
    int mStartingTemperature;
    int mStartingPower;
    // settings

    // controls
    Chronometer mChronometerRoastTime;
    Button mButtonStartEndRoast;
    TextView mTextCurrentTemperature;
    GraphView mGraph;
    LineGraphSeries<DataPoint> mGraphSeriesTemperature;
    LineGraphSeries<DataPoint> mGraphSeriesPower;

    // fields
    int mSecondsElapsed;
    int m1cTimeInSeconds = -1;
    RoastReading mCurrentReading;
    boolean mRoastIsRunning = false;
    SparseArray<RoastReading> mReadingsSparceArray;


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

        // controls
//        setSupportActionBar(findViewById(R.id.toolbar));
        mChronometerRoastTime = (Chronometer) findViewById(R.id.chrono_roast_time);
        mButtonStartEndRoast = (Button) findViewById(R.id.button_start_end_roast);
        mTextCurrentTemperature = (TextView) findViewById(R.id.text_current_temperature);
        setPowerRadioButton(mStartingPower);

        mCurrentReading = new RoastReading(0, mStartingTemperature, mStartingPower);
        mReadingsSparceArray = new SparseArray<>();
        mGraph = (GraphView) findViewById(R.id.graph);
        initGraph();

        // settings and preferences
        androidx.preference.PreferenceManager
                .setDefaultValues(this, R.xml.root_preferences, false);
        SharedPreferences sharedPreferences =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        mTemperatureCheckFrequency = Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_TEMP_CHECK_FREQ, "60"));
        mAllowedTempChange = Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_ALLOWED_TEMP_CHANGE, "50"));
        mStartingTemperature = Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_STARTING_TEMPERATURE, "68"));
        mStartingPower = Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_STARTING_POWER, "100"));


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

            mReadingsSparceArray = savedInstanceState.getSparseParcelableArray(READINGS_KEY);

            m1cTimeInSeconds = savedInstanceState.getInt(FIRST_CRACK_TIME_KEY);
            if(m1cTimeInSeconds != -1 && mReadingsSparceArray.get(m1cTimeInSeconds) != null) {
                ((TextView) findViewById(R.id.text_1c_time)).setText(Integer.toString(m1cTimeInSeconds));
                ((TextView) findViewById(R.id.text_1c_temperature))
                        .setText(Integer.toString(mReadingsSparceArray.get(m1cTimeInSeconds).getTemperature()));
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
        // todo save graph state???

        // fields to save
        outState.putInt(SECONDS_ELAPSED_KEY, mSecondsElapsed);
        outState.putInt(FIRST_CRACK_TIME_KEY, m1cTimeInSeconds);
        outState.putSparseParcelableArray(READINGS_KEY, mReadingsSparceArray);
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
        startActivity(roastDetailsIntent);
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
        Toast toast = Toast.makeText(this, R.string.string_roast_started_message,
                Toast.LENGTH_SHORT);
        toast.show();
        mChronometerRoastTime.setBase(elapsedRealtime());
        mChronometerRoastTime.start();
        mButtonStartEndRoast.setText(R.string.string_button_end_roast);
        mSecondsElapsed = 0;
        mRoastIsRunning = true;
    }

    public boolean firstCrackOccurred() {
        return m1cTimeInSeconds != -1;
    }

    public void endRoast() {
        Toast toast = Toast.makeText(this, R.string.string_roast_ended_message,
                Toast.LENGTH_SHORT);
        toast.show();
        mChronometerRoastTime.stop();
        mButtonStartEndRoast.setText(R.string.string_button_start_roast);
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
                if(resultCode == RESULT_OK && data != null) processRecognizerResults(data, true);
                else Toast.makeText(getApplicationContext(), "Failed to recognize speech!", Toast.LENGTH_LONG).show();
                break;
            case REQUEST_CODE_TEMPERATURE:
                if(resultCode == RESULT_OK && data != null) processRecognizerResults(data, false);
                else Toast.makeText(getApplicationContext(), "Failed to recognize speech!", Toast.LENGTH_LONG).show();
                break;
            case REQUEST_CODE_ROAST_DETAILS_ACTIVITY:
                if(resultCode == RESULT_OK && data != null)
                    storeRoastDetails(data.getParcelableExtra(RoastDetailsActivity.EXTRA_REPLY));
                break;
        }
    }



    private void processRecognizerResults(Intent data, boolean isFirstCrack) {
        String stringCurrTemp = stringTempFromResult(data);
        if (isValidTemperature(stringCurrTemp)) {
            recordTemperature(Integer.parseInt(stringCurrTemp));
            if(isFirstCrack) record1cInfo();
        } else {
            Toast.makeText(getApplicationContext(),
                    "These aren't the numbers we're looking for...try that again",
                    Toast.LENGTH_LONG);
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
        ((TextView) findViewById(R.id.text_1c_temperature)).setText(getCurrentReading().getTemperature() + " degrees");
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
        mReadingsSparceArray.put(mSecondsElapsed, mCurrentReading);
        mTextCurrentTemperature.setText(Integer.toString(temperature));
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
        try {
            // GRAPH SETTINGS
            mGraph.setVisibility(View.VISIBLE);
            mGraph.getViewport().setScalable(false);
            mGraph.getViewport().setScrollable(false);

            // TEMPERATURE SERIES
            mGraphSeriesTemperature = new LineGraphSeries<>();
            mGraphSeriesTemperature.setColor(Color.BLUE);
            mGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLUE);
            mGraph.getGridLabelRenderer().setLabelVerticalWidth(50);
            mGraph.addSeries(mGraphSeriesTemperature);
            mGraph.getViewport().setYAxisBoundsManual(true);
            mGraph.getViewport().setMinY(mStartingTemperature - 20);
            mGraph.getViewport().setMaxY(400);

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
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            System.err.println("graph failed to initialize.");
        }
    }

    public void updateGraph(RoastReading reading) {
        try {
            mGraphSeriesTemperature.appendData(new DataPoint(reading.getTimeStamp(), reading.getTemperature()), false, 99999);
            mGraphSeriesPower.appendData(new DataPoint(reading.getTimeStamp(), reading.getPowerPercentage()), false, 99999);
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