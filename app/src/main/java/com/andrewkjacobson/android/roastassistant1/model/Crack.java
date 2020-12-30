package com.andrewkjacobson.android.roastassistant1.model;

public interface Crack extends Reading {
    void setHasOccurred(boolean occurred);
    boolean hasOccurred();
    void setCrackNumber(int crackNumber);
    int getCrackNumber();
}
