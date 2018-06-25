
<img src="https://github.com/lvabarajithan/BatteryStatsLibrary/blob/master/ic_launcher-web.png" alt="Sample" width="200" height="200">

## BatteryStatsLibrary

BatteryStatsLibrary is an Android library for implementing basic battery informations and to calculate remaining time for charge and discharge.

This library provides a background service to track the battery usage and reports almost accurate time to charge and discharge.

## Installation

```gradle
dependencies {
    compile 'com.github.lvabarajithan:batterystatslibrary:1.0.0'
}
```

## Screenshots

<img src="https://github.com/lvabarajithan/BatteryStatsLibrary/blob/master/sample.png" alt="Sample" width="360" height="640">

## How to use

To track the changes in battery, create an instance of BatteryStats class.

```java
BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {

          BatteryStats batteryStats = new BatteryStats(intent);

      }
};
registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
```

To get current level of battery..

```java
int level = batteryStats.getLevel();
```

To get the live time like in screenshot..

```java
int liveTime = batteryStats.getLiveTime();
```

Check against these values.. (View sample)

```java
BatteryStats.LIVE_TIME_MORNING
BatteryStats.LIVE_TIME_AFTERNOON
BatteryStats.LIVE_TIME_EVENING
BatteryStats.LIVE_TIME_NIGHT
```

To listen for battery changes **once**, create an instance of BatteryStats class with context.

```java
BatteryStats batteryStats = new BatteryStats(context);
  
boolean charging = batteryStats.isCharging();
```

## And One more thing... 👻

To calculate the remaining time for battery to charge and discharge, Create a class extending BatteryTimeService.

```java
public class NotificationService extends BatteryTimeService {

    @Override
    protected void onCalculatingChargingTime() {
      //Called while charging and time to charge is being calculated.
    }
    
    @Override
    protected void onChargingTimePublish(int hours, int mins) {
      //hours and mins params indicates remaining time for full charge.
    }

    @Override
    protected void onDischargeTimePublish(int days, int hours, int mins) {
      //days, hours and mins params indicates remaining time for discharge.
    }

    @Override
    protected void onCalculatingDischargingTime() {
      //Called while discharging and time to discharge is being calculated.
    }

    @Override
    protected void onFullBattery() {
      //Called when device is charging and battery becomes full.
    }
    
    /**
    * Other default Android service methods can be overridden.
    */
    
}
```

Finally declare the service in **AndroidManifest.xml**

```xml
<service android:name=".NotificationService" 
              android:exported="false"/>
```

## Note 👈🏼

>The calculated time may be inaccurate at first. Later, after tracking the battery usage for 3-4 levels.
>It reports almost accurate time.

### License

```
Copyright 2018 Abarajithan Lv

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
