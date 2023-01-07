/*
Based on the EchoProtocol and interface StompMassagingProtocol
 */

package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.newsfeed.NewsFeed;
import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;
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
        System.out.println("the massage proccessing now: "+msg);
        BlockingConnectionHandler<String> CH = null;    //the ConnectionHandler of the client from whom the massage is recieved.
        FrameFormat recievedFrame =string2Frame(msg);
        FrameFormat responseFrame = null;
        switch (recievedFrame.stompCommand){
            case ("SUBSCRIBE"):
            System.out.print("im in SUBSCRIBE case");    
            //  responseFrame = subscribeCMD(null, null, 0);
                //  break;
            case ("UNSUBSCRIBE"):
                 responseFrame = unsubscribeCMD(null,null,0);
                 break;
            case ("CONNECT"):
                 responseFrame = connectCMD(null);
                 break;
            case ("DISCONNECT"):
                 responseFrame = disconnectCMD(null);
                 break;
            case ("SEND"):
                 responseFrame = sendCMD();
                 break;
            default:
            System.out.print("im in Deafult case");    
            //  responseFrame=new FrameFormat("ERROR", null, "your title is wrong")  ;//return ERROR

            
        }
        return frame2String(responseFrame);
        //return ((Command) message).execute(NewsDataStructure,ConnectionsDataStructure);        //TODO - change to fit XxxxxCommand.execute (as we designed)
    };
	

    public boolean shouldTerminate(){           // SMP interface method
        //shouldTerminate = true;
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
        if (splitByFields.length!=3)  System.out.println("amount of fields is "+ splitByFields.length+", should be 3 (error im StompProtocolImpl::string2frame");
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

//SUBSCRIBE:

private FrameFormat subscribeCMD (String topic, ConnectionHandler<String> CH,int subscriptionID ){
    //add topic to CH (in connections):
    ConnectionsDataStructure.addCHtoDB(CH);
    //add CH to topic (in subscriptions):
    ConnectionsDataStructure.addTopicToCH(CH, topic, subscriptionID);
    //response if ok:

    //response if error:

    return new FrameFormat(EndOfLine, null, EndOfField);
}

private FrameFormat unsubscribeCMD (String topic, ConnectionHandler<String> CH,int subscriptionID ){
    //remove CH from topic (in subscriptions) and remove topic from CH (in connections):
    ConnectionsDataStructure.removeTopic_CH_Topic(CH, topic, subscriptionID);

        //response if ok:

    //response if error:

    return new FrameFormat(EndOfLine, null, EndOfField);
}


private FrameFormat connectCMD (ConnectionHandler<String> CH){

    //check login?

    //add CH to connections
    ConnectionsDataStructure.addCHtoDB(CH);
    //response if ok:

    //response if error:

    return new FrameFormat(EndOfLine, null, EndOfField);
}

private FrameFormat disconnectCMD (ConnectionHandler<String> CH){

    //check if CH has activ subscriptions, if TRUE - unsubscribe
    

    //remove CH from connections
    ConnectionsDataStructure.disconnect(0); //TODO - add connectionID field to CH.
    //response if ok:

    //response if error:

    return new FrameFormat(EndOfLine, null, EndOfField);
}


private FrameFormat sendCMD (){
    //check if subscribed to the desired topic

    //add publishing to newsFeed

    //send publishing to all subscribed clients

    //response if ok:

    //response if error:

    return new FrameFormat(EndOfLine, null, EndOfField);
}












 }