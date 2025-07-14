/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npm.prob.datasource;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author vlxvijay
 */
public class Datasource {
//jdbc.driverClassName = com.mysql.cj.jdbc.Driver
//jdbc.url = jdbc:mysql://localhost:9007/npm?useSSL=false
//jdbc.username =root
//jdbc.password =Syst3m4$
    public static Connection getConnection() {
        Connection con = null;
        // JDBC URL for MySQL 8
        //String jdbcURL = "jdbc:mysql://localhost:9007/npm?useSSL=false";
         String jdbcURL = "jdbc:mysql://localhost:9007/bescom?useSSL=false&rewriteBatchedStatements=true";
        
       
        String username = "root";
        String password = "Syst3m4$";
        try {
//            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
//            MysqlDataSource mds = null;
//            mds = new MysqlDataSource();
//            mds.setUser(AppData.DB_USER);
//            mds.setPassword(AppData.DB_PASS);
//            mds.setURL("jdbc:mysql://" + AppData.DB_SERVER + ":" + AppData.DB_PORT + "/" + AppData.SCHEMA + "?useSSL=false");
//            con = mds.getConnection();

            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            con = DriverManager.getConnection(jdbcURL, username, password);

        } catch (Exception e) {
            System.out.println("Excep DB Connection" + e);
        }

        return con;
    }
}
