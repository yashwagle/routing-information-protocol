import java.io.*;


public class Main {
    /**
     * @author yashwagle
     *
     */


    /**
     * The main class sets the broadcast IP and the port.
     * Starts the receiver thread so that the router is continuously receiving
     * Creates the routing table, starts the timer for the router
     * and sends request on the multicast for rest of the node data
     *
     * */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String myIP = args[0];
        int podNumber = Integer.parseInt(args[1]);
        String subnetIP = "10.0."+podNumber+".0";
        String subnetMask = "255.255.255.0";
        int port = 63001;
        String broadcastIP = "230.230.230.230";
        RoutingTable routingTable = RoutingTable.getRoutingTable();
        routingTable.setRouterIP(myIP);
        routingTable.setNode(podNumber);
        routingTable.setPort(port);
        RoutingTable.setBroadcastAddress(broadcastIP);
        RouteTableEntry.setRouterIP(myIP);
        RouteTableEntry routeTableEntry = new RouteTableEntry(subnetIP,myIP,0,subnetMask,false);
        routingTable.addRouteTableEntry(routeTableEntry);
        new sendRequest(subnetIP,myIP,subnetIP,port,broadcastIP,podNumber).requestEntireTable();
        new RouterTimer().start();
        new MultiCastReciever(port,broadcastIP,podNumber).start();

    }
}
