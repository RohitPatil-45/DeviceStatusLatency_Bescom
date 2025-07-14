/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npm.prob.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import npm.prob.datasource.Datasource;
import npm.prob.main.NodeStatusLatencyMonitoring;

/**
 *
 * @author NPM
 */
public class LatencyLog implements Runnable {

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    String sql = null;

    //  PreparedStatement preparedStatement2 = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void run() {

        System.out.println("Start Laytency Status Log");
        while (true) {

            LocalDateTime currentDateTime1 = LocalDateTime.now();
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@(info) DeviceStatusLatency 1:" + currentDateTime1.format(formatter));

            int count5 = 0;
            try {
                Thread.sleep(12000);
            } catch (Exception e) {
                //System.out.println("Thread Sleep Exception " + e);
            }
            try {
                NodeStatusLatencyMonitoring.latency_list_temp.clear();
                NodeStatusLatencyMonitoring.latency_list_temp.addAll(NodeStatusLatencyMonitoring.latency_list);
                NodeStatusLatencyMonitoring.latency_list.clear();
                // System.out.println("batch latency insert=" + BranchICMPPacket.latency_list_temp.size());
            } catch (Exception e) {
                System.out.println("Exception in batch insert=" + e);
            }
            LocalDateTime currentDateTime2 = LocalDateTime.now();
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@(info) DeviceStatusLatency 2:" + currentDateTime2.format(formatter));

//             logLatency = new LatencyModel();
//                            logLatency.setDevice_ip(device_ip);
//                            logLatency.setAvg_response(avg_responce);
//                            logLatency.setMin_response(min_responce);
//                            logLatency.setMax_response(max_responce);
//                            logLatency.setPacket_loss(drop_per);
//                            logLatency.setDevice_status(router_status);
//                            logLatency.setWorkingHourFlag(workingHourFlag);
            try {
                 connection = Datasource.getConnection();
//                connection = DriverManager.getConnection(
//                        "jdbc:mysql://localhost:9907/npm?rewriteBatchedStatements=true",
//                        "root", "Syst3m4$");

                //  jdbc:mysql://localhost:9007/npm?useSSL=false
                //   String username = "root";
                // String password = "Syst3m4$";
                sql = "INSERT INTO device_status_latency_history (device_ip,status,latency,packetdrop,working_hour_flag,timestamp,timestamp_epoch) VALUES (?,?,?,?,?,?,?)";
                //  String sql2 = "INSERT INTO latency_history (NODE_IP,PACKET_LOSS,MIN_LATENCY,MAX_LATENCY,AVG_LATENCY,EVENT_TIMESTAMP) VALUES (?,?,?,?,?,?)";
                preparedStatement = connection.prepareStatement(sql);
                //preparedStatement2 = connection.prepareStatement(sql2);
                LocalDateTime currentDateTime3 = LocalDateTime.now();
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@(info) DeviceStatusLatency 3:" + currentDateTime3.format(formatter));
                connection.setAutoCommit(false);
                for (int i = 0; i < NodeStatusLatencyMonitoring.latency_list_temp.size(); i++) {
                    count5 = count5 + 1;
                    try {

                        preparedStatement.setString(1, NodeStatusLatencyMonitoring.latency_list_temp.get(i).getDevice_ip());
                        preparedStatement.setString(2, NodeStatusLatencyMonitoring.latency_list_temp.get(i).getDevice_status());
                        preparedStatement.setFloat(3, NodeStatusLatencyMonitoring.latency_list_temp.get(i).getAvg_response());
                        preparedStatement.setFloat(4, NodeStatusLatencyMonitoring.latency_list_temp.get(i).getPacket_loss());
                       // preparedStatement.setInt(5, NodeStatusLatencyMonitoring.latency_list_temp.get(i).getWorkingHourFlag());
                        preparedStatement.setInt(5, 1);
                        preparedStatement.setString(6, NodeStatusLatencyMonitoring.latency_list_temp.get(i).getDatetime());
                        preparedStatement.setLong(7, NodeStatusLatencyMonitoring.latency_list_temp.get(i).getEpochTimeL());
                        preparedStatement.addBatch();

//                        preparedStatement2.setString(1, NodeStatusLatencyMonitoring.latency_list_temp.get(i).getDevice_ip());
//                        preparedStatement2.setFloat(2, NodeStatusLatencyMonitoring.latency_list_temp.get(i).getPacket_loss());
//                        preparedStatement2.setInt(3, NodeStatusLatencyMonitoring.latency_list_temp.get(i).getMin_response());
//                        preparedStatement2.setInt(4, NodeStatusLatencyMonitoring.latency_list_temp.get(i).getMax_response());
//                        preparedStatement2.setFloat(5, NodeStatusLatencyMonitoring.latency_list_temp.get(i).getAvg_response());
//                        preparedStatement2.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
//                        preparedStatement2.addBatch();
                        if (count5 % 1000 == 0) {
                            //  System.out.println(count5 + "match count5:");
                            LocalDateTime currentDateTime4 = LocalDateTime.now();
                            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@(info) DeviceStatusLatency 4:" + currentDateTime4.format(formatter));

                            preparedStatement.executeBatch();
                            //System.out.println(count5 + "Insert Branch COunt:" + count.length);
                            LocalDateTime currentDateTime5 = LocalDateTime.now();
                            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@(info) DeviceStatusLatency 5:" + currentDateTime5.format(formatter));
                            preparedStatement = null;
                            preparedStatement = connection.prepareStatement(sql);
                        }
                    } catch (Exception e) {
                        System.out.println("Exception in insert packet drop log=" + e);
                    }
                }
                preparedStatement.executeBatch();
                connection.commit();
                //System.out.println("@@Insert device_status_latency_history count:" + count.length);
                LocalDateTime currentDateTime6 = LocalDateTime.now();
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@(info) DeviceStatusLatency 6:" + currentDateTime6.format(formatter));

//                int[] count2 = preparedStatement2.executeBatch();
//                System.out.println("#Insert status Latency history:" + count2.length);
            } catch (Exception exp) {
                System.out.println("Exception Batch InsertDeviceL:" + exp);
            } finally {

                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
//                     if (preparedStatement2 != null) {
//                        preparedStatement2.close();
//                    }
                    if (connection != null) {
                        connection.close();
                    }

                } catch (Exception ep) {
                    System.out.println("*&&&&&&&&" + ep);
                }
            }

        }

    }
}
