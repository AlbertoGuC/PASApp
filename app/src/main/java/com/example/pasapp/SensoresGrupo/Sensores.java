package com.example.pasapp.SensoresGrupo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
    private Sensor acelerometro, giroscopo, luzometro, magnetometro;
    private float lastX, lastY, lastZ;
    private float lastGX, lastGY, lastGZ;
    private float luz;
    private float MagX, MagY, MagZ;
    private DatabaseReference rtdbAcelerometro, rtdbGiroscopo, rtdbLuz, rtdbMagnet;
    private static final long tiempo = 10000;
    private TextView VAceX, VAceY, VAceZ;
    private TextView VGX, VGY, VGZ;
    private TextView VLuz;
    private TextView VMagX, VMagY, VMagZ;
    private FirebaseAuth mAuth;
    private FirebaseUser usuario;
    private String nombreUsuario;
    private Thread thread;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensores_vista);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        mAuth = FirebaseAuth.getInstance();
        usuario = mAuth.getCurrentUser();

        if (usuario != null) {
            nombreUsuario = usuario.getDisplayName();
        } else {
            nombreUsuario = "No Logeado";
        }
        VAceX = findViewById(R.id.AceX);
        VAceY = findViewById(R.id.AceY);
        VAceZ = findViewById(R.id.AceZ);
        VGX = findViewById(R.id.GX);
        VGY = findViewById(R.id.GY);
        VGZ = findViewById(R.id.GZ);
        VLuz = findViewById(R.id.Luz);
        VMagX = findViewById(R.id.MagX);
        VMagY = findViewById(R.id.MagY);
        VMagZ = findViewById(R.id.MagZ);

        rtdbAcelerometro = FirebaseDatabase.getInstance().getReference("Acelerómetro");
        rtdbGiroscopo = FirebaseDatabase.getInstance().getReference("Gisóscopo");
        rtdbLuz = FirebaseDatabase.getInstance().getReference("Luz");
        rtdbMagnet = FirebaseDatabase.getInstance().getReference("Magnetómetro");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            giroscopo = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            luzometro = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, giroscopo, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, luzometro, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
        Toast.makeText(this, "Se empezarán a mandar los datos cada 10 segundos", Toast.LENGTH_LONG).show();
        thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(tiempo);
                } catch (InterruptedException e) {
                    break;
                }
                sendSensorDataToFirebase();
                insertSensorDataToSQLite();
            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
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
            case Sensor.TYPE_LIGHT:
                luz = event.values[0];

                VLuz.setText("Intensidad luz: " + luz);
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

        //Acelerometro
        String accelId = rtdbAcelerometro.push().getKey();
        Map<String, Object> accelData = new HashMap<>();
        accelData.put("user", nombreUsuario);
        accelData.put("x", lastX);
        accelData.put("y", lastY);
        accelData.put("z", lastZ);
        accelData.put("timestamp", timestamp);

        assert accelId != null;
        rtdbAcelerometro.child(accelId).setValue(accelData);

        // Giroscopio
        String gyroId = rtdbGiroscopo.push().getKey();
        Map<String, Object> gyroData = new HashMap<>();
        gyroData.put("user", nombreUsuario);
        gyroData.put("gx", lastGX);
        gyroData.put("gy", lastGY);
        gyroData.put("gz", lastGZ);
        gyroData.put("timestamp", timestamp);

        assert gyroId != null;
        rtdbGiroscopo.child(gyroId).setValue(gyroData);
        // Sensor de luz
        String luzId = rtdbLuz.push().getKey();
        Map<String, Object> luzData = new HashMap<>();
        luzData.put("user", nombreUsuario);
        luzData.put("IntensidadLum", luz);
        luzData.put("timestamp", timestamp);

        assert luzId != null;
        rtdbLuz.child(luzId).setValue(luzData);
        //Magnetometro
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

    private void insertSensorDataToSQLite() {
        ContentValues values = new ContentValues();
        values.put(TablaDatosContract.TablaEntry.COLUMN_SENSOR, "Acelerómetro");
        values.put(TablaDatosContract.TablaEntry.COLUMN_MEDIDA1, lastX);
        values.put(TablaDatosContract.TablaEntry.COLUMN_MEDIDA2, lastY);
        values.put(TablaDatosContract.TablaEntry.COLUMN_MEDIDA3, lastZ);
        db.insert(TablaDatosContract.TablaEntry.TABLE_NAME, null, values);

        values.clear();
        values.put(TablaDatosContract.TablaEntry.COLUMN_SENSOR, "Giroscopio");
        values.put(TablaDatosContract.TablaEntry.COLUMN_MEDIDA1, lastGX);
        values.put(TablaDatosContract.TablaEntry.COLUMN_MEDIDA2, lastGY);
        values.put(TablaDatosContract.TablaEntry.COLUMN_MEDIDA3, lastGZ);
        db.insert(TablaDatosContract.TablaEntry.TABLE_NAME, null, values);

        values.clear();
        values.put(TablaDatosContract.TablaEntry.COLUMN_SENSOR, "Luz");
        values.put(TablaDatosContract.TablaEntry.COLUMN_MEDIDA1, luz);
        values.put(TablaDatosContract.TablaEntry.COLUMN_MEDIDA2, 0);  // No se utiliza
        values.put(TablaDatosContract.TablaEntry.COLUMN_MEDIDA3, 0);  // No se utiliza
        db.insert(TablaDatosContract.TablaEntry.TABLE_NAME, null, values);

        values.clear();
        values.put(TablaDatosContract.TablaEntry.COLUMN_SENSOR, "Magnetómetro");
        values.put(TablaDatosContract.TablaEntry.COLUMN_MEDIDA1, MagX);
        values.put(TablaDatosContract.TablaEntry.COLUMN_MEDIDA2, MagY);
        values.put(TablaDatosContract.TablaEntry.COLUMN_MEDIDA3, MagZ);
        db.insert(TablaDatosContract.TablaEntry.TABLE_NAME, null, values);
    }
    public void launchBBDD(View view) {
        Intent intent = new Intent(this, DatosSensores.class);
        startActivity(intent);
    }
}
