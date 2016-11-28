package com.redhat.demo.channel;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;

public class ProcessChannel implements Channel
  {
    public static final String CHANNEL_ID   = "process";
    public static final String MSG_TOKEN    = "##";
    private static URL         instanceUrl  = null;
    private String             deploymentId = "com.redhat.demo.bpm:kpi:1.4";
    private static String      user         = "bpmsAdmin";
    private static String      password     = "gt2wrk*1";

    static
      {
        try
          {
            instanceUrl = new URL("http://localhost:8080/business-central/");
          }
        catch(Exception ex)
          {
            System.err.println(ex);
          }
      }

    public void send(Object object)
      {
        String msg = null;
        String processid = "";
        String[] parts = null;
        HashMap<String, Object> procVars = new HashMap<>();
        Calendar cal = null;
        SimpleDateFormat sdf = null;
        
        System.out.println("ProcessChannel called");
        if((object != null) && ((msg = object.toString()).indexOf(MSG_TOKEN) != -1))
          {
            parts = msg.split(MSG_TOKEN);
            
            if(parts.length == 3)
              {
                try
                  {
                    Float sensor = new Float(parts[0]);
                    Float threshold = new Float(parts[1]);
                    procVars.put("sensorTemp", sensor);
                    procVars.put("threshold", threshold);
                    cal = Calendar.getInstance();
                    sdf = new SimpleDateFormat("HH:mm:ss");
                    procVars.put("name", "[" + sdf.format(cal.getTime()) + "] " + parts[2]);
                  }
                catch(Exception ex)
                  {
                    System.err.println(ex);
                  }
              }

            for(int i = 0; i < parts.length; i++)
              System.out.println("Part" + i + ": " + parts[i]);
            
            if(parts[2].equals("Heat rate too low"))
              {
                System.out.println("received heat rate below provided threshold");
                processid = "kpi.heatRateLowNotify";
              }
            else
              processid = "kpi.defaultProcess";
              
            System.out.println("calling BPM");
            startProcess(processid, procVars);
            
          }
      }

    private ProcessInstance startProcess(String processid, Map<String, Object> variables)
      {
        ProcessInstance processInstance = null;
        
        // Set up the factory class with the necessary information to communicate
        // with the REST services.
        try
          {
            RuntimeEngine engine = RemoteRuntimeEngineFactory.newRestBuilder().addUrl(instanceUrl).addUserName(user).addPassword(password).addDeploymentId(deploymentId).build();
            KieSession ksession = engine.getKieSession();
            processInstance = ksession.startProcess(processid, variables);
          }
        catch(Exception ex)
          {
            System.err.println(ex);            
          }
        return(processInstance);
      }
  }