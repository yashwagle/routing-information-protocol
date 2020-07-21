
public class RouterTimer extends Thread {

    /**
     * @author yashwagle
     *
     */

    /**
     * Timer for the router which will
     * trigger the timely updates which a router
     * sends after every 5 seconds
     * */

    @Override
    public void run() {
        while (true) {
            RoutingTable r = RoutingTable.getRoutingTable();
            r.normalUpdate();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Error while sleeping");
                e.printStackTrace();
            }
        }
    }
}
