package org.dimdev.dimdoors.core;

public interface IDimRegistrationCallback {
    NewDimData registerDimension(int dimensionID, int rootID, DimensionType type);
}
