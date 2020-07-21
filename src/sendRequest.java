public class sendRequest {


    RIPPacket ripPacket;
    String IPAddress;
    String nextHop;
    String subnetMask;
    int port;


    /**
     * @author yashwagle
     *
     */
    /**
     *  A send request class which will ask for the entire routing
     * table of the routers on the multicast.
     *
     **/

    public sendRequest( String IPAddress, String nextHop, String subnetMask, int port, String broadCastAddress, int node) {
        this.IPAddress = IPAddress;
        this.nextHop = nextHop;
        this.subnetMask = subnetMask;
        this.port = port;
        this.broadCastAddress = broadCastAddress;
        this.node = node;
    }

    String broadCastAddress;
    int node;

    public void requestEntireTable(){
        ripPacket = new RIPPacket(1);
        RouteEntry routeEntry = new RouteEntry(IPAddress,nextHop,16,subnetMask);
        routeEntry.setAddressFamily(0);
        ripPacket.addRouteEntry(routeEntry);
        new MultiCastSender(port,broadCastAddress,node,ripPacket).start();
    }

}
