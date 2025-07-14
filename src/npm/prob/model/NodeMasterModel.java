package npm.prob.model;

import java.io.Serializable;

public class NodeMasterModel implements Serializable {

    private static final long serialVersionUID = -2264642949863409860L;

    private String DEVICE_IP;
    private String DEVICE_NAME;
    private String DEVICE_TYPE;
    private String GROUP_NAME;
    private String SNMP;
    private String SERVICE_PROVIDER;
    private String COMPANY;
    private String STATE;
    private String ZONE;
    private String DISTRICT;
    private String LOCATION;
    private int LATENCY_THRESHOLD;
    private String LATENCY_HISTORY;

    public int getLATENCY_THRESHOLD() {
        return LATENCY_THRESHOLD;
    }

    public void setLATENCY_THRESHOLD(int LATENCY_THRESHOLD) {
        this.LATENCY_THRESHOLD = LATENCY_THRESHOLD;
    }

    public String getLATENCY_HISTORY() {
        return LATENCY_HISTORY;
    }

    public void setLATENCY_HISTORY(String LATENCY_HISTORY) {
        this.LATENCY_HISTORY = LATENCY_HISTORY;
    }

    public String getDEVICE_IP() {
        return DEVICE_IP;
    }

    public void setDEVICE_IP(String dEVICE_IP) {
        DEVICE_IP = dEVICE_IP;
    }

    public String getDEVICE_NAME() {
        return DEVICE_NAME;
    }

    public void setDEVICE_NAME(String dEVICE_NAME) {
        DEVICE_NAME = dEVICE_NAME;
    }

    public String getDEVICE_TYPE() {
        return DEVICE_TYPE;
    }

    public void setDEVICE_TYPE(String dEVICE_TYPE) {
        DEVICE_TYPE = dEVICE_TYPE;
    }

    public String getGROUP_NAME() {
        return GROUP_NAME;
    }

    public void setGROUP_NAME(String gROUP_NAME) {
        GROUP_NAME = gROUP_NAME;
    }

    public String getSNMP() {
        return SNMP;
    }

    public void setSNMP(String sNMP) {
        SNMP = sNMP;
    }

    public String getSERVICE_PROVIDER() {
        return SERVICE_PROVIDER;
    }

    public void setSERVICE_PROVIDER(String sERVICE_PROVIDER) {
        SERVICE_PROVIDER = sERVICE_PROVIDER;
    }

    public String getCOMPANY() {
        return COMPANY;
    }

    public void setCOMPANY(String cOMPANY) {
        COMPANY = cOMPANY;
    }

    public String getSTATE() {
        return STATE;
    }

    public void setSTATE(String sTATE) {
        STATE = sTATE;
    }

    public String getZONE() {
        return ZONE;
    }

    public void setZONE(String zONE) {
        ZONE = zONE;
    }

    public String getDISTRICT() {
        return DISTRICT;
    }

    public void setDISTRICT(String dISTRICT) {
        DISTRICT = dISTRICT;
    }

    public String getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(String lOCATION) {
        LOCATION = lOCATION;
    }

    @Override
    public String toString() {
        return "AddNodeModel [ DEVICE_IP=" + DEVICE_IP + ", DEVICE_NAME=" + DEVICE_NAME + ", DEVICE_TYPE="
                + DEVICE_TYPE + ", GROUP_NAME=" + GROUP_NAME + ", SNMP=" + SNMP + ", SERVICE_PROVIDER="
                + SERVICE_PROVIDER + ", COMPANY=" + COMPANY + ", STATE=" + STATE + ", ZONE=" + ZONE + ", DISTRICT="
                + DISTRICT + ", LOCATION=" + LOCATION + "]";
    }

}
