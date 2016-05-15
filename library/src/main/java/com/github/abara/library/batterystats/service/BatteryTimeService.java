package com.github.abara.library.batterystats.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.abara.library.batterystats.BatteryStats;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by abara on 14/05/16.
 */
public abstract class BatteryTimeService extends Service {

    /**
     * Lists to store the tracked data for calculating charging and discharging time.
     */
    private ArrayList<Long> batteryDischargingTimes, batteryChargingTimes;
    /**
     * A broadcast receiver for tracking the level changes and the battery usage.
     */
    private BroadcastReceiver levelReceiver = new BroadcastReceiver() {
        int oldDischargeLevel = 101, oldChargeLevel = 0;
        long oldDischargeTime = 0, oldChargeTime = 0;

        @Override
        public void onReceive(Context context, Intent intent) {

            BatteryStats batteryStats = new BatteryStats(intent);
            boolean charging = batteryStats.isCharging();
            int level = batteryStats.getLevel();

            if ((!charging) && (level <= 100)) {

                if (level < oldDischargeLevel) {

                    long time = System.currentTimeMillis();
                    if (oldDischargeTime != 0) {
                        long diffTime = time - oldDischargeTime;
                        batteryDischargingTimes.add(diffTime);
                        publishDischargingText(level);
                    } else {
                        onCalculatingDischargingTime();
                    }
                    oldDischargeTime = time;
                    oldDischargeLevel = level;
                }

                batteryChargingTimes.clear();
                oldChargeLevel = 0;
                oldChargeTime = 0;

            }

            if (charging) {

                if (oldChargeLevel < level) {

                    long time = System.currentTimeMillis();
                    if (oldChargeTime != 0) {
                        long diffTime = time - oldChargeTime;
                        batteryChargingTimes.add(diffTime);
                        publishChargingText(level);
                    } else {
                        onCalculatingChargingTime();
                    }
                    oldChargeTime = time;
                    oldChargeLevel = level;

                }

                if (level == 100) {
                    onFullBattery();
                }

                batteryDischargingTimes.clear();
                oldDischargeLevel = 100;
                oldDischargeTime = 0;

            }

        }
    };

    /**
     * Method called when the charging time is calculated.
     * The {@param hours} and {@param mins} remaining for full charge.
     */
    protected abstract void onChargingTimePublish(int hours, int mins);

    /**
     * Method called when the charging time is being calculated.
     * Any status text indicating that "charging time is being calculated" can be used here.
     */
    protected abstract void onCalculatingChargingTime();

    /**
     * Method called when the charging time is calculated.
     * The {@param days} , {@param hours} and {@param mins} remaining for the battery to drain.
     */
    protected abstract void onDischargeTimePublish(int days, int hours, int mins);

    /**
     * Method called when the discharging time is being calculated.
     * Any status text indicating that "discharging time is being calculated" can be used here.
     */
    protected abstract void onCalculatingDischargingTime();

    /**
     * Method called when the battery is fully charged.
     * Called only when the device is connected to charger and battery becomes full.
     */
    protected abstract void onFullBattery();

    /**
     * Start the calculation when the activity is created.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        batteryDischargingTimes = new ArrayList<>();
        batteryChargingTimes = new ArrayList<>();

        registerReceiver(levelReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    /**
     * Method to calculate the charging time based on the timely usage.
     */
    private void publishChargingText(int level) {

        long average, sum = 0;
        for (Long time : batteryChargingTimes) {

            sum += time;

        }
        average = (sum / (batteryChargingTimes.size())) * (100 - level);

        //Since charging cannot take days
        //int days = (int) TimeUnit.MILLISECONDS.toDays(average);
        int hours = (int) (TimeUnit.MILLISECONDS.toHours(average) % TimeUnit.DAYS.toHours(1));
        int mins = (int) (TimeUnit.MILLISECONDS.toMinutes(average) % TimeUnit.HOURS.toMinutes(1));

        onChargingTimePublish(hours, mins);

    }

    /**
     * Method to calculate the discharging time based on the timely usage.
     */
    private void publishDischargingText(int level) {

        long average, sum = 0;
        for (Long time : batteryDischargingTimes) {

            sum += time;

        }
        average = (sum / (batteryDischargingTimes.size())) * level;

        int days = (int) TimeUnit.MILLISECONDS.toDays(average);
        int hours = (int) (TimeUnit.MILLISECONDS.toHours(average) % TimeUnit.DAYS.toHours(1));
        int mins = (int) (TimeUnit.MILLISECONDS.toMinutes(average) % TimeUnit.HOURS.toMinutes(1));

        onDischargeTimePublish(days, hours, mins);

    }

    /**
     * Returns START_STICKY by default. Method can be overridden to customize.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * Unregister the level receiver onDestroy of the service to avoid leakage.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(levelReceiver);
    }

    /**
     * Binding not required by default. Can be overridden for binding.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}