package com.andrewkjacobson.android.roastassistant1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.andrewkjacobson.android.roastassistant1.R;
import com.andrewkjacobson.android.roastassistant1.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant1.viewmodel.RoastViewModel;
import com.google.android.material.slider.Slider;

import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoastFragment newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoastFragment extends Fragment
        implements View.OnClickListener {
    private RoastViewModel viewModel;

    // constants
    private static final int REQUEST_CODE_QUERY_TEMPERATURE = 10;
    private static final int REQUEST_CODE_1C_CLICKED = 20;
    private static final String CHRONO_BASE_KEY = "chronometer base";
    private static final String LOG_TAG = RoastActivity.class.getSimpleName();

    // controls
    private Chronometer mChronometerRoastTime;
    private Button mButtonStartEndRoast;
    private Button mButtonChargeTemperature;
    private Button mButton1C;
    private Button mButtonRecordTemp;
    private TextView mTextCurrentTemperature;

    final Observer<List<ReadingEntity>> readingsObserver = new Observer<List<ReadingEntity>>() {
        @Override
        public void onChanged(@Nullable final List<ReadingEntity> readings) {
            if(readings != null && !readings.isEmpty()) {
                ReadingEntity curr = readings.get(readings.size() - 1);
                mTextCurrentTemperature.setText(String.format("%d°", curr.getTemperature()));
                setPowerRadioButton(curr.getPower(), getView());
            }
        }
    };

    public RoastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);             //scoped to parent activity
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // The default ViewModel factory provides the appropriate SavedStateHandle to your ViewModel
        //  see https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate
        viewModel = new ViewModelProvider(requireActivity()).get(RoastViewModel.class); // this is all we need to do it

        initControls(view);

        // Create the observers that updates the UI.
        viewModel.getReadingsLiveData().observe(getViewLifecycleOwner(), readingsObserver);
//        viewModel.getCracksLiveData().observe(getViewLifecycleOwner(), crackObserver);

        if(savedInstanceState != null) {
            if (viewModel.getElapsed() > 0) {
                mChronometerRoastTime.setBase(savedInstanceState.getLong(CHRONO_BASE_KEY));
            }
            if (viewModel.isRunning()) {
                ((Button) view.findViewById(R.id.button_start_end_roast)).setText(R.string.string_button_end_roast);
                mChronometerRoastTime.setBase(viewModel.getStartTime());
                mChronometerRoastTime.start();
            } else {
                ((Button) view.findViewById(R.id.button_start_end_roast))
                        .setText(R.string.string_button_start_roast);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_1C_CLICKED:
                if(resultCode == RESULT_OK && data != null) {
                    processRecognizerResults(data, true);
                }
                break;
            case REQUEST_CODE_QUERY_TEMPERATURE:
                if(resultCode == RESULT_OK && data != null) {
                    processRecognizerResults(data, false);
                }
                break;
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v instanceof RadioButton) {
            onRadioButtonPowerClicked(v);
            return;
        }

        switch(v.getId()) {
            case R.id.button_start_end_roast:
                toggleRoast(v);
                break;
            case R.id.button_first_crack:
                queryTemperature(REQUEST_CODE_1C_CLICKED);
                mButton1C.setVisibility(View.GONE);
                break;
            case R.id.button_record_temperature:
            case R.id.button_charge_temperature:
                queryTemperature(REQUEST_CODE_QUERY_TEMPERATURE);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CHRONO_BASE_KEY, mChronometerRoastTime.getBase());
    }

    // ****************
    // PRIVATE METHODS
    // ****************
    private void initControls(View view) {
        mChronometerRoastTime = (Chronometer) view.findViewById(R.id.chrono_roast_time);

        mButtonStartEndRoast = (Button) view.findViewById(R.id.button_start_end_roast);
        mButtonStartEndRoast.setOnClickListener(this);

        mButtonChargeTemperature = (Button) view.findViewById(R.id.button_charge_temperature);
        mButtonChargeTemperature.setOnClickListener(this);

        mButton1C = (Button) view.findViewById(R.id.button_first_crack);
        mButton1C.setOnClickListener(this);

        mButtonRecordTemp = (Button) view.findViewById(R.id.button_record_temperature);
        mButtonRecordTemp.setOnClickListener(this);

        mTextCurrentTemperature = (TextView) view.findViewById(R.id.text_current_temperature);

        ((RadioButton)view.findViewById(R.id.radio_button_0)).setOnClickListener(this);
        ((RadioButton)view.findViewById(R.id.radio_button_25)).setOnClickListener(this);
        ((RadioButton)view.findViewById(R.id.radio_button_50)).setOnClickListener(this);
        ((RadioButton)view.findViewById(R.id.radio_button_75)).setOnClickListener(this);
        ((RadioButton)view.findViewById(R.id.radio_button_100)).setOnClickListener(this);

//        SeekBar seekBar = (view.findViewById(R.id.slider_power));
//        seekBar.setProgress(4); // todo should not be hardcoded
//        seekBar.setMax(4);
//
        Slider sliderPower = (view.findViewById(R.id.slider_power));
        sliderPower.addOnChangeListener((slider, value, fromUser) -> {
            if(fromUser) {
                Log.d(LOG_TAG, "Power changed to " + Integer.toString((int)value));
                viewModel.recordPower((int)value);
            }
        });
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            int seekBarValue;
//            Toast toast;
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(fromUser) {
//                    seekBarValue = progress * 25; // todo step should not be hardcoded
//                    if(toast != null) toast.cancel();
//                    toast = Toast.makeText(
//                            getContext(),
//                            String.format("%d%%", seekBarValue),
//                            Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                Log.d(LOG_TAG, "Power changed to " + Integer.toString(seekBarValue));
//                viewModel.recordPower(seekBarValue);
//            }
//        });

        // set tick listener
        mChronometerRoastTime.setOnChronometerTickListener(chronometer -> {
            // fire five seconds before each time increment
            if((viewModel.getElapsed() + 5) % viewModel.getSettings().getTemperatureCheckFrequency() == 0) {
                queryTemperature(REQUEST_CODE_QUERY_TEMPERATURE);
            }
            // update first crack percentage
            if(viewModel.firstCrackOccurred()) {
                TextView text1C = getActivity().findViewById(R.id.text_1c_percent_floating);
                text1C.setVisibility(View.VISIBLE);
                text1C.setText(String.format("%.2f", viewModel.getFirstCrackPercent()) + "%");
//                text1C.

                // move the text view to center of section between 0 and 1C time
                float biasedValue = (float) (viewModel.getFirstCrackTime()
                        / (float)(viewModel.getElapsed() + GraphFragment.SPACE_RIGHT_OF_LAST_ENTRY))
                        - .2f;
//                float biasedValue = .2f;
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(getActivity(), R.layout.fragment_graph);
                constraintSet.setHorizontalBias(R.id.text_1c_percent, biasedValue);
                constraintSet.applyTo((ConstraintLayout)
                        getActivity().findViewById(R.id.fragment_graph_constraint_layout));
                getActivity().findViewById(R.id.text_1c_percent_floating).setVisibility(View.VISIBLE);
            }
        });
    }

    private void setPowerRadioButton(int power, View view) {
        switch (power) {
            case 0:
                ((RadioButton)view.findViewById(R.id.radio_button_0)).setChecked(true);
                break;
            case 25:
                ((RadioButton)view.findViewById(R.id.radio_button_25)).setChecked(true);
                break;
            case 50:
                ((RadioButton)view.findViewById(R.id.radio_button_50)).setChecked(true);
                break;
            case 75:
                ((RadioButton)view.findViewById(R.id.radio_button_75)).setChecked(true);
                break;
            case 100:
                ((RadioButton)view.findViewById(R.id.radio_button_100)).setChecked(true);
                break;
            default:
                // do nothing
                break;
        }
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
        viewModel.recordPower(power);
    }

    public void queryTemperature(int requestCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        startActivityForResult(intent, requestCode); // callback is onActivityResult()
    }

    private void processRecognizerResults(Intent data, boolean isFirstCrack) {
        String stringCurrTemp = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                .get(0).replaceAll("[^0-9]", "");

        if(isFirstCrack) {
            processFirstCrack(stringCurrTemp);
        } else {
            processTemperature(stringCurrTemp);
        }
    }

    private void processTemperature(String stringCurrTemp) {
        if(!viewModel.recordTemperature(stringCurrTemp)) {
                queryTemperature(REQUEST_CODE_QUERY_TEMPERATURE);
        }
    }

    private void processFirstCrack(String stringCurrTemp) {
        if(!viewModel.record1c(stringCurrTemp)) {
            queryTemperature(REQUEST_CODE_1C_CLICKED);
        }
    }

    private void toggleRoast(View view) {
        if (!viewModel.isRunning()) {
            startRoast(view);
        } else {
            endRoast();
        }
    }

    public void startRoast(View view) {

        viewModel.startRoast(); // triggers observer

        // todo do in observer instead??
        // how to tell if roast JUST started
        // viewModel.isRunning() && viewModel.getSecondsElapsed() == 0
        mChronometerRoastTime.setBase(viewModel.getStartTime());
        mChronometerRoastTime.start();
        mButtonStartEndRoast.setText(R.string.string_button_end_roast);
        mButtonChargeTemperature.setVisibility(View.GONE);
        mButton1C.setVisibility(View.VISIBLE);
        mButtonRecordTemp.setVisibility(View.VISIBLE);
    }

    public void endRoast() {
        mChronometerRoastTime.stop();
        mButtonStartEndRoast.setText(R.string.string_button_start_roast);
        mButton1C.setVisibility(View.GONE);
        mButtonRecordTemp.setVisibility(View.GONE);
        viewModel.endRoast();
    }
}