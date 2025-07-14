package npm.prob.model;

import java.io.Serializable;

public class IcmpConfigModel implements Serializable {

    private static final long serialVersionUID = -2264642949863409860L;

    private Long ID;

    private String PACKET_SIZE;

    private String PACKET_COUNT;

    private String POOLING_TIME;

    private String TIMEOUT;

    private String TTL;

    private String CUSTOMER_NAME;

    private String LOCATION;

    public Long getID() {
        return ID;
    }

    public void setID(Long iD) {
        ID = iD;
    }

    public String getPACKET_SIZE() {
        return PACKET_SIZE;
    }

    public void setPACKET_SIZE(String pACKET_SIZE) {
        PACKET_SIZE = pACKET_SIZE;
    }

    public String getPACKET_COUNT() {
        return PACKET_COUNT;
    }

    public void setPACKET_COUNT(String pACKET_COUNT) {
        PACKET_COUNT = pACKET_COUNT;
    }

    public String getPOOLING_TIME() {
        return POOLING_TIME;
    }

    public void setPOOLING_TIME(String pOOLING_TIME) {
        POOLING_TIME = pOOLING_TIME;
    }

    public String getTIMEOUT() {
        return TIMEOUT;
    }

    public void setTIMEOUT(String tIMEOUT) {
        TIMEOUT = tIMEOUT;
    }

    public String getTTL() {
        return TTL;
    }

    public void setTTL(String tTL) {
        TTL = tTL;
    }

    public String getCUSTOMER_NAME() {
        return CUSTOMER_NAME;
    }

    public void setCUSTOMER_NAME(String cUSTOMER_NAME) {
        CUSTOMER_NAME = cUSTOMER_NAME;
    }

    public String getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(String lOCATION) {
        LOCATION = lOCATION;
    }

    @Override
    public String toString() {
        return "IcmpConfigModel [ID=" + ID + ", PACKET_SIZE=" + PACKET_SIZE + ", PACKET_COUNT=" + PACKET_COUNT
                + ", PACKET_TIME=" + POOLING_TIME + ", TIMEOUT=" + TIMEOUT + ", TTL=" + TTL + ", CUSTOMER_NAME="
                + CUSTOMER_NAME + ", LOCATION=" + LOCATION + "]";
    }

}
