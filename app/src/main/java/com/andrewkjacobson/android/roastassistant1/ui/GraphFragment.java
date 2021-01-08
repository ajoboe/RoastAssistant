package com.andrewkjacobson.android.roastassistant1.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewkjacobson.android.roastassistant1.R;
import com.andrewkjacobson.android.roastassistant1.db.entity.CrackReadingEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant1.model.Reading;
import com.andrewkjacobson.android.roastassistant1.viewmodel.RoastViewModel;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphFragment extends Fragment {

    private RoastViewModel viewModel;

    GraphView mGraph;
    LineGraphSeries<DataPoint> mGraphSeriesTemperature;
    LineGraphSeries<DataPoint> mGraphSeriesPower;

    // todo this stuff needs to go
    private int mExpectedRoastLength = 60 * 12; // todo should be a setting
    private final int mMaxGraphTemperature = 400; // todo should be a setting

    final Observer<List<ReadingEntity>> readingObserver = new Observer<List<ReadingEntity>>() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onChanged(@Nullable final List<ReadingEntity> readings) {
            // todo update the graph
            if(readings != null && !readings.isEmpty()) {
                updateGraph(readings.get(readings.size()-1));
            }
//            // check if it's first crack // todo should be done in crack observer, not here
//            if(viewModel.isFirstCrack()) {
//                graphFirstCrack();
//            }
        }
    };

    final Observer<List<CrackReadingEntity>> crackObserver = new Observer<List<CrackReadingEntity>>() {
        @Override
        public void onChanged(List<CrackReadingEntity> crackReadingEntities) {
            graphFirstCrack(crackReadingEntities);
        }
    };
//    final Observer<RoastEntity> roastObserver = new Observer<RoastEntity>() {
//        @Override
//        public void onChanged(@Nullable final RoastEntity roast) {
//            // todo update the graph
//            if(roast != null) {
//                updateGraph(roast.getCurrentReading());
//            }
//
//            // check if it's first crack
//            if(viewModel.isFirstCrack()) {
//                graphFirstCrack();
//            }
//
//
//        }
//    };

    public GraphFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GraphFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphFragment newInstance(String param1, String param2) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false);
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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // The default ViewModel factory provides the appropriate SavedStateHandle to your ViewModel
        //  see https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate
        viewModel = new ViewModelProvider(requireActivity()).get(RoastViewModel.class); // this is all we need to do it
        initGraph();
//        updateGraph(viewModel.getRoast().getValue().getCurrentReading()); // todo this should be taken care of by an observer
        viewModel.getReadings().observe(getViewLifecycleOwner(), readingObserver);

    }






    // ****************
    // PRIVATE METHODS
    // ****************

    public void initGraph() {
        mGraph = getView().findViewById(R.id.graph);
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
            mGraph.getViewport().setMinY(viewModel.getSettings().getStartingTemperature() - 20);
            mGraph.getViewport().setMaxY(mMaxGraphTemperature);
            mGraph.getViewport().setXAxisBoundsManual(true);
            mGraph.getViewport().setMinX(0);
            mGraph.getViewport().setMaxX(viewModel.getSettings().getExpectedRoastLength());
//            mGraph.getViewport().setScalable(false); // if true, messes with the time


            // POWER SERIES
            mGraphSeriesPower = new LineGraphSeries<>();
            mGraphSeriesPower.setColor(Color.RED);
            mGraph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);
            mGraph.getGridLabelRenderer().setSecondScaleLabelVerticalWidth(50); // todo doesn't do anything
            mGraph.getSecondScale().addSeries(mGraphSeriesPower);
            mGraph.getSecondScale().setMinY(0);
            mGraph.getSecondScale().setMaxY(100);
        } catch (IllegalArgumentException e) {
//            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            System.err.println("graph failed to initialize.");
        }
    }


    // todo this should just get called by an observer
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateGraph(Reading reading) {
        int elapsed;
        int expectedRoastLength;
        if(viewModel.getRoast().getValue() != null) {
            elapsed = viewModel.getRoast().getValue().getElapsed();
            expectedRoastLength = viewModel.getSettings().getExpectedRoastLength();
        } else {
            elapsed = 0;
            expectedRoastLength = 0;
        }
        try {
            mGraphSeriesTemperature.appendData(new DataPoint(
                            reading.getSeconds(),
                            reading.getTemperature()),
                    false, 99999);
            mGraphSeriesPower.appendData(new DataPoint(
                            reading.getSeconds(),
                            reading.getPower()),
                    false, 99999); // if scrollToEnd is true, shows negatives in the beginning
            if(elapsed > expectedRoastLength) mGraph.getViewport().scrollToEnd();
        } catch (IllegalArgumentException e) {
            System.err.println("failed to update graph.");
        }
    }

    private void graphFirstCrack(List<CrackReadingEntity> crackReadingEntities) {
        CrackReadingEntity firstCrack = null;
        for(CrackReadingEntity c : crackReadingEntities) { // find the last 1C
            if(c.getCrackNumber() == 1) firstCrack = c;
        }
        if(firstCrack != null && firstCrack.hasOccurred()) {
            // todo add time and temp labels
            BarGraphSeries<DataPoint> series = new BarGraphSeries();
            series.appendData(new DataPoint(
                    firstCrack.getSeconds(), mMaxGraphTemperature), false, 99);
            series.setDataWidth(5);
            series.setColor(Color.GREEN);
            mGraph.addSeries(series);
        }
    }
}