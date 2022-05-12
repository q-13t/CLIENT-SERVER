
/**
  *  @author Volodymyr Davybida (s22721)
*/

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class NetworkNode {
    /* local variables */
    private static int identificator = 0;
    private static int tcpport = 0;
    private static String adress;
    private static int port = 0;
    private static ArrayList<String> arguments = new ArrayList<>();

    /**
     * This Function reads {@code arguments} prowided from Main function
     * and proceeds to establish connections.
     */
    public static void getInfo(String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-ident": {
                        identificator = Integer.valueOf(args[++i]);
                        break;
                    }
                    case "-tcpport": {
                        tcpport = Integer.valueOf(args[++i]);
                        break;
                    }
                    case "-gateway": {
                        String[] gateway = args[++i].split(":");
                        adress = gateway[0];
                        port = Integer.valueOf(gateway[1]);
                        break;
                    }

                    default: {
                        arguments.add(args[i]);
                        break;
                    }
                }
            }
            if (adress != null && port != 0) {
                Socket socket = new Socket(adress, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.write("NODE " + identificator + " " + tcpport + " " + arguments);
                out.close();
                socket.close();
            } else {
                new Node(identificator, tcpport, arguments);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        getInfo(args);
    }

}
