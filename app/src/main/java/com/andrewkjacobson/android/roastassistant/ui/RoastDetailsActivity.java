package com.andrewkjacobson.android.roastassistant.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.andrewkjacobson.android.roastassistant.R;
import com.andrewkjacobson.android.roastassistant.db.entity.DetailsEntity;
import com.andrewkjacobson.android.roastassistant.viewmodel.RoastViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class RoastDetailsActivity extends AppCompatActivity {
    private final String DETAILS_KEY = "roast details";
    public static final String EXTRA_REPLY = "com.andrewkjacobson.android.roastassistant.REPLY";
    private DetailsEntity mDetails;
    private ArrayAdapter<CharSequence> mSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast_details);
        mDetails = null;
        RoastViewModel viewModel = new ViewModelProvider(this).get(RoastViewModel.class);

        viewModel.getDetails().observe(this, details -> {
            if(details != null) populateUI(details);
        });

        // action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // roast degree spinner
        Spinner spinner = findViewById(R.id.spinnerRoastDegree);
        mSpinnerAdapter = ArrayAdapter
                .createFromResource(this, R.array.roast_degree_spinner_labels_array,
                        android.R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(spinner != null) spinner.setAdapter(mSpinnerAdapter);

        // calculate weight loss
        ((TextInputEditText)findViewById(R.id.text_decimal_yield)).addTextChangedListener(new WeightWatcher());
        ((TextInputEditText)findViewById(R.id.text_decimal_batch_size)).addTextChangedListener(new WeightWatcher());

        // save button
        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(v -> saveAndReturn());

        // restore state
        if(savedInstanceState != null) {
            mDetails = savedInstanceState.getParcelable(DETAILS_KEY);
            populateUI(mDetails);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        saveAndReturn();
    }

    private void saveAndReturn()  {
        Intent replyIntent = new Intent();
        mDetails = fetchDetailsFromControls();
        replyIntent.putExtra(EXTRA_REPLY, mDetails);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    private void populateUI(DetailsEntity details) {
        // todo Eventually populate cards with all roasts from mAllRoasts
        ((TextInputEditText)findViewById(R.id.textDate)).setText(details.getDate());
        autofillDate(); // if date is empty, fill it in

        ((TextInputEditText)findViewById(R.id.autoTextBeanType)).setText(details.getBeanType());
        ((TextInputEditText)findViewById(R.id.text_decimal_batch_size))
                .setText(String.format("%.2f", details.getBatchSize()));
        ((TextInputEditText)findViewById(R.id.text_decimal_yield))
                .setText(String.format("%.2f", details.getYield()));
        ((TextInputEditText)findViewById(R.id.textDecimalWeightLoss))
                .setText(String.format("%.2f%%", 100 * details.getWeightLossPercentage()));
        ((Spinner)findViewById(R.id.spinnerRoastDegree))
                .setSelection(mSpinnerAdapter.getPosition(details.getRoastDegree()));
        ((TextInputEditText)findViewById(R.id.textMultiRoastNotes)).setText(details.getRoastNotes());
        ((TextInputEditText)findViewById(R.id.textMultiTastingNotes)).setText(details.getTastingNotes());
        ((TextInputEditText)findViewById(R.id.autoTextRoaster)).setText(details.getRoaster());
        ((TextInputEditText)findViewById(R.id.autoTextAmbientTemp)).setText(Integer.toString(details.getAmbientTemperature()));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mDetails = fetchDetailsFromControls();
        outState.putParcelable(DETAILS_KEY, mDetails);
    }

    private DetailsEntity fetchDetailsFromControls() {
        DetailsEntity details = new DetailsEntity();
        details.setDate(String.valueOf(((TextInputEditText)findViewById(R.id.textDate)).getText()));
        details.setBeanType(String.valueOf(((TextInputEditText)findViewById(R.id.autoTextBeanType)).getText()));

        String batchSize = String.valueOf(((TextInputEditText)findViewById(R.id.text_decimal_batch_size)).getText());
        if(batchSize.length() > 0) details.setBatchSize(Float.parseFloat(batchSize));

        String yield = String.valueOf(((TextInputEditText)findViewById(R.id.text_decimal_yield)).getText());
        if(yield.length() > 0) details.setYield(Float.parseFloat(yield));

        details.setRoastDegree(((Spinner)findViewById(R.id.spinnerRoastDegree)).getSelectedItem().toString());
        details.setRoastNotes(String.valueOf(((TextInputEditText)findViewById(R.id.textMultiRoastNotes)).getText()));
        details.setTastingNotes(String.valueOf(((TextInputEditText)findViewById(R.id.textMultiTastingNotes)).getText()));
        details.setRoaster(String.valueOf(((TextInputEditText)findViewById(R.id.autoTextRoaster)).getText()));

        String ambient = String.valueOf(((TextInputEditText)findViewById(R.id.autoTextAmbientTemp)).getText());
        if(ambient.length() >0 ) details.setAmbientTemperature(Integer.parseInt(ambient));

        return details;
    }

    private void autofillDate() {
        TextInputEditText textDate = findViewById(R.id.textDate);
        if(textDate == null) return;
        if(Objects.requireNonNull(textDate.getText()).length() == 0) {
            textDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()));
        }
    }

    private class WeightWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ((TextInputEditText)findViewById(R.id.textDecimalWeightLoss))
                    .setText(String.format("%.2f%%", 100 * fetchDetailsFromControls().getWeightLossPercentage()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}