package com.example.pasapp.SensoresGrupo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pasapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Sensores extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView accelerometerText;
    private DatabaseReference databaseReference;

    private float x, y, z;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensores_vista);

        accelerometerText = findViewById(R.id.accelerometer_text);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Inicializar Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Iniciar el temporizador para enviar datos cada 10 segundos
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendAccelerometerData();
            }
        }, 0, 10000); // 10,000 milisegundos = 10 segundos
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
            String accelerometerValues = String.format("Accelerometer values:\n" +
                    "x: %.2f\n" +
                    "y: %.2f\n" +
                    "z: %.2f", x, y, z);
            accelerometerText.setText(accelerometerValues);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario implementar esto para este ejemplo
    }

    private void sendAccelerometerData() {
        // Crear un mapa con los datos del acelerómetro
        Map<String, Object> accelerometerData = new HashMap<>();
        accelerometerData.put("x", x);
        accelerometerData.put("y", y);
        accelerometerData.put("z", z);
        accelerometerData.put("timestamp", System.currentTimeMillis());

        // Enviar los datos a Firebase Realtime Database
        databaseReference.child("accelerometerData").push().setValue(accelerometerData);
    }
}
