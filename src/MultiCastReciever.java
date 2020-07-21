import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * @author yashwagle
 *
 */

/**
 * Receiver thread which will be continuously running.
 * It will keep listening to the multicast and gather packets
 * from the multicast.
 *
 * */



public class MultiCastReciever extends Thread{
    private int port;
    private String broadcastAddress;
    private int node;

    public MultiCastReciever(int port,String broadcastAddress, int node){
        this.port = port;
        this.broadcastAddress = broadcastAddress;
        this.node = node;
    }

    public DatagramPacket recievePackets() throws IOException {
        MulticastSocket dataSocket = new MulticastSocket(port);
        InetAddress group = InetAddress.getByName(broadcastAddress);
        dataSocket.joinGroup(group);
        byte[] inputData = new byte[65000];
        DatagramPacket datagramPacket = new DatagramPacket(inputData,inputData.length);
        dataSocket.receive(datagramPacket);
        return datagramPacket;
    }

    @Override
    public void run() {
        byte[] data;
        while (true){
            try {
                DatagramPacket p = recievePackets();
                InetAddress sourceIP = p.getAddress();
                data = p.getData();
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                ObjectInputStream outputStream = new ObjectInputStream(bis);
                RIPPacket r = (RIPPacket)outputStream.readObject();
                RoutingTable routingTable = RoutingTable.getRoutingTable();
             //   System.out.println("Log: Packet recieved source IP "+sourceIP);
              //  System.out.println(r);.

                if(r.command==2) {
                    routingTable.addRIPPacket(r, sourceIP.toString().substring(1).trim());
                    }
                else if (r.command==1){
                    if(r.entries.size()>0){
                        RouteEntry routeEntry = r.entries.get(0);
                        if(r.entries.size()==1 && routeEntry.getAddressFamily()==0 && routeEntry.getMetric()==16){
                       //     System.out.println("size");
                            routingTable.normalUpdate();
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error Exceotion in recieve");
                e.printStackTrace();
            }
        }
    }
}
