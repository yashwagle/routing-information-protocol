import java.io.Serializable;


/**
 * @author yashwagle
 *
 */

/**
* A class representing a single route entry
* that will be sent in an RIP packet
* */

public class RouteEntry implements Serializable {

    private String IPAddress,nextHop;
    private int metric;
    private String subnetMask;

    public int getAddressFamily() {
        return addressFamily;
    }

    public void setAddressFamily(int addressFamily) {
        this.addressFamily = addressFamily;
    }

    private int addressFamily;

    public String getIPAddress() {
        return IPAddress;
    }

    public String getNextHop() {
        return nextHop;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public int getMetric() {
        return metric;
    }



    public RouteEntry(String IPAddress, String NextHop, int metric, String subnetMask){
        this.IPAddress = IPAddress;
        this.nextHop = NextHop;
        this.subnetMask = subnetMask;
        this.metric = metric;
        this.addressFamily =2;
    }

    @Override
    public String toString() {
        return "IP Address :"+IPAddress+"\n"+"Subnet Mask :"+subnetMask+"\n"+"Next Hop :"+nextHop+"\n"+" Metric:"+metric;
    }
}
