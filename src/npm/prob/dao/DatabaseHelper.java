/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npm.prob.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;

import npm.prob.datasource.Datasource;
import npm.prob.main.NodeStatusLatencyMonitoring;
import npm.prob.model.NodeMasterModel;

/**
 *
 * @author NPM
 */
public class DatabaseHelper {

    public static void main(String[] args) {
        DatabaseHelper helper = new DatabaseHelper();
        helper.getNodeData();
    }

    public HashMap getNodeData() {
        Connection connection = null;
        Statement st1 = null;
        ResultSet rs = null;

        HashMap<String, NodeMasterModel> mapNodeData = new HashMap();

        try {
            connection = Datasource.getConnection();
            st1 = connection.createStatement();
            // String query = "select node.DEVICE_IP,node.DEVICE_NAME,node.DEVICE_TYPE,node.GROUP_NAME,node.COMPANY,node.LOCATION,node.DISTRICT,node.STATE,node.ZONE,parm.LATENCY_HISTORY,parm.LATENCY_THRESHOLD,mon.NODE_STATUS FROM ADD_NODE node JOIN NODE_PARAMETER parm ON node.DEVICE_IP=parm.DEVICE_IP JOIN node_monitoring mon ON node.DEVICE_IP=mon.NODE_IP  WHERE parm.MONITORING='yes' ORDER BY node.ID ";
            // String query = "select node.DEVICE_IP,node.DEVICE_NAME,node.DEVICE_TYPE,node.GROUP_NAME,node.COMPANY,node.LOCATION,node.DISTRICT,node.STATE,node.ZONE,parm.LATENCY_HISTORY,parm.LATENCY_THRESHOLD,mon.NODE_STATUS FROM ADD_NODE node JOIN NODE_PARAMETER parm ON node.DEVICE_IP=parm.DEVICE_IP JOIN node_monitoring mon ON node.DEVICE_IP=mon.NODE_IP  WHERE parm.MONITORING='yes' ORDER BY node.ID ";
            String query = "select node.DEVICE_IP,node.DEVICE_NAME,node.DEVICE_TYPE,node.GROUP_NAME,node.COMPANY,node.LOCATION,node.DISTRICT,node.STATE,node.ZONE,"
                    + "parm.LATENCY_HISTORY,parm.LATENCY_THRESHOLD,mon.NODE_STATUS FROM ADD_NODE node JOIN NODE_PARAMETER parm ON node.DEVICE_IP=parm.DEVICE_IP "
                    + "JOIN node_monitoring mon ON node.DEVICE_IP=mon.NODE_IP  WHERE parm.MONITORING='yes'  ORDER BY node.ID ";

            rs = st1.executeQuery(query);
            while (rs.next()) {
                NodeMasterModel node = new NodeMasterModel();

                String device_ip = rs.getString(1);
                node.setDEVICE_IP(device_ip);
                node.setDEVICE_NAME(rs.getString(2));
                node.setDEVICE_TYPE(rs.getString(3));
                node.setGROUP_NAME(rs.getString(4));
                node.setCOMPANY(rs.getString(5));
                node.setLOCATION(rs.getString(6));
                node.setDISTRICT(rs.getString(7));
                node.setSTATE(rs.getString(8));
                node.setZONE(rs.getString(9));
                //Latency
                node.setLATENCY_HISTORY(rs.getString(10));
                node.setLATENCY_THRESHOLD(rs.getInt(11));
                mapNodeData.put(device_ip, node);
                NodeStatusLatencyMonitoring.latency_map.put(device_ip, "Low");
                //NodeProbMonitoring.deviceStatusMap.put(device_ip, rs.getString(12));
            }

        } catch (Exception ex) {
            System.out.println("Exception node read:" + ex);
        } finally {
            if (st1 != null) {
                try {
                    st1.close();
                } catch (SQLException e) {
                    //System.out.println(e.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    //System.out.println(e.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    //System.out.println(e.getMessage());
                }
            }
        }
        System.out.println("Node Map Data:" + mapNodeData);

        //System.out.println("StatusMap:" + NodeStatusLatencyMonitoring.deviceStatusMap);
        return mapNodeData;
    }

    public void latencyThreshold(String device_ip, int latency_threshold, float avg_responce, String latency_status) {
        PreparedStatement pstmtThresholdLog = null;
        Connection threshold_log_con = null;
        String msgBody1 = null;
        String msgFormat = null;
        String mail_sub_msg = null;
        StatusLog log = new StatusLog();
        Timestamp logDateTime = new Timestamp(System.currentTimeMillis());
        try {
            threshold_log_con = Datasource.getConnection();
            
            pstmtThresholdLog = threshold_log_con.prepareStatement("INSERT INTO latency_threshold_history (NODE_IP,LATENCY_THRESHOLD,LATENCY_VAL,LATENCY_STATUS,EVENT_TIMESTAMP) VALUES (?,?,?,?,?)");
            pstmtThresholdLog.setString(1, device_ip);
            pstmtThresholdLog.setInt(2, latency_threshold);
            pstmtThresholdLog.setFloat(3, avg_responce);
            pstmtThresholdLog.setString(4, latency_status);
            pstmtThresholdLog.setTimestamp(5, logDateTime);
            pstmtThresholdLog.executeUpdate();

            if (latency_status.equalsIgnoreCase("High")) {
                msgBody1 = "Project Name: NMS\nNode IP:" + device_ip + " has crossed the latency threshold above " + latency_threshold + "ms \nCurrent Latency:" + avg_responce + "ms \nDate:" + logDateTime;
                msgFormat = "Dear Sir/Madam,\r \nPlease Check For\r \n" + msgBody1 + "\r \nKindly take appropriate action.\r \nThanks And Regards,\r \nNMS Team";
                mail_sub_msg = "Alert: Latency threshold crossed above " + latency_threshold + "ms || " + device_ip;

               
                log.sendMailAlert(device_ip, logDateTime, msgFormat, mail_sub_msg, "Latency_Threshold", "Normal");
            }

        } catch (Exception e1) {
            System.out.println("Exception latency High" + e1);
        } finally {
            try {
                if (pstmtThresholdLog != null) {
                    pstmtThresholdLog.close();
                }
                if (threshold_log_con != null) {
                    threshold_log_con.close();
                }
            } catch (SQLException e) {
                System.out.println("Error 1" + e);
            }
        }
        Connection threshold_update_con = null;
        PreparedStatement pstmtThresholdUpdate = null;
        try {
            threshold_update_con = Datasource.getConnection();
            pstmtThresholdUpdate = threshold_update_con.prepareStatement("UPDATE node_monitoring SET LATENCY=?,LATENCY_THRESHOLD=?,LATENCY_STATUS=?,LATENCY_TIMESTAMP=?,LATENCY_STATUS_Generated_Time=?,LATENCY_STATUS_Cleared_Time=? WHERE NODE_IP=? ");
            pstmtThresholdUpdate.setFloat(1, avg_responce);
            pstmtThresholdUpdate.setInt(2, latency_threshold);
            pstmtThresholdUpdate.setString(3, latency_status);
            pstmtThresholdUpdate.setTimestamp(4, logDateTime);
            pstmtThresholdUpdate.setTimestamp(5, latency_status.equalsIgnoreCase("High") ? logDateTime : null);
            pstmtThresholdUpdate.setTimestamp(6, latency_status.equalsIgnoreCase("Low") ? logDateTime : null);
            
            pstmtThresholdUpdate.setString(7, device_ip);
            pstmtThresholdUpdate.executeUpdate();
        } catch (Exception e) {
            System.out.println("insert latency alert exception normal:" + e);
        } finally {
            try {
                if (pstmtThresholdUpdate != null) {
                    pstmtThresholdUpdate.close();
                }
                if (threshold_update_con != null) {
                    threshold_update_con.close();
                }
            } catch (Exception exp) {
                System.out.println("insert cpu log exp:" + exp);
            }
        }
    }

    public void insertStatusTatusTimeDiff(String device_ip, Timestamp down_event_time, Timestamp up_event_time, String difference_time, Long totalsecofdate) {
        PreparedStatement preparedStatement1 = null;
        Connection connection = null;
        try {
            connection = Datasource.getConnection();
            preparedStatement1 = connection.prepareStatement("INSERT INTO STATUS_DIFF_TIME (DEVICE_IP,DOWN_EVENT_TIME,UP_EVENT_TIME,EVENT_DIFFERENCE,EVENT_DIFFERENCE_SECOND) VALUES (?,?,?,?,?)");
            preparedStatement1.setString(1, device_ip);
            preparedStatement1.setTimestamp(2, down_event_time);
            preparedStatement1.setTimestamp(3, up_event_time);
            preparedStatement1.setString(4, difference_time);
            preparedStatement1.setLong(5, totalsecofdate);
            preparedStatement1.executeUpdate();
            System.out.println("insert uptime webserive:" + device_ip);
        } catch (Exception e) {
            System.out.println(device_ip + "inser node status time Exception:" + e);
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
    }

}
