#ifndef READFROMSERVER_H
#define READFROMSERVER_H

#include "ConnectionHandler.h"
#include "StompProtocol.h"

class ReadFromServer
{
private:
	ConnectionHandler &connectionHandler;
    StompProtocol &MyProtocol;
public:
    ReadFromServer (ConnectionHandler &_connectionHandler, StompProtocol &_protocol);
    void run ();

};
#endif