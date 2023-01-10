#pragma once

#include "../include/ConnectionHandler.h"

// TODO: implement the STOMP protocol
class StompProtocol
{
private:
public:
string process(string message); 
FrameFormat string2Frame(string line);
string Frame2String(FrameFormat frame);
};
