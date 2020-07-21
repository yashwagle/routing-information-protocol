import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author yashwagle
 *
 */

/**
 * The RIP packet which will be sent over UDP and contain multiple
 * route entries (atmost 25)
 * Contains a list of route entries, where each route entry is an RTE of the packet.
 *
 * */

public class RIPPacket implements Serializable {


    int command,version;
    private int numRouteEntries;
    List<RouteEntry> entries;

    public RIPPacket(int command){
        this.command = command;
        this.version = 2;
        entries = new ArrayList<>();
    }



    public boolean addRouteEntry(RouteEntry r){
        if(numRouteEntries>25)
            return false;
        numRouteEntries++;
        entries.add(r);
        return true;
    }

    public void setEntries(ArrayList<RouteEntry> entries){
        this.entries = entries;
    }

    @Override
    public String toString() {
        String packetOutput="";
        Iterator<RouteEntry> routeEntryIterator= entries.iterator();
        while (routeEntryIterator.hasNext()){
            packetOutput = packetOutput + routeEntryIterator.next() + "\n";
        }
        return "Packet Entries are:\n"+packetOutput;
    }
}
