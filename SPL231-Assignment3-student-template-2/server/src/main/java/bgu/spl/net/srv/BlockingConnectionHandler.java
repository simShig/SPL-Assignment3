package bgu.spl.net.srv;


import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.stomp.StompEncoderDecoder;
import bgu.spl.net.impl.stomp.stompUser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final StompMessagingProtocol<T> protocol;
    private final StompEncoderDecoder encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    public int connectionId;
    static int connectionIdCounter=1;
    public String activeUserName = null;


    public BlockingConnectionHandler(Socket sock, StompEncoderDecoder reader, StompMessagingProtocol<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.connectionId = connectionIdCounter++;
    }

    @Override
    public void run() {
        protocol.setCH(this);
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            // System.out.println("inside CH - finished sock.getInput"+ in.read());
            out = new BufferedOutputStream(sock.getOutputStream());
            // System.out.println("inside CH - finished sock.getoutput"+ out);
            // System.out.println("should terminate: "+ protocol.shouldTerminate());
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                    // System.out.println("Read:"+ read);
                String nextMessage = encdec.decodeNextByte((byte) read);
                // System.out.println("inside CH - WHILE loop, next massage:"+ nextMessage);
                if (nextMessage != null) {
                    // System.out.println("inside CH - if next massage!=null");
                    
                    String response = protocol.process(nextMessage);
                    if (response != null) {
                        out.write(encdec.encode(response));
                        out.flush();
                    }
                }
            }

        } catch (IOException ex) {
            //System.out.println("inside catch IOException");
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        //IMPLEMENT IF NEEDED
    }

    @Override
    public void setActiveUser(String username) {
        activeUserName = username;
        
    }
}
