/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npm.prob.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import npm.prob.datasource.Datasource;
import npm.prob.main.NodeStatusLatencyMonitoring;

/**
 *
 * @author NPM
 */
public class StatusUpdate implements Runnable {

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    PreparedStatement preparedStatement2 = null;
    ResultSet resultSet = null;
    String sql = null;
    int result = 0;
    boolean flag = false;
    Connection connection2 = null;
    String router_ip = null;
    String router_status = null;
    String cdate = null;
    String ctime = null;
    String change_type = null;

    public void run() {
        System.out.println("Status Update Thread");
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                //System.out.println("Thread Sleep Exception " + e);
            }
            try {
                NodeStatusLatencyMonitoring.statusUpdateListTemp.clear();
                NodeStatusLatencyMonitoring.statusUpdateListTemp.addAll(NodeStatusLatencyMonitoring.statusUpdateList);
                NodeStatusLatencyMonitoring.statusUpdateList.clear();
            } catch (Exception e) {
                System.out.println("Exception in batch insert=" + e);
            }
            try {
                connection = Datasource.getConnection();
                sql = "UPDATE node_monitoring SET NODE_STATUS=?,STATUS_TIMESTAMP=?,NODE_STATUS_Generated_Time=?,NODE_STATUS_Cleared_Time=? WHERE NODE_IP=? ";
                preparedStatement = connection.prepareStatement(sql);
                for (int i = 0; i < NodeStatusLatencyMonitoring.statusUpdateListTemp.size(); i++) {
                    try {
                        
                        preparedStatement.setString(1, NodeStatusLatencyMonitoring.statusUpdateListTemp.get(i).getDevice_status());
                        preparedStatement.setTimestamp(2, NodeStatusLatencyMonitoring.statusUpdateListTemp.get(i).getEvent_time());    
                        preparedStatement.setTimestamp(3, NodeStatusLatencyMonitoring.statusUpdateListTemp.get(i).getDevice_status().equalsIgnoreCase("Down") ? NodeStatusLatencyMonitoring.statusUpdateListTemp.get(i).getEvent_time() : null);
                        preparedStatement.setTimestamp(4, NodeStatusLatencyMonitoring.statusUpdateListTemp.get(i).getDevice_status().equalsIgnoreCase("Up") ? NodeStatusLatencyMonitoring.statusUpdateListTemp.get(i).getEvent_time() : null);
                        preparedStatement.setString(5, NodeStatusLatencyMonitoring.statusUpdateListTemp.get(i).getDevice_ip());
                        
                        preparedStatement.addBatch();
                        if (i == 100) {
                            int[] count = preparedStatement.executeBatch();

                            System.out.println("Update NODE_STATUS inside count:" + count.length);
                            preparedStatement = null;
                            preparedStatement = connection.prepareStatement(sql);

                        }

                        //System.out.println(dateFormat.format(in_startdate));
                    } catch (Exception e) {
                        System.out.println("Exception in node_monitoring drop=" + e);
                    }

                }

                int[] count = preparedStatement.executeBatch();
                System.out.println("update NODE_STATUS count :" + count.length);

                // System.out.println("###Update Record update: " + count.length);
            } catch (Exception exp) {
                System.out.println("--$$$$$Exception In ICMP Batch Update " + exp);

            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception ex) {
                    System.out.println("Exception1111Insweertr==== " + ex);
                }

            }
        }
    }
}
