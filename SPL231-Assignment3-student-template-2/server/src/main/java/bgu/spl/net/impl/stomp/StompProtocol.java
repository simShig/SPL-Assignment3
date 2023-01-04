/*
Based on the EchoProtocol and interface StompMassagingProtocol
 */

package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import java.time.LocalDateTime;

public class StompProtocol implements StompMessagingProtocol<String> {

    private boolean shouldTerminate = false;
    
    // public void start(int connectionId, Connections<String> connections){   //someone in the group said Hadi allowed to delete it
    //     return;
    // };
    
    public void process(String message){
        return;
    };
	

    public boolean shouldTerminate(){
        shouldTerminate = true;
        return shouldTerminate;

    };


    /*
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
