package npm.prob.model;


import java.sql.Date;
import java.sql.Timestamp;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author VIVEK
 */
public class NodeStausModel {

    String device_ip = null;
    String device_status = null;
    Timestamp event_time = null;

    public String getDevice_ip() {
        return device_ip;
    }

    public void setDevice_ip(String device_ip) {
        this.device_ip = device_ip;
    }

    public String getDevice_status() {
        return device_status;
    }

    public void setDevice_status(String device_status) {
        this.device_status = device_status;
    }

    public Timestamp getEvent_time() {
        return event_time;
    }

    public void setEvent_time(Timestamp event_time) {
        this.event_time = event_time;
    }
 
   
    
}
