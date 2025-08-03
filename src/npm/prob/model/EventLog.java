package npm.prob.model;

import java.sql.Timestamp;

public class EventLog {

    private Long id;
    private String alarmAcknowledgedBy;
    private Timestamp alarmAcknowledgedTime;
    private Timestamp clearedEventTimestamp;
    private String deviceType;
    private String problemClear;
    private String serviceID;
    private int acknowledgementStatus;
    private String deviceId;
    private String deviceName;
    private String eventMsg;
    private Timestamp eventTimestamp;
    private String isNotification;
    private Integer isAcknowledged;
    private Integer isaffected;
    private String netadminMsg;
    private String serviceName;
    private String severity;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlarmAcknowledgedBy() {
        return alarmAcknowledgedBy;
    }

    public void setAlarmAcknowledgedBy(String alarmAcknowledgedBy) {
        this.alarmAcknowledgedBy = alarmAcknowledgedBy;
    }

    public Timestamp getAlarmAcknowledgedTime() {
        return alarmAcknowledgedTime;
    }

    public void setAlarmAcknowledgedTime(Timestamp alarmAcknowledgedTime) {
        this.alarmAcknowledgedTime = alarmAcknowledgedTime;
    }

    public Timestamp getClearedEventTimestamp() {
        return clearedEventTimestamp;
    }

    public void setClearedEventTimestamp(Timestamp clearedEventTimestamp) {
        this.clearedEventTimestamp = clearedEventTimestamp;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getProblemClear() {
        return problemClear;
    }

    public void setProblemClear(String problemClear) {
        this.problemClear = problemClear;
    }

    public int getAcknowledgementStatus() {
        return acknowledgementStatus;
    }

    public void setAcknowledgementStatus(int acknowledgementStatus) {
        this.acknowledgementStatus = acknowledgementStatus;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getEventMsg() {
        return eventMsg;
    }

    public void setEventMsg(String eventMsg) {
        this.eventMsg = eventMsg;
    }

    public Timestamp getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Timestamp eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getIsNotification() {
        return isNotification;
    }

    public void setIsNotification(String isNotification) {
        this.isNotification = isNotification;
    }

    public Integer getIsAcknowledged() {
        return isAcknowledged;
    }

    public void setIsAcknowledged(Integer isAcknowledged) {
        this.isAcknowledged = isAcknowledged;
    }

    public String getNetadminMsg() {
        return netadminMsg;
    }

    public void setNetadminMsg(String netadminMsg) {
        this.netadminMsg = netadminMsg;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public Integer getIsaffected() {
        return isaffected;
    }

    public void setIsaffected(Integer isaffected) {
        this.isaffected = isaffected;
    }
    
    
}
