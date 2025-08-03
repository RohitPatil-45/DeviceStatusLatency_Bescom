/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npm.prob.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.icmp4j.Icmp4jUtil;
import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingUtil;
import npm.prob.dao.DatabaseHelper;
import npm.prob.datasource.Datasource;
import npm.prob.model.EventLog;

import npm.prob.model.NodeStausModel;
import npm.prob.model.LatencyModel;
import npm.prob.model.NodeMasterModel;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author NPM
 */
public class NodeMon implements Runnable {

    List ip_list = null;
    String isAffected = "";
    String problem = "";
    String serviceId = "";

    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;

    private final ObjectMapper mapper = new ObjectMapper();

    NodeMon(List liste) {
        this.ip_list = liste;
    }

    public void run() {
        // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        //Rohit
        System.out.println(Thread.currentThread().getName() + ":IPList:" + ip_list);

        HashMap hMap = new HashMap<String, String>();
        Connection xmlcon = null;
        try {

            xmlcon = Datasource.getConnection();
            for (int i = 0; i < ip_list.size(); i++) {

                String SQL = "select NODE_IP, NODE_STATUS  from node_monitoring where NODE_IP ='" + ip_list.get(i).toString().replaceAll(" ", "") + "'";
                Statement customerRS = xmlcon.createStatement();
                ResultSet xmlrs = customerRS.executeQuery(SQL);
                while (xmlrs.next()) {
                    ////System.out.println("value:" + xmlrs.getString(2));
                    hMap.put(xmlrs.getString(1), xmlrs.getString(2));
                }

            }
        } catch (Exception ex) {
            //System.out.println("create error " + ex);
        } finally {
            if (xmlcon != null) {
                try {
                    xmlcon.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());

                }
            }
        }
        ArrayList templist = null;
        templist = new ArrayList();
        try {
            templist.addAll(ip_list);
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }

        System.out.println(" Status:" + hMap);
        //System.out.println("start monitoring");
        Icmp4jUtil.initialize();
        //System.out.println("Initialize ICMP");
        while (true) {

            try {

                Thread.sleep(10000); // pooling time

                Iterator it2 = templist.iterator();

                //  System.out.println("In router ping list=" + templist);
                while (it2.hasNext()) {

                    String router_status = "";
                    String router_status_xml = null;
                    String device_ip = null;
                    try {

                        String tep_r_ip = null;
                        tep_r_ip = it2.next().toString();
                        device_ip = tep_r_ip.replaceAll(" ", "");

                        //String laptencyHisotryParam = "Yes";
                        int latency_threshold = 100;
                        NodeMasterModel nodeData = NodeStatusLatencyMonitoring.mapNodeData.get(device_ip);
                        // String laptencyHisotryParam = nodeData.getLATENCY_HISTORY();
                        try {
                            latency_threshold = nodeData.getLATENCY_THRESHOLD();
                        } catch (Exception e) {
                            System.out.println("Exception latency Threshold:" + e);
                        }
                        //                       System.out.println(device_ip + ":Node Param:" + laptencyHisotryParam + ":" + latency_threshold);
                        float total = 0;
                        float timeout = 0;
                        float transmit = 0;
                        float status_count = 0;
                        float timeR = 0;
                        int max_responce = 0;
                        int min_responce = 100000;
                        int loop_count = 0;
                        float avg_responce = 0;
                        float drop_per = 0;

                        try {
                            //  System.out.println("ping requests");
                            final IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest();
                            request.setHost(device_ip);
                            //  while (System.currentTimeMillis() < end) {
                            for (int count = 1; count <= 3; count++) {
                                // System.out.println("Start:"+count);
                                final IcmpPingResponse response = IcmpPingUtil.executePingRequest(request);
                                final String formattedResponse = IcmpPingUtil.formatResponse(response);
                                //System.out.println("FormatString==:" + formattedResponse);
                                Pattern pattern = Pattern.compile("time=(\\d+)ms");
                                Matcher m = null;
                                m = pattern.matcher(formattedResponse);
                                if (m.find()) {
                                    loop_count = loop_count + 1;
                                    int responce_timee = Integer.parseInt(m.group(1));
                                    timeR = timeR + responce_timee;
                                    if (responce_timee > max_responce) {
                                        max_responce = responce_timee;
                                    }
                                    if (responce_timee < min_responce) {
                                        min_responce = responce_timee;
                                    }
                                }
                                total = total + 1;
                                if (formattedResponse.contains("Timeout") || formattedResponse.contains("IP_DEST_HOST_UNREACHABLE") || formattedResponse.contains("IP_TTL_EXPIRED_TRANSIT") || formattedResponse.contains("IP_DEST_NET_UNREACHABLE")) {
                                    timeout = timeout + 1;

                                    status_count = status_count + 1;
                                } else {
                                    transmit = transmit + 1;
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(device_ip + "icmp ping exception:" + e);
//                            try {
//                                Thread.sleep(5000);
//                            } catch (Exception e5) {
//                            }
                        }

                        if (min_responce == 100000) {
                            min_responce = 0;
                        }
                        try {
                            drop_per = (timeout / total) * 100;
                            if (drop_per == 100.0) {
                                avg_responce = 0;
                            } else {
                                avg_responce = timeR / loop_count;
                            }
                        } catch (Exception e) {
                            System.out.println("Exception" + e);
                        }

                        //  System.out.println(device_ip + "Data:" + avg_responce);
                        // latency Update
                        try {
                            LatencyModel updateList = null;
                            updateList = new LatencyModel();
                            updateList.setDevice_ip(device_ip);
                            updateList.setAvg_response(avg_responce);
                            updateList.setMin_response(min_responce);
                            updateList.setMax_response(max_responce);
                            updateList.setPacket_loss(drop_per);
                            NodeStatusLatencyMonitoring.latency_update.add(updateList);
                        } catch (Exception e) {
                            System.out.println("Exception in Add Update List " + e);
                        }
                        float down_percent = 0;
                        try {
                            down_percent = ((status_count * 100) / total);
                        } catch (Exception ex) {
                            down_percent = 0;
                            System.out.println("Exception in down percent=" + ex);
                        }

                        //check Latency
                        try {
                            if (down_percent <= 80) {
                                checkDeviceLatency(device_ip, latency_threshold, avg_responce, nodeData.getDEVICE_NAME(), nodeData.getDEVICE_TYPE());
                            }
                        } catch (Exception e) {
                            System.out.println("Latency Exception:" + e);
                        }
                        //Status Monitoring
                        //System.out.println(device_ip + " : down_percent:" + down_percent);
                        try {
                            System.out.println(device_ip + ":Old status hashmap data:" + hMap + ":" + device_ip);
                            router_status_xml = hMap.get(device_ip).toString();
                            // System.out.println(device_ip + " :old  router status xml ::::@" + router_status_xml);
                            if (down_percent >= 60) {
                                router_status = "Down";
                                // System.out.println("########Router Down:  " + device_ip + ":" + router_status_xml);
                            } else {
                                router_status = "Up";
                                // System.out.println("@@@@@@Router UP:" + device_ip + ":" + router_status_xml);
                            }
                        } catch (Exception e) {
                            System.out.println(device_ip + ":Exception old status:" + e);
                            e.printStackTrace();
                        }

//                        LocalTime startOfWork = LocalTime.of(7, 0); // 9:00 AM
//                        LocalTime endOfWork = LocalTime.of(21, 0);  // 6:00 PM
//
//                        // Get the current time
//                        LocalTime currentTime = LocalTime.now();
//
//                        // Check if the current time is within working hours
//                        boolean isWorkingHour = !currentTime.isBefore(startOfWork) && !currentTime.isAfter(endOfWork);
//                        LocalTime startOfWork = LocalTime.of(7, 0); // 7:00 AM
//                        LocalTime endOfWork = LocalTime.of(21, 0);  // 9:00 PM
                        LocalTime startOfWork = LocalTime.of(8, 0); // 7:00 AM
                        LocalTime endOfWork = LocalTime.of(19, 0);  // 9:00 PM

                        // Get the current time and day
                        LocalTime currentTime = LocalTime.now();
                        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();

                        // Check if it's a working day (Monday to Friday)
                        boolean isWorkingDay = currentDay != DayOfWeek.SATURDAY && currentDay != DayOfWeek.SUNDAY;

                        // Check if the current time is within working hours
                        boolean isWorkingHour = isWorkingDay && !currentTime.isBefore(startOfWork) && !currentTime.isAfter(endOfWork);

                        //  System.out.println("Is it working hour? " + isWorkingHour);
                        int workingHourFlag = 1;

                        if (isWorkingHour) {
                            workingHourFlag = 1;
                        } else {
                            workingHourFlag = 0;
                        }
                        LocalDateTime currentDateTime = LocalDateTime.now();

                        // Format it as a string in 'YYYY-MM-DD HH:MM:SS' format
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDateTime = currentDateTime.format(formatter);

                        //Latnecy Hisotry
                        //  if (laptencyHisotryParam != null && laptencyHisotryParam.toLowerCase().equals("yes")) {
                        long epochTime = System.currentTimeMillis() / 1000;

                        try {
                            LatencyModel logLatency = null;
                            logLatency = new LatencyModel();
                            logLatency.setDevice_ip(device_ip);
                            logLatency.setAvg_response(avg_responce);
                            logLatency.setMin_response(min_responce);
                            logLatency.setMax_response(max_responce);
                            logLatency.setPacket_loss(drop_per);
                            logLatency.setDevice_status(router_status);
                            logLatency.setWorkingHourFlag(workingHourFlag);
                            logLatency.setDatetime(formattedDateTime);
                            logLatency.setEpochTimeL(epochTime);
                            NodeStatusLatencyMonitoring.latency_list.add(logLatency);
                        } catch (Exception exp) {
                            System.out.println(device_ip + "!!!!Exception router ping in Add Arraylist " + exp);
                        }
                        //  }

                        String eventMsg1 = "";
                        String netadminMsg = "";
                        //check Status start
                        if (router_status == null || router_status_xml == null || router_status.equals(router_status_xml)) {
                            //  //System.out.println("********************Not Change Router Status****************");
                        } else {
                            Timestamp event_time = new Timestamp(System.currentTimeMillis());
                            if (router_status_xml.equals("Up") && router_status.equals("Down")) {
                                System.out.println("1st down:" + device_ip);
                                hMap.put(device_ip, "Down1");

                            } else if (router_status_xml.equals("Down1") && router_status.equals("Down")) {
                                System.out.println("up to warrning:" + device_ip);
                                hMap.put(device_ip, "Down2");
                                updateDeviceStatus(device_ip, "Warning", event_time);
//                                try {
//                                    Thread.sleep(2000);
//                                } catch (Exception e) {
//                                    //System.out.println("e:" + e);
//                                }
                            } else if (router_status_xml.equals("Down2") && router_status.equals("Down")) {
                                System.out.println("@@$$Down Device:" + device_ip);

                                hMap.put(device_ip, "Down3");
                                updateDeviceStatus(device_ip, "Down", event_time);
                                deviceStatusLog(device_ip, "Down", event_time);
                                eventMsg1 = "PING ICMP : " + nodeData.getDEVICE_NAME() + " is Down";
                                netadminMsg = "Down : PING ICMP : pl = 100 - Got no echo reply";
                                isAffected = "1";
                                serviceId = "device_status";
                                problem = "problem";
                                // insertIntoEventLog(device_ip, nodeData.getDEVICE_NAME(), eventMsg1, 4, "PING ICMP", event_time, netadminMsg, isAffected, problem, serviceId, nodeData.getDEVICE_TYPE());

                                sendEventLogToApi(device_ip, nodeData.getDEVICE_NAME(), eventMsg1, 4, "PING ICMP", event_time, netadminMsg, isAffected, problem, serviceId, nodeData.getDEVICE_TYPE(), 0);

                            } else if (router_status_xml.equals("Down3") && router_status.equals("Down")) {
                                //    //System.out.println("%%%%%..Skip Down condition ");
                            } else if (router_status_xml.equals("Down3") && router_status.equals("Up")) {
                                System.out.println("Down to Up");
                                hMap.put(device_ip, "Up");
                                updateDeviceStatus(device_ip, "Up", event_time);
                                deviceStatusLog(device_ip, "Up", event_time);
                                eventMsg1 = "PING ICMP : " + nodeData.getDEVICE_NAME() + " is Up";
                                netadminMsg = "Up : PING ICMP : pl = 0";
                                isAffected = "0";
                                serviceId = "device_status";
                                problem = "Cleared";
//                                insertIntoEventLog(device_ip, nodeData.getDEVICE_NAME(), eventMsg1, 0, "PING ICMP", event_time, netadminMsg, isAffected, problem, serviceId, nodeData.getDEVICE_TYPE());
                                sendEventLogToApi(device_ip, nodeData.getDEVICE_NAME(), eventMsg1, 0, "PING ICMP", event_time, netadminMsg, isAffected, problem, serviceId, nodeData.getDEVICE_TYPE(), 0);
                                try {
                                    StatusChangeDiff t22 = null;
                                    t22 = new StatusChangeDiff();
                                    t22.insertStatusDiff(device_ip, event_time);
                                } catch (Exception e) {
                                    System.out.println("Uptime Thread Exception:" + e);
                                }

                            } else if (router_status_xml.equals("Down1") && router_status.equals("Up")) {
                                System.out.println("1st down then Up:" + device_ip);
                                hMap.put(device_ip, "Up");
                                updateDeviceStatus(device_ip, "Up", event_time);

                            } else if (router_status_xml.equals("Down2") && router_status.equals("Up")) {
                                System.out.println("2nd down Warning then Up:" + device_ip);;
                                hMap.put(device_ip, "Up");
                                updateDeviceStatus(device_ip, "Up", event_time);

                            } else if (router_status_xml.equals("Down") && router_status.equals("Up")) {

                                hMap.put(device_ip, "Up");
                                updateDeviceStatus(device_ip, "Up", event_time);
                                deviceStatusLog(device_ip, "Up", event_time);
                                eventMsg1 = "PING ICMP : " + nodeData.getDEVICE_NAME() + " is Up";
                                netadminMsg = "Up : PING ICMP : pl = 0";
                                isAffected = "0";
                                serviceId = "device_status";
                                problem = "Cleared";
                                //insertIntoEventLog(device_ip, nodeData.getDEVICE_NAME(), eventMsg1, 0, "PING ICMP", event_time, netadminMsg, isAffected, problem, serviceId, nodeData.getDEVICE_TYPE());
                                sendEventLogToApi(device_ip, nodeData.getDEVICE_NAME(), eventMsg1, 0, "PING ICMP", event_time, netadminMsg, isAffected, problem, serviceId, nodeData.getDEVICE_TYPE(), 0);

                                try {
                                    StatusChangeDiff t22 = null;
                                    t22 = new StatusChangeDiff();
                                    t22.insertStatusDiff(device_ip, event_time);
                                } catch (Exception e) {
                                    System.out.println("Uptime Thread Exception:" + e);
                                }
                            } else if (router_status_xml.equals("Warning") && router_status.equals("Up")) {
                                hMap.put(device_ip, "Up");
                                updateDeviceStatus(device_ip, "Up", event_time);
                                System.out.println("1st down then Up:" + device_ip);
                            } else if (router_status_xml.equals("Warning") && router_status.equals("Down")) {
                                hMap.put(device_ip, "Down");
                                updateDeviceStatus(device_ip, "Down", event_time);
                            } else {
                                //System.out.println(router_ipadress + "Else Condition*********************************** old:" + router_status_xml + ":New:" + router_status);
                            }

                        }
                        //check status end
                    } catch (Exception e) {
                        System.out.println("Exception:" + e);
                    }
                    //    LocalDateTime now2 = LocalDateTime.now();

                    // System.out.println("@Node montiroing end:" + device_ip + ":" + dtf.format(now2));
                }

            } catch (Exception e) {
                System.out.println("Exception:" + e);
            }

            try {
                Thread.sleep(30000);
            } catch (Exception e) {
                System.out.println("Exception:" + e);
            }

        }

    }

    public void deviceStatusLog(String device_ip, String device_status, Timestamp eventTime) {
        try {
            NodeStausModel node = new NodeStausModel();
            node.setDevice_ip(device_ip);
            node.setDevice_status(device_status);
            node.setEvent_time(new Timestamp(System.currentTimeMillis()));
            NodeStatusLatencyMonitoring.statusLogList.add(node);
        } catch (Exception exp) {
            System.out.println(device_ip + "Exception in adding update icmp status=" + exp);
        }
    }

    public void updateDeviceStatus(String device_ip, String device_status, Timestamp eventTime) {
        try {
            NodeStausModel node = new NodeStausModel();
            node.setDevice_ip(device_ip);
            node.setDevice_status(device_status);
            node.setEvent_time(eventTime);
            NodeStatusLatencyMonitoring.statusUpdateList.add(node);
        } catch (Exception exp) {
            System.out.println(device_ip + "Exception in adding update icmp status=" + exp);
        }
    }

    public void checkDeviceLatency(String device_ip, int latency_threshold, float avg_responce, String deviceName, String deviceType) {

        String eventMsg1 = "";
        String netadminMsg = "";
        String isAffected = "";
        String problem = "";
        String serviceId = "latency_threshold";

        try {
            String h_latencystatus = NodeStatusLatencyMonitoring.latency_map.get(device_ip).toString();
            Timestamp thresholdTime = new Timestamp(System.currentTimeMillis());
            if (avg_responce > latency_threshold && h_latencystatus.equals("Low")) {
                System.out.println("Latency Threshold:High" + avg_responce + " latency threshold value=" + latency_threshold + " latency status=" + h_latencystatus + " ip=" + device_ip);
                NodeStatusLatencyMonitoring.latency_map.put(device_ip, "High");
                DatabaseHelper db = new DatabaseHelper();
                db.latencyThreshold(device_ip, latency_threshold, avg_responce, "High");
                isAffected = "1";
                problem = "problem";
                eventMsg1 = "Alert: Latency threshold crossed above " + latency_threshold + "ms || " + device_ip;
                netadminMsg = "Latency Threshold:High" + avg_responce + " latency threshold value=" + latency_threshold + " latency status=High ip=" + device_ip;
//                insertIntoEventLog(device_ip, deviceName, eventMsg1, 4, "Latency threshold", thresholdTime, netadminMsg, isAffected, problem, serviceId, deviceType);
                sendEventLogToApi(device_ip, deviceName, eventMsg1, 4, "Latency threshold", thresholdTime, netadminMsg, isAffected, problem, serviceId, deviceType, 0);
            } else if (avg_responce < latency_threshold && h_latencystatus.equals("High")) {
                System.out.println("Latency Threshold:Low" + avg_responce + " latency threshold value=" + latency_threshold + " latency status=Low ip=" + device_ip);
                NodeStatusLatencyMonitoring.latency_map.put(device_ip, "Low");
                DatabaseHelper db = new DatabaseHelper();
                db.latencyThreshold(device_ip, latency_threshold, avg_responce, "Low");
                eventMsg1 = "Alert: Latency threshold change from " + latency_threshold + "ms || " + device_ip;
                netadminMsg = "Latency Threshold:Low" + avg_responce + " latency threshold value=" + latency_threshold + " latency status=Low ip=" + device_ip;
                isAffected = "0";
                problem = "Cleared";
//                insertIntoEventLog(device_ip, deviceName, eventMsg1, 0, "Latency threshold", thresholdTime, netadminMsg, isAffected, problem, serviceId, deviceType);
                sendEventLogToApi(device_ip, deviceName, eventMsg1, 0, "Latency threshold", thresholdTime, netadminMsg, isAffected, problem, serviceId, deviceType, 0);
            }
        } catch (Exception e4) {
            System.out.println(" latency threshold exception:" + e4);
        }

        //Stop Threshold  of latency
    }

    public void insertIntoEventLog(String deviceID, String deviceName, String eventMsg, int severity, String serviceName, Timestamp evenTimestamp, String netadmin_msg, String isAffected, String problem, String serviceId, String deviceType) {
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        Connection connection = null;
        try {
            connection = Datasource.getConnection();
            preparedStatement1 = connection.prepareStatement("INSERT INTO event_log (device_id, device_name, service_name, event_msg, netadmin_msg, severity,"
                    + " event_timestamp, acknowledgement_status, isAffected, Problem_Clear, Service_ID, Device_Type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement1.setString(1, deviceID);
            preparedStatement1.setString(2, deviceName);
            preparedStatement1.setString(3, serviceName);
            preparedStatement1.setString(4, eventMsg);
            preparedStatement1.setString(5, netadmin_msg);
            preparedStatement1.setInt(6, severity);
            preparedStatement1.setTimestamp(7, evenTimestamp);
            preparedStatement1.setBoolean(8, false);
            preparedStatement1.setString(9, isAffected);
            preparedStatement1.setString(10, problem);
            preparedStatement1.setString(11, serviceId);
            preparedStatement1.setString(12, deviceType);

            preparedStatement1.executeUpdate();

        } catch (Exception e) {
            System.out.println(deviceID + "inserting in event log Exception:" + e);
        } finally {
            try {
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception exp) {
                System.out.println("excep:" + exp);
            }
        }

        try {
            if ("Cleared".equalsIgnoreCase(problem)) {

                String updateQuery = "UPDATE event_log\n"
                        + "SET\n"
                        + "    Cleared_event_timestamp = ?,\n"
                        // + "    netadmin_msg = ?,\n"
                        + "netadmin_msg = CONCAT(netadmin_msg, ' => ', ?),\n"
                        + "    isAffected = ?\n"
                        + "WHERE\n"
                        + "    ID = (\n"
                        + "        SELECT id_alias.ID\n"
                        + "        FROM (\n"
                        + "            SELECT ID\n"
                        + "            FROM event_log\n"
                        + "            WHERE service_id = ?\n"
                        + "              AND device_id = ?\n"
                        + "            AND isaffected = '1' ORDER BY ID DESC\n"
                        + "            LIMIT 1\n"
                        + "        ) AS id_alias\n"
                        + "    )\n"
                        + ";";

                connection = Datasource.getConnection();

                preparedStatement2 = connection.prepareStatement(updateQuery);
                preparedStatement2.setTimestamp(1, evenTimestamp);

                preparedStatement2.setString(2, netadmin_msg); // To Do
                preparedStatement2.setString(3, "0");
                preparedStatement2.setString(4, serviceId);
                preparedStatement2.setString(5, deviceID);

                preparedStatement2.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Exception in update device node = " + e);
        } finally {
            try {
                if (preparedStatement2 != null) {
                    preparedStatement2.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception exp) {
                System.out.println("excep:" + exp);
            }
        }
    }

    public void sendEventLogToApi(String deviceID, String deviceName, String eventMsg, int severity, String serviceName, Timestamp evenTimestamp,
            String netadmin_msg, String isAffected, String problem, String serviceId, String deviceType, int attempt) {
        EventLog log = new EventLog();
        log.setDeviceId(deviceID);
        log.setDeviceName(deviceName);
        log.setEventMsg(eventMsg);
        log.setSeverity(String.valueOf(severity));
        log.setServiceName(serviceName);
        log.setEventTimestamp(evenTimestamp);
        log.setNetadminMsg(netadmin_msg);
        log.setIsaffected(Integer.valueOf(isAffected));
        log.setProblemClear(problem);
        log.setServiceID(serviceId);
        log.setDeviceType(deviceType);

        System.out.println("service id = " + serviceId);
        System.out.println("sAffected = " + isAffected);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = mapper.writeValueAsString(log);
            HttpPost request = new HttpPost("http://localhost:8083/api/event/log"); // adjust host/port
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(json));

            CloseableHttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 200 && statusCode < 300) {
                System.out.println("Log sent successfully: " + statusCode);
            } else {
                System.err.println("Failed to send log, status: " + statusCode);
                retryIfNeeded(log, attempt);
            }

            response.close();
        } catch (IOException e) {
            System.err.println("Exception while sending log: " + e.getMessage());
            retryIfNeeded(log, attempt);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void retryIfNeeded(EventLog log, int attempt) {
        if (attempt < MAX_RETRIES) {
            System.out.println("Retrying sendEventLogToApi... Attempt " + (attempt + 1));
            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // Preserve interrupt status
                return;
            }

            // Retry the API call with incremented attempt count
            sendEventLogToApi(
                    log.getDeviceId(),
                    log.getDeviceName(),
                    log.getEventMsg(),
                    Integer.valueOf(log.getSeverity()),
                    log.getServiceName(),
                    log.getEventTimestamp(),
                    log.getNetadminMsg(),
                    log.getIsaffected().toString(),
                    log.getProblemClear(),
                    log.getServiceID(),
                    log.getDeviceType(),
                    attempt + 1
            );
        } else {
            System.err.println("Max retries reached. Dropping event log.");
        }
    }

}
