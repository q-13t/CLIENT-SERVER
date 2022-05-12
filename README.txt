NetworkNode
This application represents of communication of Clients with server in order to use (or not) available resources.
NetworkNode class is the main class, which begins functionality of the program. It accepts parameters like: -ident <identificator> -tcpport <number of TCP port> -gateway <IP address:port> [<resourses>]
-ident <identificator> will be used as identificator of newly created Node
-tcpport <number of TCP port> will be used as port which will be listening for new connections
-gateway <IP address:port> (!SHOULD NOT BE PROWIDED FOR FIRST NODE!) specifies address and port which will be used to connect to already existing Node and create new
-resources will be assigned to newly created node.

EXAMPLE:
First node should be called with parameters like:
-ident 1 -tcpport 4500 A:10 B:10 C:10

This will create Node on port 4500 and with resources A (10), B (10), C (10) without connecting to existing Node (This Node is initial for program)
-ident 2 -tcpport 4499 -gateway localhost:4500 A:10 B:10 C:10

Will use Node at IP localhost and connect to it by port 4500, which will result in creatin new Node with port 4499 and resources A (10), B (10), C (10).

NOTE: You can’t create two nodes with same port number

When new node is created it listens on provided port and listens for new Clients to connect and provide requests. When Node encounters connection with Client it will check if whole system has enough resources to handle client’s request. 
-If there are not enough resources Client receives FAILED communicate, connection is truncated and resources will NOT be used.
-If there is enough resources Client will receive ALLOCATED communicate and node will proceed to execute provided tasks. 
NOTE: if selected by Client does not have enough resources it will use other Nodes resources in order to fulfill Client’s request
-If Client sends TERMINATE communicate in ANY of parameter’s program will shut down and all Nodes will be destroyed.
EXAMPLE:
SERVER VIEW:

CLIENT VIEW:

After each connection with Client Node will execute function to check resourses of EACH of Nodes. If Node has no resources left it will be destroyed.


Resources
This class imitates resources available for each Node. When Node Created this class will hold the number of available resources and add new to the overall resources amount poll.
Service function is used to allocate resources.
Serve function is used to execute number of provided services They are submitted to ExecutorService as different Callables.
NOTE: each task is represented as Callable class, and actually doesn’t do anything (watch picture below)

If you want A class to do something it will require further implementations…


HOW TO EXECUTE
In order to run this program you need to 
1. Open command line (Win + R -> cmd -> Enter)
2. In command line change your location to the src folder of the project for example:

3. Now you are able to use program by entering specified earlier commands in command prompt.

EXAMPLE:




NOTES:
1. I assume that since NetworcClient was provided to us there is no need to analyze and write documentation for it.
2. Shutting whole system down MIGHT require some time.
Volodymyr Davybida (s22721) group 25c


