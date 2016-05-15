package com.github.abara.library.batterystatsexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;

import com.github.abara.library.batterystats.BatteryStats;

public class MainActivity extends AppCompatActivity {

    private AppCompatTextView greetText, statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        greetText = (AppCompatTextView) findViewById(R.id.greeting_text);
        statusText = (AppCompatTextView) findViewById(R.id.geek_text);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                BatteryStats batteryStats = new BatteryStats(intent);

                int liveTime = batteryStats.getLiveTime();

                // Init the greet string
                String greetings = "OMG! You are not on earth.";
                switch (liveTime) {
                    case BatteryStats.LIVE_TIME_MORNING:
                        greetings = "Good morning!";
                        break;
                    case BatteryStats.LIVE_TIME_AFTERNOON:
                        greetings = "Good afternoon!";
                        break;
                    case BatteryStats.LIVE_TIME_EVENING:
                        greetings = "Good evening!";
                        break;
                    case BatteryStats.LIVE_TIME_NIGHT:
                        greetings = "Good night!";
                        break;
                }

                greetText.setText(greetings);

                statusText.setText(getResources().getString(R.string.geek_status_text,
                        batteryStats.getBatteryTechnology(),
                        batteryStats.getLevel() + "%",
                        batteryStats.getHealthText()));

            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }
}
