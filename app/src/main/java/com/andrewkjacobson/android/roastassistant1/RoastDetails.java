package com.andrewkjacobson.android.roastassistant1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RoastDetails extends AppCompatActivity {
    //      roast metadata:
//          - date (autofilled date widget)
//          - bean type (text...autocomplete/combobox)
//          - batch size in grams (text)
//          - yield in grams (text)
//          - roast degree (spinner...other->listener->text)

//          - roast notes (multi-line text)
//          - tasting notes (multi-line text)
//          - roaster (autofilled text)
//          - ambient temp (autofilled text)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast_details);
    }
}