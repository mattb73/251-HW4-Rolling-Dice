package ucsb.ece251.matt.rollingdice_251_hw4;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.util.Log;

/**
 * Created by Administrator on 2/28/2015.
 */
public class ShakeDetector implements SensorEventListener {
    private long lastUpdate = -1;
    //private float x, y, z;
    //private float last_x, last_y, last_z;
    private static final float GRAVITY_THRESHOLD=2f;

    private OnShakeListener mListener;
    public interface OnShakeListener{
        public void onShake();
    }

    public void setOnShakeListener(OnShakeListener listener){
        mListener=listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.d("MattRun++", "=============== onSensorChanged() ===================");
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gForce = FloatMath.sqrt(x * x + y * y + z * z)/SensorManager.GRAVITY_EARTH;

        if(gForce > GRAVITY_THRESHOLD){ // if movement big enough to be a shake
            long now = System.currentTimeMillis();
            if(now - lastUpdate > 500){ //event counts if 200ms apart
                lastUpdate = now;
                mListener.onShake();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
