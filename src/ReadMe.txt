To run the program we need to provide 2 arguments the IP address of the router and the pod number.
The IP address of the internal network is considered as 10.0.<pod number>.0/24
The multicast address  is 230.230.230.230
The port number used is 63001
Sample run
java ReciverMain "172.18.0.21" "1"
Tested on the testing software provided using the following commands.
docker build -t javaapptest .       // building the image from docker file
docker run -it -p 8081:8080 --cap-add=NET_ADMIN --net nodenet --ip 172.18.0.21 javaapptest "172.18.0.21" 1 //running the program

