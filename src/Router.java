import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Router extends RoutingEntry implements Runnable {
    private String routerName;
    private BlockingQueue<Packet> q;
    private Map<String,RoutingEntry> entries;
    private Map<String,String> routingTable;

    public Router(String routerName) {
        this.routerName=routerName; //Router name
        this.q = new LinkedBlockingQueue<>();//Queue for packets
        this.routingTable = new HashMap<>();//Routing table
        this.entries = new HashMap<>();//Interface connected to which switch or router
    }

    public String getRouterName() {
        return routerName;
    }

    public void addRoutingEntry(String i,RoutingEntry e){//Tells router which device is connected to which interface
        this.entries.put(i,e);
    }
    public void run() {//Thread runs this method continuously
        while (true) {
            if (q.isEmpty()) {//If no packet found for this thread,go to sleep for 1 second
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Iterator<Packet> listOfPackets = q.iterator();
            while (listOfPackets.hasNext()) {
                Packet packet = listOfPackets.next();
                RoutingEntry entry = entries.get(routingTable.get(getNetMask(packet,"255.0.0.0")));//Get routing table entry
                if(entry.getClass().getSimpleName().equals("Switch")) {//If to be sent to switch
                    Switch sw = (Switch) entry;
                    if (sw.addToQueue(packet)) {//add to switch's queue
                        q.remove(packet);//Remove from router's queue
                    }
                }
                else if(entry.getClass().getSimpleName().equals("Router")){//If to be sent to router
                    Router ro = (Router) entry;
                    if (ro.addToQueue(packet)) {//Add to that router's queue
                        q.remove(packet);//Remove from this router's queue
                    }
                }
            }
        }
    }
    public void makeDirectRoutingTable(){//make routing table based on direct connections
        System.out.println("Making Direct Routing Table");
        for (Map.Entry<String, RoutingEntry> me : entries.entrySet()) {
            RoutingEntry entry = me.getValue();
            if((entry.getClass().getSimpleName()).equals("Switch")){//if switch add directly as it is known that which subnetwork
                Switch curr = (Switch)entry ;
                routingTable.put(curr.getNetId(),me.getKey());
            }
        }
    }
    public void printRoutingTable(){//print routing table
        for (Map.Entry<String, String> me : routingTable.entrySet()) {
            System.out.print(me.getKey() + ":");
            System.out.println(me.getValue());
        }
    }

    public Map<String, String> getRoutingTable() {
        return routingTable;
    }//returns routing table

    public void shareRoutingTable(){//share routing table
        System.out.println("Making Routing Table from shared info for "+routerName);
        for (Map.Entry<String, RoutingEntry> me : entries.entrySet()) {//to all router's directly connected
            RoutingEntry entry = me.getValue();
            if(entry.getClass().getSimpleName().equals("Router")) {
                Router router = (Router) entry;
                Map<String, String> map = router.getRoutingTable();
                for (Map.Entry<String, String> rt : map.entrySet()) {
                    if(!routingTable.containsKey(rt.getKey()))//Add if not in table already
                        routingTable.put(rt.getKey(), me.getKey());
                }
            }
        }
    }
    private String getNetMask(Packet packet,String mask){//Gives AND of mask and ip i.e. Network id
        String networkAddr="";
        String[] ipAddrParts=packet.getReceiver().split("\\.");
        String[] maskParts=mask.split("\\.");

        for(int i=0;i<4;i++){
            int x=Integer.parseInt(ipAddrParts[i]);
            int y=Integer.parseInt(maskParts[i]);
            int z=x&y;
            if(i!=3)
                networkAddr+=z+".";
            else
                networkAddr+=z;
        }
        return networkAddr;
    }
    public boolean addToQueue(Packet packet){//add to router's queue(added by switch)
        System.out.println("Received by "+routerName);
        return q.add(packet);
    }
}
