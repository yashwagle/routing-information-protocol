import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MultiCastSender extends Thread {

    /**
     * @author yashwagle
     *
     */
    /**
     * Sender class it will send the data on the multicast
     * It will take RIP packet as an input and send it on the multicast.
     *
     * */
    private int port;
    private String broadcastAddress;
    private int node;
    private RIPPacket ripPacket;


    public MultiCastSender(int port,String broadcastAddress, int node, RIPPacket ripPacket){
        this.port = port;
        this.broadcastAddress = broadcastAddress;
        this.node = node;
        this.ripPacket = ripPacket;
    }

    /**
     * Sends the RIP packet to a multicast network
     *
     */

    public void sendRIPMessageMulticast() throws IOException {
        // Serializing the RIP packet
        byte[] serializedRIPObject = serializeRIPPacket(ripPacket);

        DatagramSocket senderSocket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(broadcastAddress);
    //    System.out.println("Packet sent is :");
     //   System.out.println(ripPacket);
        DatagramPacket packet = new DatagramPacket(serializedRIPObject,serializedRIPObject.length,group,port);
        senderSocket.send(packet);
        senderSocket.close();

    }

    /**
     * Take the RIP packet as input and return the serialized byte array
     *
     */

    public byte[] serializeRIPPacket(RIPPacket ripPacket) throws IOException {
        ByteArrayOutputStream ripByteStream = new ByteArrayOutputStream();
        ObjectOutputStream ripObjectOutputStream =new ObjectOutputStream(ripByteStream);
        ripObjectOutputStream.writeObject(ripPacket);
        byte[] serializedRIPObject = ripByteStream.toByteArray();
        ripObjectOutputStream.close();
        ripByteStream.close();
        return serializedRIPObject;
    }

    public void senRIPMessageUnicast(RIPPacket ripPacket,String IPaddress) throws IOException {
        byte[] serializedRIPObject = serializeRIPPacket(ripPacket);
        DatagramSocket senderSocket = new DatagramSocket();
        InetAddress ip = InetAddress.getByName(IPaddress);
        DatagramPacket datagramPacket = new DatagramPacket(serializedRIPObject,serializedRIPObject.length,ip,port);

        senderSocket.send(datagramPacket);
    }

    @Override
    public void run() {

            try {
                sendRIPMessageMulticast();
            } catch (IOException ie) {
                System.out.println("IO Exception while sending data in pod " + node);
                ie.printStackTrace();
            }

        }

    }

