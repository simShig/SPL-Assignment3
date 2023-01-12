package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.net.impl.stomp.stompUser;

public class ConnectionsImpl<T> implements Connections<T>{

    //fields:
    
    public ConcurrentHashMap<ConnectionHandler<T>, ConcurrentHashMap<String,String>> connectionsDB = new ConcurrentHashMap<>();//map<CH,map<Topic,subscriptionID> 

    public ConcurrentHashMap<String, ConcurrentHashMap<ConnectionHandler<T>,String>> subscriptionsDB = new ConcurrentHashMap<>();//map<topic,map<CH,subscriptionID>
    
    private ConcurrentHashMap<String,stompUser> users = new ConcurrentHashMap<>();  //map<userName,userOBJ>
    public static int connectionID = 1;
    static int clientID;        //global (static)
    static int recieptID;        //global (static)
    public static int massageID;           //global (static)


    //TODO - Implement this class entirely - only implemented skelaton so i can referance Type ConnectionsImpl as a Type.

    //methods:
    public boolean send(int connectionId, T msg){  //send to certain CH. needs to apply CH::send()
    Collection<ConnectionHandler<T>> registeredCHs = connectionsDB.keySet(); 
    for (ConnectionHandler<T> CH : registeredCHs) {
        if(CH.getConnectionID()==connectionId){
            CH.send(msg);
            return true;
        }

    } return false;
}

    public void send(String channel, T msgT){   //send ALL SUBSCRIBED! needs to apply CH::send()
        //note that the msg here is already translated frame2string!!!
        //also, because subscriptionID needed, i split and merge here:
        String msg = (String)msgT;


        ///continue FROM HERREEEEE~!~!@#!#$@!#$#R@$ 
        //check every proccess(CMD) method
        //in the end - implement setters and getters in CH (interface&blocking,non-blocking)





        String[] splited = msg.split("FILLSUBSCRIPTIONHERE", 1);
        Collection<ConnectionHandler<T>> registeredCHs = subscriptionsDB.get(channel).keySet();
        for (ConnectionHandler<T> CH : registeredCHs) {
            String subId = subscriptionsDB.get(channel).get(CH);    //get CHs subscription id
            msg = splited[0]+"subscription:"+subId+splited[1];      //chain it all to a new String
            CH.send((T)msg);
        }
    };

    public void disconnect(int connectionId){

    };

    public boolean addCHtoDB(ConnectionHandler<String> CH){     //add ConnectionHandler to connectionsDB,part of CONNECT
    
       connectionsDB.computeIfAbsent(CH, k -> new ConcurrentHashMap<>());    //
        return true;
    };

    public boolean removeTopic_CH_Topic (ConnectionHandler<String> CH, String topic){   //removes topic from CH and CH from TOPIC
        connectionsDB.get(CH).remove(topic);
        subscriptionsDB.get(topic).remove(CH);
        return true;
    }

    public boolean addTopicToCH(ConnectionHandler<T> CH, String topic,String subscriptionID){     //add Topic to subscriptionDB (and to connectionsDB[CH]),part of SUBSCRIBE
        //add topic to connectionsDB:
        try{
            connectionsDB.get(CH).put(topic, subscriptionID);
        }catch(NullPointerException e){return false;}   //in case one of the arguments in addTopicToDB was null
    
        //add CH and subID to subscriptionsDB
       
        ConcurrentHashMap<ConnectionHandler<T>,String> innerMap = subscriptionsDB.computeIfAbsent(topic, k -> new ConcurrentHashMap<>());
        innerMap.put(CH, subscriptionID);
        
        /*
         * ## inspired by this part of NewsFeed:  ##
         * 
         public void publish(String channel, String news) {
             ConcurrentLinkedQueue<String> queue = channels.computeIfAbsent(channel, k -> new ConcurrentLinkedQueue<>());
             queue.add(news);
             
             */
        return true;
    };

public boolean removeCH(ConnectionHandler<String> CH){
    
    if (!connectionsDB.get(CH).keySet().isEmpty()){
        for (String key : connectionsDB.get(CH).keySet()) {
            removeTopic_CH_Topic(CH, key);
        }
    }
    return true;
}

public boolean isLoginOk(String login, String passcode) {
    String realPassword = users.get(login).passcode;       //realPassword is the one already in the database
    if (realPassword==null){        //if its a new client
        users.put(login, new stompUser(login,passcode ));
        return true;
    }
    if (realPassword!=passcode) return false;   //wrong password
    return true;
}

}