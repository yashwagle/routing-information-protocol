import java.util.TimerTask;

public class RouteEntryTimer extends TimerTask {
    /**Timer for each route table entry
     * If the timer expires then run method will
     * be executed.
     * On executing the run method the IP address will
     * be removed from the routing table
     *
     * */
    String IPAddress;

    public RouteEntryTimer(String IPAddress){
        this.IPAddress = IPAddress;
    }

    @Override
    public void run() {
        System.out.println("Timed out "+IPAddress);
        RoutingTable r = RoutingTable.getRoutingTable();
        r.removeEntry(IPAddress);
    }
}
