import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author yashwagle
 *
 */

/**
 * Setting the orignator IP address as the next hop.
 * While sending the packets keeping next hop field as the hop count
 * indicated by the routing table.
 * Split horizon is implemented at the receiver side
 * When entering in the routing table, each pod will check
 * if the RIP entry's next hop is its IP address, if it is then
 * it will ignore the RIP entry.
 * A singleton class only one instance of Routing table will
 * exist.
 *
 * */

public class RoutingTable {

    private ArrayList<RouteTableEntry> tableEntries;
    private static volatile RoutingTable routingTable = new RoutingTable();
    private String routerIP;
    private Object lockObject = new Object();

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    private int port;
    private int node;
    private static String broadcastAddress;



    private RoutingTable(){
        tableEntries = new ArrayList<>();
    }

    public static String getBroadcastAddress() {
        return broadcastAddress;
    }

    public static void setBroadcastAddress(String broadcastAddress) {
        RoutingTable.broadcastAddress = broadcastAddress;
    }





    public static RoutingTable getRoutingTable(){
        return routingTable;
    }

    public String getRouterIP() {
        return routerIP;
    }

    public void setRouterIP(String routerIP) {
        this.routerIP = routerIP;
        RouteTableEntry.routerIP = routerIP;
    }



    public void addRouteTableEntry(RouteTableEntry r){
        synchronized (lockObject) {
            tableEntries.add(r);
        }
    }



    /**
     *
     * @param r: The route Entry which needs to be checked
     * @param orignatorIPAddress: The IP address of the host sending the route entry
     * @return: void
     * It will set the route change flag to check if identify if the route has changed
     */
    private void addEntry(RouteEntry r, String orignatorIPAddress) {
        String IPAddress = r.getIPAddress();
        Iterator<RouteTableEntry> routeTableEntryIterator = tableEntries.iterator();
        RouteTableEntry routeTableEntry;
        int cost = Math.min(r.getMetric()+1,16);
        boolean found = false;
        outer:while (routeTableEntryIterator.hasNext()){
            routeTableEntry = routeTableEntryIterator.next();
            if(routeTableEntry.getIPAddress().equals(IPAddress)){

                if(r.getNextHop().equals(routerIP) ){
                    //ignore the IPAddress since the next hop is its own
                    // IP address
                    found = true;
                    break outer;
                }
                //Destination IP exists and its cost is less than existing cost
                // Add the routing tble entry
                else if(routeTableEntry.getCost()>r.getMetric()+1 && r.getMetric()!=16){
                    RouteTableEntry newRouteTableEntry = new RouteTableEntry(IPAddress,orignatorIPAddress,cost,r.getSubnetMask(),true);
                    tableEntries.remove(routeTableEntry);
                    tableEntries.add(newRouteTableEntry);
                    System.out.println("Routing entry inserted in condition 1");
                    System.out.println(newRouteTableEntry);
                    routeTableEntry.timeout.cancel();


                }

                else if(r.getMetric()==16 && routeTableEntry.getNextHop().equals(orignatorIPAddress)){
                    //Destination cost is now infinity
                    RouteTableEntry newRouteTableEntry = new RouteTableEntry(IPAddress,orignatorIPAddress,16,r.getSubnetMask(),true);
                    tableEntries.remove(routeTableEntry);
                    //Begin deletion process
                    tableEntries.add(newRouteTableEntry);
                    System.out.println("Routing entry inserted in condition 2");
                    System.out.println(newRouteTableEntry);
                    routeTableEntry.timeout.cancel();

                }
                // Destination IP exists and its cost is greater than the existing cost
                else if(r.getMetric()+1 > routeTableEntry.getCost() && routeTableEntry.getNextHop().equals(orignatorIPAddress)){
                    RouteTableEntry newRouteTableEntry = new RouteTableEntry(IPAddress,orignatorIPAddress,cost,r.getSubnetMask(),true);
                    tableEntries.remove(routeTableEntry);
                    tableEntries.add(newRouteTableEntry);
                    routeTableEntry.timeout.cancel();
                    System.out.println("Routing entry inserted in condition 3");
                    System.out.println(newRouteTableEntry);
                }
                // The routing entry is same as the one present in the table
                // thus we reset the timer.
                else if(routeTableEntry.getNextHop().equals(orignatorIPAddress) && r.getMetric()+1 == routeTableEntry.getCost()){
                    routeTableEntry.resetTimer();
                }
                found = true;
                break;
            }
        }
        // Destination IP does not exist
        if(found==false){
            RouteTableEntry newRouteTableEntry = new RouteTableEntry(r.getIPAddress(),r.getNextHop(),r.getMetric()+1,r.getSubnetMask(),true);
            tableEntries.add(newRouteTableEntry);
        }

    }

    /**
     *
     * Method for normal updates in which we send the entire routing table
     * Will take the entire routing table, make an RIP packet and
     * send it.
     */

    public void normalUpdate(){
        RIPPacket ripPacket = null;
        synchronized (lockObject) {
            ArrayList<RouteEntry> routeEntries = new ArrayList<>();
            Iterator<RouteTableEntry> routeTableEntryIterator = tableEntries.iterator();
            RouteEntry routeEntry;
            RouteTableEntry routeTableEntry;
            while (routeTableEntryIterator.hasNext()) {
                routeTableEntry = routeTableEntryIterator.next();
                routeEntry = new RouteEntry(routeTableEntry.getIPAddress(), routeTableEntry.getNextHop(), routeTableEntry.getCost(), routeTableEntry.getSubnetMask());
                routeEntries.add(routeEntry);
            }
             ripPacket = new RIPPacket(2);
            ripPacket.setEntries(routeEntries);
         //  System.out.println("log:    Normal update packet");
          //  System.out.println(ripPacket);

        }
        if(ripPacket!=null) {
            new MultiCastSender(port, broadcastAddress, node, ripPacket).start();
           System.out.println(" Routing table");
            System.out.println(routingTable);
        }
    }

    /**
     * Will take RIP packet as input and update
     * the routing table
     * */
    public void addRIPPacket(RIPPacket ripPacket, String orignatorIPAddress){
        synchronized (lockObject) {
            List<RouteEntry> routeEntries = ripPacket.entries;
            Iterator<RouteEntry> routeEntryIterator = routeEntries.iterator();
            if (!orignatorIPAddress.equals(routerIP)) {
                System.out.println("Packet from"+orignatorIPAddress);
                while (routeEntryIterator.hasNext()) {
                    addEntry(routeEntryIterator.next(), orignatorIPAddress);
                }
                triggerUpdate();
            }

        }
    }



    /***
     * Method for triggered updates
     * will check for route change flag if it is true it will
     * add to the trigger updates and send the packet
     */

    private void triggerUpdate(){
        RIPPacket ripPacket=null;
        synchronized (lockObject) {
            Iterator<RouteTableEntry> routeTableEntryIterator = tableEntries.iterator();
            ArrayList<RouteEntry> routeEntries = new ArrayList<>();
            RouteTableEntry routeTableEntry;
            RouteEntry routeEntry;
            // Iterate through the routing table
            while (routeTableEntryIterator.hasNext()) {
                routeTableEntry = routeTableEntryIterator.next();
                // Check if the routing entry has changed
                if (routeTableEntry.isRouteChangeFlag()) {
                    routeEntry = new RouteEntry(routeTableEntry.getIPAddress(), routeTableEntry.getNextHop(), routeTableEntry.getCost(), routeTableEntry.getSubnetMask());
                    routeEntries.add(routeEntry);
                    routeTableEntry.setRouteChangeFlag(false);
                }
            }
            if(routeEntries.isEmpty()==false) {
                 ripPacket = new RIPPacket(2);
                ripPacket.setEntries(routeEntries);
               System.out.println("log:    Trigger update routing table");
     //           System.out.println(ripPacket);

            }
        }
        if(ripPacket!=null) {
            new MultiCastSender(port, broadcastAddress, node, ripPacket).start();
            System.out.println(routingTable);
        }

    }

    public void removeEntry(String IP){
        synchronized (lockObject){
            Iterator<RouteTableEntry> routeTableEntryIterator = tableEntries.iterator();
            RouteTableEntry rdelete;
            while (routeTableEntryIterator.hasNext()){
                rdelete = routeTableEntryIterator.next();
                if(rdelete.getIPAddress().equals(IP)){
                    rdelete.setRouteChangeFlag(true);
                    rdelete.setCost(16);
                }
            }
            triggerUpdate();
        }
    }


    @Override
    public String toString() {
        String packetOutput="IPAddress\tNextHop\tcost\n";
        Iterator<RouteTableEntry> routeEntryIterator= tableEntries.iterator();
        while (routeEntryIterator.hasNext()){
            packetOutput = packetOutput + routeEntryIterator.next() + "\n";
        }
        return "Packet Entries are:\n"+packetOutput;
    }
}
