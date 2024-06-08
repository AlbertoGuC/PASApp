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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Sensores extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;
    private float lastX, lastY, lastZ;
    private float lastGX, lastGY, lastGZ;
    private Handler handler;
    private Runnable runnable;
    private DatabaseReference databaseAccelerometer, databaseGyroscope;
    private static final long INTERVAL = 10000; // 10 segundos

    private TextView textViewX, textViewY, textViewZ;
    private TextView textViewGX, textViewGY, textViewGZ;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensores_vista);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userName = currentUser.getDisplayName(); // Obtener el nombre del usuario
        } else {
            userName = "Unknown"; // O manejar el caso donde el usuario no esté autenticado
        }

        // Inicializar vistas
        textViewX = findViewById(R.id.textViewX);
        textViewY = findViewById(R.id.textViewY);
        textViewZ = findViewById(R.id.textViewZ);
        textViewGX = findViewById(R.id.textViewGX);
        textViewGY = findViewById(R.id.textViewGY);
        textViewGZ = findViewById(R.id.textViewGZ);

        // Configuración de Firebase
        databaseAccelerometer = FirebaseDatabase.getInstance().getReference("accelerometer");
        databaseGyroscope = FirebaseDatabase.getInstance().getReference("gyroscope");

        // Configuración del acelerómetro y giroscopio
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Configuración del handler para enviar datos cada 10 segundos
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                sendSensorDataToFirebase();
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
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            lastGX = event.values[0];
            lastGY = event.values[1];
            lastGZ = event.values[2];

            // Actualizar las vistas con los valores del giroscopio
            textViewGX.setText("GX: " + lastGX);
            textViewGY.setText("GY: " + lastGY);
            textViewGZ.setText("GZ: " + lastGZ);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se necesita implementar en este caso
    }

    private void sendSensorDataToFirebase() {
        long timestamp = System.currentTimeMillis();

        // Enviar datos del acelerómetro
        String accelId = databaseAccelerometer.push().getKey();
        Map<String, Object> accelData = new HashMap<>();
        accelData.put("user", userName);
        accelData.put("x", lastX);
        accelData.put("y", lastY);
        accelData.put("z", lastZ);
        accelData.put("timestamp", timestamp);

        assert accelId != null;
        databaseAccelerometer.child(accelId).setValue(accelData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Sensores.this, "Accelerometer data sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Sensores.this, "Failed to send accelerometer data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Enviar datos del giroscopio
        String gyroId = databaseGyroscope.push().getKey();
        Map<String, Object> gyroData = new HashMap<>();
        gyroData.put("user", userName);
        gyroData.put("gx", lastGX);
        gyroData.put("gy", lastGY);
        gyroData.put("gz", lastGZ);
        gyroData.put("timestamp", timestamp);

        assert gyroId != null;
        databaseGyroscope.child(gyroId).setValue(gyroData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Sensores.this, "Gyroscope data sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Sensores.this, "Failed to send gyroscope data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
