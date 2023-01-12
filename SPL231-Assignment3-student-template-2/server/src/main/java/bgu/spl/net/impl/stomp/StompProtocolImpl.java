/*
Based on the EchoProtocol and interface StompMassagingProtocol
 */

package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.newsfeed.NewsFeed;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.ConnectionsImpl;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;



public class StompProtocolImpl implements StompMessagingProtocol<String> {

    private String EndOfLine = FrameFormat.EndOfLine;
    private String EndOfField = FrameFormat.EndOfField;
    private boolean shouldTerminate = false;
    private NewsFeed NewsDataStructure; 
    private ConnectionsImpl<String> ConnectionsDataStructure;
    
    public ConnectionHandler myCH = null;
    
    // public void start(int connectionId, Connections<String> connections){   //someone in the group said Hadi allowed to delete it
    //     return;
    // };
    public StompProtocolImpl (NewsFeed NDS, ConnectionsImpl<String> CDS){   //CONSTRUCTOR - inspired by RCIprotocol
        NewsDataStructure = NDS;
        ConnectionsDataStructure = CDS;
    }
    
    public boolean setCH(ConnectionHandler<String> CH){
        myCH = CH;
        return true;
    }

    public String process(String  msg){           //SMP interface method
        //message PARSE() method                  //TODO - method to parse the massage 
       
       //something weird -when we get a massage it starts it with second char (miss the first one...)

                    System.out.println("the massage proccessing now: "+msg);//for debug

        ConnectionHandler<String> CH = myCH;    //the ConnectionHandler of the client from whom the massage is recieved.
        FrameFormat recievedFrame =string2Frame(msg);
        FrameFormat responseFrame = null;
        switch (recievedFrame.stompCommand){
            case ("SUBSCRIBE"):
            System.out.print("im in SUBSCRIBE case");    
                responseFrame = subscribeCMD(recievedFrame, CH);
                 break;
            case ("UNSUBSCRIBE"):
                 responseFrame = unsubscribeCMD(recievedFrame,CH);
                 break;
            case ("CONNECT"):
                 responseFrame = connectCMD(recievedFrame,CH);
                 break;
            case ("DISCONNECT"):
                 responseFrame = disconnectCMD(recievedFrame,CH);
                 break;
            case ("SEND"):
                 responseFrame = sendCMD(recievedFrame,CH);
                 break;
            default:
            System.out.print("im in Deafult case");    
            responseFrame=ErrorFrame(recievedFrame," short explanation","Error Massage Body!!!");  //return ERROR

            
        }
        return frame2String(responseFrame);
        
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
                                System.out.println("EndOfField delimiter is: "+EndOfField);//for DEBUG
        //commandHeader:
        String sCommand = splitByFields[0];
                                 System.out.println("sCommand is: "+sCommand);//for DEBUG
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
    if (frame==null){               //addad it after recieving null pointer exception 
        System.out.print("no frame recieved (frame==null)");
        return null;
    }
        String ans=null;
    //add commandHeader:
        ans=ans+frame.stompCommand + EndOfField;
    
    //add stompHeaders:
        for (LinkedList<String> header : frame.stompHeaders) {
            ans=ans+header.get(0) + ":"+header.get(1) + EndOfLine;
        }
        ans+=EndOfField;
    
    //add body:
        ans+= frame.FrameBody+EndOfField +" ";
    //add EOM:
        ans+=frame.EndOfMassage;
        return ans;
    }

/*
 * ~~~~~~~~~~~~Proccessing Commands (methods)~~~~~~~~~~~~~~~~
 */

 /*
  * Types of server responses: MESSAGE,CONNECTED,RECIEPT,ERROR/
  */

//SUBSCRIBE:

private FrameFormat subscribeCMD  (FrameFormat recievedFrame, ConnectionHandler<String> CH){
    String topic = recievedFrame.headerName2Value("destination:");  //gets headerName,returns headerValue (null if not found) 
    String msgBody = recievedFrame.FrameBody;
    String recieptID = recievedFrame.headerName2Value("recieptID");
    String subscriptionID = recievedFrame.headerName2Value("id");
    if (subscriptionID==null) return ErrorFrame(recievedFrame," short explanation","Error Massage Body!!!");
    //add CH to topic (in subscriptions):
    boolean flag = ConnectionsDataStructure.addTopicToCH(CH, topic, subscriptionID);
    //add topic to CH (in connections):
    if (flag){
        ConnectionsDataStructure.addCHtoDB(CH);
        ConnectionsDataStructure.connectionsDB.get(CH).put(topic, subscriptionID);
    }
    
    //response if error:
        //no subscriptionId sent - error as written upstairs.
    if (!flag) return ErrorFrame(recievedFrame," short explanation","Error Massage Body!!!");

    //response if ok:
    if (recieptID!=null) return RecieptFrame(recieptID,CH);    
    return null;
}

private FrameFormat unsubscribeCMD (FrameFormat recievedFrame, ConnectionHandler<String> CH){
    String topic = recievedFrame.headerName2Value("destination:");  //gets headerName,returns headerValue (null if not found) 
    // String msgBody = recievedFrame.FrameBody;
    String recieptID = recievedFrame.headerName2Value("recieptID");
    String subscriptionID = recievedFrame.headerName2Value("id");
    if (subscriptionID==null) return ErrorFrame(recievedFrame," short explanation","Error Massage Body!!!");
    //remove CH from topic (in subscriptions) and remove topic from CH (in connections):
    ConnectionsDataStructure.removeTopic_CH_Topic(CH, topic);
    //response if ok:
    if (recieptID!=null) return RecieptFrame(recieptID,CH);    
    //response if error:

    return null;
}


private FrameFormat connectCMD (FrameFormat recievedFrame, ConnectionHandler<String> CH){
    String topic = recievedFrame.headerName2Value("destination:");  //gets headerName,returns headerValue (null if not found) 
    String msgBody = recievedFrame.FrameBody;
    String versionID = recievedFrame.headerName2Value("version-id");
    String login = recievedFrame.headerName2Value("login");
    String passcode = recievedFrame.headerName2Value("passcode");
    //check login?
    boolean isLoginOk = ConnectionsDataStructure.isLoginOk(login, passcode);
    if (isLoginOk){
        //change CH::activeUser to logged user:
        CH.setActiveUser(login);
        CH.setConnectionId(ConnectionsImpl.connectionID++);
        //add CH to connections
        ConnectionsDataStructure.addCHtoDB(CH);
    //response if ok:
        FrameFormat ConnectedResponseFrame = new FrameFormat("CONNECTED",null,null);
        LinkedList<LinkedList<String>> stompHeaders=new LinkedList<>();
        LinkedList<String> pair = new LinkedList<>();
        pair.add("version-id");
        pair.addLast(""+versionID);
        stompHeaders.add(pair);
        ConnectedResponseFrame.stompHeaders=stompHeaders;
        return ConnectedResponseFrame;
    }
    //response if error:
    else return ErrorFrame(recievedFrame," short explanation","Error Massage Body!!!");
   // return null;
}

private FrameFormat disconnectCMD (FrameFormat recievedFrame,ConnectionHandler<String> CH){
    // String topic = recievedFrame.headerName2Value("destination:");  //gets headerName,returns headerValue (null if not found) 
    // String msgBody = recievedFrame.FrameBody;
    String recieptID = recievedFrame.headerName2Value("recieptID");
    //check if CH has activ subscriptions, if TRUE - unsubscribe
    ConnectionsDataStructure.removeCH(CH);      //remove from connectionsDB, if has subscriptions - unsubscribe from all
    //response if ok:
    FrameFormat response = RecieptFrame(recieptID, CH);
    //response if error:

    return response;
}


private FrameFormat sendCMD (FrameFormat recievedFrame, ConnectionHandler<String> CH){  //returns MESSAGE frame
    String topic = recievedFrame.headerName2Value("destination:");  //gets headerName,returns headerValue (null if not found) 
    String msgBody = recievedFrame.FrameBody;
    String recieptID = recievedFrame.headerName2Value("recieptID");
    //check if subscribed to the desired topic
    if (!ConnectionsDataStructure.subscriptionsDB.get(topic).contains(CH)) return ErrorFrame(recievedFrame," short explanation","Error Massage Body!!!");
    //add publishing to newsFeed
    NewsDataStructure.publish(topic, msgBody);

    //convert to MASSAGE frame
    FrameFormat massageFrame = new FrameFormat("MESSAGE", null, msgBody);
    
    //add headersList:
    LinkedList<LinkedList<String>> stompHeaders=new LinkedList<>();
    LinkedList<String> pair = new LinkedList<>();
    pair.add("subscription-id");      //will be added inside connectionsImpl::send() because subscription id of the specific client is needed.
    pair.addLast("FILLSUBSCRIPTIONHERE");   //because subscription id is unique for each CH, it will be added inside send()
    stompHeaders.add(pair);

    pair = new LinkedList<>();
    pair.add("message-id");
    pair.addLast(""+ConnectionsImpl.massageID++);
    stompHeaders.add(pair);

    pair = new LinkedList<>();
    pair.add("destination");
    pair.addLast(topic);
    stompHeaders.add(pair);

    massageFrame.stompHeaders=stompHeaders;
    //send publishing to all subscribed clients (inside the method -  adding subscription id))
    String partialMessage = frame2String(massageFrame);
    ConnectionsDataStructure.send(topic, partialMessage);      //sends each of subscribed CH the massage, using CH::send()
    //response if ok:
    if (recieptID!=null) return RecieptFrame(recieptID,CH);    
    //response if error: 
        //"not subscribed error" - sent in the beginning

    return null;        //only respond if cliet wants reciept
}



private FrameFormat RecieptFrame(String recieptID, ConnectionHandler CH) {
    FrameFormat recieptResponseFrame = new FrameFormat("RECIEPT", null, null);
    if (recieptID!=null){        //add reciept header if needed.
        LinkedList<LinkedList<String>> stompHeaders=new LinkedList<>();
        LinkedList<String> pair = new LinkedList<>();
        pair.add("receipt-id");
        pair.addLast(""+recieptID);
        stompHeaders.add(pair);
        recieptResponseFrame.stompHeaders=stompHeaders;
    }
    return null;
}

private FrameFormat ErrorFrame(FrameFormat recievedFrame,String errorMsgHeader,String errorMsgBody) {
    String msgBody = recievedFrame.FrameBody;
    String recieptID = recievedFrame.headerName2Value("recieptID");
    String topic = recievedFrame.headerName2Value("destination");
    FrameFormat errorResponseFrame = new FrameFormat("Error",null, errorMsgBody);
    errorResponseFrame.stompHeaders = new LinkedList<>();
    //first header - reciept-id
    LinkedList<String> pair = new LinkedList<>();
    pair.add("receipt-id");
    pair.addLast(""+recieptID);
    errorResponseFrame.stompHeaders.add(pair);
    //second header - message (short explanation)
    pair = new LinkedList<>();
    pair.add("message: ");
    pair.addLast(""+errorMsgHeader);
    errorResponseFrame.stompHeaders.add(pair);

    //msgBody:
    String responseFrameMsgBody = 
    "The message: \n"+
    "----\nMESSAGE\n"+
    "destined :"+topic + "\n" +
    "receipt : "+recieptID + "\n"+
    msgBody+
    "\n-----"+
    errorMsgBody;
    errorResponseFrame.FrameBody=responseFrameMsgBody;

    return errorResponseFrame;
}












 }