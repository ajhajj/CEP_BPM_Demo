package com.redhat.demo.model;

public class Thresholds
  {
    static private double engineTempLow;

    static public double getEngineTempLow()
      {
        return(engineTempLow);
      }

    static public void setEngineTempLow(double temp)
      {
        engineTempLow = temp;
      }
  }
