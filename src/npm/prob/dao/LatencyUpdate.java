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

public class LatencyUpdate implements Runnable {

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    PreparedStatement preparedStatement2 = null;
    ResultSet resultSet = null;
    String sql = null;

    public void run() {
        System.out.println("Start LatencyUpdate");
        while (true) {
            try {
                Thread.sleep(4000);
            } catch (Exception expp) {
                System.out.println("Exception In Thread Sleep BatchUpdate :" + expp);
            }

            try {
                NodeStatusLatencyMonitoring.latency_update_temp.clear();
                NodeStatusLatencyMonitoring.latency_update_temp.addAll(NodeStatusLatencyMonitoring.latency_update);
                NodeStatusLatencyMonitoring.latency_update.clear();
            } catch (Exception e) {
                System.out.println("Excpetion in try catch packet drop=" + e);
            }

            try {
                connection = Datasource.getConnection();

                sql = "UPDATE node_monitoring SET PACKET_LOSS=?,MIN_RESPONSE=?,MAX_RESPONSE=?,LATENCY=? WHERE NODE_IP=? ";
                preparedStatement = connection.prepareStatement(sql);
                for (int i = 0; i < NodeStatusLatencyMonitoring.latency_update_temp.size(); i++) {
                    try {
                        preparedStatement.setFloat(1, NodeStatusLatencyMonitoring.latency_update_temp.get(i).getPacket_loss());
                        preparedStatement.setInt(2, NodeStatusLatencyMonitoring.latency_update_temp.get(i).getMin_response());
                        preparedStatement.setInt(3, NodeStatusLatencyMonitoring.latency_update_temp.get(i).getMax_response());
                        preparedStatement.setFloat(4, NodeStatusLatencyMonitoring.latency_update_temp.get(i).getAvg_response());
                        preparedStatement.setString(5, NodeStatusLatencyMonitoring.latency_update_temp.get(i).getDevice_ip());
                        preparedStatement.addBatch();
                      //  System.out.println("updated IP :" + NodeStatusLatencyMonitoring.latency_update_temp.get(i).getDevice_ip());
                       // System.out.println("updated values  :" + NodeStatusLatencyMonitoring.latency_update_temp.get(i).getAvg_response());
                        if (i == 1000) {
                            int[] count = preparedStatement.executeBatch();

                            System.out.println("UPDATE node_monitoring inside: " + count.length);
                            preparedStatement = null;
                            preparedStatement = connection.prepareStatement(sql);
                        }

                        //System.out.println(dateFormat.format(in_startdate));
                    } catch (Exception e) {
                        System.out.println("Exception in packet drop=" + e);
                    }
                }
                int[] count = preparedStatement.executeBatch();

               System.out.println("##UPDATE node_monitoring MIN_RESPONSE Count: " + count.length);
            } catch (Exception exp) {
                System.out.println("--$$$$$Exception In Batch Update " + exp);
            } finally {
                try {

                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }

                } catch (Exception ep) {
                    System.out.println("Exception1111Insweertr in update==== " + ep);
                }
            }

        }

    }
}
