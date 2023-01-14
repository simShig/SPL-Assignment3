package bgu.spl.net.api;

import java.util.LinkedList;

import bgu.spl.net.impl.stomp.FrameFormat;
import bgu.spl.net.impl.stomp.StompProtocolImpl;
import bgu.spl.net.srv.ConnectionsImpl;

public class InputStringGenerator {     //main that is used for creating STRINGS as should be recieved from the client.
    public static void main(String[] args){
        StompProtocolImpl protocol = new StompProtocolImpl(null, null);
        String stompCommand = "SEND";
        String frameBody = "wow this game is the best!";
        LinkedList<LinkedList<String>> stompHeaders = new LinkedList<>();
        FrameFormat generatedFrame = new FrameFormat(stompCommand, stompHeaders, frameBody);

        //headers:
        generatedFrame.addHeaders("destination","topic1");
        // generatedFrame.addHeaders("passcode","125689");
        // generatedFrame.addHeaders("login","Efrat");
        // generatedFrame.addHeaders("passcode","234234");
        
        //turn to string:

        System.out.println("\n"+protocol.frame2String(generatedFrame)+"\n");
    }
}
