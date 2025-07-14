
SELECT 
    device_ip,
    SEC_TO_TIME(SUM(CASE 
        WHEN status = 'Up' THEN TIME_TO_SEC(TIMEDIFF(
            IFNULL((SELECT timestamp 
                    FROM device_status_latency_history d2 
                    WHERE d2.device_ip = d1.device_ip 
                    AND d2.timestamp > d1.timestamp 
                    ORDER BY d2.timestamp ASC LIMIT 1), NOW()), timestamp))
        ELSE 0
    END)) AS uptime_hours,
    SEC_TO_TIME(SUM(CASE 
        WHEN status = 'Down' THEN TIME_TO_SEC(TIMEDIFF(
            IFNULL((SELECT timestamp 
                    FROM device_status_latency_history d2 
                    WHERE d2.device_ip = d1.device_ip 
                    AND d2.timestamp > d1.timestamp 
                    ORDER BY d2.timestamp ASC LIMIT 1), NOW()), timestamp))
        ELSE 0
    END)) AS downtime_hours,
    ROUND(SUM(CASE 
        WHEN status = 'Up' THEN TIME_TO_SEC(TIMEDIFF(
            IFNULL((SELECT timestamp 
                    FROM device_status_latency_history d2 
                    WHERE d2.device_ip = d1.device_ip 
                    AND d2.timestamp > d1.timestamp 
                    ORDER BY d2.timestamp ASC LIMIT 1), NOW()), timestamp))
        ELSE 0
    END) / (SUM(TIME_TO_SEC(TIMEDIFF(
            IFNULL((SELECT timestamp 
                    FROM device_status_latency_history d2 
                    WHERE d2.device_ip = d1.device_ip 
                    AND d2.timestamp > d1.timestamp 
                    ORDER BY d2.timestamp ASC LIMIT 1), NOW()), timestamp))) + 1e-6) * 100, 2) 
    AS uptime_percentage,
    ROUND(SUM(CASE 
        WHEN status = 'Down' THEN TIME_TO_SEC(TIMEDIFF(
            IFNULL((SELECT timestamp 
                    FROM device_status_latency_history d2 
                    WHERE d2.device_ip = d1.device_ip 
                    AND d2.timestamp > d1.timestamp 
                    ORDER BY d2.timestamp ASC LIMIT 1), NOW()), timestamp))
        ELSE 0
    END) / (SUM(TIME_TO_SEC(TIMEDIFF(
            IFNULL((SELECT timestamp 
                    FROM device_status_latency_history d2 
                    WHERE d2.device_ip = d1.device_ip 
                    AND d2.timestamp > d1.timestamp 
                    ORDER BY d2.timestamp ASC LIMIT 1), NOW()), timestamp))) + 1e-6) * 100, 2) 
    AS downtime_percentage
FROM 
    device_status_latency_history d1
-- WHERE 
    -- working_hour_flag = 1 -- Only working hours
    -- AND DATE(timestamp) BETWEEN '2024-01-01' AND '2024-01-31' -- Date picker (adjust dates as needed)
GROUP BY 
    device_ip;
