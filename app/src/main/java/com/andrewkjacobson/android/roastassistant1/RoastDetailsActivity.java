package com.andrewkjacobson.android.roastassistant1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RoastDetailsActivity extends AppCompatActivity {
    //      roast metadata:
//          - date (autofilled date widget)
//          - bean type (text...autocomplete/combobox)
//          - batch size in grams (text)
//          - yield in grams (text)
    //      - weight loss
//          - roast degree (spinner...other->listener->text)
//          - roast notes (multi-line text)
//          - tasting notes (multi-line text)
//          - roaster (autofilled text)
//          - ambient temp (autofilled text)
    //      - preheat time
    private final String DETAILS_KEY = "roast details";
    public static final String EXTRA_REPLY = "com.andrewkjacobson.android.roastassistant1.REPLY";
    private RoastDetails mDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast_details);
        mDetails = null;

        autofillDate();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        }
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
        details.setBeanType(((TextInputEditText)findViewById(R.id.autoTextBeanType)).toString());
        details.setBatchSize(Float.parseFloat(((TextInputEditText)findViewById(R.id.textDecimalBatchSize)).toString()));
        details.setYield(Float.parseFloat(((TextInputEditText)findViewById(R.id.textDecimalYield)).toString()));
        //        details.setRoastDegree(((Spinner)findViewById(R.id.spinnerRoastDegree)).get  );
        details.setRoastNotes(((TextInputEditText)findViewById(R.id.textMultiRoastNotes)).toString());
        details.setTastingNotes(((TextInputEditText)findViewById(R.id.textMultiTastingNotes)).toString());
        details.setRoaster(((TextInputEditText)findViewById(R.id.autoTextRoaster)).toString());
        details.setAmbientTemperature(Integer.valueOf(((TextInputEditText)findViewById(R.id.autoTextAmbientTemp)).toString()));
        return details;
    }

    private void autofillDate() {
        TextInputEditText textDate = findViewById(R.id.textDate);
        if(textDate.getText().length() == 0) {
            textDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()));
        }
    }
}