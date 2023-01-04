/*
Based on the EchoProtocol and interface StompMassagingProtocol
 */

package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.newsfeed.NewsFeed;
import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.ConnectionsImpl;

import java.io.Serializable;
import java.time.LocalDateTime;

public class StompProtocol implements StompMessagingProtocol<String> {

    private boolean shouldTerminate = false;
    private NewsFeed NewsDataStructure; 
    private ConnectionsImpl ConnectionsDataStructure;  
    
    // public void start(int connectionId, Connections<String> connections){   //someone in the group said Hadi allowed to delete it
    //     return;
    // };
    public StompProtocol (NewsFeed NDS, ConnectionsImpl CDS){   //CONSTRUCTOR - inspired by RCIprotocol
        NewsDataStructure = NDS;
        ConnectionsDataStructure = CDS;
    }
    
    public void process(Serializable  message){           //SMP interface method
        //message PARSE() method                  //TODO - method to parse the massage 
        return ((Command) message).execute(NewsDataStructure,ConnectionsDataStructure);        //TODO - change to fit XxxxxCommand.execute (as we designed)
    };
	

    public boolean shouldTerminate(){           // SMP interface method
        shouldTerminate = true;
        return shouldTerminate;

    };


    /*
 * EchoProtocol:
 * 
 * 
 @Override
 public String process(String msg) {
     shouldTerminate = "bye".equals(msg);
     System.out.println("[" + LocalDateTime.now() + "]: " + msg);
     return createEcho(msg);
    }
    
    private String createEcho(String message) {
        String echoPart = message.substring(Math.max(message.length() - 2, 0), message.length());
        return message + " .. " + echoPart + " .. " + echoPart + " ..";
    }
    
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
    */
}
