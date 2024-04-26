import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Consumer;

public class Server{
    int count = 1;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<Serializable> callback;
    private int portNumber;


    // Consumer<Serializable> call allows the server to communicate with the GUI
    Server(Consumer<Serializable> call, int portNumber){
        callback = call;
        server = new TheServer();
        server.start();
        this.portNumber = portNumber;
    }


    public class TheServer extends Thread{
        public void run() {
            try(ServerSocket mysocket = new ServerSocket(portNumber);){
               callback.accept("Server is waiting for a client!");
                while(true) {
                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    callback.accept("client has connected to server: " + "client #" + count);
                    clients.add(c);
                    c.start();
                    count++;
                }
            }
            catch(Exception e) {
                callback.accept("Server socket did not launch");
            }
        }
    }

    class ClientThread extends Thread{
        Socket connection;
        int count;
        ClientHandler game;

        ClientThread(Socket s, int count){
            this.connection = s;
            this.count = count;
            game = new ClientHandler(connection,callback);
        }

        @Override
        public void run(){
            while(!this.connection.isClosed()) {
                try {
                    callback.accept("client: " + count + " is playing a game");
                    game.run(); // Is this the right place for this?
                }
                catch(Exception e) {
                    callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
                    clients.remove(this);
                    try {
                        connection.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            }
            callback.accept("Socket connection is closed for client " + count);
            clients.remove(this);
        }

    }
}


