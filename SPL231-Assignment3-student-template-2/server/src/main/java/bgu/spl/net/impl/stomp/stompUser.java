package bgu.spl.net.impl.stomp;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class stompUser {
    public boolean isConnected=false;      //is user currently connected on a client
    String userName;
    String passcode;
    ConcurrentHashMap<String,LinkedList<String>> userReportsByGame;     //map<gameName,linkedlist<reportsAsString>>

public stompUser(String uName,String uPass){
    this.userName = uName;
    this.passcode = uPass;
    this.userReportsByGame = new ConcurrentHashMap<>();

}

public void newGame (String gameName){
    userReportsByGame.put(gameName, new LinkedList<>());
}

public void addReport (String gameName, String report){  //notice that reports are already as a long string
    userReportsByGame.get(gameName).addLast(report);
}

}
