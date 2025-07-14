/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npm.prob.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import npm.prob.dao.DatabaseHelper;
import npm.prob.datasource.Datasource;

/**
 *
 * @author NPM
 */
public class StatusChangeDiff {

    public StatusChangeDiff() {

        //       this.router_ip = router_ip;
//        this.cdate = cdate;
//        this.ctime = ctime;
    }

    public void insertStatusDiff(String device_ip, Timestamp up_event_time) {
        System.out.println("Node status time start.." + device_ip);

        long totalsecofdate = 0;
        String difference_time = "";
        long diff = 0;
        long diffSeconds = 0;
        long diffMinutes = 0;
        long diffHours = 0;
        long diffDays = 0;
        Timestamp down_event_time = null;
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = Datasource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT EVENT_TIMESTAMP FROM node_status_history where  NODE_IP ='" + device_ip + "' AND NODE_STATUS='Down' ORDER BY ID DESC");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                down_event_time = resultSet.getTimestamp(1);
            }
        } catch (Exception a) {
            System.out.println("excep node status time:" + a);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception exp) {
                System.out.println("excep:" + exp);
            }
        }

        diff = up_event_time.getTime() - down_event_time.getTime();
        diffSeconds = diff / 1000 % 60;
        diffMinutes = diff / (60 * 1000) % 60;
        diffHours = diff / (60 * 60 * 1000) % 24;
        diffDays = diff / (24 * 60 * 60 * 1000);
        difference_time = diffDays + " Days " + diffHours + " Hours " + diffMinutes + " Minutes " + diffSeconds + " Seconds.";
        long numa2 = diffDays * 86400;
        long numb2 = diffHours * 3600;
        long numc2 = diffMinutes * 60;
        long numd2 = diffSeconds;
        totalsecofdate = numa2 + numb2 + numc2 + numd2;

        System.out.println(device_ip + ":node status time Up Event:" + up_event_time + ":Down Event:" + down_event_time + ":Diff:" + difference_time);
        try {
            DatabaseHelper helper = new DatabaseHelper();
            helper.insertStatusTatusTimeDiff(device_ip, down_event_time, up_event_time, difference_time, totalsecofdate);
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }

    }
}
