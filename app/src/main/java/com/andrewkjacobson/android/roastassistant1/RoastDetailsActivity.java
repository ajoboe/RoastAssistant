package com.andrewkjacobson.android.roastassistant1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class RoastDetailsActivity extends AppCompatActivity {
    private final String DETAILS_KEY = "roast details";
    public static final String EXTRA_REPLY = "com.andrewkjacobson.android.roastassistant1.REPLY";
    private RoastDetails mDetails;
    private List<RoastDetails> mAllRoasts;
    private RoastDetailsViewModel mRoastDetailsViewModel;
    private ArrayAdapter<CharSequence> mSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast_details);
        mDetails = null;
        mRoastDetailsViewModel = new ViewModelProvider(this).get(RoastDetailsViewModel.class);
        mRoastDetailsViewModel.getAllRoasts().observe(this, new Observer<List<RoastDetails>>() {
            @Override
            public void onChanged(List<RoastDetails> roastDetails) {
                mAllRoasts = roastDetails;
                if(roastDetails.size() > 0) populateUI(roastDetails.get(roastDetails.size() - 1));
            }
        });

        // todo only do this if all fields are empty
        autofillDate();

        // action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // roast degree spinner
        Spinner spinner = findViewById(R.id.spinnerRoastDegree);
//        if(spinner != null) spinner.setOnItemSelectedListener(this);
        mSpinnerAdapter = ArrayAdapter
                .createFromResource(this, R.array.roast_degree_spinner_labels_array,
                        android.R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(spinner != null) spinner.setAdapter(mSpinnerAdapter);

        // save button
        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                mDetails = fetchDetailsFromControls();
                replyIntent.putExtra(EXTRA_REPLY, mDetails);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });

        // restore state
        if(savedInstanceState != null) {
            mDetails = savedInstanceState.getParcelable(DETAILS_KEY);
            populateUI(mDetails);
        }
    }

    private void populateUI(RoastDetails details) {
        // todo Eventually populate cards with all roasts from mAllRoasts
        ((TextInputEditText)findViewById(R.id.textDate)).setText(details.getDate());
        ((TextInputEditText)findViewById(R.id.autoTextBeanType)).setText(details.getBeanType());
        ((TextInputEditText)findViewById(R.id.textDecimalBatchSize))
                .setText(String.format("%.2f", details.getBatchSize()));
        ((TextInputEditText)findViewById(R.id.textDecimalYield))
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

    private RoastDetails fetchDetailsFromControls() {
        RoastDetails details = new RoastDetails();
        details.setDate(((TextInputEditText)findViewById(R.id.textDate)).getText().toString());
        details.setBeanType(((TextInputEditText)findViewById(R.id.autoTextBeanType)).getText().toString());

        String batchSize = ((TextInputEditText)findViewById(R.id.textDecimalBatchSize)).getText().toString();
        if(batchSize.length() > 0) details.setBatchSize(Float.parseFloat(batchSize));

        String yield = ((TextInputEditText)findViewById(R.id.textDecimalYield)).getText().toString();
        if(yield.length() > 0) details.setYield(Float.parseFloat(yield));

        details.setRoastDegree(((Spinner)findViewById(R.id.spinnerRoastDegree)).getSelectedItem().toString());
        details.setRoastNotes(((TextInputEditText)findViewById(R.id.textMultiRoastNotes)).getText().toString());
        details.setTastingNotes(((TextInputEditText)findViewById(R.id.textMultiTastingNotes)).getText().toString());
        details.setRoaster(((TextInputEditText)findViewById(R.id.autoTextRoaster)).getText().toString());

        String ambient = ((TextInputEditText)findViewById(R.id.autoTextAmbientTemp)).getText().toString();
        if(ambient.length() >0 ) details.setAmbientTemperature(Integer.valueOf(ambient));

        return details;
    }

    private void autofillDate() {
        TextInputEditText textDate = findViewById(R.id.textDate);
        if(textDate.getText().length() == 0) {
            textDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()));
        }
    }
//
//    /**
//     * <p>Callback method to be invoked when an item in this view has been
//     * selected. This callback is invoked only when the newly selected
//     * position is different from the previously selected position or if
//     * there was no selected item.</p>
//     * <p>
//     * Implementers can call getItemAtPosition(position) if they need to access the
//     * data associated with the selected item.
//     *
//     * @param parent   The AdapterView where the selection happened
//     * @param view     The view within the AdapterView that was clicked
//     * @param position The position of the view in the adapter
//     * @param id       The row id of the item that is selected
//     */
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////        mDetails.setRoastDegree(parent.getItemAtPosition(position).toString());
////        Spinner spinner = ;
//        Toast.makeText(getApplicationContext(),
//                ((Spinner)findViewById(R.id.spinnerRoastDegree)).getSelectedItem().toString()
//                , Toast.LENGTH_SHORT).show();
//    }
//
//    /**
//     * Callback method to be invoked when the selection disappears from this
//     * view. The selection can disappear for instance when touch is activated
//     * or when the adapter becomes empty.
//     *
//     * @param parent The AdapterView that now contains no selected item.
//     */
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
}