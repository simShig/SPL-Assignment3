#ifndef READFROMSERVER_CPP
#define READFROMSERVER_CPP

#include "../include/ReadFromServer.h"
#include "../include/StompProtocol.h"
#include "../include/StompClient.h"


ReadFromServer:: ReadFromServer(ConnectionHandler &_connectionHandler, StompProtocol &_protocol):connectionHandler(_connectionHandler),MyProtocol(_protocol) {};

void ReadFromServer::run()
{
    while (!MyProtocol.shouldTerminate())
    {
        std::string answer;
        // Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
        // We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end
        if (!connectionHandler.getLine(answer))
        {
            std::cout << "Disconnected. Exiting...\n"
                      << std::endl;
            break;
        }
// std::cout<<"answer is:" + answer << std::endl;
        int len = answer.length();
        // A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
        // we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
        answer.resize(len - 1);
        MyProtocol.stompToString(answer);
        // std::cout<<"after stomp2string calling:" + answer << std::endl;
    }
}
#endif




