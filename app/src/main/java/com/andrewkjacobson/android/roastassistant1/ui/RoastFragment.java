package com.andrewkjacobson.android.roastassistant1.ui;

import android.content.Intent;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.andrewkjacobson.android.roastassistant1.R;
import com.andrewkjacobson.android.roastassistant1.db.entity.CrackReadingEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant1.viewmodel.RoastViewModel;

import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.os.SystemClock.elapsedRealtime;

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
            // set first crack info
//            if(viewModel.firstCrackOccurred()) {
//                ((TextView) getView().findViewById(R.id.text_1c_time)).setText(
//                        String.format("%d:%d",
//                                viewModel.getFirstCrackTime() / 60, // minutes
//                                viewModel.getFirstCrackTime() % 60)); // seconds
//                ((TextView) getView().findViewById(R.id.text_1c_temperature)).setText(
//                        String.format("%d°", viewModel.get1cReading().getTemperature()));
//                ((TextView) getView().findViewById(R.id.text_1c_percent)).setText(
//                        String.format("%.2f", viewModel.getFirstCrackPercent()) + "%");
//            }
        }
    };

    final Observer<List<CrackReadingEntity>> crackObserver = new Observer<List<CrackReadingEntity>>() {

        /**
         * Called when the data is changed.
         *
         * @param crackReadingEntities The new data
         */
        @Override
        public void onChanged(List<CrackReadingEntity> crackReadingEntities) {
            // set first crack info
            CrackReadingEntity firstCrack = null;
            for(CrackReadingEntity c : crackReadingEntities) { // find the last 1C
                if(c.getCrackNumber() == 1) firstCrack = c;
            }
            if(firstCrack != null && firstCrack.hasOccurred()) {
                ((TextView) getView().findViewById(R.id.text_1c_time)).setText(
                        String.format("%d:%d",
                                firstCrack.getSeconds() / 60, // minutes
                                firstCrack.getSeconds() % 60)); // seconds
                ((TextView) getView().findViewById(R.id.text_1c_temperature)).setText(
                        String.format("%d°", firstCrack.getTemperature()));
//                    float percent = (float) firstCrack.getSeconds() / ((float) viewModel.getElapsed()) * 100;
//                    ((TextView) getView().findViewById(R.id.text_1c_percent)).setText(
//                        String.format("%.2f", percent) + "%");
            }
        }
    };

    final Observer<RoastEntity> roastObserver = new Observer<RoastEntity>() {

        /**
         * Called when the data is changed.
         *
         * @param roastEntity The new data
         */
        @Override
        public void onChanged(RoastEntity roastEntity) {
            // todo doing this in Tick now...remove
//            if(viewModel.firstCrackOccurred()) {
//                ((TextView) getView().findViewById(R.id.text_1c_percent)).setText(
//                        String.format("%.2f", viewModel.getFirstCrackPercent()) + "%");
//            }
        }
    };

    public RoastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided roastId. If no roastId
     * is provided, a new roast is started.
     *
     * @param roastId the ID of the roast to load.
     * @return A new instance of fragment RoastFragment.
     */
//    public static RoastFragment newInstance(int roastId) { // todo probably don't need the roastId stuff
//        RoastFragment fragment = new RoastFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt(RoastViewModel.KEY_ROAST_ID, roastId);
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * onAttach(Activity) and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     *
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * <p>Any restored child fragments will be created before the base
     * <code>Fragment.onCreate</code> method returns.</p>
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);             //scoped to parent activity
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null. This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>A default View can be returned by calling Fragment(int) in your
     * constructor. Otherwise, this method returns null.
     *
     * <p>It is recommended to <strong>only</strong> inflate the layout in this method and move
     * logic that operates on the returned View to {@link #onViewCreated(View, Bundle)}.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roast, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // The default ViewModel factory provides the appropriate SavedStateHandle to your ViewModel
        //  see https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate
        viewModel = new ViewModelProvider(requireActivity()).get(RoastViewModel.class); // this is all we need to do it

        initControls(view);

        // Create the observers that updates the UI.
        viewModel.getReadings().observe(getViewLifecycleOwner(), readingsObserver);
        viewModel.getCracks().observe(getViewLifecycleOwner(), crackObserver);
        viewModel.getRoast().observe(getViewLifecycleOwner(), roastObserver);

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

//        setPowerRadioButton(viewModel.getSettings().getStartingPower(), view); // todo need to get this from settings in the model

        // set tick listener
        mChronometerRoastTime.setOnChronometerTickListener(chronometer -> {
            // fire five seconds before each time increment
            if((viewModel.getElapsed() + 5) % viewModel.getSettings().getTemperatureCheckFrequency() == 0) {
                queryTemperature(REQUEST_CODE_QUERY_TEMPERATURE);
            }
            viewModel.incrementSeconds();

            // update first crack percentage
            if(viewModel.firstCrackOccurred()) {
//                ((TextView) view.findViewById(R.id.text_1c_percent)).setText(
//                        String.format("%.2f", viewModel.getFirstCrackPercent()) + "%");
                ((TextView) getActivity().findViewById(R.id.text_1c_percent_floating))
                        .setVisibility(View.VISIBLE);
                ((TextView) getActivity().findViewById(R.id.text_1c_percent_floating))
                        .setText(String.format("%.2f", viewModel.getFirstCrackPercent()) + "%");
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processRecognizerResults(Intent data, boolean isFirstCrack) {
        String stringCurrTemp = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                .get(0).replaceAll("[^0-9]", "");

        if(!viewModel.recordTemperature(stringCurrTemp)) {
            if(isFirstCrack) {
                queryTemperature(REQUEST_CODE_1C_CLICKED);
            } else {
                queryTemperature(REQUEST_CODE_QUERY_TEMPERATURE);
            }
        }

        if(isFirstCrack) viewModel.set1c();
    }

    private void toggleRoast(View view) {
        if (!viewModel.isRunning()) {
            startRoast(view);
        } else {
            endRoast();
        }
    }

    public void startRoast(View view) {
        int chronoAddend =  -(viewModel.getSettings().getRoastTimeInSecAddend() * 1000);
        long startTime = elapsedRealtime() + chronoAddend;
        viewModel.setStartTime(startTime);
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