package com.github.abara.library.batterystats;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by abara on 14/05/16.
 */
public class BatteryStats {

    /**
     * Values indicating the live time of the day.
     */
    public static final int LIVE_TIME_MORNING = 0;
    public static final int LIVE_TIME_AFTERNOON = 1;
    public static final int LIVE_TIME_EVENING = 2;
    public static final int LIVE_TIME_NIGHT = 3;

    /**
     * Values indicating the plugged state.
     */
    public static final int PLUGGED_STATE_AC = 0;
    public static final int PLUGGED_STATE_USB = 1;
    public static final int PLUGGED_STATE_WIRELESS = 2;
    public static final int PLUGGED_STATE_UNKNOWN = 3;

    /**
     * Values for Extras.
     */
    public static final String DEGREE = "\u00b0";

    /**
     * Values for indicating battery health.
     */
    public static final int BATTERY_HEALTH_COLD = 0;
    public static final int BATTERY_HEALTH_DEAD = 1;
    public static final int BATTERY_HEALTH_GOOD = 2;
    public static final int BATTERY_HEALTH_OVER_VOLTAGE = 3;
    public static final int BATTERY_HEALTH_OVERHEAT = 4;
    public static final int BATTERY_HEALTH_UNKNOWN = 5;
    public static final int BATTERY_STATUS_FAILURE = -1;

    /**
     * private instance to handle the battery intent.
     */
    private Intent batteryIntent;

    /**
     * Default constructor is not accessible.
     */
    private BatteryStats() {
    }

    /**
     * Constructor with Intent as parameter.
     * <p/>
     * {@param batteryIntent} Returned when registering a null receiver with a Intent filter ( Changes listened once )
     * or an intent from onReceive of broadcast receiver.
     */
    public BatteryStats(Intent batteryIntent) {
        this.batteryIntent = batteryIntent;
    }

    /**
     * Constructor with Context as parameter.
     * <p/>
     * {@param context} Used for initializing a default Battery Intent. ( Changes listened once only ).
     */
    public BatteryStats(Context context) {
        this.batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    /**
     * Getter for batteryIntent.
     */
    public Intent getBatteryIntent() {
        return batteryIntent;
    }

    /**
     * Method to get Live time (Morning, Afternoon, Evening and Night).
     */
    public int getLiveTime() {

        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            return BatteryStats.LIVE_TIME_MORNING;
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            return BatteryStats.LIVE_TIME_AFTERNOON;
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            return BatteryStats.LIVE_TIME_EVENING;
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            return BatteryStats.LIVE_TIME_NIGHT;
        } else {
            return -1;
        }

    }

    /**
     * Method to get the battery level.
     */
    public int getLevel() {
        return batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
    }

    /**
     * Method to get the battery level accurate. (Since the returned value is float).
     */
    public float getLevelAccurate() {
        int level = getLevel();
        int scale = getScale();
        return level / (float) scale;
    }

    public int getScale() {
        return batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    }

    /**
     * Method to get the battery technology.
     */
    public String getBatteryTechnology() {
        return batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
    }

    /**
     * Method to get the the plugged state.
     */
    public int getPluggedState() {

        int plug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        if (plug == BatteryManager.BATTERY_PLUGGED_AC)
            return BatteryStats.PLUGGED_STATE_AC;
        else if (plug == BatteryManager.BATTERY_PLUGGED_USB)
            return BatteryStats.PLUGGED_STATE_USB;
        else if (plug == BatteryManager.BATTERY_PLUGGED_WIRELESS)
            return BatteryStats.PLUGGED_STATE_WIRELESS;
        else
            return BatteryStats.PLUGGED_STATE_UNKNOWN;

    }

    /**
     * Method to check whether device is charging.
     */
    public boolean isCharging() {

        int plugState = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        return (plugState == BatteryManager.BATTERY_PLUGGED_AC) ||
                (plugState == BatteryManager.BATTERY_PLUGGED_USB) || (plugState == BatteryManager.BATTERY_PLUGGED_WIRELESS);
    }

    /**
     * Method to get the battery health.
     */
    public int getHealth() {

        int health = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);

        if (health == BatteryManager.BATTERY_HEALTH_COLD)
            return BATTERY_HEALTH_COLD;
        else if (health == BatteryManager.BATTERY_HEALTH_DEAD)
            return BATTERY_HEALTH_DEAD;
        else if (health == BatteryManager.BATTERY_HEALTH_GOOD)
            return BATTERY_HEALTH_GOOD;
        else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE)
            return BATTERY_HEALTH_OVER_VOLTAGE;
        else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT)
            return BATTERY_HEALTH_OVERHEAT;
        else if (health == BatteryManager.BATTERY_HEALTH_UNKNOWN)
            return BATTERY_HEALTH_UNKNOWN;
        else
            return BATTERY_STATUS_FAILURE;

    }

    /**
     * Method to get the battery health text.
     */
    public String getHealthText() {

        int health = getHealth();

        if (health == BATTERY_HEALTH_COLD)
            return "Cold";
        else if (health == BATTERY_HEALTH_DEAD)
            return "Dead";
        else if (health == BATTERY_HEALTH_GOOD)
            return "Good";
        else if (health == BATTERY_HEALTH_OVER_VOLTAGE)
            return "Bad";
        else if (health == BATTERY_HEALTH_OVERHEAT)
            return "Hot";
        else if (health == BATTERY_HEALTH_UNKNOWN)
            return "N/A";
        else
            return "FAIL";

    }

    /**
     * Method to get the battery temperature without text in Celsius or Fahrenheit.
     */
    public double getTemperature(boolean fahrenheit) {
        double celsius = (double) (batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10);
        if (fahrenheit) {
            return celsius * 1.8 + 32;
        } else {
            return celsius;
        }
    }

    /**
     * Method to get the battery temperature with text in Celsius or Fahrenheit.
     */
    public String getTemperatureText(boolean fahrenheit) {
        return String.valueOf(getTemperature(fahrenheit)) + BatteryStats.DEGREE + "C";
    }

    /**
     * Method to get the battery voltage.
     */
    public double getVoltage() {
        double volt = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        return Double.valueOf(new DecimalFormat("#.##").format(volt / 1000.0D));
    }

    /**
     * Method to get the battery voltage with custom pattern.
     */
    public double getVoltage(String pattern) {
        double volt = getVoltage();
        return Double.valueOf(new DecimalFormat(pattern).format(volt / 1000.0D));
    }

}
