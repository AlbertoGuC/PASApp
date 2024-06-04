package com.example.pasapp.SensoresGrupo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pasapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Sensores extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ;
    private Handler handler;
    private Runnable runnable;
    private DatabaseReference databaseAccelerometer;
    private static final long INTERVAL = 10000; // 10 segundos

    private TextView textViewX, textViewY, textViewZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensores_vista);

        // Inicializar vistas
        textViewX = findViewById(R.id.textViewX);
        textViewY = findViewById(R.id.textViewY);
        textViewZ = findViewById(R.id.textViewZ);

        // Configuración de Firebase
        databaseAccelerometer = FirebaseDatabase.getInstance().getReference("accelerometer");

        // Configuración del acelerómetro
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Configuración del handler para enviar datos cada 10 segundos
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                sendAccelerometerDataToFirebase();
                handler.postDelayed(this, INTERVAL);
            }
        };
        handler.postDelayed(runnable, INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            lastX = event.values[0];
            lastY = event.values[1];
            lastZ = event.values[2];

            // Actualizar las vistas con los valores del acelerómetro
            textViewX.setText("X: " + lastX);
            textViewY.setText("Y: " + lastY);
            textViewZ.setText("Z: " + lastZ);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se necesita implementar en este caso
    }

    private void sendAccelerometerDataToFirebase() {
        String id = databaseAccelerometer.push().getKey();
        long timestamp = System.currentTimeMillis();
        Map<String, Object> data = new HashMap<>();
        data.put("x", lastX);
        data.put("y", lastY);
        data.put("z", lastZ);
        data.put("timestamp", timestamp);

        assert id != null;
        databaseAccelerometer.child(id).setValue(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Sensores.this, "Data sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Sensores.this, "Failed to send data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
