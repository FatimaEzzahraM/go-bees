package com.davidmiguel.gobees.addeditapiary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.davidmiguel.gobees.Injection;
import com.davidmiguel.gobees.R;
import com.davidmiguel.gobees.data.source.cache.GoBeesRepository;
import com.davidmiguel.gobees.utils.ActivityUtils;

/**
 * Add / edit apiary activity.
 */
public class AddEditApiaryActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_APIARY = 1;
    public static final int NEW_APIARY = -1;

    private Fragment addEditApiaryFragment;
    private GoBeesRepository goBeesRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addeditapiary_act);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // Get apiary id (if edit)
        long apiaryId = getIntent()
                .getLongExtra(AddEditApiaryFragment.ARGUMENT_EDIT_APIARY_ID, NEW_APIARY);

        // Add fragment to the activity and set title
        addEditApiaryFragment = getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (addEditApiaryFragment == null) {
            addEditApiaryFragment = AddEditApiaryFragment.newInstance();
            if (getIntent().hasExtra(AddEditApiaryFragment.ARGUMENT_EDIT_APIARY_ID)) {
                // If edit -> set edit title
                if (actionBar != null) {
                    actionBar.setTitle(R.string.edit_apiary);
                }
                Bundle bundle = new Bundle();
                bundle.putString(AddEditApiaryFragment.ARGUMENT_EDIT_APIARY_ID, apiaryId + "");
                addEditApiaryFragment.setArguments(bundle);
            } else {
                // If new -> set add title
                if (actionBar != null) {
                    actionBar.setTitle(R.string.add_apiary);
                }
            }
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditApiaryFragment, R.id.contentFrame);
        }

        // Init db
        goBeesRepository = Injection.provideApiariesRepository();
        goBeesRepository.openDb();

        // Create the presenter
        new AddEditApiaryPresenter(goBeesRepository,
                (AddEditApiaryContract.View) addEditApiaryFragment, apiaryId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close database
        goBeesRepository.closeDb();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (addEditApiaryFragment != null) {
            addEditApiaryFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
