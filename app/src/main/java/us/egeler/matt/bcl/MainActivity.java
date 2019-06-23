package us.egeler.matt.bcl;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String[]> launchers;
    private int selectedIndex = 0;
    private boolean ignoreInput = false;

    private void setScreenBrightness(int brightness) {
        //Get the content resolver
        ContentResolver cResolver = getContentResolver();

        //Get the current window
        Window window = getWindow();


        Settings.System.putInt(cResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);

        //Get the current window attributes
        WindowManager.LayoutParams layoutpars = window.getAttributes();
        //Set the brightness of this window
        layoutpars.screenBrightness = brightness;
        //Apply attribute changes to this window
        window.setAttributes(layoutpars);
    }


    private void handleKeyPress(String action) {
        if (action.equals("power_button.long_pressed")) {
            Log.d("BCL","Power button long press, trying to shut down...");
            Intent intent=new Intent("com.wahoofitness.bolt.system.shutdown");
            sendBroadcast(intent);
        }

        if (action.equals("up_button.pressed")) {
            launchers.get(selectedIndex)[3] = "0";
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = 0;
            }
            launchers.get(selectedIndex)[3] = "1";
            mAdapter.notifyDataSetChanged();
        }

        if (action.equals("down_button.pressed")) {
            launchers.get(selectedIndex)[3] = "0";
            selectedIndex++;
            if (selectedIndex > (launchers.size()-1)) {
                selectedIndex = launchers.size()-1;
            }
            launchers.get(selectedIndex)[3] = "1";

            mAdapter.notifyDataSetChanged();
        }

        if (action.equals("center_button.pressed")) {
            // launch based on selectedIndex
            String activityToLaunch = launchers.get(selectedIndex)[0];
                Intent intent = new Intent();
                intent.setClassName(launchers.get(selectedIndex)[2],activityToLaunch);
                startActivity(intent);
        }
    }

    private void handleInactiveKeyPress(String action) {
        if (action.equals("right_button.long_pressed")) {
            Intent intent= new Intent(this,MainActivity.class);
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        }

    }


    private void startKeyPressListener() {
        // create broadcastReceiver to listen for button presses
        IntentFilter filter = new IntentFilter("com.wahoofitness.bolt.buttons.center_button.pressed");
        filter.addAction("com.wahoofitness.bolt.buttons.center_button.down");
        filter.addAction("com.wahoofitness.bolt.buttons.center_button.up");
        filter.addAction("com.wahoofitness.bolt.buttons.center_button.long_prossed");
        filter.addAction("com.wahoofitness.bolt.buttons.left_button.down");
        filter.addAction("com.wahoofitness.bolt.buttons.left_button.up");
        filter.addAction("com.wahoofitness.bolt.buttons.left_button.pressed");
        filter.addAction("com.wahoofitness.bolt.buttons.left_button.long_pressed");
        filter.addAction("com.wahoofitness.bolt.buttons.right_button.down");
        filter.addAction("com.wahoofitness.bolt.buttons.right_button.up");
        filter.addAction("com.wahoofitness.bolt.buttons.right_button.pressed");
        filter.addAction("com.wahoofitness.bolt.buttons.right_button.long_pressed");
        filter.addAction("com.wahoofitness.bolt.buttons.up_button.down");
        filter.addAction("com.wahoofitness.bolt.buttons.up_button.up");
        filter.addAction("com.wahoofitness.bolt.buttons.up_button.pressed");
        filter.addAction("com.wahoofitness.bolt.buttons.up_button.long_pressed");
        filter.addAction("com.wahoofitness.bolt.buttons.down_button.down");
        filter.addAction("com.wahoofitness.bolt.buttons.down_button.up");
        filter.addAction("com.wahoofitness.bolt.buttons.down_button.pressed");
        filter.addAction("com.wahoofitness.bolt.buttons.down_button.long_pressed");
        filter.addAction("com.wahoofitness.bolt.buttons.power_button.down");
        filter.addAction("com.wahoofitness.bolt.buttons.power_button.up");
        filter.addAction("com.wahoofitness.bolt.buttons.power_button.pressed");
        filter.addAction("com.wahoofitness.bolt.buttons.power_button.long_pressed");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ignoreInput == false) {
                    handleKeyPress(intent.getAction().split("\\.")[4] + "." + intent.getAction().split("\\.")[5]);
                } else {
                    handleInactiveKeyPress(intent.getAction().split("\\.")[4] + "." + intent.getAction().split("\\.")[5]);
                }
            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        ignoreInput = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        ignoreInput = false;
        setScreenBrightness(10);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setScreenBrightness(10);
        startKeyPressListener();

        ((TextView) findViewById(R.id.selectText)).getPaint().setAntiAlias(false);

        // get a list of launchers available
        launchers = new ArrayList<String[]>();

        PackageManager pm = getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> lst = pm.queryIntentActivities(i, 0);
        int it = 0;
        for (ResolveInfo resolveInfo : lst) {
            ApplicationInfo app = null;

            try {
                app = this.getPackageManager().getApplicationInfo(resolveInfo.activityInfo.packageName, 0);
            } catch (Exception e) {
                Log.e("OBC", ""+e);
            }

            String appName = (String) this.getPackageManager().getApplicationLabel(app);

            if (resolveInfo.activityInfo.packageName.equals("com.wahoofitness.boltlauncher")) {
                appName = "Official wahoo app";
            }

            if (!(resolveInfo.activityInfo.packageName.equals("us.egeler.matt.bcl"))) {
                launchers.add(new String[4]);
                String[] s = {resolveInfo.activityInfo.name, appName, resolveInfo.activityInfo.packageName,"0"};
                launchers.set(it, s);
                it++;
            }

        }

        recyclerView = (RecyclerView) findViewById(R.id.rview);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        launchers.get(selectedIndex)[3] = "1";
        mAdapter = new menuAdapter(launchers);
        recyclerView.setAdapter(mAdapter);
    }
}
