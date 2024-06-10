package com.example.pasapp.SensoresGrupo;

public final class TablaDatos {
    private int id;
    private String Sensor;
    private Float Medida1;
    private Float Medida2;
    private Float Medida3;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSensor() {
        return Sensor;
    }

    public void setSensor(String sensor) {
        Sensor = sensor;
    }

    public Float getMedida1() {
        return Medida1;
    }

    public void setMedida1(Float medida1) {
        Medida1 = medida1;
    }

    public Float getMedida2() {
        return Medida2;
    }

    public void setMedida2(Float medida2) {
        Medida2 = medida2;
    }

    public Float getMedida3() {
        return Medida3;
    }

    public void setMedida3(Float medida3) {
        Medida3 = medida3;
    }
}

