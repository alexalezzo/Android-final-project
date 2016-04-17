package com.example.alex.android_final_project;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.hardware.Camera.Parameters;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    private int counter = 0;
    private float median = 200;
    private float min = 999999;
    private float max = 0;
    private int lastChangingSignal = -1;
    private boolean switcher = false;
    private Timer updateTimer;
    private EditText et;
    private TextView tv_log;
    private TextView tv_codes;
    private TextView tv_output;
    private Morse m = new Morse();
    private MorseCover mc = new MorseCover();
    private Camera camera;
    private Parameters parameter;
    private boolean deviceHasFlash;
    //private boolean isFlashLightOn = false;
    private boolean timerToggle = false;
    private boolean logging = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.btn_vibro);
        Button flash = (Button) findViewById(R.id.btn_flash);
        //Button rec = (Button) findViewById(R.id.btn_receive);
        tv_log = (TextView) findViewById(R.id.tw_log);
        tv_codes = (TextView) findViewById(R.id.tv_codes);
        tv_output = (TextView) findViewById(R.id.tv_message);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds

                et = (EditText) findViewById(R.id.et_input);
                String str = m.toMorse(et.getText().toString().toCharArray());
                int[] code = mc.GetCode(str);
                for(int i = 0; i < code.length; i+=2)
                {
                    v2.vibrate(code[i]);
                    try {
                        Thread.sleep(code[i]);
                        Thread.sleep(code[i+1]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        SensorManager mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        Sensor LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(LightSensor != null){
            mySensorManager.registerListener(
                    LightSensorListener,
                    LightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        }
        deviceHasFlash = getApplication().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if(!deviceHasFlash){
            Toast.makeText(MainActivity.this, "Sorry, you device does not have any camera", Toast.LENGTH_LONG).show();
            return;
        }
        else{
            this.camera = Camera.open(0);
            parameter = this.camera.getParameters();
        }
        assert flash != null;
        flash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                et = (EditText) findViewById(R.id.et_input);
                String str = m.toMorse(et.getText().toString().toLowerCase().toCharArray());
                int[] code = mc.GetCode(str);
                for(int i = 0; i < code.length; i+=2)
                {
                    if(code[i] != 0)
                        turnOnTheFlash();
                    try {
                        Thread.sleep(code[i]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    turnOffTheFlash();
                    try {
                        Thread.sleep(code[i+1]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                turnOffTheFlash();
            }
        });



    }


    private final SensorEventListener LightSensorListener
            = new SensorEventListener(){
        @Override
        public void onSensorChanged(SensorEvent event) {

            DataHolder.time.add(System.currentTimeMillis());
            DataHolder.lightLevels.add(event.values[0]);
            if(event.values[0] > max)
                max = event.values[0];
            if(event.values[0] < min)
                min = event.values[0];
            log();
            //median = (min + max) / 2;
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }
    };

    private void turnOffTheFlash() {
        parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
        this.camera.setParameters(parameter);
        this.camera.stopPreview();
        //isFlashLightOn = false;
    }

    private void turnOnTheFlash() {
        if(this.camera != null){
            parameter = this.camera.getParameters();
            parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
            this.camera.setParameters(parameter);
            this.camera.startPreview();
            //isFlashLightOn = true;
        }
    }

    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                parameter = camera.getParameters();
            } catch (RuntimeException e) {
                System.out.println("Error: Failed to Open: " + e.getMessage());
            }
        }
    }

    public void openLog(MenuItem item)
    {
        tv_log.setVisibility(View.VISIBLE);
        tv_codes.setVisibility(View.VISIBLE);
        logging = !logging;
    }
    @Override
    protected void onStart() {
        super.onStart();
        updateTimer = new Timer();
        updateTimer.schedule(new updateTask(new Handler(), this), 0, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateTimer.cancel();
        updateTimer.purge();
    }

    public void update() {
        // This is where the UI calls go
        //tv.setText(String.valueOf(++counter));
       if(et == null)
           et = (EditText) findViewById(R.id.et_input);
        if(tv_log == null) {
            tv_log = (TextView) findViewById(R.id.tw_log);
            tv_codes = (TextView) findViewById(R.id.tv_codes);
        }
       if( DataHolder.lightLevels.size() > counter)
       {
           for(int i = counter; i <  DataHolder.lightLevels.size(); i++)
           {
                counter++;

                if( DataHolder.lightLevels.get(i) >= median && !switcher)
                {
                    long prev = lastChangingSignal >= 0 ? DataHolder.time.get(lastChangingSignal) : 0;
                    long timeDiff = prev -  DataHolder.time.get(i);
                    DataHolder.codes.add(timeDiff);
                    lastChangingSignal = i;
                    switcher = true;
                }
                else if( DataHolder.lightLevels.get(i) < median && switcher)
                {
                    long prev = lastChangingSignal >= 0 ? DataHolder.time.get(lastChangingSignal) : 0;
                    long timeDiff = prev -  DataHolder.time.get(i);
                    DataHolder.codes.add(-timeDiff);
                    lastChangingSignal = i;
                    switcher = false;
                }
           }
        tv_output.setText(tv_output.getText() + m.toEnglish(mc.ParseCode(DataHolder.codes)));
       }
    log();
    }

    private void log()
    {
        if(logging)
        {
            String report = "";
            for(int i = 0; i < DataHolder.lightLevels.size(); i++)
            {
                report += "L: " + DataHolder.lightLevels.get(i) + ";  T: " + DataHolder.time.get(i) + "\n";
            }
            tv_log.setText(report);
            report = "";
            for(int i = 0; i < DataHolder.codes.size(); i++)
            {
                report += "Code: " + DataHolder.codes.get(i) + "; " + "\n";
            }
            tv_codes.setText(report);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_context_menu, menu);
        return true;
    }


    private class updateTask extends TimerTask {
        Handler handler;
        MainActivity ref;

        public updateTask(Handler handler, MainActivity ref) {
            super();
            this.handler = handler;
            this.ref = ref;
        }

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ref.update();
                }
            });
        }
    }

}

