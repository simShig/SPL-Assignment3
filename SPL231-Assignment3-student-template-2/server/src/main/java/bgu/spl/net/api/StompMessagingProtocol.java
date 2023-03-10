package bgu.spl.net.api;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.MessagingProtocol;

public interface StompMessagingProtocol<T> {
	/**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
	**/
    // void start(int connectionId, Connections<T> connections);        //someone in the group said Hadi allowed to delete it
    boolean setCH (ConnectionHandler<T> CH);

    String process(String message);
	
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
}
