package com.andrewkjacobson.android.roastassistant.model;

public interface Crack extends Reading {
    void setHasOccurred(boolean occurred);
    boolean hasOccurred();
    void setCrackNumber(int crackNumber);
    int getCrackNumber();
}
