#ifndef STOMPCLIENT_CPP
#define STOMPCLIENT_CPP

#include "../include/StompProtocol.h"
#include "../include/ConnectionHandler.h"
#include "../include/StompClient.h"
#include "../include/FrameFormat.h"
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

    while (!MyProtocol.shouldTerminate())
    {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);

        // we added----
        //  FrameFormat inputFrame = string2Frame(line);
        //  string convertedFrame = Frame2String(inputFrame);
        string convertedFrame = MyProtocol.stringToStomp(line);

        //---we ended adding

        int len = convertedFrame.length();
        if (convertedFrame != "%")
		{ // meaning it was a legal command
			if (!connectionHandler.sendLine(convertedFrame))
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