
package npm.prob.mail;


import npm.prob.dao.MailConfigDao;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import npm.prob.datasource.Datasource;


public final class MailConfig {

    public void loadConfig() {
        try {
            
            System.out.println("INSIDE Node Prob Mail config");
            
            Connection con = Datasource.getConnection();
            Statement stmt55 = con.createStatement();
            
            System.out.println("INSIDE LOAD CONFIG!!");

            String query5 = "select SMTP_SERVER,SMTP_AUTH,PORT,EMAIL_ID,PASSWORD,IS_SSL_TLS from email_config_master";
            ResultSet set5 = stmt55.executeQuery(query5);
            String hostname = "";
            String auth = "";
            String port = "";
            String sender_name = "";
            String sender_password = "";
            String isStarttls = "";
            while (set5.next()) {
                hostname = set5.getString(1);
                auth = set5.getString(2);
                port = set5.getString(3);
                sender_name = set5.getString(4);
                sender_password = set5.getString(5);
                isStarttls = set5.getString(6);
            }
            System.out.println("Mail hostname:" + hostname);

            MailConfigDao.mail_host = hostname;
            MailConfigDao.mail_port = port;
            MailConfigDao.mail_auth = auth;
            MailConfigDao.sender = sender_name;
            MailConfigDao.mail_password = sender_password;
            MailConfigDao.isStarttls = isStarttls;
            
            System.out.println(":"+MailConfigDao.mail_host+":"+ MailConfigDao.mail_port+":"+MailConfigDao.sender+":"+MailConfigDao.mail_password);

        } catch (SQLException ex) {
            System.out.println("Mail Config Exception:" + ex);
        }
    }

}
