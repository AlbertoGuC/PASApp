package com.example.pasapp.SensoresGrupo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pasapp.R;

public class DatosSensores extends AppCompatActivity {

    private TextView datosTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_sensores);

        datosTextView = findViewById(R.id.datosTextView);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TablaDatosContract.TablaEntry.TABLE_NAME,
                null,  // Todas las columnas
                null,  // No hay cláusula WHERE
                null,  // No hay valores para la cláusula WHERE
                null,  // No hay cláusula GROUP BY
                null,  // No hay cláusula HAVING
                null   // Orden por defecto
        );

        StringBuilder datos = new StringBuilder();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TablaDatosContract.TablaEntry._ID));
            String sensor = cursor.getString(cursor.getColumnIndexOrThrow(TablaDatosContract.TablaEntry.COLUMN_SENSOR));
            float medida1 = cursor.getFloat(cursor.getColumnIndexOrThrow(TablaDatosContract.TablaEntry.COLUMN_MEDIDA1));
            float medida2 = cursor.getFloat(cursor.getColumnIndexOrThrow(TablaDatosContract.TablaEntry.COLUMN_MEDIDA2));
            float medida3 = cursor.getFloat(cursor.getColumnIndexOrThrow(TablaDatosContract.TablaEntry.COLUMN_MEDIDA3));

            datos.append("ID: ").append(id)
                    .append(", Sensor: ").append(sensor)
                    .append(", Medida1: ").append(medida1)
                    .append(", Medida2: ").append(medida2)
                    .append(", Medida3: ").append(medida3)
                    .append("\n\n");
        }
        cursor.close();

        datosTextView.setText(datos.toString());
    }
}
