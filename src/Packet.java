public class Packet {
    private String sender,receiver,msg;
    int type;//1 for msg,2 for arp broadcast,3 for arp reply
    public Packet(String s,String r,String m,int t){
        this.sender=s;//Sender ip address
        this.type=t;//Packet type
        this.receiver=r;//Receiver ip address
        this.msg=m;//Message
    }

    public String getMsg() {
        return msg;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {//to print packet
        return "Packet{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", msg='" + msg + '\'' +
                ", type=" + type +
                '}';
    }
}
