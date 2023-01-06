package bgu.spl.net.impl.stomp;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
public class FrameFormat {
    
// FIELDS - by the general format:

String stompCommand = null;
public LinkedList<LinkedList<String>> stompHeaders = new LinkedList<>();    //list<list(headerName,headerValue)
//public ConcurrentHashMap<String,String> stompHeaders = new ConcurrentHashMap<>();//map<headerName,headerValue> 
String FrameBody = null;
char EndOfMassage = '\u0000';

//delimiters for transfering to\from string
static String EndOfLine = "\n";
static String EndOfField = "\n\ff";    //end of field is also the end of some line


//methods:
public FrameFormat (String stompCmd,LinkedList<LinkedList<String>> stompHDRS,String frameBody){
    this.stompCommand=stompCmd;
    this.stompHeaders = stompHDRS;
    this.FrameBody = frameBody;
}


// public String frame2String (){
//     String ans=null;
// //add commandHeader:
//     ans=ans+stompCommand + EndOfField;

// //add stompHeaders:
//     for (LinkedList<String> header : stompHeaders) {
//         ans=ans+header.get(0) + ":"+header.get(1) + EndOfLine;
//     }
//     ans+=EndOfField;

// //add body:
//     ans+= FrameBody+EndOfField;
// //add EOM:
//     ans+=EndOfMassage;
//     return ans;
// }

// public FrameFormat string2Frame (String str){               //transforms msg back to frame
//     String[] splitByFields = str.split(EndOfField);
//     //commandHeader:
//     String sCommand = splitByFields[0];
//     //stompHeaders:
//     LinkedList<LinkedList<String>> sHeaders = new LinkedList<>();
//     String[] headersSplitByEOL = splitByFields[1].split(EndOfLine);
//     for (int i = 1; i < headersSplitByEOL.length-1; i++) {
//       String headerName = headersSplitByEOL[i].split(":")[0];
//       String headerValue = headersSplitByEOL[i].split(":")[1];
//       LinkedList<String> Pair = new LinkedList<>();
//       Pair.add(headerName);
//       Pair.add(headerValue);
//       sHeaders.add(Pair);
//     }
//     //body:
//     String sBody = splitByFields[2];

//     FrameFormat recievedFrame = new FrameFormat(sCommand, sHeaders, sBody);
//     return recievedFrame;
// }





}
