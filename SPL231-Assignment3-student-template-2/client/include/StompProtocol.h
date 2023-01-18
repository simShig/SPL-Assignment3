#pragma once
#include "../include/ConnectionHandler.h"
#include "../include/FrameFormat.h"
#include "../include/Event.h"
#include "../include/Summary.h"
#include <unordered_map>
#include <string>
#include <vector>
#include <map>
#include <deque>
using std::string;
using namespace std;
class Summary;

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
    std::string handleReport(std::vector<std::string> &splitedFrame, std::string &dataFromUser);
    std::string handleLogout(std::vector<std::string> &splitedFrame);
    std::vector<std::string> splitString(const std::string &str, char x);
    std::vector<std::string> sendFrame(std::vector<std::string> &splitedInput);
    std::string createEventFrame(Event &event, string topic); /////////////////////

    int subCounter;
    int recieptCounter;
    std::string userName;
    std::map<string, string> subScribed; // topic - id
    std::map<string, std::map<string, Summary>> summaries;
    std::map<string, string> recieptMap; // reciept - action

public:
    string process(string message);
    FrameFormat string2Frame(string line);
    string Frame2String(FrameFormat frame);
    StompProtocol();
    bool shouldTerminate();
    void stompToString(std::string &stompFrame);
    std::string stringToStomp(std::string &dataFromUser); /////////////////

    vector<string> processUser(string);
    void processLogIn(string s, ConnectionHandler &connectionHandler);
    string processServer(string);
    string transmit(string c, std::map<string, string> h, string b);
    string hashMapToString(std::map<string, string> h);
    string createFrameEvent(Event e);
    vector<string> createVector(names_and_events parsed);
    void addEvents(names_and_events parsed);
    bool isSummary(string s);
    string summary(string s);
    void send(std::vector<string> lines);
    bool logIn(string s);
    string CONNECTED(string s);
    string ERROR(string s);
    string MESSEGE(string s);
    string RECIEPT(string s);
    std::map<std::string, std::string> mappify2(std::string const &s);
    bool GameSummaryNotContainsUser(string userName, string gameName);
    bool summariesNotContainGame(string gameName);
    void addGameToSummaries(string gameName);
    void addUserNameToGameSummary(string userName, string gameName);
    void checksBeforeAddEvent(string userName, string gameName);
    string logoutFrame();

    // for the event constructure from procces
    string get_user_name(std::deque<std::string> &s);
    void addingEvent(Event &e, string userid);
    void logout();
    std::string ans2Frame(std::vector<std::string> ans);

};

