/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.drools.mas.ReverseLineInputStream;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.util.helper.SessionLocator;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 *
 * @author esteban
 */
public class MgmtConsoleServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(MgmtConsoleServlet.class);
    private static Properties agentProperties;
    private static String agentUrl;
    private static String logFile;

    @Autowired
    private DroolsAgent agent;

    @Override
    public void init() throws ServletException {
        try {
            super.init();

            SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());

            agentProperties = new Properties();
            agentProperties.load(MgmtConsoleServlet.class.getResourceAsStream("/agentsConfig.properties"));

            agentUrl = agentProperties.getProperty("agent.endpoint.url");
            logFile = agentProperties.getProperty("agent.logfile");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try (PrintWriter out = response.getWriter()) {
            String action = request.getParameter("action");

            Gson gson = new Gson();
            JsonObject result = null;
            switch (action) {
                case "listAgentConfigProperties":
                    result = this.listAgentConfigProperties();
                    break;
                case "getSessionsStatus":
                    result = this.getSessionsStatus();
                    break;
                case "getSessionFactsDetails":
                    String sessionId = request.getParameter("sessionId");
                    result = this.getSessionFactsDetails(sessionId);
                    break;
                case "getLogMessages":
                    int startIndex = request.getParameter("startIndex") == null? 0 : Integer.parseInt(request.getParameter("startIndex"));
                    int numberOfRecords = request.getParameter("numberOfRecords") == null? 20 : Integer.parseInt(request.getParameter("numberOfRecords"));
                    result = this.getLogLines(startIndex, numberOfRecords);
                    break;
                case "testUrl":
                    String url = request.getParameter("url");
                    result = this.testUrl(url);
                    break;
                case "writeLogLines":
                    response.setContentType("text/plain;charset=UTF-8");
                    this.writeLogLines(out);
                    return;
            }

            response.setContentType("text/json;charset=UTF-8");
            if (result != null) {
                out.write(gson.toJson(result));
            } else {
                out.write("{}");
            }
        }
    }

    private JsonObject listAgentConfigProperties() {
        JsonObject result = new JsonObject();
        for (Map.Entry<Object, Object> entry : agentProperties.entrySet()) {
            result.addProperty(entry.getKey().toString().replace(".", "_"), (String) entry.getValue());
        }

        return result;
    }

    private JsonObject getSessionsStatus() {

        JsonObject result = new JsonObject();
        JsonArray sessions = new JsonArray();
        result.add("sessions", sessions);

        //We can't invoke the agent as a WS (i.e. using DialgueHelper) because 
        //we need to interact with the mind and not the subsessions.
        QueryResults results = agent.getMind().getQueryResults("getSessions");
        Iterator<QueryResultsRow> iterator = results.iterator();
        while (iterator.hasNext()) {
            QueryResultsRow queryResultsRow = iterator.next();
            SessionLocator sessionLocator = (SessionLocator) queryResultsRow.get("$sessionLocator");

            JsonObject session = new JsonObject();
            session.addProperty("nodeId", sessionLocator.getNodeId());
            session.addProperty("sessionId", sessionLocator.getSessionId());
            session.addProperty("mind", sessionLocator.isMind());

            StatefulKnowledgeSession innerSession = agent.getInnerSession(sessionLocator.getSessionId());
            session.addProperty("objectCount", innerSession.getObjects().size());

            sessions.add(session);
        }

        return result;
    }
    
    private JsonObject getSessionFactsDetails(String sessionId) {

        JsonObject result = new JsonObject();
        JsonArray facts = new JsonArray();
        result.add("facts", facts);
        
        //We can't invoke the agent as a WS (i.e. using DialgueHelper) because 
        //we need to interact with the mind and not the subsessions.
        QueryResults results = agent.getMind().getQueryResults("getSessionById",sessionId);
        Iterator<QueryResultsRow> iterator = results.iterator();
        while (iterator.hasNext()) {
            QueryResultsRow queryResultsRow = iterator.next();
            SessionLocator sessionLocator = (SessionLocator) queryResultsRow.get("$sessionLocator");
            StatefulKnowledgeSession innerSession = agent.getInnerSession(sessionLocator.getSessionId());
            
            //count how many objects of each class the session has
            Map<String, Long> factCounter = new HashMap<>();
            for (Object fact : innerSession.getObjects()) {
                Long c = factCounter.get(fact.getClass().getName());
                c = c == null? 1L : ++c;
                factCounter.put(fact.getClass().getName(), c);
            }
            
            for (Map.Entry<String, Long> entry : factCounter.entrySet()) {
                JsonObject fact = new JsonObject();
                fact.addProperty("class", entry.getKey());
                fact.addProperty("count", entry.getValue());
                facts.add(fact);
            }
        }

        return result;
    }

    private void writeLogLines(Writer writer) throws FileNotFoundException, IOException {
        IOUtils.copy(new BufferedReader(new FileReader(new File(logFile))), writer);
    }
    
    private JsonObject getLogLines(int startIndex, int numberOfLines) throws FileNotFoundException, IOException {
        
        JsonObject result = new JsonObject();
        JsonArray messages = new JsonArray();
        
        result.add("messages", messages);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(new File(logFile))));

        for (int i = 0; i < startIndex+numberOfLines; i++) {
            String line = in.readLine();
            if (line == null){
                break;
            }
            if (i >= startIndex){
                JsonObject message = new JsonObject();
                message.addProperty("message", line);
                messages.add(message);
            }
        }
        
        return result;
    }
    
    private JsonObject testUrl(String urlString) throws MalformedURLException, IOException{
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int code = connection.getResponseCode();
        
        JsonObject result = new JsonObject();
        result.addProperty("responseCode", code);
        
        return result;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
