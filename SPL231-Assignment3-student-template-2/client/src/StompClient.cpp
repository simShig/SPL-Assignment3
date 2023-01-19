#ifndef STOMPCLIENT_CPP
#define STOMPCLIENT_CPP

#include "../include/StompProtocol.h"
#include "../include/ConnectionHandler.h"
#include "../include/StompClient.h"
#include "../include/ReadFromServer.h"
#include <thread>
#include <stdlib.h>

int main(int argc, char *argv[])
{
    if (argc < 3)
    {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl
                  << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect())
    {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    // creating server reader thread

    StompProtocol MyProtocol;

    ReadFromServer task1(connectionHandler, MyProtocol);
    std::thread th1(&ReadFromServer::run, &task1);
    std::vector<std::string> framesarchive; 
    while (!MyProtocol.shouldTerminate())
    {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);

        // we added----
        //  FrameFormat inputFrame = string2Frame(line);
        //  string convertedFrame = Frame2String(inputFrame);
        if (MyProtocol.isSummary(line))
        {
            std::cout << MyProtocol.summary(line);
            continue;
        }
        string convertedFrame = MyProtocol.stringToStomp(line);
        

        std::string largeString = convertedFrame;
        // std::vector<std::string> sendStrings;
        size_t start = 0;

        while(true) {
            if(largeString.find("SEND",0)==std::string::npos){
                framesarchive.push_back(largeString);
                break;
            }
            std::cout << "in while(true)" << std::endl;
            size_t sendPos = largeString.find("SEND", start);
            if (sendPos == std::string::npos) {
                std::cout << "in if npos :" + largeString << std::endl;
                break;
            }
            size_t end = largeString.find('\0', sendPos);
            if (end != std::string::npos) {
                framesarchive.push_back(largeString.substr(sendPos, end - sendPos));
                std::cout << "in else :" + framesarchive.front() << std::endl;
                start = end + 1;
                std::cout << "start = "+start << std::endl;
            }
        }
        //SUBSCRIBEcvxcvxcvxcv
        // arcivheFrames.pushback(SENDxccvxxcxcxcvxc\0)

        //---we ended adding
        while (framesarchive.size()!=0){

            string actually_sent_to_server = framesarchive.front();
            framesarchive.pop_back();
            int len = actually_sent_to_server.length();          // //archiveFrames.pop_last().
            if (!connectionHandler.sendLine(actually_sent_to_server))        //archiveFrames.pop_last()
            {
                std::cout << "Disconnected. Exiting...\n"
                        << std::endl;
                break;
            }
        // connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.
        std::cout << "Sent " << len + 1 << " bytes to server" << std::endl;
        }
    }

th1.join();
}
#endif