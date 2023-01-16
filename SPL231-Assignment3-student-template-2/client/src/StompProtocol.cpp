
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
    // std::cout << "inside StompProtocol::stompToString";
    std::stringstream ss(stompFrame);
    std::string frameHeadLine;
    std::getline(ss, frameHeadLine, ';');
    //std::cout << "====headline is:===="+frameHeadLine + "===stompFrame is: ===" +stompFrame + "***************\n";

    if (frameHeadLine == "CONNECTED")
    {
    //std::cout << ">>>>>>>>>>>>>>>>"+stompFrame;

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
    std::cout << "inside ~if ERROR~: \n";
        std::cout << stompFrame;
    }
    else
    {
        return;
        //std::cout << "what the hell???@?";
    }
}

void StompProtocol ::handleReceipt(std::string &stompFrame)
{
    std::vector<std::string> splitedFrame = splitString(stompFrame, ';');
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
        // std::cout << "im in login if, ---" + splitedFrame[0] + "--- is splitedFrame0, 1---" + splitedFrame[1] 
        // + "--- 2---" + splitedFrame[2]+ "--- 3---" + splitedFrame[3]<< std::endl;
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
    //std::cout << "im in handleLogin: \n" << std::endl;
    if (splitedFrame.size() != 4)
    {
        //std::cout << "im in if (splitedFrame.size() != 4): \n" << std::endl;
        std::cout << "illigal command, login dont have 4 words" << std::endl;
        return "%";
    }
    else
    {
        //std::cout << "im in if ELSE HANDLELOGIN: \n" << std::endl;
        receipt_id_to_message[std::to_string(recipt_id_counter)] = "Login successful";
        std::string frame = "CONNECT;F;receipt-id:" + std::to_string(recipt_id_counter) + ";L;" + "accept-version:1.2;L;host:" + splitedFrame[1] + ";L;login:" + splitedFrame[2] + ";L;passcode:" + splitedFrame[3] + ";F; emptybodie";
        recipt_id_counter += 1;
        //std::cout << frame << std::endl;
        std::cout << "Login successful" << std::endl;
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
        std::string frame = "SUBSCRIBE;F;receipt-id:" + std::to_string(recipt_id_counter) + ";L;destination:" + splitedFrame[1] + ";L;id:" + std::to_string(game_id_counter) + ";F;  ";
        recipt_id_counter += 1;
        game_id_counter += 1;
        std::cout << "Joined channel " + splitedFrame[1] << std::endl;
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
        std::string frame = "UNSUBSCRIBE;F;receipt-id:" + std::to_string(recipt_id_counter) + ";L;destination:" + splitedFrame[1] + ";L;ID:" + game_id + ";F;   ";
        recipt_id_counter += 1;
        std::cout << "Exited channel " + splitedFrame[1] << std::endl;
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
    std::string frame = "DISCONNECT;F;receipt:" + std::to_string(recipt_id_counter) +";F;  ";
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
