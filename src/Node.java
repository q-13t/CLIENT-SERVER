import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class implements {@link Runnable} and acts as server,
 * holding different Nodes, redirecting execution of
 * requested by clients requests and replays to
 * the clients
 */
public class Node implements Runnable {
    private ServerSocket serverSocket;
    private Socket socket;
    private int SERVER_PORT = 0;
    private int IDENTIFICATOR = 0;
    private boolean alive = true;
    public Resources resourses = new Resources();
    public ArrayList<String> arguments = new ArrayList<>();
    public static ArrayList<Node> nodes = new ArrayList<>();
    public static ExecutorService executorService = Executors.newCachedThreadPool();
    public PrintWriter out;
    public BufferedReader in;

    /**
     * This function checks if there are no Nodes with same
     * {@code Identificator}, if not sets the provided parameters and submits to
     * {@link ExecutorService} newly created Node and adds this Node to
     * {@code nodes} pool.
     * 
     * @param identificator unicue identificator of Node
     * @param serverPort    unicue port on which server listens
     * @param arguments     resourses for future Node
     */

    public Node(int identificator, int serverPort, ArrayList<String> arguments) {
        boolean can = true;
        for (Node iterable : nodes) {
            if (iterable.SERVER_PORT == serverPort) {
                System.out.println("THE SAME IDENTIFICATOR CANT BE USED");
                can = false;
                break;
            }
        }
        if (can) {
            this.SERVER_PORT = serverPort;
            this.IDENTIFICATOR = identificator;
            this.arguments = arguments;
            allocateResourses();
            executorService.submit(this);
            nodes.add(this);
            if (nodes.size() == 1) {
                showAvailableNodes();
            }
        }
    }

    /**
     * Main function of any {@link Node}
     * <p>
     * Creates the {@link ServerSocket} which listens untill {@code alive} is true.
     * If not then {@link ServerSocket} is closed and Node is no longer awailable.
     * <p>
     * When {@link NetworkClient} is connected to specific {@code SERVER_PORT}
     * Node creates {@link BufferedReader} and {@link PrintWriter} to comunicate.
     * <p>
     * When Client provides request, if it contains {@code NODE}, then this node
     * creates new
     * Node (fter creation it still is alive and keeps listening for new Clients).
     * <p>
     * If Client has'nt provided specific flag, then Node proceeds to check if
     * there are enoung {@link Resources} in current system.
     * <p>
     * - If there are enoung - flag {@code ALLOCATED} is sent to Client and
     * each requested resourse is being executed by any Node that has enough
     * resourses to handle the task.
     * Afterwards Node sends {@code <resourse>:<amount>:<server IP>:<server PORT>}
     * for each task that was executed on different Node.
     * <p>
     * - If there are NOT enoung - flag {@code FAILED} is sent to Client and
     * connection is destroyed. NOTE all resourses will be secured.
     * 
     * @throws Hopefully Nothing
     */

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            while (alive) {
                try {
                    socket = serverSocket.accept();
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    System.out.println(
                            "Cliet -> " + socket.getInetAddress() + " has connected on port -> " + socket.getPort());
                } catch (Exception e) {
                    this.alive = false;
                    break;
                }

                String request;
                request = in.readLine();
                System.out.println("Request -> " + request);
                ArrayList<String> request_separated = new ArrayList<>();
                for (String string : request.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", "")
                        .split(" ")) {
                    request_separated.add(string);
                }
                if (request_separated.contains("NODE")) {
                    ArrayList<String> arguments = new ArrayList<>();

                    for (int i = 3; i < request_separated.size(); i++) {
                        arguments.add(request_separated.get(i));
                    }
                    new Node(Integer.parseInt(request_separated.get(1)),
                            Integer.parseInt(request_separated.get(2)),
                            arguments);
                } else {
                    if (request_separated.contains("TERMINATE")) {
                        System.out.println("TERMINATE detected, shuting down system");
                        this.alive = false;
                        shutdown(this);
                        System.out.println("System shuted down!");
                        break;
                    } else {

                        boolean allocated = true;

                        for (int i = 0; i < request_separated.size(); i++) {

                            if (request_separated.get(i).replaceAll(":[0-9]+", "").equals("A")) {
                                if (Resources.Asum < Integer
                                        .parseInt(request_separated.get(i).replaceAll("[^0-9]+", ""))) {
                                    print("FAILED", this.out);
                                    System.out.println("FAILED to allocate resourses!");
                                    allocated = false;
                                    break;
                                }
                            }
                            if (request_separated.get(i).replaceAll(":[0-9]+", "").equals("B")) {
                                if (Resources.Bsum < Integer
                                        .parseInt(request_separated.get(i).replaceAll("[^0-9]+", ""))) {
                                    print("FAILED", this.out);
                                    System.out.println("FAILED to allocate resourses!");
                                    allocated = false;
                                    break;
                                }
                            }
                            if (request_separated.get(i).replaceAll(":[0-9]+", "").equals("C")) {
                                if (Resources.Csum < Integer
                                        .parseInt(request_separated.get(i).replaceAll("[^0-9]+", ""))) {
                                    print("FAILED", this.out);
                                    System.out.println("FAILED to allocate resourses!");
                                    allocated = false;
                                    break;
                                }
                            }
                        }

                        if (allocated) {
                            print("ALLOCATED", this.out);
                            for (int i = 1; i < request_separated.size(); i++) {
                                String[] separated = request_separated.get(i).split(":");
                                int resourse_requested_ammount = Integer.parseInt(separated[1]);
                                boolean finished = false;
                                for (Node iterable : nodes) {
                                    if (finished) {
                                        break;
                                    } else {
                                        switch (separated[0]) {
                                            case "A": {

                                                if (iterable.resourses.Ares > 0) {
                                                    int handeled = resourses.serve(separated[0],
                                                            resourse_requested_ammount,
                                                            iterable);

                                                    print("A:" + handeled + ":"
                                                            + iterable.serverSocket.getInetAddress()
                                                            + ":" + iterable.serverSocket.getLocalPort(), this.out);
                                                    if (handeled != resourse_requested_ammount) {
                                                        resourse_requested_ammount = resourse_requested_ammount
                                                                - handeled;
                                                        finished = false;
                                                    } else {
                                                        finished = true;
                                                    }
                                                }
                                                break;
                                            }
                                            case "B": {
                                                if (iterable.resourses.Bres > 0) {
                                                    int handeled = resourses.serve(separated[0],
                                                            resourse_requested_ammount,
                                                            iterable);

                                                    print("B:" + handeled + ":"
                                                            + iterable.serverSocket.getInetAddress()
                                                            + ":" + iterable.serverSocket.getLocalPort(), this.out);
                                                    if (handeled != resourse_requested_ammount) {
                                                        resourse_requested_ammount = resourse_requested_ammount
                                                                - handeled;
                                                        finished = false;
                                                    } else {
                                                        finished = true;
                                                    }
                                                }
                                                break;
                                            }
                                            case "C": {
                                                if (iterable.resourses.Cres > 0) {
                                                    int handeled = resourses.serve(separated[0],
                                                            resourse_requested_ammount, iterable);

                                                    print("C:" + handeled + ":"
                                                            + iterable.serverSocket.getInetAddress()
                                                            + ":"
                                                            + iterable.serverSocket.getLocalPort(), this.out);
                                                    if (handeled != resourse_requested_ammount) {
                                                        resourse_requested_ammount = resourse_requested_ammount
                                                                - handeled;
                                                        finished = false;
                                                    } else {
                                                        finished = true;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }

                                }
                            }
                        }

                    }
                }
                checkresourses();
                showAvailableNodes();
                in.close();
                out.close();
                socket.close();
            }
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the response to Client
     * 
     * @param msg Messedge to be sent to client
     * @param out Tool to sent messedge to client
     */
    public void print(String msg, PrintWriter out) {
        out.println(msg);
    }

    /**
     * Shuts down all, exept Passed Nodes
     * 
     * @param passed Node from which method was called (to not cause some conflicts)
     * @throws Hopefully Nothing
     */
    private void shutdown(Node passed) {
        try {
            for (Node iterable : nodes) {
                if (iterable.equals(passed)) {
                    continue;
                }
                if (iterable.serverSocket != null) {
                    iterable.serverSocket.close();
                }
            }
            Node.executorService.shutdown();
            nodes.clear();
        } catch (Exception e) {
        }

    }

    /**
     * Checks if all Nodes have some resourses left,
     * if not Node will be destroyed.
     */
    public void checkresourses() {
        try {

            for (Node iterable : nodes) {
                boolean shutdown = true;
                if (iterable.resourses.Ares != 0) {
                    shutdown = false;
                    continue;
                } else if (iterable.resourses.Bres != 0) {
                    shutdown = false;
                    continue;
                } else if (iterable.resourses.Cres != 0) {
                    shutdown = false;
                    continue;
                }
                if (shutdown) {
                    if (iterable.serverSocket != null) {
                        iterable.serverSocket.close();
                    }
                    iterable.resourses.executorService.shutdown();
                    iterable.socket.close();
                    iterable.alive = false;
                    nodes.remove(iterable);
                    continue;
                }
            }
        } catch (Exception e) {
        }

    }

    /**
     * Aloocates resourses to newly created Node.
     */
    public void allocateResourses() {
        resourses.service(arguments);
    }

    /**
     * @return int
     */
    public int getSERVER_PORT() {
        return SERVER_PORT;
    }

    /**
     * @param sERVER_PORT
     */
    public void setSERVER_PORT(int sERVER_PORT) {
        SERVER_PORT = sERVER_PORT;
    }

    /**
     * @return int
     */
    public int getIDENTIFICATOR() {
        return IDENTIFICATOR;
    }

    /**
     * @param iDENTIFICATOR
     */
    public void setIDENTIFICATOR(int iDENTIFICATOR) {
        IDENTIFICATOR = iDENTIFICATOR;
    }

    /**
     * Displays current state of programm.
     */
    public static void showAvailableNodes() {
        System.out.println("*************************************************");
        if (nodes.size() > 1) {
            System.out.println("Thre are -> " + nodes.size() + " Nodes!");
        } else {
            System.out.println("Thre is -> 1 Node!");
        }

        for (Node iterable : nodes) {
            System.out.print(
                    "Node -> " + iterable.getIDENTIFICATOR() + " Works at port -> " + iterable.getSERVER_PORT());
            iterable.resourses.seeAvailableResourses();
        }
    }

}
