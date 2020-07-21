**RIP Version 2**

Implemented the routing information protocol version 2 as specified by [RFC 2453](https://tools.ietf.org/html/rfc2453). 
This project was developed for inter-pod communication for a set of moving pods.
Each pod has an internal network and a small radio that can only send messages over a short distance.
Hence pods can move in and out of range.
Pods exchange routing information with other pods that are in range and based on these packets they calculate the shortest path to all other pods in the network.
Each pod has its own IP address (in addition to the internal network) and sends/receives data from multicast IP.
The disappearing of pods is simulated by blocking packets from a particular IP.

**Brief Description**

Each RIP packet has upto 25 routing entries and each routing entry has the following fields
1. IP Address of the internal network of the pod.
2. The subnet mask
3. The next hop i.e. the IP address of the pod to which internal IP belongs
4. The cost of reaching the pod with next hop IP address

Each pod stores this information and sends it on the multicast IP after an interval of 5 seconds.
The pods store these routing entries in a routing table. 
The pod needs to continuously receive these routing entries to make sure the pod is in range of its radio network.
If the pod does not receive a routing entry for 10 seconds, the routing entry will timeout and the its cost will be modified to 16.
This change will be immediately sent on the multicast.
Any change in the routing table will be immediately sent on the multicast.

**Working of the protocol**

![](RIPv2Working.gif)
