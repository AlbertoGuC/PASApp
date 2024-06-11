package com.example.pasapp.SensoresGrupo;

import android.provider.BaseColumns;

public final class TablaDatosContract {
    private TablaDatosContract() {}

    public static class TablaEntry implements BaseColumns {
        public static final String TABLE_NAME = "Sensores";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_SENSOR = "Sensor";
        public static final String COLUMN_MEDIDA1 = "Medida1";
        public static final String COLUMN_MEDIDA2 = "Medida2";
        public static final String COLUMN_MEDIDA3 = "Medida3";
    }
}

