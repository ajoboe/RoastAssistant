package com.andrewkjacobson.android.roastassistant1.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.andrewkjacobson.android.roastassistant1.R;
import com.andrewkjacobson.android.roastassistant1.db.entity.CrackReadingEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant1.model.Reading;
import com.andrewkjacobson.android.roastassistant1.viewmodel.RoastViewModel;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;
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
    private CombinedChart chart;
    private static final int TEMPERATURE_SET_INDEX = 0;
    private static final int POWER_SET_INDEX = 1;
    private static final int FIRST_CRACK_SET_INDEX = 2;

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
            if(readings != null && !readings.isEmpty()) {
                updateGraph(readings.get(readings.size()-1));
            }
        }
    };

    final Observer<List<CrackReadingEntity>> crackObserver = new Observer<List<CrackReadingEntity>>() {
        @Override
        public void onChanged(List<CrackReadingEntity> crackReadingEntities) {
            graphFirstCrack(crackReadingEntities);
        }
    };

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
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // The default ViewModel factory provides the appropriate SavedStateHandle to your ViewModel
        //  see https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate
        viewModel = new ViewModelProvider(requireActivity()).get(RoastViewModel.class); // this is all we need to do it
        initGraph();
        viewModel.getReadings().observe(getViewLifecycleOwner(), readingObserver);
        viewModel.getCracks().observe(getViewLifecycleOwner(), crackObserver);
    }

    // ****************
    // PRIVATE METHODS
    // ****************

    public void initGraph() {
        chart = getView().findViewById(R.id.chart);
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.BAR,
        });

        // disable description
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);
//        chart.setScaleXEnabled(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.DKGRAY);

        // add empty data
        CombinedData data = new CombinedData();
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
//        l.setTypeface(tfLight);
        l.setTextColor(Color.WHITE);

        XAxis xAxis = chart.getXAxis();
//        xl.setTypeface(tfLight);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
//        xAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return Math.floor(value / 60) + ":" + value%60 ;
//            }
//        });
        xAxis.setEnabled(true);

        // axis of temperature (left)
        YAxis leftAxis = chart.getAxisLeft();
//        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(viewModel.getSettings().getMaxGraphTemperature());
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        // axis of power (right)
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setAxisMaximum(100f);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setGranularity(25f);
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(true);

//        mGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
//            @Override
//            public String formatLabel(double value, boolean isValueX) {
//                if (isValueX) {
//                    // show normal x values
//                    return String.format("%d:%02d",  (int)value/60, (int)value%60);
//                } else {
//                    // convert seconds to minutes:seconds
////                    return super.formatLabel(value, isValueX) + " €";
//                    return super.formatLabel(value, isValueX);
//                }
//            }
//        });
//        try {
//
//            // GRAPH SETTINGS
//            mGraph.setVisibility(View.VISIBLE);
//            mGraph.getViewport().setScalable(false);
//            mGraph.getViewport().setScrollable(false);
////            mGraph.getGridLabelRenderer().setVerticalLabelsVisible(true);
//            mGraph.getGridLabelRenderer().setHorizontalLabelsVisible(true);
//
//            // TEMPERATURE SERIES
//            mGraphSeriesTemperature = new LineGraphSeries<>();
//            mGraphSeriesTemperature.setColor(Color.rgb(173, 216, 230)); // light blue
//            mGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.rgb(173, 216, 230)); // light blue
//            mGraph.getGridLabelRenderer().setLabelVerticalWidth(50);
//            mGraph.addSeries(mGraphSeriesTemperature);
//            mGraph.getViewport().setYAxisBoundsManual(true);
//            mGraph.getViewport().setMinY(viewModel.getSettings().getStartingTemperature() - 20);
//            mGraph.getViewport().setMaxY(mMaxGraphTemperature);
//            mGraph.getViewport().setXAxisBoundsManual(true);
//            mGraph.getViewport().setMinX(0);
//            mGraph.getViewport().setMaxX(viewModel.getSettings().getExpectedRoastLength());
////            mGraph.getViewport().setScalable(false); // if true, messes with the time
//
//
//            // POWER SERIES
//            mGraphSeriesPower = new LineGraphSeries<>();
//            mGraphSeriesPower.setColor(Color.RED);
//            mGraph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);
//            mGraph.getGridLabelRenderer().setSecondScaleLabelVerticalWidth(50); // todo doesn't do anything
//            mGraph.getSecondScale().addSeries(mGraphSeriesPower);
//            mGraph.getSecondScale().setMinY(0);
//            mGraph.getSecondScale().setMaxY(100);
//        } catch (IllegalArgumentException e) {
////            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//            System.err.println("graph failed to initialize.");
//        }
    }

    public void updateGraph(Reading reading) {
        CombinedData combinedData = chart.getData();

        if(combinedData.getLineData() == null) {
            combinedData.setData(new LineData());
        }

        if(combinedData != null) {
            ILineDataSet temperatureSet = combinedData.getLineData().getDataSetByIndex(TEMPERATURE_SET_INDEX);
            ILineDataSet powerSet = combinedData.getLineData().getDataSetByIndex(POWER_SET_INDEX);
            // temperatureSet.addEntry(...); // can be called as well

            // initialize data sets as needed
            if(temperatureSet == null) {
                temperatureSet = createTemperatureSet();
                combinedData.getLineData().addDataSet(temperatureSet);
//                combinedData.addDataSet(temperatureSet);
            }
            if(powerSet == null) {
                powerSet = createPowerSet();
                combinedData.getLineData().addDataSet(powerSet);
//                combinedData.addDataSet(powerSet);
            }

            // add temperature and power
            combinedData.getLineData().addEntry(new Entry(
                    reading.getSeconds(), reading.getTemperature()), TEMPERATURE_SET_INDEX);
            combinedData.getLineData().addEntry(new Entry(
                    reading.getSeconds(), reading.getPower()), POWER_SET_INDEX);

            combinedData.getLineData().notifyDataChanged();
            combinedData.notifyDataChanged();
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMinimum(0);
            chart.setVisibleXRangeMaximum(viewModel.getSettings().getExpectedRoastLength());
            chart.setVisibleYRange(50, viewModel.getSettings().getMaxGraphTemperature(), YAxis.AxisDependency.LEFT);
            chart.setVisibleYRange(0, 100, YAxis.AxisDependency.RIGHT);

            // move to the latest entry
            chart.moveViewToX(combinedData.getEntryCount());
            chart.invalidate();
            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f, AxisDependency.LEFT);

        } else {
            throw new RuntimeException("chartData was null");
        }
// END MPCHART-----
//
//        int elapsed;
//        int expectedRoastLength;
//        if(viewModel.getRoast().getValue() != null) {
//            elapsed = viewModel.getElapsed();
//            expectedRoastLength = viewModel.getSettings().getExpectedRoastLength();
//        } else {
//            elapsed = 0;
//            expectedRoastLength = 0;
//        }
//        try {
////            mGraphSeriesTemperature.
//            mGraphSeriesTemperature.appendData(new DataPoint(
//                            reading.getSeconds(),
//                            reading.getTemperature()),
//                    false, 99999);
//            mGraphSeriesPower.appendData(new DataPoint(
//                            reading.getSeconds(),
//                            reading.getPower()),
//                    false, 99999); // if scrollToEnd is true, shows negatives in the beginning
//            if(elapsed > expectedRoastLength) mGraph.getViewport().scrollToEnd();
//        } catch (IllegalArgumentException e) {
//            System.err.println("failed to update graph.");
//        }
    }

    private LineDataSet createGeneralSet() {
        LineDataSet set = new LineDataSet(null, "");
        set.setLineWidth(2f);
        set.setCircleRadius(1f);
        set.setCircleColor(Color.WHITE);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);

        return set;
    }

    private LineDataSet createTemperatureSet() {
        LineDataSet set = createGeneralSet();
        set.setLabel("Temperature");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setDrawValues(true);
        return set;
    }

    private LineDataSet createPowerSet() {
        LineDataSet set = createGeneralSet();
        set.setLabel("Power");
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set.setColor(ColorTemplate.rgb("#FF0000"));
        set.setDrawValues(false);
        return set;
    }

    private BarDataSet createFirstCrackSet() {
        BarDataSet set = new BarDataSet(null, "First Crack");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.rgb("#00FF00"));
        // todo setBarWidth(4f) width on the BarData
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(12f);
        set.setDrawValues(true);
        return set;
    }

    private void graphFirstCrack(List<CrackReadingEntity> crackReadingEntities) {
        BarData chartData = chart.getData().getBarData();

        if(chartData != null) {
            IBarDataSet firstCrackSet = chartData.getDataSetByIndex(FIRST_CRACK_SET_INDEX);
            firstCrackSet.setDrawValues(true);
            // temperatureSet.addEntry(...); // can be called as well

            // initialize data sets as needed
            if(firstCrackSet == null) {
                firstCrackSet = createFirstCrackSet();
                chartData.addDataSet(firstCrackSet);
            }

            // add first crack entry
            CrackReadingEntity firstCrack = null;
            for(CrackReadingEntity c : crackReadingEntities) { // find the last 1C
                if(c.getCrackNumber() == 1) firstCrack = c;
            }
            if(firstCrack != null && firstCrack.hasOccurred()) {





                // todo add time and temp labels
                chartData.addEntry(new Entry(firstCrack.getSeconds(),
                        firstCrack.getTemperature()), FIRST_CRACK_SET_INDEX);
                chartData.notifyDataChanged();
                chart.notifyDataSetChanged();
                // limit the number of visible entries
//            chart.setVisibleXRangeMinimum(0);
//            chart.setVisibleXRangeMaximum(viewModel.getSettings().getExpectedRoastLength());
//            chart.setVisibleYRange(50, viewModel.getSettings().getMaxGraphTemperature(), YAxis.AxisDependency.LEFT);
//            chart.setVisibleYRange(0, 100, YAxis.AxisDependency.RIGHT);
            }
            // END MPChart--------
//
//
//        CrackReadingEntity firstCrack = null;
//        for(CrackReadingEntity c : crackReadingEntities) { // find the last 1C
//            if(c.getCrackNumber() == 1) firstCrack = c;
//        }
//        if(firstCrack != null && firstCrack.hasOccurred()) {
//            BarGraphSeries<DataPoint> series = new BarGraphSeries();
//            series.appendData(new DataPoint(
//                    firstCrack.getSeconds(), mMaxGraphTemperature), false, 99);
//            series.setDataWidth(5);
//            series.setColor(Color.GREEN);
//            mGraph.addSeries(series);
//            double firstCrackLineLeftX = mGraph.getSeries().get(mGraph.getSeries().indexOf(series)).getLowestValueX();
//
//            Canvas canvas = new Canvas();
//            mGraph.getViewport().draw(canvas);
        }
    }
}