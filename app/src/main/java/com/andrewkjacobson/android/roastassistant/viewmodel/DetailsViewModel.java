package com.andrewkjacobson.android.roastassistant.viewmodel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import com.andrewkjacobson.android.roastassistant.RoastRepository;
import com.andrewkjacobson.android.roastassistant.db.entity.DetailsEntity;

import static com.andrewkjacobson.android.roastassistant.ui.RoastActivity.ROAST_ID_KEY;

/**
 * This ViewModel corresponds to the RoastDetailsActivity
 */
public class DetailsViewModel extends AndroidViewModel {
    private final Application application;
    private final RoastRepository repository;
    private LiveData<DetailsEntity> mDetailsLiveData;

    public DetailsViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.application = application;
        this.repository = new RoastRepository(application);
        long roastId = 0;
        if(savedStateHandle != null && savedStateHandle.contains(ROAST_ID_KEY)) {
            roastId = savedStateHandle.get(ROAST_ID_KEY);
        }

    }

    public void recordDetails(DetailsEntity details, long roastId) {
        details.setRoastId(roastId);
        repository.insert(details);
    }


    public LiveData<DetailsEntity> getDetails(long roastId) {
        if(mDetailsLiveData == null) {
            mDetailsLiveData = repository.getDetailsLiveData(roastId);
        }
        return mDetailsLiveData;
    }
}
