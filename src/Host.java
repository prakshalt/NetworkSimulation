import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Host implements Runnable {
    private Switch sw;
    private BlockingQueue<NetTask> t;
    private Map<String,String> arpCache;
    private String ip,mac;

    public String getMac() {
        return mac;
    }

    public Host(Switch sw, BlockingQueue<NetTask> t, String ip, String mac) {//Constructor for host
        this.sw=sw; //Switch host is connected to
        this.t = t;//Common Task queue
        this.ip=ip; //Host ip address
        this.mac=mac; //Host mac address
        this.arpCache = new HashMap<>(); //Arp cache declaration
    }
    public void printArpCache(){//Method to print Arp Cache
        System.out.println("Printing new arp cache for host "+ip);
        for (Map.Entry<String, String> me : arpCache.entrySet()) {
            System.out.print(me.getKey() + ":");
            System.out.println(me.getValue());
        }
        System.out.println();
    }
    public void run() {//Thread runs this method continuously
            while(true) {
                if(t.isEmpty()) {//If no task found for this thread,go to sleep for 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Iterator<NetTask> listOfTasks = t.iterator();//Go through all tasks in task queue
                while (listOfTasks.hasNext()) {
                    NetTask task = listOfTasks.next();
                    if (task.getTaskFor().equals(ip)) {//If task in queue is for this host
                        if (task.getId() == 1 && task.getPriority()==1) {//If it is a sending task with higher priority
                           addPacket(task);//send packet
                        }
                        else if(task.getId()==2){//If it is receiving task
                            removePacket(task);//receive packet
                        }
                    }
                }
            }
        }
        private void addPacket(NetTask task){//Method to send packet from host
            Packet p = task.getPacket();//Retrieves packet from task built in main

            if(arpCache.containsKey(p.getReceiver())){//If receiver mac address is found in Arp Cache
                System.out.println("Found in Arp Cache");
                System.out.println("Sent packet from "+ip+": "+p);
                if (sw.addToQueue(p)) {//Send to switch
                    t.remove(task);//Remove task from queue to avoid doing more than once

                }
            }
            else {//If receiver mac address is not found in Arp Cache
                Packet arpReq = new Packet(p.getSender(),p.getReceiver(),"",2);//form Arp Request packet
                System.out.println("Not found in Arp Cache, sending Arp Request");
                System.out.println("Sent Arp Request packet from "+ip+": "+arpReq);
                if (sw.addToQueue(arpReq)) {//Send to switch
                    t.remove(task);//Remove task from from queue to avoid doing more than once
                    task.setPriority(2);//Set waiting priority for this task until arp reply is received
                    t.add(task);//Add this lower priority task
                    t.add(new NetTask(p.getReceiver(),2));//Add task for receiver to receive arp request
                }
            }
        }
        private void removePacket(NetTask task){
            Packet p = sw.removeFromQueue(ip);//Get packet from switch if it is for this host
            if(p!=null){
                if(p.getType()==2){//If packet is Arp request
                    System.out.println("Received Arp Request packet by " + ip + " :" + p);
                    Packet arpRep = new Packet(p.getReceiver(),p.getSender(),getMac(),3);//form arp reply
                    System.out.println("Sent Arp Reply packet to " + p.getSender() + " :" + arpRep);
                    if (sw.addToQueue(arpRep)) {//send arp reply to switch
                        t.remove(task);//Remove this task from queue to avoid doing more than once
                        t.add(new NetTask(p.getSender(),2));//Add task for receiver to receive arp reply
                    }
                }
                else if(p.getType()==3){//If packet is Arp reply
                    System.out.println("Received Arp Reply packet by " + ip + " :" + p);
                    arpCache.put(p.getSender(),p.getMsg());//Add to arp cache
                    t.remove(task);//Remove this task from queue to avoid doing more than once
                    printArpCache();//Print new arp cache
                    Iterator<NetTask> listOfTasks = t.iterator();
                    while (listOfTasks.hasNext()) {//Find all remaining tasks
                        NetTask task1 = listOfTasks.next();
                        if(task1.getTaskFor().equals(p.getReceiver())){
                            if(task1.getId()==1 && task1.getPacket().getType()==1){//Found original message sending task
                                t.remove(task1);
                                task1.setPriority(1);//Increase priority so that it can be sent now
                                t.add(task1);
                            }
                        }
                    }
                }
                else {//If packet is message
                    t.remove(task);//Remove task from queue to avoid doing more than once
                    System.out.println("Received packet by " + ip + " :" + p);
                }
            }
        }
}