package com.redhat.demo.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.demo.model.SensorReading;

/**
 * Loads {@link SensorReading objects from the given (CSV) file.
 */
@SuppressWarnings("restriction")
public class FactsLoader
  {
    public static final String  SEPARATOR_TOKEN = ",";
    public static final String  COMMENT_TOKEN   = "#";
    private static final Logger LOGGER          = LoggerFactory.getLogger(FactsLoader.class);

    public static List<SensorReading> loadEvents(File eventsFile)
      {

        BufferedReader br;
        try
          {
            br = new BufferedReader(new FileReader(eventsFile));
          }
        catch(FileNotFoundException fnfe)
          {
            String message = "File not found.";
            LOGGER.error(message, fnfe);
            throw new IllegalArgumentException(message, fnfe);
          }

        return(loadEvents(br));
      }

    public static List<SensorReading> loadEvents(InputStream eventsInputStream)
      {
        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(eventsInputStream));

        return(loadEvents(br));
      }

    private static List<SensorReading> loadEvents(BufferedReader reader)
      {
        String nextLine = null;
        SensorReading sensorEvent = null;
        List<SensorReading> eventList = new ArrayList<SensorReading>();

        try
          {
            while((nextLine = reader.readLine()) != null)
              {
                nextLine = nextLine.trim();
                if((nextLine.length() > 0) && !nextLine.startsWith(COMMENT_TOKEN))
                  {
                    sensorEvent = readEvent(nextLine);
                    if(sensorEvent != null)
                      eventList.add(sensorEvent);
                  }
              }
          }
        catch(IOException ioe)
          {
            throw new RuntimeException("Got an IO exception while reading events.", ioe);
          }
        finally
          {
            if(reader != null)
              {
                try
                  {
                    reader.close();
                  }
                catch(IOException ioe)
                  {
                    // Swallowing exception, not much we can do here.
                    LOGGER.error("Unable to close reader.", ioe);
                  }
              }
          }

        return(eventList);
      }

    /**
     * Layout of a Sensor KPI line has to be {sensorId}, {value}, {unit}.
     * 
     * @param line
     *          the line to parse.
     * @return the {@link SensorReading}
     */
    private static SensorReading readEvent(String line)
      {
        SensorReading event = null;
        String[] eventData = null;

        eventData = line.split(SEPARATOR_TOKEN);

        if(eventData.length != 3)
          LOGGER.error("Unable to parse string: " + line);
        else
          {
            try
              {
                event = new SensorReading(eventData[0].trim(), 0L, Double.parseDouble(eventData[1].trim()), eventData[2].trim());
              }
            catch(NumberFormatException nfe)
              {
                LOGGER.error("Error parsing line: " + line, nfe);
              }
          }

        return(event);
      }
  }