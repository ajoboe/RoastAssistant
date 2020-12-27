package com.andrewkjacobson.android.roastassistant1.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.andrewkjacobson.android.roastassistant1.R;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastDetailsEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastReadingEntity;
import com.andrewkjacobson.android.roastassistant1.viewmodel.RoastViewModel;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
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
    private static final int REQUEST_CODE_TEMPERATURE = 10;
    private static final int REQUEST_CODE_1C = 20;
    private static final int REQUEST_CODE_ROAST_DETAILS_ACTIVITY = 30;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // keys for saving instance state
    private static final String CHRONO_BASE_KEY = "chronometer base";
    public static final String ROAST_ID_KEY = "roast id";
    private static final String CURRENT_ROAST_KEY = "current roast";

    // settings  todo perhaps this should all be in its own class
    private int mTemperatureCheckFrequency;
    private int mAllowedTempChange;
    private int mStartingTemperature;
    private int mStartingPower;
    private int mRoastTimeInSecAddend;
    private int mExpectedRoastLength = 60 * 12; // todo should be a setting
    private final int mMaxGraphTemperature = 400; // todo should be a setting

    // controls
    Chronometer mChronometerRoastTime;
    Button mButtonStartEndRoast;
    Button mButton1C;
    Button mButtonRecordTemp;
    TextView mTextCurrentTemperature;

    // graph
    GraphView mGraph;
    LineGraphSeries<DataPoint> mGraphSeriesTemperature;
    LineGraphSeries<DataPoint> mGraphSeriesPower;

    // roast-specific fields
    private RoastViewModel mRoastViewModel;

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
                                                // scoped to MainActivity
        mRoastViewModel = new ViewModelProvider(this).get(RoastViewModel.class);
//        mCurrRoast = new RoastEntity();

//        mRoastViewModel.getCurrentRoast().observe(this, new Observer<RoastEntity>() {
//            @Override
//            public void onChanged(RoastEntity roast) {
//                mCurrRoast = roast;
//            }
//        });

        loadSettings(); // this will be done in the activity that hosts the fragments (currently RoastActivity)
        initControls();
        initGraph();
        updateGraph(mCurrRoast.getCurrentReading()); // todo should the graph just listen for changes to currentReading

        // set tick listener
        mChronometerRoastTime.setOnChronometerTickListener(chronometer -> {
            if(mCurrRoast.firstCrackOccurred()) update1cPercent();
            if((mCurrRoast.getSecondsElapsed() + 5) % mTemperatureCheckFrequency == 0) { // fire five seconds before each time increment
                queryTemperature(REQUEST_CODE_TEMPERATURE);
            }
            mCurrRoast.incrementSeconds();
        });

        // Restore saved instance
        if(savedInstanceState != null) {
//            int roastId = savedInstanceState.getInt(ROAST_ID_KEY);
            mCurrRoast = savedInstanceState.getParcelable(CURRENT_ROAST_KEY);

            if(mCurrRoast.firstCrackOccurred() && mCurrRoast.get1cReading() != null) {
                ((TextView) findViewById(R.id.text_1c_time))
                        .setText(Integer.toString(mCurrRoast.get1cReading().getTimeStamp()));
                ((TextView) findViewById(R.id.text_1c_temperature))
                        .setText(Integer.toString(mCurrRoast.get1cReading().getTemperature()));
            }

            mTextCurrentTemperature.setText(Integer.toString(mCurrRoast.getCurrentReading().getTemperature()));


            if(mCurrRoast.getSecondsElapsed() > 0) {
                mChronometerRoastTime.setBase(savedInstanceState.getLong(CHRONO_BASE_KEY));
            }
            if(mCurrRoast.isRunning()) {
                ((Button) findViewById(R.id.button_start_end_roast)).setText(R.string.string_button_end_roast);
                mChronometerRoastTime.setBase(mCurrRoast.getStartTime());
                mChronometerRoastTime.start();
            } else {
                ((Button) findViewById(R.id.button_start_end_roast)).setText(R.string.string_button_start_roast);
            }
        } // END (savedInstanceState != null)
    }

    private void initControls() {
        mChronometerRoastTime = (Chronometer) findViewById(R.id.chrono_roast_time);
        mButtonStartEndRoast = (Button) findViewById(R.id.button_start_end_roast);
        mButton1C = (Button) findViewById(R.id.button_first_crack);
        mButtonRecordTemp = (Button) findViewById(R.id.button_record_temperature);
        mTextCurrentTemperature = (TextView) findViewById(R.id.text_current_temperature);
        EditText et = new EditText(getApplicationContext());
        setPowerRadioButton(mStartingPower);
        mGraph = (GraphView) findViewById(R.id.graph);
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
        recordReading(mStartingTemperature, mStartingPower);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // this will help restore from the ViewModel
//        outState.putInt(ROAST_ID_KEY, mCurrRoast.getRoastId()); // todo do we really need this?
//        outState.putParcelable(CURRENT_ROAST_KEY, mCurrRoast);

        outState.putLong(CHRONO_BASE_KEY, mChronometerRoastTime.getBase());
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
        roastDetailsIntent.putExtra(ROAST_ID_KEY, mCurrRoast.getRoastId());
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

        // todo WHAT DO I NEED TO DO???
        mCurrRoast = new RoastEntity(mStartingTemperature, mStartingPower);

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
        mCurrRoast.startRoast();

        int chronoAddend =  -(mRoastTimeInSecAddend * 1000);
        long startTime = elapsedRealtime() + chronoAddend;
        mChronometerRoastTime.setBase(startTime);
        mChronometerRoastTime.start();
        mButtonStartEndRoast.setText(R.string.string_button_end_roast);
        mButton1C.setVisibility(View.VISIBLE);
        mButtonRecordTemp.setVisibility(View.VISIBLE);

        mCurrRoast.setStartTime(startTime);
    }

    public void endRoast() {
        mChronometerRoastTime.stop();
        mButtonStartEndRoast.setText(R.string.string_button_start_roast);
        mButton1C.setVisibility(View.GONE);
        mButtonRecordTemp.setVisibility(View.GONE);
        mCurrRoast.endRoast();

        storeRoast();
    }

    private void storeRoast() {
        mRoastViewModel.insert(mCurrRoast);
        Bitmap bitmap = mGraph.takeSnapshot(); // todo save to disk and assoc. w/ roast id
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
                if(resultCode == RESULT_OK && data != null) {
                    processRecognizerResults(data, false);
                    updateGraph(mCurrRoast.getCurrentReading());
                }
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
        series.appendData(new DataPoint(
                mCurrRoast.getFirstCrackTime(), mMaxGraphTemperature), false, 99);
        series.setDataWidth(5);
        series.setColor(Color.GREEN);
        mGraph.addSeries(series);
    }

    private void storeRoastDetails(Intent data) {
            RoastDetailsEntity details = data.getParcelableExtra(RoastDetailsActivity.EXTRA_REPLY);
            mCurrRoast.setDetails(details);
            mRoastViewModel.insert(mCurrRoast);
    }

    // todo isValidTemperature will be in RoastEntity class
    //  or maybe recordReading returns a boolean
    private void processRecognizerResults(Intent data, boolean isFirstCrack) {
        String stringCurrTemp = stringTempFromResult(data);
        if (isValidTemperature(stringCurrTemp)) {
            recordTemperature(Integer.parseInt(stringCurrTemp));
            if(isFirstCrack) record1cInfo();
        } else {
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

    // todo get this info from RoastEntity (through the view model)
    private void record1cInfo() {
        ((TextView) findViewById(R.id.text_1c_temperature))
                .setText(mCurrRoast.getCurrentReading().getTemperature() + " degrees");
        ((TextView) findViewById(R.id.text_1c_time)).setText(mChronometerRoastTime.getText());
        mCurrRoast.set1c();
        update1cPercent();
    }

    public void recordTemperature(int temperature) {
        recordReading(temperature, mCurrRoast.getCurrentReading().getPowerPercentage());
    }


    /** records power and updates graph **/
    public void recordPower(int power) {
        recordReading(mCurrRoast.getCurrentReading().getTemperature(), power);
    }

    /**
     * Records the current time, temperature and power
     *
     * @param temperature
     * @param power
     */
    public void recordReading(int temperature, int power) {
        mCurrRoast.recordReading(temperature, power);
        ((TextView)findViewById(R.id.text_current_temperature)).setText(Integer.toString(temperature));
        setPowerRadioButton(power);
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
        float fPercentage = ((float) mCurrRoast.getFirstCrackTime()) / ((float) mCurrRoast.getSecondsElapsed()) * 100;
        String percentage = String.format("%.2f", fPercentage) + "%";
        ((TextView) findViewById(R.id.text_1c_percent)).setText(percentage);
    }

    public void toggleRoast(View view) {
        if (!mCurrRoast.isRunning()) {
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
            // mCurrentReading was initialized by loadSettings()
            updateGraph(mCurrRoast.getCurrentReading());
        } catch (IllegalArgumentException e) {
//            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            System.err.println("graph failed to initialize.");
        }
    }

    // todo this should just get called by an observer
    public void updateGraph(RoastReadingEntity reading) {
        try {
            mGraphSeriesTemperature.appendData(new DataPoint(
                    reading.getTimeStamp(),
                    reading.getTemperature()),
                    false, 99999);
            mGraphSeriesPower.appendData(new DataPoint(
                    reading.getTimeStamp(),
                    reading.getPowerPercentage()),
                    false, 99999); // if scrollToEnd is true, shows negatives in the beginning
            if(mCurrRoast.getSecondsElapsed() > getExpectedRoastLength()) mGraph.getViewport().scrollToEnd(); // todo should be expectedRoastLen
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

    private boolean isValidTemperature(String temperature) {
        return temperature.length() > 0
                && Integer.valueOf(temperature) < mCurrRoast.getCurrentReading().getTemperature() + mAllowedTempChange
                && Integer.valueOf(temperature) > mCurrRoast.getCurrentReading().getTemperature() - mAllowedTempChange;
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
        updateGraph(mCurrRoast.getCurrentReading());
    }

    // todo LEFT OFF HERE
    // todo LEFT OFF HERE
    // todo LEFT OFF HERE
}