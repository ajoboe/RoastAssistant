package com.andrewkjacobson.android.roastassistant.viewmodel;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RoastViewModelTest extends TestCase {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    private  RoastViewModel viewModel;

    public void setUp() throws Exception {
        super.setUp();
        Application application = (Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        viewModel = new RoastViewModel(application, null);
    }

    public void testGetRoastLiveData() {
    }

    @Test
    public void testGetRoast() {
        assertNotNull(viewModel.getRoast());
    }

    public void testGetDetails() {
    }

    public void testGetReadingsLiveData() {
    }

    public void testGetCracksLiveData() {
    }

    public void testGetSettings() {
    }

    public void testGetElapsed() {
    }

    public void testRecordTemperature() {
    }

    public void testRecord1c() {
    }

    public void testRecordPower() {
    }

    public void testIsRunning() {
    }

    public void testGetStartTime() {
    }

    public void testStartRoast() {
    }

    public void testEndRoast() {
    }

    public void testGetFirstCrackPercent() {
    }

    public void testGetFirstCrackLookaheadPercent() {
    }
}