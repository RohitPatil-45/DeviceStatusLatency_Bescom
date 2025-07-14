package npm.prob.model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author VIVEK
 */
public class LatencyModel {

    String device_ip = null;
    float packet_loss = 0;
    int min_response = 0;
    int max_response = 0;
    float avg_response = 0;
    String device_status = null;
    int workingHourFlag = 0;
    String datetime=null;
    long epochTimeL =0;

    public long getEpochTimeL() {
        return epochTimeL;
    }

    public void setEpochTimeL(long epochTimeL) {
        this.epochTimeL = epochTimeL;
    }
    
    

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
    

    public int getWorkingHourFlag() {
        return workingHourFlag;
    }

    public void setWorkingHourFlag(int workingHourFlag) {
        this.workingHourFlag = workingHourFlag;
    }
    
    

    public String getDevice_status() {
        return device_status;
    }

    public void setDevice_status(String device_status) {
        this.device_status = device_status;
    }
    
    

    public String getDevice_ip() {
        return device_ip;
    }

    public void setDevice_ip(String device_ip) {
        this.device_ip = device_ip;
    }

    public int getMin_response() {
        return min_response;
    }

    public void setMin_response(int min_response) {
        this.min_response = min_response;
    }

    public int getMax_response() {
        return max_response;
    }

    public void setMax_response(int max_response) {
        this.max_response = max_response;
    }

    public float getAvg_response() {
        return avg_response;
    }

    public void setAvg_response(float avg_response) {
        this.avg_response = avg_response;
    }

    public float getPacket_loss() {
        return packet_loss;
    }

    public void setPacket_loss(float packet_loss) {
        this.packet_loss = packet_loss;
    }

}
