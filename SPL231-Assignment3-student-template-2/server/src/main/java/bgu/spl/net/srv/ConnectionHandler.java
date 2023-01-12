/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.net.srv;

import java.io.Closeable;

import bgu.spl.net.impl.stomp.stompUser;

/**
 * The ConnectionHandler interface for Message of type T
 */
public interface ConnectionHandler<T> extends Closeable {

    /**
     * Comment the following lines (both send methods) for the existing implentations to work.
     *
     */
    int getConnectionID();
    void setActiveUser(String username);
    String getActiveUser ();
    void setConnectionId(int id);
     void send(T msg);

}
