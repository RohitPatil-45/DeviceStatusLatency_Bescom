/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npm.prob.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import npm.prob.dao.DatabaseHelper;
import npm.prob.model.LatencyModel;
import npm.prob.model.NodeMasterModel;
import npm.prob.model.NodeStausModel;

/**
 *
 * @author NPM
 */
public class NodeStatusLatencyMonitoring implements Runnable {

    public static HashMap<String, NodeMasterModel> mapNodeData = null;
    public static ArrayList<LatencyModel> latency_list = null;
    public static ArrayList<LatencyModel> latency_list_temp = null;
    public static ArrayList<LatencyModel> latency_update = null;
    public static ArrayList<LatencyModel> latency_update_temp = null;
    public static HashMap latency_map = null;
    //public static HashMap<String, String> deviceStatusMap = null;

    public static ArrayList<NodeStausModel> statusUpdateList = null;
    public static ArrayList<NodeStausModel> statusUpdateListTemp = null;
    public static ArrayList<NodeStausModel> statusLogList = null;
    public static ArrayList<NodeStausModel> statusLogListTemp = null;

    public void run() {

        latency_list = new ArrayList<>();
        latency_update = new ArrayList<>();
        latency_update_temp = new ArrayList<>();
        latency_list_temp = new ArrayList<>();
        latency_map = new HashMap();
      //  deviceStatusMap = new HashMap();

        statusUpdateList = new ArrayList<>();
        statusLogList = new ArrayList<>();

        statusUpdateListTemp = new ArrayList<>();
        statusLogListTemp = new ArrayList<>();

        ArrayList inner_list = null;
        inner_list = new ArrayList();
        ArrayList outer_list = null;
        outer_list = new ArrayList();
        int m = 0;

        DatabaseHelper helper = new DatabaseHelper();
        mapNodeData = helper.getNodeData();
        System.out.println(mapNodeData.size() + ":NodeProbMonitoring:" + mapNodeData);
        Iterator<Map.Entry<String, NodeMasterModel>> itr = mapNodeData.entrySet().iterator();
        while (itr.hasNext()) {
            try {
                Map.Entry<String, NodeMasterModel> entry = itr.next();
                // System.out.println("Key = " + entry.getKey()
                //         + ", Value = " + entry.getValue());
                m = m + 1;
                inner_list.add(entry.getKey());
                if (m % 1 == 0) {
                    outer_list.add(inner_list.toString());
                    inner_list.clear();
                }
            } catch (Exception e) {
                System.out.println("Exception:" + e);
            }

        }

        if ((inner_list.size()) != 0) {
            outer_list.add(inner_list);
        }
        System.out.println("outer list:" + outer_list);
        System.out.println("Thread size:" + outer_list.size());
        int pool_sizee5 = outer_list.size();
        System.out.println("Thread Pool Size " + pool_sizee5);

        ExecutorService executor = null;
        Runnable worker = null;
        executor = null;
        executor = Executors.newFixedThreadPool(pool_sizee5);
        Iterator out_itr = outer_list.iterator();
        int thread_count = 0;

        while (out_itr.hasNext()) {
            String a = out_itr.next().toString();
            String b = a.substring(1, a.length() - 1);
            List<String> myList = null;
            myList = new ArrayList<String>(Arrays.asList(b.split(",")));
            // System.out.println("list1:" + myList);
            try {
                thread_count++;
                System.out.println("Thread Count:" + thread_count);
                worker = null;
                worker = new NodeMon(myList);
                executor.execute(worker);
                Thread.sleep(500);
                //System.out.println(thread_count + "th Thread started ");
            } catch (Exception e) {
                System.err.println("Exceptionn: " + e.getMessage());
            }

        }
    }

}
