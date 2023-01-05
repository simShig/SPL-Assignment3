package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionsImpl implements Connections<String>{

    //fields:
    
    private ConcurrentHashMap<ConnectionHandler<String>, ConcurrentHashMap<String,Integer>> connectionsDB = new ConcurrentHashMap<>();//map<CH,map<Topic,subscriptionID> 

    private ConcurrentHashMap<String, ConcurrentHashMap<ConnectionHandler<String>,Integer>> subscriptionsDB = new ConcurrentHashMap<>();//map<topic,map<CH,subscriptionID>
    
    //TODO - Implement this class entirely - only implemented skelaton so i can referance Type ConnectionsImpl as a Type.

    //methods:
    public boolean send(int connectionId, String msg){  //needs to apply CH::send()
        
            return false;
    };

    public void send(String channel, String msg){   //needs to apply CH::send()

    };

    public void disconnect(int connectionId){

    };

    public boolean addCHtoDB(ConnectionHandler<String> CH){     //add ConnectionHandler to connectionsDB,part of CONNECT
    
       connectionsDB.computeIfAbsent(CH, k -> new ConcurrentHashMap<>());    //
        return true;
    };


    public boolean addTopicToCH(ConnectionHandler<String> CH, String topic,int subscriptionID){     //add Topic to subscriptionDB (and to connectionsDB[CH]),part of SUBSCRIBE
        //add topic to connectionsDB:
        try{
            connectionsDB.get(CH).put(topic, subscriptionID);
        }catch(NullPointerException e){return false;}   //in case one of the arguments in addTopicToDB was null
    
        //add CH and subID to subscriptionsDB
       
        ConcurrentHashMap<ConnectionHandler<String>,Integer> innerMap = subscriptionsDB.computeIfAbsent(topic, k -> new ConcurrentHashMap<>());
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



}
