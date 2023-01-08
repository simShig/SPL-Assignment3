package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionsImpl implements Connections<String>{

    //fields:
    
    public ConcurrentHashMap<ConnectionHandler<String>, ConcurrentHashMap<String,String>> connectionsDB = new ConcurrentHashMap<>();//map<CH,map<Topic,subscriptionID> 

    public ConcurrentHashMap<String, ConcurrentHashMap<ConnectionHandler<String>,String>> subscriptionsDB = new ConcurrentHashMap<>();//map<topic,map<CH,subscriptionID>
    
    private ConcurrentHashMap<String,String> usersNpasswords = new ConcurrentHashMap<>();

    static int clientID;        //global (static)
    static int recieptID;        //global (static)


    //TODO - Implement this class entirely - only implemented skelaton so i can referance Type ConnectionsImpl as a Type.

    //methods:
    public boolean send(int connectionId, String msg){  //send to certain CH. needs to apply CH::send()
        
            return false;
    };

    public void send(String channel, String msg){   //send ALL SUBSCRIBED! needs to apply CH::send()
        Collection<ConnectionHandler<String>> registeredCHs = subscriptionsDB.get(channel).keySet();
        for (ConnectionHandler<String> CH : registeredCHs) {
            CH.send(msg);
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

    public boolean addTopicToCH(ConnectionHandler<String> CH, String topic,String subscriptionID){     //add Topic to subscriptionDB (and to connectionsDB[CH]),part of SUBSCRIBE
        //add topic to connectionsDB:
        try{
            connectionsDB.get(CH).put(topic, subscriptionID);
        }catch(NullPointerException e){return false;}   //in case one of the arguments in addTopicToDB was null
    
        //add CH and subID to subscriptionsDB
       
        ConcurrentHashMap<ConnectionHandler<String>,String> innerMap = subscriptionsDB.computeIfAbsent(topic, k -> new ConcurrentHashMap<>());
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

}