package ohbams.serverApplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private final static int SERVER_PORT = 9292;
    private ServerSocket serverSocket;
    private boolean isRunning = true;

    private static final HashMap<String, ServeClient> clientConnections = new HashMap<>();

    public Server() throws IOException {
        this(SERVER_PORT);
    }

    public Server(int serverPort) throws IOException{
        serverSocket = new ServerSocket(serverPort);
        System.out.println("Server started on "+ serverPort +"....");
    }

    public void run(){
        while (isRunning){
            try{
                Socket clientSocket = serverSocket.accept();
                createClientConnection(clientSocket);
            }catch (IOException e){
                System.err.println("Client socket not accepted!");
            }
        }
        close();
    }

    public void createClientConnection(Socket clientSocket){
        new Thread(()->{
            try{
                ServeClient serveClientThread = new ServeClient(clientSocket);
                serveClientThread.start();
                System.out.println("Client joined >> "+clientSocket);
            }catch(IOException exception){
                System.err.println("Database connection not established!");
            }
        }).start();
    }

    public static HashMap<String, ServeClient> getClientConnections() {
        return clientConnections;
    }

    public static void addClient(String userId, ServeClient clientConnection){
        clientConnections.put(userId, clientConnection);
    }

    public static void removeClient(String userId) {
        clientConnections.remove(userId);
    }

    public static boolean isClientOnline(String userId){
        return clientConnections.containsKey(userId);
    }

    public static ServeClient getClient(String userId){
        return clientConnections.get(userId);
    }

    public void close(){
        try{
            serverSocket.close();
            isRunning = false;
        }catch (IOException e){
            System.err.println("Server connection error on close!");
        }
    }

    public static void main(String []s) throws IOException{
        Server lutonHotelServer = new Server();
        lutonHotelServer.run();
    }
}
