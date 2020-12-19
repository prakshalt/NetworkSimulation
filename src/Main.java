import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) {
        int n;
        /*
        (Network 1)  [h1,h2,h3]---[sw1]----{r1}----[sw2]---[h1,h2,h3]  (Network 2)
                                            |
                                            |
                                           {r2}
                                            |
                                            |
              (Network 3)   [h1,h2,h3]----[sw3]

         */
        BlockingQueue<NetTask> tasks = new LinkedBlockingQueue<>();//tasks queue used so that mainThread can assign tasks to individual threads
        Router r1 = new Router("Router 1");//connects switch 1,2 & router 2
        Router r2 = new Router("Router 2");//connects switch 3 & router 1
        Switch s1 = new Switch("10.0.0.0","Switch 1",r1);
        Switch s2 = new Switch("20.0.0.0","Switch 2",r1);
        Switch s3 = new Switch("30.0.0.0","Switch 3",r2);

        r1.addRoutingEntry("Interface 1",s1);//Tells router which device is connected to which interface
        r1.addRoutingEntry("Interface 2",s2);
        r1.addRoutingEntry("Interface 3",r2);

        r2.addRoutingEntry("Interface 1",s3);
        r2.addRoutingEntry("Interface 2",r1);

        //Threads are made for 3 hosts for each network
        Runnable n1_h1=new Host(s1,tasks,"10.0.0.1","01-ab-02-cd-01-01");//Tells host which switch it is connected to
        Runnable n1_h2=new Host(s1,tasks,"10.0.0.2","01-ab-02-cd-01-02");//ip and mac address are assigned
        Runnable n1_h3=new Host(s1,tasks,"10.0.0.3","01-ab-02-cd-01-03");
        Runnable n2_h1=new Host(s2,tasks,"20.0.0.1","01-ab-02-cd-02-01");
        Runnable n2_h2=new Host(s2,tasks,"20.0.0.2","01-ab-02-cd-02-02");
        Runnable n2_h3=new Host(s2,tasks,"20.0.0.3","01-ab-02-cd-02-03");
        Runnable n3_h1=new Host(s3,tasks,"30.0.0.1","01-ab-02-cd-03-01");
        Runnable n3_h2=new Host(s3,tasks,"30.0.0.2","01-ab-02-cd-03-02");
        Runnable n3_h3=new Host(s3,tasks,"30.0.0.3","01-ab-02-cd-03-03");

        //Routing Table based on direct connections is made and printed
        r1.makeDirectRoutingTable();
        r1.printRoutingTable();
        r2.makeDirectRoutingTable();
        r2.printRoutingTable();

        //Both routers share routing table with each other
        r1.shareRoutingTable();
        r1.printRoutingTable();
        r2.shareRoutingTable();
        r2.printRoutingTable();

        //Threads for 9 hosts,2 routers are made
        Thread t1=new Thread(n1_h1);
        t1.start();
        Thread t2=new Thread(n1_h2);
        t2.start();
        Thread t3=new Thread(n1_h3);
        t3.start();
        Thread t4=new Thread(n2_h1);
        t4.start();
        Thread t5=new Thread(n2_h2);
        t5.start();
        Thread t6=new Thread(n2_h3);
        t6.start();
        Thread t7=new Thread(n3_h1);
        t7.start();
        Thread t8=new Thread(n3_h2);
        t8.start();
        Thread t9=new Thread(n3_h3);
        t9.start();
        Thread t10=new Thread(r1);
        t10.start();
        Thread t11=new Thread(r2);
        t11.start();
        Scanner sc= new Scanner(System.in);
        String sender,receiver,msg;
        //Take input from user for operations
        while(true){
            System.out.println("Press 1 to instruct host to send packet,-1 to quit");
            n=sc.nextInt();
            if(n==-1) {
                System.exit(0);

            }
            if(n==1){
                System.out.println("Enter source ip,destination ip and message to be sent in packet");
                sender=sc.next();
                receiver=sc.next();
                msg=sc.next();
                Packet p = new Packet(sender,receiver,msg,1);
                NetTask task= new NetTask(sender,1,p,1);
                tasks.add(task);
                NetTask task1= new NetTask(receiver,2);
                tasks.add(task1);
            }
        }
    }
}