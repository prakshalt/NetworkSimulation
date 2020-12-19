import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Switch extends RoutingEntry{
    private String netId;
    private String swName;
    private BlockingQueue<Packet> q;
    private Router r;

    public Switch(String netId,String swName,Router r) {
        this.netId = netId;  //Network Id (Not in real world switch, but kept for implementation)
        this.swName=swName; //Switch Name
        this.q = new LinkedBlockingQueue<Packet>();//Packet Queue(Real Switch works on frames,but kept for implementation)
        this.r = r;//Router switch is connected to
    }

    public String getNetId() {
        return netId;
    }

    public boolean addToQueue(Packet packet) {//host calls this method to send to switch
        if (checkNetMask(packet)) {//If receiver is in same network
            System.out.println("Received by "+swName);
            return q.add(packet);//Then add to queue from which receiver takes
        }
        else {//If receiver is not in same network
            System.out.println("Received by "+swName);
            System.out.println("Sent to "+r.getRouterName());
            return r.addToQueue(packet);//Then add to router's queue
        }
    }

    private boolean checkNetMask(Packet packet){//checks if packet is in same network
        String recIp = packet.getReceiver();
        return recIp.split("\\.")[0].equals(netId.split("\\.")[0]);
    }
    public Packet removeFromQueue(String ip){
        Iterator<Packet> listOfPackets = q.iterator();
        while (listOfPackets.hasNext()) {//Check all packets in switch queue
            Packet packet =listOfPackets.next();
            if(packet.getReceiver().equals(ip)){//Check if it is for receiver host
                if(q.remove(packet))//Remove from switch queue
                    return packet;//return to host
            }
        }
        return null;
    }
}
