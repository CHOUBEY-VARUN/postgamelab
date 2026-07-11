package com.postgamelab.breakdown;

public class BreakdownNotFoundException extends RuntimeException {

    public BreakdownNotFoundException() {
        super("Breakdown not found.");
    }
}
