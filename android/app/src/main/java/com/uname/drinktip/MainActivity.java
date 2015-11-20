package com.uname.drinktip;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.zsofware.androidMqttLib.activity.MqttActivity;
import com.zsofware.androidMqttLib.client.MqttClientHelper;
import com.zsofware.androidMqttLib.entry.SubscribeEntry;
import com.zsofware.androidMqttLib.service.MqttService;

import java.util.ArrayList;

public class MainActivity extends MqttActivity {

    private static final String TAG = MainActivity.class.getSimpleName() ;
    private Button drinkTipButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        MqttClientHelper.getInstance().setOnConnectListener(new MqttClientHelper.OnConnectListener() {
            @Override
            public void connectResult(boolean result) {
                Log.i(TAG, result ? "Yes, connected" : "No, connect failed");
                if(result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setViewOnMqttClientConnected();
                        }
                    });
                }
            }
        });

        Intent intent = new Intent("com.zsoftware.mqttservice");
        Bundle b = new Bundle();
        b.putString(MqttService.FIELD_PROJECT, "DRTP");
        b.putString(MqttService.FIELD_BROKER, "m2m.eclipse.org");
        b.putString(MqttService.FIELD_USERNAME, "111"); // 用户名
        b.putString(MqttService.FIELD_PASSWORD, "222"); // 密码
        b.putString(MqttService.FIELD_PROT, "1883");
        SubscribeEntry sub1 = new SubscribeEntry("DrinkTip", SubscribeEntry.QOS_2);
        ArrayList<SubscribeEntry> list = new ArrayList<SubscribeEntry>();
        list.add(sub1);
        b.putSerializable(MqttService.FIELD_SUBSCRIBELIST, list);
        intent.putExtras(b);
        intent.setPackage(getPackageName());
        try {
            setMQTTReceive(MessageReceiver.class);
            Log.d(TAG, "ok");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        progressDialog.show();
        startService(intent);
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting...");
        progressDialog.setCancelable(true);

        drinkTipButton = (Button) findViewById(R.id.drink_tip_button);
        drinkTipButton.setVisibility(View.INVISIBLE);
    }

    private void setViewOnMqttClientConnected() {
        progressDialog.dismiss();
        drinkTipButton.setVisibility(View.VISIBLE);
        drinkTipButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_anim));
    }

    public void onDrinkTipButtonClicked(View v) {
        Log.d(TAG, ">> " + MqttClientHelper.getInstance().getMqttClient().isConnected());
        MqttClientHelper.getInstance().publish("Hiapache", "NiHao".getBytes(), 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
