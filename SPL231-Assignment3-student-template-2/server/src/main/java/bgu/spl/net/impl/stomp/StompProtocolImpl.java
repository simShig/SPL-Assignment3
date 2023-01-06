/*
Based on the EchoProtocol and interface StompMassagingProtocol
 */

package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.newsfeed.NewsFeed;
import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.ConnectionsImpl;
import java.util.LinkedList;

import java.io.Serializable;
import java.time.LocalDateTime;

public class StompProtocolImpl implements StompMessagingProtocol<String> {

    private String EndOfLine = FrameFormat.EndOfLine;
    private String EndOfField = FrameFormat.EndOfField;
    private boolean shouldTerminate = false;
    private NewsFeed NewsDataStructure; 
    private ConnectionsImpl ConnectionsDataStructure;  
    
    // public void start(int connectionId, Connections<String> connections){   //someone in the group said Hadi allowed to delete it
    //     return;
    // };
    public StompProtocolImpl (NewsFeed NDS, ConnectionsImpl CDS){   //CONSTRUCTOR - inspired by RCIprotocol
        NewsDataStructure = NDS;
        ConnectionsDataStructure = CDS;
    }
    
    public String process(String  msg){           //SMP interface method
        //message PARSE() method                  //TODO - method to parse the massage 
        FrameFormat recievedFrame =string2Frame(msg);
        

        return "something";
        //return ((Command) message).execute(NewsDataStructure,ConnectionsDataStructure);        //TODO - change to fit XxxxxCommand.execute (as we designed)
    };
	

    public boolean shouldTerminate(){           // SMP interface method
        shouldTerminate = true;
        return shouldTerminate;

    };

/*
 * ~~~~~~~~~String2Frame2String:~~~~~~~~~~~~~~~~~~~:
 */


    public FrameFormat string2Frame (String str){               //transforms msg back to frame
        String[] splitByFields = str.split(EndOfField);
        //commandHeader:
        String sCommand = splitByFields[0];
        //stompHeaders:
        LinkedList<LinkedList<String>> sHeaders = new LinkedList<>();
        String[] headersSplitByEOL = splitByFields[1].split(EndOfLine);
        for (int i = 1; i < headersSplitByEOL.length-1; i++) {
          String headerName = headersSplitByEOL[i].split(":")[0];
          String headerValue = headersSplitByEOL[i].split(":")[1];
          LinkedList<String> Pair = new LinkedList<>();
          Pair.add(headerName);
          Pair.add(headerValue);
          sHeaders.add(Pair);
        }
        //body:
        String sBody = splitByFields[2];
    
        FrameFormat recievedFrame = new FrameFormat(sCommand, sHeaders, sBody);
        return recievedFrame;
    }

    public String frame2String (FrameFormat frame){
        String ans=null;
    //add commandHeader:
        ans=ans+frame.stompCommand + EndOfField;
    
    //add stompHeaders:
        for (LinkedList<String> header : frame.stompHeaders) {
            ans=ans+header.get(0) + ":"+header.get(1) + EndOfLine;
        }
        ans+=EndOfField;
    
    //add body:
        ans+= frame.FrameBody+EndOfField;
    //add EOM:
        ans+=frame.EndOfMassage;
        return ans;
    }

/*
 * ~~~~~~~~~~~~Proccessing Commands (methods)~~~~~~~~~~~~~~~~
 */


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
