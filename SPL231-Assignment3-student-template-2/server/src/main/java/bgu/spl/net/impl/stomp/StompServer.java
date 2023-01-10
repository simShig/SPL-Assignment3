package bgu.spl.net.impl.stomp;

import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.impl.rci.RemoteCommandInvocationProtocol;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Server;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.newsfeed.NewsFeed;

public class StompServer {

    public static void main(String[] args) {
        // TODO: implement this
        NewsFeed feed = new NewsFeed(); //one shared object
        ConnectionsImpl connections = new ConnectionsImpl();

        // you can use any server... 
        Server.threadPerClient(
                7777, //port
                () ->   new StompProtocolImpl(feed,connections), //protocol factory
                StompEncoderDecoder::new //message encoder decoder factory
        ).serve();

        // Server.reactor(
        //         Runtime.getRuntime().availableProcessors(),
        //         7777, //port
        //         () ->  new StompProtocolImpl(feed,connections), //protocol factory
        //         StompEncoderDecoder::new //message encoder decoder factory
        // ).serve();
    }
}
