package com.andrewkjacobson.android.roastassistant1.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.andrewkjacobson.android.roastassistant1.R;
import com.andrewkjacobson.android.roastassistant1.db.entity.CrackReadingEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant1.model.Reading;
import com.andrewkjacobson.android.roastassistant1.viewmodel.RoastViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;

public class GraphFragment extends Fragment {

    private RoastViewModel viewModel;
    private LineChart chart;
    private CrackReadingEntity firstCrack = null;
    private Reading currentReading;
    private static final int TEMPERATURE_SET_INDEX = 0;
    private static final int POWER_SET_INDEX = 1;
    private static final int FIRST_CRACK_SET_INDEX = 2;

    final Observer<RoastEntity> roastObserver = roast -> {
        if(roast != null && roast.isRunning()) {
            plotNew(currentReading);
        }
    };

    final Observer<List<ReadingEntity>> readingObserver = readings -> {
        if(readings != null && !readings.isEmpty()) {
            currentReading = readings.get(readings.size() - 1);
            if (viewModel.isRunning()) {
                plotNew(currentReading);
            }
        }
    };

    final Observer<List<CrackReadingEntity>> crackObserver = this::plotFirstCrack;

    public GraphFragment() {
        // Required empty public constructor
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
        viewModel.getReadingsLiveData().observe(getViewLifecycleOwner(), readingObserver);
        viewModel.getCracksLiveData().observe(getViewLifecycleOwner(), crackObserver);
        viewModel.getRoastLiveData().observe(getViewLifecycleOwner(), roastObserver);
        initGraph();
    }

    private void initGraph() {
        chart = getView().findViewById(R.id.chart);

        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(true);
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.DKGRAY);

        // add empty data
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        // legend setup
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setGranularity(2); // will be reset when data is added
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return secondsToMinSec(value);
            }
        });
        xAxis.setEnabled(true);

        // axis of temperature (left)
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(viewModel.getSettings().getMaxGraphTemperature());
        leftAxis.setAxisMinimum(viewModel.getSettings().getMinGraphTemperature());
        leftAxis.setDrawGridLines(true);

        // axis of power (right)
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setAxisMaximum(101f);
        rightAxis.setAxisMinimum(0); // 0% power
        rightAxis.setGranularity(25f);
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(true);
    }

    public void plotNew(Reading reading) {
//        if(!viewModel.isRunning()) return;

        LineData lineData = chart.getData();

        // todo maybe I should have an observer for isRunning in the VM
        if(viewModel.isRunning()) chart.getXAxis().setGranularityEnabled(false);

        if(lineData != null) {
            ILineDataSet temperatureSet = lineData.getDataSetByIndex(TEMPERATURE_SET_INDEX);
            ILineDataSet powerSet = lineData.getDataSetByIndex(POWER_SET_INDEX);

            // initialize data sets as needed
            if(temperatureSet == null) {
                temperatureSet = createTemperatureSet();
                lineData.addDataSet(temperatureSet);
            }
            if(powerSet == null) {
                powerSet = createPowerSet();
                lineData.addDataSet(powerSet);
            }

            // add temperature and power
            lineData.addEntry(new Entry(
                    reading.getSeconds(), reading.getTemperature()), TEMPERATURE_SET_INDEX);
            lineData.addEntry(new Entry(
                    reading.getSeconds(), reading.getPower()), POWER_SET_INDEX);

            lineData.notifyDataChanged();
            chart.notifyDataSetChanged();

            // todo remove hardcoded 0s and 100 (move to settings)
            // limit the number of visible entries
            chart.setVisibleXRangeMinimum(chart.getXRange() + 2);
            chart.setVisibleXRangeMaximum(viewModel.getSettings().getExpectedRoastLength());
            chart.setVisibleYRange(
                    viewModel.getSettings().getMinGraphTemperature(),
                    viewModel.getSettings().getMaxGraphTemperature(),
                    YAxis.AxisDependency.LEFT);
            chart.setVisibleYRange(
                    0,
                    101,
                    YAxis.AxisDependency.RIGHT);

            chart.invalidate(); // updates the chart
        } else {
            Log.w(this.getClass().toString(), "lineData was null in GraphFragment.updateGraph()");
        }
    }

    private void plotFirstCrack(List<CrackReadingEntity> crackReadingEntities) {
        // find first crack (the last one logged)
        for(CrackReadingEntity c : crackReadingEntities) {
            if(c.getCrackNumber() == 1) firstCrack = c;
        }
        // check if first crack happened
        if(firstCrack != null && firstCrack.hasOccurred()) {
            LineData lineData = chart.getData();
            if(lineData != null) {
                ILineDataSet firstCrackSet = lineData.getDataSetByIndex(FIRST_CRACK_SET_INDEX);

                // initialize data sets as needed
                if (firstCrackSet == null) {
                    firstCrackSet = createFirstCrackSet();
                    lineData.addDataSet(firstCrackSet);
                }

                // add bottom point
                lineData.addEntry(new Entry(
                        firstCrack.getSeconds(),
                        viewModel.getSettings().getMinGraphTemperature()),
                        FIRST_CRACK_SET_INDEX);
                // add top point
                lineData.addEntry(new Entry(
                        firstCrack.getSeconds() + 0.00001f, // make a vertical line representing 1C
                        viewModel.getSettings().getMaxGraphTemperature()),
                        FIRST_CRACK_SET_INDEX);

                chart.setVisibleXRangeMinimum(chart.getXRange() + 5);
                lineData.notifyDataChanged();
                chart.notifyDataSetChanged();
                chart.moveViewToX(lineData.getEntryCount());
            }
            plotNew(firstCrack); // plot the temp and power lines too
        }
    }

    private LineDataSet createGeneralSet() {
        LineDataSet set = new LineDataSet(null, "");
        set.setLineWidth(2.5f);
        set.setCircleRadius(1f);
        set.setCircleColor(Color.WHITE);
        set.setFillAlpha(65);
        set.setFillColor(R.color.TemperatureColor);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(9f);
        return set;
    }

    private LineDataSet createTemperatureSet() {
        LineDataSet set = createGeneralSet();
        set.setLabel("Temperature");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(getResources().getColor(R.color.TemperatureColor));
        set.setCircleColor(getResources().getColor(R.color.TemperatureColor));
        set.setDrawValues(true);
        set.setValueTextSize(18); // todo extract to resource
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                // only label the first, last, and 1c temperature entries
                if(firstCrack != null && entry.getY() == firstCrack.getTemperature()) {
                    return String.format("%.0f°", entry.getY());
                }
                if(entry.getX() == 0 || entry.getX() == currentReading.getSeconds()) {
                    return String.format("%.0f°", entry.getY());
                }

                return "";
            }
        });

        return set;
    }

    private LineDataSet createPowerSet() {
        LineDataSet set = createGeneralSet();
        set.setLabel("Power");
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set.setColor(getResources().getColor(R.color.PowerColor));
        set.setCircleColor(getResources().getColor(R.color.PowerColor));
        set.setDrawValues(false);
        return set;
    }

    private LineDataSet createFirstCrackSet() {
        LineDataSet set = createGeneralSet();
        set.setLabel("First Crack");
        set.setLineWidth(3.5f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(getResources().getColor(R.color.FirstCrackColor));
        set.setCircleColor(getResources().getColor(R.color.FirstCrackColor));
        set.setDrawValues(true);
        set.setValueTextSize(18); // todo extract to resource
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                if(firstCrack == null) {
                    return "";
                }
                if(entry.getY() == viewModel.getSettings().getMinGraphTemperature()) {
                    return secondsToMinSec(firstCrack.getSeconds());
                }
                return "";
            }
        });
        return set;
    }
    
    private String secondsToMinSec(float seconds) {
        return String.format("%d:%02d", (int)seconds / 60, (int)seconds % 60);
    }
}