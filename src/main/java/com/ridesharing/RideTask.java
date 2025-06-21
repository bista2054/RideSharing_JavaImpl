package com.ridesharing;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RideTask {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private final String rideId;
    private final String pickupLocation;
    private final String destination;
    private final int passengerCount;
    private final Date requestTime;

    public RideTask(String rideId, String pickupLocation, String destination, int passengerCount) {
        this.rideId = rideId;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.passengerCount = passengerCount;
        this.requestTime = new Date();
    }

    public String getFormattedTime() {
        return dateFormat.format(requestTime);
    }

    @Override
    public String toString() {
        return String.format("%s Ride[%s] from %s to %s (%d passengers)",
                getFormattedTime(), rideId, pickupLocation, destination, passengerCount);
    }
}