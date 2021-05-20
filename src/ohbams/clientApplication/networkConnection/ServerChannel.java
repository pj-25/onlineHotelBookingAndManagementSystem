package ohbams.clientApplication.networkConnection;

import ohbams.messageHandler.MessageConsumer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class ServerChannel {

    private Socket socket = null;
    private DataInputStream inStream = null;
    private DataOutputStream outStream = null;
    private MessageConsumer messageDecoder;

    public static final int SERVER_PORT = 9292;
    public static final String SERVER_IP = "3.135.64.114"; //3.135.64.114

    public ServerChannel(MessageConsumer messageDecoder) throws IOException{
        this(SERVER_IP, SERVER_PORT, messageDecoder);
    }

    public ServerChannel(String serverIP, int port, MessageConsumer messageDecoder) throws IOException{
        this.messageDecoder = messageDecoder;
        connect(serverIP, port);
    }

    public MessageConsumer getMessageDecoder() {
        return messageDecoder;
    }

    public void setMessageDecoder(MessageConsumer messageDecoder) {
        this.messageDecoder = messageDecoder;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public DataInputStream getInStream() {
        return inStream;
    }

    public void setInStream(DataInputStream inStream) {
        this.inStream = inStream;
    }

    public DataOutputStream getOutStream() {
        return outStream;
    }

    public void setOutStream(DataOutputStream outStream) {
        this.outStream = outStream;
    }

    public boolean isRunning() {
        return !socket.isClosed();
    }

    void connect(String serverIP, int port) throws IOException{
        if(socket != null){
            close();
        }
        socket = new Socket(InetAddress.getByName(serverIP), port);
        inStream = new DataInputStream(socket.getInputStream());
        outStream = new DataOutputStream(socket.getOutputStream());
    }

    public void run() {
        new Thread(()->{
            while(isRunning()){
                try {
                    //System.out.println("Waiting for msg......");
                    String msg = inStream.readUTF();
                    //System.out.println("Received message >> " +msg);
                    messageDecoder.consume(msg);
                }
                catch (IOException e){
                    close();
                    System.out.println("Connection Closed!!");
                }
            }
        }).start();
    }

    public void send(String msg) throws IOException{
        outStream.writeUTF(msg);
        //System.out.println("Sent msg >>" + msg);
    }

    public void close(){
        try{
            socket.close();
        }catch (IOException ignore){

        }
    }
}
