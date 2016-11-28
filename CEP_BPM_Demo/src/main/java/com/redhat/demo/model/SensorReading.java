package com.redhat.demo.model;

public class SensorReading
  {
    private String sensorId;
    private long   timestamp;
    private double value;
    private String unit;

    public SensorReading(String id, long time, double value, String unit)
      {
        this.sensorId = id;
        this.timestamp = time;
        this.value = value;
        this.unit = unit;
      }

    public String getSensorId()
      {
        return(sensorId);
      }

    public void setSensorId(String sensorId)
      {
        this.sensorId = sensorId;
      }

    public long getTimestamp()
      {
        return(timestamp);
      }

    public void setTimestamp(long timestamp)
      {
        this.timestamp = timestamp;
      }

    public double getValue()
      {
        return(value);
      }

    public void setValue(double value)
      {
        this.value = value;
      }

    public String getUnit()
      {
        return(unit);
      }

    public void setUnit(String unit)
      {
        this.unit = unit;
      }
  }
