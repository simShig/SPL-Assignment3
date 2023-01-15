
#ifndef STOMPPROTOCOL_CPP
#define STOMPPROTOCOL_CPP

#include "../include/StompProtocol.h"
#include "../include/FrameFormat.h"
#include "../include/ConnectionHandler.h"
#include "../include/StompClient.h"
#include <thread>
#include <stdlib.h>
#include <sstream>
#include <iostream>

// TODO: implement the STOMP protocol
StompProtocol ::StompProtocol() : recipt_id_counter(1), game_id_counter(1), should_terminate(false), id_to_game(), game_to_id(), receipt_id_to_message() {}

bool StompProtocol::shouldTerminate()
{
    return should_terminate;
}

void StompProtocol ::stompToString(std::string &stompFrame)
{
    std::stringstream ss(stompFrame);
    std::string frameHeadLine;
    std::getline(ss, frameHeadLine, '\n');
    if (frameHeadLine == "CONNECTED")
    {
    std::cout << "helloWorld";
    std::cout << stompFrame;
        return;
    }
    else if (frameHeadLine == "RECEIPT")
    {
        StompProtocol::handleReceipt(stompFrame);
    }
    else if (frameHeadLine == "MESSAGE")
    {
        StompProtocol::handleMassage(stompFrame);
    }
    else if (frameHeadLine == "ERROR")
    {
        std::cout << stompFrame;
    }
    else
    {
        std::cout << "what the fuck";
    }
}

void StompProtocol ::handleReceipt(std::string &stompFrame)
{
    std::vector<std::string> splitedFrame = splitString(stompFrame, ' ');
    std::string receiptID = splitedFrame[1];
    std::string content = receipt_id_to_message[receiptID];
    if (content == "disconnect")
    {
        std ::cout << "Bye Bye <3" << std::endl;
        should_terminate = true;
    }
    else
    {
        std::cout << content << std::endl;
    }
}

void StompProtocol ::handleMassage(std::string &stompFrame)
{
    std::cout << stompFrame << std::endl;
}

std::vector<std::string> StompProtocol::splitString(const std::string &str, char x)
{
    std::stringstream ss(str);
    std::string line;
    std::vector<std::string> split_str;
    while (std::getline(ss, line, x))
    {
        split_str.push_back(line);
    }
    return split_str;
}

//______________________________________________________

std::string StompProtocol::stringToStomp(std::string &dataFromUser)
{
    std::vector<std::string> splitedFrame = splitString(dataFromUser, ' ');
    std::string action = splitedFrame[0];
    if (action == "login")
    {
        return handleLogin(splitedFrame);
    }
    else if (action == "join")
    {
        return handleJoin(splitedFrame);
    }
    else if (action == "exit")
    {
        return handleExit(splitedFrame);
    }
    else if (action == "report")
    {
        return handleReport(splitedFrame);
    }
    else if (action == "logout")
    {
        return handleLogout(splitedFrame);
    }
    else
    {
        std:: cout<<"illigal command"<<std::endl;
        return "%";
    }
}

std::string StompProtocol::handleLogin(std::vector<std::string> &splitedFrame)
{
    if (splitedFrame.size() != 4)
    {
        std::cout << "illigal command, login dont have 4 words" << std::endl;
        return "%";
    }
    else
    {
        receipt_id_to_message[std::to_string(recipt_id_counter)] = "Login successful";
        std::string frame = "CONNECT;F;receipt-id:" + std::to_string(recipt_id_counter) + ";L;" + "accept-version:1.2;L;host:" + splitedFrame[1] + ";L;login:" + splitedFrame[2] + ";L;passcode:" + splitedFrame[3] + ";F;\nemptyBody";
        recipt_id_counter += 1;
        return frame;
    }
}

std::string StompProtocol::handleJoin(std::vector<std::string> &splitedFrame)
{
    if (splitedFrame.size() != 2)
    {
        std::cout << "illigal command, join dont have 2 words" << std::endl;
        return "%";
    }
    else
    {
        id_to_game[std::to_string(game_id_counter)] = splitedFrame[1];
        game_to_id[splitedFrame[1]] = std::to_string(game_id_counter);
        receipt_id_to_message[std::to_string(recipt_id_counter)] = "Joined channel " + splitedFrame[1];
        std::string frame = "SUBSCRIBE\nreceipt-id: " + std::to_string(recipt_id_counter) + "\ndestination: " + splitedFrame[1] + "\nID: " + std::to_string(game_id_counter) + "\n\n";
        recipt_id_counter += 1;
        game_id_counter += 1;
        return frame;
    }
}

std::string StompProtocol::handleExit(std::vector<std::string> &splitedFrame)
{
    if (splitedFrame.size() != 2)
    {
        std::cout << "illigal command, exit dont have 2 words" << std::endl;
        return "%";
    }
    else
    {
        std ::string game_id = game_to_id[splitedFrame[1]];
        id_to_game.erase(game_id);
        game_to_id.erase(splitedFrame[1]);
        receipt_id_to_message[std::to_string(recipt_id_counter)] = "Exited channel " + splitedFrame[1];
        std::string frame = "UNSUBSCRIBE\nreceipt-id: " + std::to_string(recipt_id_counter) + "\ndestination: " + splitedFrame[1] + "\nID: " + game_id + "\n\n";
        recipt_id_counter += 1;
        return frame;
    }
}

std::string StompProtocol::handleReport(std::vector<std::string> &splitedFrame)
{
    return "";
}
std::string StompProtocol::handleLogout(std::vector<std::string> &splitedFrame)
{
    receipt_id_to_message[std::to_string(recipt_id_counter)] = "disconnect";
    std::string frame = "DISCONNECT\nreceipt: " + std::to_string(recipt_id_counter) + "\n\n";
    recipt_id_counter += 1;
    return frame;
}

// string StompProtocol::process(string message)
// {
    
// }

// FrameFormat StompProtocol::string2Frame(string line)
// {
 
// }

// string StompProtocol::Frame2String(FrameFormat frame)
// {
    
// }


#endif
