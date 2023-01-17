#pragma once
#include "../include/ConnectionHandler.h"
#include "../include/FrameFormat.h"
#include <unordered_map>
#include <string>
#include <vector>


// TODO: implement the STOMP protocol
class StompProtocol
{
private:
    int recipt_id_counter;
    int game_id_counter;
    bool should_terminate;
    std::unordered_map<std::string, std::string> id_to_game;
    std::unordered_map<std::string, std::string> game_to_id;
    std::unordered_map<std::string, std::string> receipt_id_to_message;
    void handleReceipt(std::string &frame);
    void handleMassage(std::string &frame);
    std::string handleLogin(std::vector<std::string> &splitedFrame);
    std::string handleJoin(std::vector<std::string> &splitedFrame);
    std::string handleExit(std::vector<std::string> &splitedFrame);
    std::string handleReport(std::vector<std::string> &splitedFrame);
    std::string handleLogout(std::vector<std::string> &splitedFrame);
    std::vector<std::string> splitString(const std::string &str, char x);
    std::vector<std::string> sendFrame(std::vector<std::string> &splitedInput);
    std::string StompProtocol::createEventFrame(Event &event, string topic);


public:
    string process(string message);
    FrameFormat string2Frame(string line);
    string Frame2String(FrameFormat frame);
    StompProtocol();
    bool shouldTerminate();
    void stompToString(std::string &stompFrame);
    std::string stringToStomp(std::string &dataFromUser);
};
