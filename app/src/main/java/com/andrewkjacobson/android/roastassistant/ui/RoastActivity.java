package com.andrewkjacobson.android.roastassistant.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.andrewkjacobson.android.roastassistant.R;
import com.andrewkjacobson.android.roastassistant.viewmodel.RoastViewModel;

public class RoastActivity extends AppCompatActivity {
    public static final String ROAST_ID_KEY = "roast id";
    private static final int REQUEST_CODE_ROAST_DETAILS_ACTIVITY = 30;

    private RoastViewModel roastViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roast);
        roastViewModel = new ViewModelProvider(this).get(RoastViewModel.class);

        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.graph_fragment_container_view, GraphFragment.class, null)
                .commit();
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.roast_fragment_container_view, RoastFragment.class, null)
                .commit();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ROAST_DETAILS_ACTIVITY:
                if(resultCode == RESULT_OK && data != null)
                    roastViewModel.setDetails(data.getParcelableExtra(RoastDetailsActivity.EXTRA_REPLY));
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ROAST_ID_KEY, roastViewModel.getRoastId());
    }

    private void showRoastDetails() {
        Intent roastDetailsIntent = new Intent(this, RoastDetailsActivity.class);
        roastDetailsIntent.putExtra(ROAST_ID_KEY, roastViewModel.getRoastId());
        startActivityForResult(roastDetailsIntent, REQUEST_CODE_ROAST_DETAILS_ACTIVITY);
    }


    private void showNewRoastDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Clear the current roast and start a new one?");
        alertBuilder.setPositiveButton("Yes", (dialog, which) -> newRoast());
        alertBuilder.setNegativeButton("No", (dialog, which) -> Toast.makeText(getApplicationContext(), "New roast canceled.", Toast.LENGTH_SHORT).show());
        alertBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void newRoast() {

        if (Build.VERSION.SDK_INT >= 11) {
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
}