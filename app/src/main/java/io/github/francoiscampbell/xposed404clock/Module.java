package io.github.francoiscampbell.xposed404clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XResources;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

/**
 * Created by francois on 16-03-20.
 */
public class Module implements IXposedHookInitPackageResources {
    public static final String PACKAGE_SYSTEM_UI = "com.android.systemui";

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals(PACKAGE_SYSTEM_UI)) {
            return;
        }

        resparam.res.hookLayout(PACKAGE_SYSTEM_UI, "layout", "status_bar", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                View view = liparam.view;
                Context hookedContext = view.getContext();
                XResources res = liparam.res;
                final TextView clock = (TextView) view.findViewById(res.getIdentifier("clock", "id", res.getPackageName()));

                IntentFilter timeFilter = new IntentFilter();
                timeFilter.addAction(Intent.ACTION_TIME_TICK);
                timeFilter.addAction(Intent.ACTION_TIME_CHANGED);
                timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

                hookedContext.registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());

                        if (c.get(Calendar.HOUR) == 4 && c.get(Calendar.MINUTE) == 4) {
                            clock.setVisibility(View.GONE);
                        } else {
                            clock.setVisibility(View.VISIBLE);
                        }
                    }
                }, timeFilter);
            }
        });
    }
}
