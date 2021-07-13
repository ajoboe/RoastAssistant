package com.andrewkjacobson.android.roastassistant.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.andrewkjacobson.android.roastassistant.db.entity.DetailsEntity;
import com.jraska.livedata.TestObserver;

import junit.framework.TestCase;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DetailsViewModelTest extends TestCase {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    private DetailsViewModel viewModel;
    private ViewModelStore viewModelStore;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Application application = (Application)InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        viewModel = new DetailsViewModel(application, null);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRecordDetails() throws InterruptedException {
        DetailsEntity deets = new DetailsEntity();
        deets.setBatchSize(200);
        viewModel.recordDetails(deets, 9);
        assertEquals(deets.getBatchSize(),
            TestObserver.test(viewModel.getDetails(9))
                .awaitValue()
                .value()
                .getBatchSize());
    }

    @Test
    public void testGetDetails() throws InterruptedException {
        DetailsEntity deets = new DetailsEntity();
        deets.setRoaster("Behmore");
        viewModel.recordDetails(deets, 9);
        Thread.sleep(50);
        assertEquals(deets.getRoaster(),
                TestObserver.test(viewModel.getDetails(9))
                        .awaitValue()
                        .value()
                        .getRoaster());
    }
}