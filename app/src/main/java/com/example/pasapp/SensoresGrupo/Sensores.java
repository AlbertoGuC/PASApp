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
    private Sensor acelerometro, giroscopo, pasometro, luzometro, magnetometro;
    private float lastX, lastY, lastZ;
    private float lastGX, lastGY, lastGZ;
    private float pasos;
    private float luz;
    private float MagX, MagY, MagZ;
    private DatabaseReference rtdbAcelerometro, rtdbGiroscopo, rtdbPasos, rtdbLuz,rtdbMagnet;
    private static final long tiempo = 10000;

    private TextView VAceX, VAceY, VAceZ;
    private TextView VGX, VGY, VGZ;
    private TextView VPasos;
    private TextView VLuz;
    private TextView VMagX, VMagY, VMagZ;
    private FirebaseAuth mAuth;
    private FirebaseUser usuario;
    private String nombreUsuario;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensores_vista);

        mAuth = FirebaseAuth.getInstance();
        usuario = mAuth.getCurrentUser();

        if (usuario!= null) {
            nombreUsuario = usuario.getDisplayName();
        } else {
            nombreUsuario = "No Logeado";
        }
        VAceX= findViewById(R.id.AceX);
        VAceY = findViewById(R.id.AceY);
        VAceZ = findViewById(R.id.AceZ);
        VGX = findViewById(R.id.GX);
        VGY = findViewById(R.id.GY);
        VGZ = findViewById(R.id.GZ);
        VPasos = findViewById(R.id.Pasos);
        VLuz = findViewById(R.id.Luz);
        VMagX = findViewById(R.id.MagX);
        VMagY = findViewById(R.id.MagY);
        VMagZ = findViewById(R.id.MagZ);

        rtdbAcelerometro = FirebaseDatabase.getInstance().getReference("Acelerómetro");
        rtdbGiroscopo = FirebaseDatabase.getInstance().getReference("Gisóscopo");
        rtdbPasos = FirebaseDatabase.getInstance().getReference("Pasos");
        rtdbLuz = FirebaseDatabase.getInstance().getReference("Luz");
        rtdbMagnet= FirebaseDatabase.getInstance().getReference("Magnetómetro");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            giroscopo = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            pasometro = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            luzometro = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            if (pasometro == null) {
                Toast.makeText(this, "No step counter detected", Toast.LENGTH_SHORT).show();
            } else {
                sensorManager.registerListener(this,pasometro,SensorManager.SENSOR_DELAY_NORMAL);
            }
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, giroscopo, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, luzometro, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_NORMAL);

        }

        thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                sendSensorDataToFirebase();
                try {
                    Thread.sleep(tiempo);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
        sendPasosFirebase();
        super.onDestroy();
        sensorManager.unregisterListener(this);
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];

                VAceX.setText("X: " + lastX);
                VAceY.setText("Y: " + lastY);
                VAceZ.setText("Z: " + lastZ);
                break;

            case Sensor.TYPE_GYROSCOPE:
                lastGX = event.values[0];
                lastGY = event.values[1];
                lastGZ = event.values[2];

                VGX.setText("GX: " + lastGX);
                VGY.setText("GY: " + lastGY);
                VGZ.setText("GZ: " + lastGZ);
                break;

            case Sensor.TYPE_STEP_COUNTER:
                if (pasos == 0) {
                    pasos = event.values[0];
                }
                float nuevosPasos = event.values[0] - pasos;
                pasos = event.values[0];

                VPasos.setText("Pasos: " + nuevosPasos);
                break;
            case Sensor.TYPE_LIGHT:
                luz = event.values[0];

                VLuz.setText("Intensidad luz: "+luz);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                MagX = event.values[0];
                MagY = event.values[1];
                MagZ = event.values[2];

                VMagX.setText("X: " + MagX);
                VMagY.setText("Y: " + MagY);
                VMagZ.setText("Z: " + MagZ);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se necesita implementar en este caso
    }

    private void sendSensorDataToFirebase() {
        long timestamp = System.currentTimeMillis();

        // Enviar datos del acelerómetro
        String accelId = rtdbAcelerometro.push().getKey();
        Map<String, Object> accelData = new HashMap<>();
        accelData.put("user", nombreUsuario);
        accelData.put("x", lastX);
        accelData.put("y", lastY);
        accelData.put("z", lastZ);
        accelData.put("timestamp", timestamp);

        assert accelId != null;
        rtdbAcelerometro.child(accelId).setValue(accelData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Sensores.this, "Accelerometer data sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Sensores.this, "Failed to send accelerometer data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Enviar datos del giroscopio
        String gyroId = rtdbGiroscopo.push().getKey();
        Map<String, Object> gyroData = new HashMap<>();
        gyroData.put("user", nombreUsuario);
        gyroData.put("gx", lastGX);
        gyroData.put("gy", lastGY);
        gyroData.put("gz", lastGZ);
        gyroData.put("timestamp", timestamp);

        assert gyroId != null;
        rtdbGiroscopo.child(gyroId).setValue(gyroData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Sensores.this, "Gyroscope data sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Sensores.this, "Failed to send gyroscope data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        String luzId = rtdbLuz.push().getKey();
        Map<String, Object> luzData = new HashMap<>();
        luzData.put("user", nombreUsuario);
        luzData.put("IntensidadLum", luz);
        luzData.put("timestamp", timestamp);

        assert luzId != null;
        rtdbLuz.child(luzId).setValue(luzData);

        String magId = rtdbMagnet.push().getKey();
        Map<String, Object> magData = new HashMap<>();
        magData.put("user", nombreUsuario);
        magData.put("mx", lastGX);
        magData.put("my", lastGY);
        magData.put("mz", lastGZ);
        magData.put("timestamp", timestamp);

        assert magId != null;
        rtdbMagnet.child(magId).setValue(magData);
    }
    private void sendPasosFirebase(){
        long timestamp = System.currentTimeMillis();

        String pasoId = rtdbPasos.push().getKey();
        Map<String, Object> pasosData = new HashMap<>();
        pasosData.put("user", nombreUsuario);
        pasosData.put("NPasos", pasos);
        pasosData.put("timestamp", timestamp);

        assert pasoId != null;
        rtdbPasos.child(pasoId).setValue(pasosData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Sensores.this, "Steps data sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Sensores.this, "Failed to send accelerometer data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
