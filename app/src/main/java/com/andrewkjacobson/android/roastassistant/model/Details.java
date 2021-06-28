package com.andrewkjacobson.android.roastassistant.model;

import androidx.annotation.NonNull;

public interface Details {
    long getRoastId();

    void setRoastId(long roastId);

    String getDate();

    void setDate(String date);

    String getBeanType();

    void setBeanType(String beanType);

    float getBatchSize();

    void setBatchSize(float batchSize);

    float getYield();

    void setYield(float yield);

    float getWeightLossPercentage();

    String getRoastDegree();

    void setRoastDegree(String roastDegree);

    String getRoastNotes();

    void setRoastNotes(@NonNull String roastNotes);

    String getTastingNotes();

    void setTastingNotes(String tastingNotes);

    String getRoaster();

    void setRoaster(String roaster);

    int getAmbientTemperature();

    void setAmbientTemperature(int ambientTemperature);
}
