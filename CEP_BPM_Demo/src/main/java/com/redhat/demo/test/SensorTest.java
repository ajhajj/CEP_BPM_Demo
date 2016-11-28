package com.redhat.demo.test;

import java.io.InputStream;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.demo.channel.ProcessChannel;
import com.redhat.demo.model.SensorReading;
import com.redhat.demo.model.Thresholds;

@SuppressWarnings("restriction")
public class SensorTest
  {
    private static final Logger LOGGER          = LoggerFactory.getLogger(SensorTest.class);
    private static final String CEP_STREAM      = "KPI Stream";
    private static final String EVENTS_CSV_FILE = "events.csv";

    public static void main(String[] args)
      {
        FactHandle factHandle = null;
        KieServices ks = null;
        KieContainer kContainer = null;
        KieSession kSession = null;
        Thresholds thresholds = null;
        EntryPoint ep = null;
        InputStream eventsInputStream = null;
        List<SensorReading> events = null;
        int count = 0;
        
        try
          {
            // initialize the knowledge base
            LOGGER.info("Initialize KIE.");
            ks = KieServices.Factory.get();
            kContainer = ks.getKieClasspathContainer();
            LOGGER.info("Creating KieSession.");
            kSession = kContainer.newKieSession("ksession-rules");

            // create instance of Thresholds config class and set as global
            thresholds = new Thresholds();
            kSession.setGlobal("thresholds", thresholds);

            // register channel
            kSession.registerChannel("process", new ProcessChannel());

            // get handle for KPI metrics stream
            ep = kSession.getEntryPoint(CEP_STREAM);

            Thresholds.setEngineTempLow(100.0);
            LOGGER.info("Setting low heat rate threshold to " + Thresholds.getEngineTempLow());

            eventsInputStream = SensorTest.class.getClassLoader().getResourceAsStream(EVENTS_CSV_FILE);
            events = FactsLoader.loadEvents(eventsInputStream);

            if(events != null)
              {
                for(SensorReading nextEvent : events)
                  {
                    if(++count == 10)
                      {
                        Thresholds.setEngineTempLow(200.0);
                        LOGGER.info("Setting low heat rate threshold to " + Thresholds.getEngineTempLow());
                      }
                    
                    LOGGER.info("Inserting event with heat rate of " + nextEvent.getValue());
                    factHandle = ep.insert(nextEvent);
                    LOGGER.debug("FactHandle: " + factHandle.toExternalForm());
                    kSession.fireAllRules();
                  }
              }

            LOGGER.info("Disposing session.");
            kSession.dispose();
          }

        catch(Throwable t)
          {
            t.printStackTrace();
          }
      }
  }
