/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npm.prob.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import npm.prob.datasource.Datasource;
import npm.prob.main.NodeStatusLatencyMonitoring;

/**
 *
 * @author NPM
 */
public class StatusLog implements Runnable {

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    PreparedStatement preparedStatement2 = null;
    ResultSet resultSet = null;
    String sql = null;
    int result = 0;
    boolean flag = false;
    Connection connection2 = null;
    String branch_name = null;
    String ssa = null;
    String zone_name = null;
    String customer_name = null;
    String customer_sname = null;
    String district = null;
    String department = null;
    String state_name = null;
    String r_ip = null;
    String routerstatus = null;
    String msgBody1 = null;
    String msgFormat = null;
    String mail_sub_msg = null;

    public void run() {

        while (true) {
            int count5 = 0;
            try {
                Thread.sleep(12000);
            } catch (Exception e) {
                //System.out.println("Thread Sleep Exception " + e);
            }

            try {
                NodeStatusLatencyMonitoring.statusLogListTemp.clear();
                NodeStatusLatencyMonitoring.statusLogListTemp.addAll(NodeStatusLatencyMonitoring.statusLogList);
                NodeStatusLatencyMonitoring.statusLogList.clear();
                // System.out.println("batch latency insert=" + NodeStatusLatencyMonitoring.latency_list_temp.size());
            } catch (Exception e) {
                System.out.println("Exception in batch insert router status report=" + e);
            }

            try {
                connection = Datasource.getConnection();
                sql = "INSERT INTO node_status_history (NODE_IP,NODE_STATUS,EVENT_TIMESTAMP) VALUES (?,?,?)";
                preparedStatement = connection.prepareStatement(sql);

                for (int i = 0; i < NodeStatusLatencyMonitoring.statusLogListTemp.size(); i++) {
                    count5 = count5 + 1;
                    try {

                        if (NodeStatusLatencyMonitoring.statusLogListTemp.get(i).getDevice_status().equals("Down")) {
                            msgBody1 = "Project Name: NMS \nNode IP:" + NodeStatusLatencyMonitoring.statusLogListTemp.get(i).getDevice_ip() + " is Down \nDate:" + NodeStatusLatencyMonitoring.statusLogListTemp.get(i).getEvent_time();
                            msgFormat = "Dear Sir/Madam,\r \nPlease Check For\r \n" + msgBody1 + "\r \nKindly take appropriate action.\r \nThanks And Regards,\r \nNMS Team";
                            mail_sub_msg = "Alert: Node Down || " + NodeStatusLatencyMonitoring.statusLogListTemp.get(i).getDevice_ip();
                            sendMailAlert(NodeStatusLatencyMonitoring.statusLogListTemp.get(i).getDevice_ip(), NodeStatusLatencyMonitoring.statusLogListTemp.get(i).getEvent_time(), msgFormat, mail_sub_msg, "Device_Status", "Critical");
                        }

                        preparedStatement.setString(1, NodeStatusLatencyMonitoring.statusLogListTemp.get(i).getDevice_ip());
                        preparedStatement.setString(2, NodeStatusLatencyMonitoring.statusLogListTemp.get(i).getDevice_status());
                        preparedStatement.setTimestamp(3, NodeStatusLatencyMonitoring.statusLogListTemp.get(i).getEvent_time());

                        preparedStatement.addBatch();
                        if (count5 % 100 == 0) {
                            System.out.println(count5 + "match count5:");
                            int[] count = preparedStatement.executeBatch();
                            System.out.println(count5 + "Insert Branch COunt:" + count.length);
                            preparedStatement = null;
                            preparedStatement = connection.prepareStatement(sql);
                        }
                    } catch (Exception e) {
                        System.out.println("Exception Log status:" + e);
                    }
                }
                int[] count = preparedStatement.executeBatch();
               System.out.println("Device Status Insert Count:" + count.length);
            } catch (Exception exp) {
                System.out.println("Exception In Batch Insweertr==== " + exp);
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception ep) {
                    System.out.println("*&&&&&&&&" + ep);
                }
            }

        }

    }

    public void sendMailAlert(String deviceIP, Timestamp eventTime, String msgFormat, String mailSubject, String alertType, String severity) {
        System.out.println("Mail Send:......" + deviceIP);

        PreparedStatement preparedStatement = null;
        Connection connection_mail = null;
        try {
            connection_mail = Datasource.getConnection();
            String sql = "INSERT INTO ALERTS_JOB (NODE_IP,ALERT_TYPE,MAIL_SUBJECT,MAIL_FORMAT,EVENT_TIMESTAMP,SEVERITY,SEND_FLAG,CUSTOMER) VALUES (?,?,?,?,?,?,?,?)";
            preparedStatement = connection_mail.prepareStatement(sql);
            preparedStatement.setString(1, deviceIP);
            preparedStatement.setString(2, alertType);
            preparedStatement.setString(3, mailSubject);
            preparedStatement.setString(4, msgFormat);
            preparedStatement.setTimestamp(5, eventTime);
            preparedStatement.setString(6, severity);
            preparedStatement.setString(7, "false");
            preparedStatement.setString(8, "NA");
            preparedStatement.executeUpdate();

            System.out.println("Insert mail Record success : ");
        } catch (Exception exp) {
            System.out.println("Exception In Batch Insert " + exp);
        } finally {
            try {
                if (connection_mail != null) {
                    connection_mail.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }

            } catch (Exception ep) {
            }
        }

    }
}
