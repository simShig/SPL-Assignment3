package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.net.impl.stomp.stompUser;

public class ConnectionsImpl<T> implements Connections<T>{

    //fields:
    
    public ConcurrentHashMap<String,ConnectionHandler<T>> connectionsDB = new ConcurrentHashMap<>();//map<connecitonID,CH>

    public ConcurrentHashMap<String,LinkedList<stompUser>> subscriptionsDB = new ConcurrentHashMap<>();//map<topic,LinkedList<stompUsers>>
    
    public ConcurrentHashMap<String,stompUser> users = new ConcurrentHashMap<>();  //map<userName,userOBJ>
    public static int connectionID = 1;
    public static int clientID;        //global (static)
    public static int recieptID;        //global (static)
    public static int massageID;           //global (static)
    public static int subscriptionId;           //global (static)


    //TODO - Implement this class entirely - only implemented skelaton so i can referance Type ConnectionsImpl as a Type.

    //methods:
    public boolean send(int connectionId, T msg){  //send to certain CH. needs to apply CH::send()
        ConnectionHandler<T> CH = connectionsDB.get(""+connectionId);
        if (CH==null) return false;
        CH.send(msg);
        return true;
    }


    public void send(String channel, T msgT){   //send ALL SUBSCRIBED! needs to apply CH::send()
        //note that the msg here is already translated frame2string!!!
        //also, because subscriptionID needed, i split and merge here:
        String msg = (String)msgT;      
        String[] splited = msg.split("FILLSUBSCRIPTIONHERE", 1);
        LinkedList<stompUser> subscribedUsers = subscriptionsDB.get(channel);
        for (stompUser user : subscribedUsers) {
            String subId = user.userSubscriptions.get(channel); //get users subId to topic
            msg = splited[0]+subId+splited[1];      //chain it all to a new String
            user.currentCH.send(msg);
        }
    };
    
    public void disconnect(int connectionId){
        connectionsDB.remove(connectionId);
    };

    public boolean addCHtoDB(ConnectionHandler<T> CH){     //add ConnectionHandler to connectionsDB,part of CONNECT
        connectionsDB.put(new String(""+CH.getConnectionID()), CH);
        return true;
    };


    public boolean removeTopic_User_Topic (String userName, String topic){   //removes topic from CH and CH from TOPIC
        stompUser user = users.get(userName);
        subscriptionsDB.get(topic).remove(user);
        user.userSubscriptions.remove(topic);
        return true;
    }



    //continue from here!!!!



    public boolean addTopicToUser(stompUser user, String topic ,String subscriptionID){     //part of SUBSCRIBE

        user.addSubscription(topic, subscriptionID);
        //add user and subID to subscriptionsDB
        subscriptionsDB.putIfAbsent(topic, new LinkedList<stompUser>());//if its a new topic
        subscriptionsDB.get(topic).add(user);
        return true;
    };

// public boolean removeCH(ConnectionHandler<String> CH){
    
//     if (!connectionsDB.get(CH).keySet().isEmpty()){
//         for (String key : connectionsDB.get(CH).keySet()) {
//             removeTopic_CH_Topic(CH, key);
//         }
//     }
//     return true;
// }

public boolean isLoginOk(String login, String passcode) {
    try{

        stompUser user =  users.get(login);
        String realPassword = user.passcode;       //realPassword is the one already in the database
        if (realPassword!=passcode) return false;   //wrong password
        return true;
    }catch (NullPointerException e){
        users.put(login, new stompUser(login, passcode));       //if couldnt find this one
    }
    return true;
}

}