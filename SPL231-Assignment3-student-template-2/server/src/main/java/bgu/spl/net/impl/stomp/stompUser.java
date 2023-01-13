package bgu.spl.net.impl.stomp;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class stompUser {
    public boolean isConnected=false;      //is user currently connected on a client
    public String userName;
    public String passcode;
    ConcurrentHashMap<String,LinkedList<String>> userReportsByGame;     //map<gameName,linkedlist<reportsAsString>>
    public ConcurrentHashMap<String,String> userSubscriptions; // map <topic,subscriptionID>
    public ConnectionHandler<String> currentCH = null;

public stompUser(String uName,String uPass){
    this.userName = uName;
    this.passcode = uPass;
    this.userReportsByGame = new ConcurrentHashMap<>();
    this.userSubscriptions = new ConcurrentHashMap<>();

}

public void newGame (String gameName){
    userReportsByGame.put(gameName, new LinkedList<>());
}

public void addReport (String gameName, String report){  //notice that reports are already as a long string
    userReportsByGame.get(gameName).addLast(report);
}

public void addSubscription (String topic,String subID){
    userSubscriptions.put(topic, subID);
}
public String toString(){
    String ans = "n:"+userName+",p:"+passcode+",subs:"+userSubscriptions+"\n";
    return ans;
}

}
