/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npm.prob.main;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author testsys
 */
public class Test {

    public static void main(String[] args) {
//        LocalTime startOfWork = LocalTime.of(9, 0); // 9:00 AM
//        LocalTime endOfWork = LocalTime.of(18, 0);  // 6:00 PM
//
//        // Get the current time
//        LocalTime currentTime = LocalTime.now();
//
//        // Check if the current time is within working hours
//        boolean isWorkingHour = !currentTime.isBefore(startOfWork) && !currentTime.isAfter(endOfWork);
//
//        // Output the result
//        System.out.println("Is it working hour? " + isWorkingHour);
//        
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Format it as a string in 'YYYY-MM-DD HH:MM:SS' format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);

                        //Latnecy Hisotry
        //  if (laptencyHisotryParam != null && laptencyHisotryParam.toLowerCase().equals("yes")) {
        long epochTime = System.currentTimeMillis() / 1000;
        
        System.out.println("Time:"+formattedDateTime+":"+epochTime);

    }
}
