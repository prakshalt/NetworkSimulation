public class NetTask {
    private String TaskFor;
    private int id;//1 for send,2 for receive
    private Packet packet;
    private int priority;//1 for quick,2 for wait

    public NetTask(String taskFor, int id, Packet packet,int priority) {//Constructor for sending task
        TaskFor = taskFor;//Task for which host
        this.id = id;//sending or receiving task
        this.packet = packet;//Packet to be sent constructed in main from user input
        this.priority=priority;//Priority for sending
    }

    public NetTask(String taskFor, int id) {//Constructor for receiving task
        TaskFor = taskFor;//Task for which host
        this.id = id;//sending or receiving task
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTaskFor() {
        return TaskFor;
    }

    public int getId() {
        return id;
    }

    public Packet getPacket() {
        return packet;
    }

    @Override
    public String toString() { // to print task
        return "NetTask{" +
                "TaskFor='" + TaskFor + '\'' +
                ", id=" + id +
                ", packet=" + packet +
                '}';
    }
}
