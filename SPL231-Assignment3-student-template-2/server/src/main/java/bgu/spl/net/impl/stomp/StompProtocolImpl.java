/*
Based on the EchoProtocol and interface StompMassagingProtocol
 */

package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.newsfeed.NewsFeed;
// import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.ConnectionsImpl;

import java.util.Collection;
import java.util.LinkedList;
// import java.util.concurrent.ConcurrentHashMap;



public class StompProtocolImpl implements StompMessagingProtocol<String> {

    private String EndOfLine = FrameFormat.EndOfLine;
    private String EndOfField = FrameFormat.EndOfField;
    private boolean shouldTerminate = false;
    private NewsFeed NewsDataStructure; 
    private ConnectionsImpl<String> ConnectionsDataStructure;
    public ConnectionHandler<String> myCH = null;       //assigned later by the connectionHandler itself
    
    // public void start(int connectionId, Connections<String> connections){   //someone in the group said Hadi allowed to delete it
    //     return;
    // };

    public StompProtocolImpl (NewsFeed NDS, ConnectionsImpl<String> CDS){   //CONSTRUCTOR - inspired by RCIprotocol
        NewsDataStructure = NDS;
        ConnectionsDataStructure = CDS;
    }
    
    public boolean setCH(ConnectionHandler<String> CH){     //used by blocking/nonBlocking connectionHandlers to set protocols myCH
        myCH = CH;
        return true;
    }

    public String process(String  msg){           //SMP interface method
        //message PARSE() method                  //TODO - method to parse the massage 
        print4Debug("im in StompProtocolImpl::proccess: ");

        ConnectionHandler<String> CH = myCH;    //the ConnectionHandler of the client from whom the massage is recieved.
        FrameFormat recievedFrame =string2Frame(msg);
        FrameFormat responseFrame = null;
        switch (recievedFrame.stompCommand){
            case ("SUBSCRIBE"):
                responseFrame = subscribeCMD(recievedFrame, CH);
                print4Debug("SUBSCRIBE executed:"); 
                break;
            case ("UNSUBSCRIBE"):
                 responseFrame = unsubscribeCMD(recievedFrame,CH);
                 print4Debug("UNSUBSCRIBE executed:"); 
                 break;
            case ("CONNECT"):
                 responseFrame = connectCMD(recievedFrame,CH);
                 print4Debug("CONNECT executed:"); 
                 break;
            case ("DISCONNECT"):
                 responseFrame = disconnectCMD(recievedFrame,CH);
                 print4Debug("DISCONNECT executed:"); 
                 break;
            case ("SEND"):
                 responseFrame = sendCMD(recievedFrame,CH);
                 print4Debug("SEND executed:"); 
                 break;
            default:
            responseFrame=ErrorFrame(recievedFrame," short explanation","Error Massage Body!!!");  //return ERROR
            print4Debug("ERROR (no relevant stompHeader) executed:"); 
           
        }
        return frame2String(responseFrame);
        
    };
	

    private void print4Debug(String header) {   //prints information regarding DBs for debbuging
    //header:
    System.out.println("~~~~~~~~~~~~~~~~~~~~~\n"+header+"~~~~~~~~~~~~~~~~~~~~~\n");
    System.out.println("connectionsDB:\n    "+ConnectionsDataStructure.connectionsDB);
    System.out.println("subscriptionsDB:\n    "+ConnectionsDataStructure.subscriptionsDB);
    System.out.println("users:\n    "+ConnectionsDataStructure.users);
    System.out.println("reports:\n    "+NewsDataStructure+"\n");




    }

    public boolean shouldTerminate(){           // SMP interface method
        //shouldTerminate = true;
        return shouldTerminate;

    };

/*
 * ~~~~~~~~~String2Frame2String:~~~~~~~~~~~~~~~~~~~:
 */

    public FrameFormat string2Frame (String str){               //transforms msg back to frame
        String[] splitByFields = str.split(EndOfField);
                                // System.out.println("EndOfField delimiter is: "+EndOfField);//for DEBUG
        //commandHeader:
        String sCommand = splitByFields[0];
                                 System.out.println("for debug - sCommand is: "+sCommand);//for DEBUG
        LinkedList<LinkedList<String>> sHeaders = new LinkedList<>();
        //body:
        String sBody = splitByFields[2];
        if (splitByFields.length!=3)  System.out.println("amount of fields is "+ splitByFields.length+", should be 3 (error im StompProtocolImpl::string2frame");
        FrameFormat recievedFrame = new FrameFormat(sCommand, sHeaders, sBody);
        //stompHeaders:
        String[] headersSplitByEOL = splitByFields[1].split(EndOfLine);
        for (int i = 0; i < headersSplitByEOL.length; i++) {
          String headerName = headersSplitByEOL[i].split(":")[0];
          String headerValue = headersSplitByEOL[i].split(":")[1];
          recievedFrame.addHeaders(headerName, headerValue);
        //   LinkedList<String> Pair = new LinkedList<>();
        //   Pair.add(headerName);
        //   Pair.add(headerValue);
        //   sHeaders.add(Pair);
        }
    
        return recievedFrame;
    }

    public String frame2String (FrameFormat frame){
    if (frame==null){               //addad it after recieving null pointer exception 
        System.out.print("no frame recieved (frame==null)");
        return null;
    }
        String ans="";
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
    // String msgBody = recievedFrame.FrameBody;
    String recieptID = recievedFrame.headerName2Value("recieptID");
    String subscriptionID = recievedFrame.headerName2Value("id");
    stompUser activeUser = ConnectionsDataStructure.users.get(CH.getActiveUser());
    if (subscriptionID==null) return ErrorFrame(recievedFrame,"no relevant subscription ID","Error Massage Body!!!");
    //add user to topic (in subscriptions):
    boolean flag = ConnectionsDataStructure.addTopicToUser(activeUser, topic, subscriptionID);
    //add topic to CH (in connections):
    if (flag){
        ConnectionsDataStructure.addCHtoDB(CH);
        activeUser.userSubscriptions.put(topic, subscriptionID);
    }
    
    //response if error:
        //no subscriptionId sent - error as written upstairs.
    if (!flag) return ErrorFrame(recievedFrame," Error adding topic to user subscriptions","Error Massage Body!!!");

    //response if ok:
    if (recieptID!=null) return RecieptFrame(recieptID,CH);    
    return null;
}

private FrameFormat unsubscribeCMD (FrameFormat recievedFrame, ConnectionHandler<String> CH){
    String topic = recievedFrame.headerName2Value("destination:");  //gets headerName,returns headerValue (null if not found) 
    // String msgBody = recievedFrame.FrameBody;
    String recieptID = recievedFrame.headerName2Value("recieptID");
    String subscriptionID = recievedFrame.headerName2Value("id");
    if (subscriptionID==null) return ErrorFrame(recievedFrame,"no relevant subscription ID","Error Massage Body!!!");
    //remove user from topic (in subscriptions) and remove topic from user (in connections):
    ConnectionsDataStructure.removeTopic_User_Topic(CH.getActiveUser(), topic);
    //response if ok:
    if (recieptID!=null) return RecieptFrame(recieptID,CH);    
    //response if error:
    return null;
}


private FrameFormat connectCMD (FrameFormat recievedFrame, ConnectionHandler<String> CH){
    String versionID = recievedFrame.headerName2Value("accept-version");
    String login = recievedFrame.headerName2Value("login");
    String passcode = recievedFrame.headerName2Value("passcode");
    if(login==null||passcode==null) return ErrorFrame(recievedFrame, "null login or passcode", "login is:"+login+"\npasscode is:"+ passcode);
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
        // LinkedList<LinkedList<String>> stompHeaders=new LinkedList<>();
        ConnectedResponseFrame.addHeaders("version-id", ""+versionID);
        // LinkedList<String> pair = new LinkedList<>();
        // pair.add("version-id");
        // pair.addLast(""+versionID);
        // stompHeaders.add(pair);
        // ConnectedResponseFrame.stompHeaders=stompHeaders;
        return ConnectedResponseFrame;
    }
    //response if error:
    else return ErrorFrame(recievedFrame,"Login error - CONNECT not succesfull","something went wrong during CONNECT");
   // return null;
}



private FrameFormat disconnectCMD (FrameFormat recievedFrame,ConnectionHandler<String> CH){
    // String topic = recievedFrame.headerName2Value("destination:");  //gets headerName,returns headerValue (null if not found) 
    // String msgBody = recievedFrame.FrameBody;
    String recieptID = recievedFrame.headerName2Value("recieptID");
    //check if CH has activ subscriptions, if TRUE - unsubscribe
    stompUser activeUser = ConnectionsDataStructure.users.get(CH.getActiveUser());
    Collection<String> userTopics = activeUser.userSubscriptions.keySet();
    for (String topic : userTopics) {
        ConnectionsDataStructure.removeTopic_User_Topic(CH.getActiveUser(),topic);      //remove from connectionsDB, if has subscriptions - unsubscribe from all
    } 
    //remove active user from users,and from CH 
    ConnectionsDataStructure.users.remove(CH.getActiveUser());
    CH.setActiveUser(null);
    //response if ok:
    FrameFormat response = RecieptFrame(recieptID, CH);
    //response if error:

    return response;
}


private FrameFormat sendCMD (FrameFormat recievedFrame, ConnectionHandler<String> CH){  //returns MESSAGE frame
    String topic = recievedFrame.headerName2Value("destination:");  //gets headerName,returns headerValue (null if not found) 
    String msgBody = recievedFrame.FrameBody;
    String recieptID = recievedFrame.headerName2Value("recieptID");
    stompUser activeUser = ConnectionsDataStructure.users.get(CH.getActiveUser());
    activeUser.userReportsByGame.putIfAbsent(topic, new LinkedList<String>());
    activeUser.userReportsByGame.get(topic).add(msgBody);
    //check if subscribed to the desired topic
    if (!ConnectionsDataStructure.subscriptionsDB.get(topic).contains(activeUser)) return ErrorFrame(recievedFrame," error while SEND","couldnt fint relevant user in subscriptions");
    //add publishing to newsFeed
    NewsDataStructure.publish(topic, msgBody);

    //convert to MASSAGE frame
    FrameFormat massageFrame = new FrameFormat("MESSAGE", null, msgBody);
    
    //add headersList:
    massageFrame.addHeaders("subscription-id", "FILLSUBSCRIPTIONHERE");//will be added inside connectionsImpl::send() because subscription id of the specific client is needed.
    massageFrame.addHeaders("user", CH.getActiveUser());    //the user who published the massage:
    massageFrame.addHeaders("message-id", ""+ConnectionsImpl.massageID++);
    massageFrame.addHeaders("destination",topic);
    // LinkedList<LinkedList<String>> stompHeaders=new LinkedList<>();
    // LinkedList<String> pair = new LinkedList<>();
    // pair.add("subscription-id");      //will be added inside connectionsImpl::send() because subscription id of the specific client is needed.
    // pair.addLast("FILLSUBSCRIPTIONHERE");   //because subscription id is unique for each CH, it will be added inside send()
    // stompHeaders.add(pair);

    // pair = new LinkedList<>();
    // pair.add("user");   //the user who published the massage:
    // pair.addLast(CH.getActiveUser());
    // stompHeaders.add(pair);

    // pair = new LinkedList<>();
    // pair.add("message-id");
    // pair.addLast(""+ConnectionsImpl.massageID++);
    // stompHeaders.add(pair);

    // pair = new LinkedList<>();
    // pair.add("destination");
    // pair.addLast(topic);
    // stompHeaders.add(pair);

    // massageFrame.stompHeaders=stompHeaders;
    //send publishing to all subscribed clients (inside the method -  adding subscription id))
    String partialMessage = frame2String(massageFrame);
    ConnectionsDataStructure.send(topic, partialMessage);      //sends each of subscribed CH the massage, using CH::send()
    //response if ok:
    if (recieptID!=null) return RecieptFrame(recieptID,CH);    
    //response if error: 
        //"not subscribed error" - sent in the beginning

    return null;        //only respond if cliet wants reciept
}



private FrameFormat RecieptFrame(String recieptID, ConnectionHandler<String> CH) {
    FrameFormat recieptResponseFrame = new FrameFormat("RECIEPT", null, null);
    if (recieptID!=null){        //add reciept header if needed.
        recieptResponseFrame.addHeaders("receipt-id",""+recieptID);
        // LinkedList<LinkedList<String>> stompHeaders=new LinkedList<>();
        // LinkedList<String> pair = new LinkedList<>();
        // pair.add("receipt-id");
        // pair.addLast(""+recieptID);
        // stompHeaders.add(pair);
        // recieptResponseFrame.stompHeaders=stompHeaders;
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
    if (recieptID!=null) errorResponseFrame.addHeaders("receipt-id", ""+recieptID);
    // LinkedList<String> pair = new LinkedList<>();
    // pair.add("receipt-id");
    // pair.addLast(""+recieptID);
    // errorResponseFrame.stompHeaders.add(pair);

    //second header - message (short explanation)
    errorResponseFrame.addHeaders("message: ", ""+errorMsgHeader);
    // pair = new LinkedList<>();
    // pair.add("message: ");
    // pair.addLast(""+errorMsgHeader);
    // errorResponseFrame.stompHeaders.add(pair);

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