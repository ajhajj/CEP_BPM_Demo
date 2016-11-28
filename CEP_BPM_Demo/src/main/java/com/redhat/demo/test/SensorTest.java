package com.redhat.demo.test;

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
    private static final Logger LOGGER     = LoggerFactory.getLogger(SensorTest.class);
    private static final String CEP_STREAM = "KPI Stream";

    public static void main(String[] args)
      {
        try
          {
            // initialize the knowledge base
            LOGGER.info("Initialize KIE.");
            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();
            LOGGER.info("Creating KieSession.");
            KieSession kSession = kContainer.newKieSession("ksession-rules");

            // create instance of Thresholds config class and set as global
            Thresholds t = new Thresholds();
            kSession.setGlobal("thresholds", t);

            // register channel
            kSession.registerChannel("process", new ProcessChannel());
            
            // get handle for KPI metrics stream
            EntryPoint ep = kSession.getEntryPoint(CEP_STREAM);

            LOGGER.info("Setting low heat rate threshold to 100.");
            Thresholds.setEngineTempLow(100.0);
            SensorReading s = new SensorReading("engineTemp", 0L, 120.6, "C");
            LOGGER.info("Inserting event with heat rate of " + s.getValue());
            FactHandle factHandle = ep.insert(s);
            LOGGER.debug("FactHandle: " + factHandle.toExternalForm());
            kSession.fireAllRules();

            LOGGER.info("Setting low heat rate threshold to 200.");
            Thresholds.setEngineTempLow(200.0);
            s = new SensorReading("engineTemp", 5L, 150.6, "C");
            LOGGER.info("Inserting event with heat rate of " + s.getValue());
            factHandle = ep.insert(s);
            LOGGER.debug("FactHandle: " + factHandle.toExternalForm());
            kSession.fireAllRules();

            LOGGER.info("Disposing session.");
            kSession.dispose();
          }

        catch(Throwable t)
          {
            t.printStackTrace();
          }
      }
  }
