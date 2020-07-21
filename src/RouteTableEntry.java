import java.util.Timer;
import java.util.TimerTask;

public class RouteTableEntry {

    /**
     * @author yashwagle
     *
     */


    /**
     * A single tuple in the route table
     * It contains the routerIP, IPAddress, cost, nextHop, subnet mask and the timeout task
     *
     * */

    public static String getRouterIP() {
        return routerIP;
    }

    public static void setRouterIP(String routerIP) {
        RouteTableEntry.routerIP = routerIP;
    }

    static String routerIP;
    private String IPAddress;
    private int cost;
    private String nextHop;
    private String subnetMask;
    Timer timeout;



    public boolean isRouteChangeFlag() {
        return routeChangeFlag;
    }

    public void setRouteChangeFlag(boolean routeChangeFlag) {
        this.routeChangeFlag = routeChangeFlag;
    }

    private boolean routeChangeFlag;

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }


    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public int getCost() {
        return cost;
    }

    public String getNextHop() {
        return nextHop;
    }

    public void resetTimer(){
        timeout.cancel();
        timeout = new Timer();
        timeout.schedule(new RouteEntryTimer(IPAddress), 10000);
    }

    public RouteTableEntry(String IPAddress, String nextHop, int cost, String subnetMask, boolean routeChangeFlag){
        this.IPAddress = IPAddress;
        this.nextHop = nextHop;
        this.cost = cost;
        this.subnetMask = subnetMask;
        this.routeChangeFlag = routeChangeFlag;
        if(!nextHop.equals(routerIP)) {
            this.timeout = new Timer();
            timeout.schedule(new RouteEntryTimer(IPAddress), 10000);
        }
    }

    @Override
    public String toString() {
        return IPAddress+"\t"+nextHop+"\t"+cost+"\n";
    }
}
