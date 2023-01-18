#ifndef STOMPPROTOCOL_CPP
#define STOMPPROTOCOL_CPP

#include "../include/StompProtocol.h"
#include "../include/FrameFormat.h"
#include "../include/ConnectionHandler.h"
#include "../include/StompClient.h"
#include "../include/Event.h"
#include "../include/Summary.h"
#include <thread>
#include <stdlib.h>
#include <sstream>
#include <iostream>
#include <string>
#include <vector>
#include <map>


// TODO: implement the STOMP protocol
StompProtocol ::StompProtocol() : recipt_id_counter(1), game_id_counter(1), should_terminate(false), id_to_game(), game_to_id(), receipt_id_to_message(), subCounter(0), recieptCounter(0),userName(""), subScribed(),summaries(),recieptMap() {}

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
    return sendFrame(splitedFrame);
 //return handleReport(splitedFrame, dataFromUser);
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
 std::string frame = "SUBSCRIBE;F;receipt-id:" + std::to_string(recipt_id_counter) + ";L;destination:" + splitedFrame[1] + ";L;id:" + std::to_string(game_id_counter) + ";F; ";
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
 std::string frame = "UNSUBSCRIBE;F;receipt-id:" + std::to_string(recipt_id_counter) + ";L;destination:" + splitedFrame[1] + ";L;id:" + game_id + ";F; ";
 recipt_id_counter += 1;
 std::cout << "Exited channel " + splitedFrame[1] << std::endl;
 return frame;
 }
}

std::string StompProtocol::handleReport(std::vector<std::string> &splitedFrame, std::string &dataFromUser)
{
string file;
std::cout << "line 193\n" << std::endl;
std::stringstream input_stringstream(dataFromUser);
std::cout << "line 195\n" << std::endl;
getline(input_stringstream, file, ' ');
std::cout << "line 197\n" << std::endl;
file = "./data/" + file;
std::cout << "line 199\n ===============" + file << std::endl;
names_and_events parsed = parseEventsFile("/home/spl211/Desktop/SPL-Assignment3-2/SPL231-Assignment3-student-template-2/client/data/events1_partial.json");
std::cout << "line 201\n" << std::endl;
addEvents(parsed);
std::cout << "line 203\n" << std::endl;
std::vector<std::string> ans;
std::cout << "line 205\n" << std::endl;
 ans = createVector(parsed);
std::cout << "line 207\n" << std::endl;
 string frame = ans2Frame(ans);
std::cout << "line 209\n" << std::endl;
 return frame; //TODO
}
std::string StompProtocol::ans2Frame(std::vector<std::string> ans)
{
 std::string frame;
 for (const std::string& str : ans) {
 frame.append(str + ";L;");
 }
 std::cout << frame+";F;  " << std::endl;
 return (frame+";F;  ");
 
}

std::string StompProtocol::handleLogout(std::vector<std::string> &splitedFrame)
{
 receipt_id_to_message[std::to_string(recipt_id_counter)] = "disconnect";
 std::string frame = "DISCONNECT;F;receipt:" + std::to_string(recipt_id_counter) +";F; ";
 recipt_id_counter += 1;
 return frame;
}
//=================================================================
std::string StompProtocol::sendFrame(std::vector<std::string> &splitedInput)
{
   // std::cout << splitedInput[1] +"\n" << std::endl;
names_and_events fileContent = parseEventsFile(splitedInput[1]);
   // std::cout << "line236\n" << std::endl;
string topic = fileContent.team_a_name + string("_") + fileContent.team_b_name;
  //  std::cout << "line238, the topic is: \n" +topic << std::endl;
std::vector<std::string> eventframes;
for (size_t i = 0; i < fileContent.events.size(); i++)
{
eventframes.push_back(createEventFrame(fileContent.events[i], topic));
}
return sendFramestring(eventframes);
}

std::string StompProtocol::sendFramestring(std::vector<std::string> &eventframes)
{
    string str ="";
    for (const std::string& s : eventframes) {
        str += s;
    }
    //std::cout << "im in line 253, str is: \n" +str << std::endl;

    return str;
}

std::string StompProtocol::createEventFrame(Event &event, string topic)
{
string generalUpdates = "";
string teamAUpdates = "";
string teamBUpdates = "";

for (auto key : event.get_game_updates())
generalUpdates += key.first + string(":=========== ") + key.second + ";L;";
for (auto key : event.get_team_a_updates())
teamAUpdates += key.first + string(":------------- ") + key.second + ";L;";
for (auto key : event.get_team_b_updates())
teamBUpdates += key.first + string(":+++++++++++++++ ") + key.second + ";L;";
string frame = "SEND;F;;L;" + string("destination:/topic/") + topic + ";L;" + "receipt-id:" + std::to_string(recipt_id_counter) + ";L;" + ";F;" +
 string("user: ") + event.get_name() + ";L;" + string("team a: ") + event.get_team_a_name() + ";L;" + string("team b: ") +
 event.get_team_b_name() + ";L;" + string("event name: ") + event.get_name() +
 ";L;" + string("time: ") + std::to_string(event.get_time()) + ";L;" + string("general game updates: ") +
 generalUpdates + ";L;" + string("team a updates: ") + teamAUpdates + ";L;" + string("team b updates: ") +
 teamBUpdates + ";L;" + string("description: ") + event.get_discription() + ";L;;F;";
return frame;
}

//----------------------------------------------------------------------------

string StompProtocol::summary(string s)
{
 string gameName;
 stringstream input_stringstream(s);
 getline(input_stringstream, gameName, ' ');
 getline(input_stringstream, gameName, ' '); // twice to skip "summary"
 string userName;
 getline(input_stringstream, userName, ' ');
 string file;
 getline(input_stringstream, file, ' ');
 // need to check if the user and the game exist ###
 return (summaries[gameName])[userName].toString(file);
}
bool StompProtocol::isSummary(string s)
{
 stringstream input_stringstream(s);
 string word;
 getline(input_stringstream, word, ' ');
 return word == "summary";
}
void StompProtocol::addEvents(names_and_events parsed)
{
 for (Event e : parsed.events)
 {
 string gameName = e.get_team_a_name() + "_" + e.get_team_b_name();
 checksBeforeAddEvent(userName, gameName);
 if (subScribed.count(gameName) == 0)
 {
 should_terminate=true;
 break;
 }
 (summaries[gameName])[userName].addEvent(e); // add event to the summary of the user
 }
}
void StompProtocol::checksBeforeAddEvent(string userName, string gameName)
{
 if (summariesNotContainGame(gameName))
 {
 addGameToSummaries(gameName);
 addUserNameToGameSummary(userName, gameName);
 }
 else if (GameSummaryNotContainsUser(userName, gameName))
 {
 addUserNameToGameSummary(userName, gameName);
 }
}
bool StompProtocol::GameSummaryNotContainsUser(string userName, string gameName)
{
 return (summaries[gameName]).count(userName) == 0;
}
bool StompProtocol::summariesNotContainGame(string gameName)
{
 return summaries.count(gameName) == 0;
}
void StompProtocol::addGameToSummaries(string gameName)
{
 std::map<std::string, Summary> map; // create new map for the game
 summaries[gameName] = map; // add to summaries the map
}
void StompProtocol::addUserNameToGameSummary(string userName, string gameName)
{
 Summary s; // create summay for the user that reported
 (summaries[gameName])[userName] = s; // add the summary to the user in the map
}
vector<string> StompProtocol::createVector(names_and_events parsed)
{
 vector<string> ans;
 for (Event e : parsed.events)
 {
 ans.push_back(createFrameEvent(e));
 }
 return ans;
}
string StompProtocol::createFrameEvent(Event e)
{
 string command = "SEND";
 string topic = e.get_team_a_name() + "_" + e.get_team_b_name();
 map<string, string> frameMap; // the frame headers
 frameMap["destination"] = topic;
 frameMap["reciept"] = to_string(recieptCounter);
 recieptMap[to_string(recieptCounter)] = "sent";
 recieptCounter++;
 string b = "";
 map<string, string> map;
 b += "user:" + userName + "\n";
 map["team a"] = e.get_team_a_name();
 map["team b"] = e.get_team_b_name();
 map["event name"] = e.get_name();
 map["time"] = to_string(e.get_time());
 b += hashMapToString(map);
 b += "general Game updates: \n";
 b += hashMapToString(e.get_game_updates());
 b += "team a updates: \n";
 b += hashMapToString(e.get_team_a_updates());
 b += "team b updates: \n";
 b += hashMapToString(e.get_team_b_updates());
 b += "description: \n";
 b += e.get_discription();
 return transmit(command, frameMap, b);
}
string StompProtocol::hashMapToString(std::map<string, string> h)
{
 string message = "";
 std::map<string, string>::iterator it = h.begin();
 while (it != h.end())
 {
 message += it->first;
 message += (":");
 message += it->second;
 message += ("\n");
 it++;
 }
 return message;
}
string StompProtocol::transmit(string c, std::map<string, string> h, string b)
{
 string message(c);
 message += ("\n");
 std::map<string, string>::iterator it = h.begin();
 while (it != h.end())
 {
 message += it->first;
 message += (":");
 message += it->second;
 message += ("\n");
 it++;
 }
 message += ("\n");
 message += (b);
 return message;
}


void StompProtocol::addingEvent(Event &e, string userid)
{
 string gameName = e.get_team_a_name() + "_" + e.get_team_b_name();
 checksBeforeAddEvent(userid, gameName);
 (summaries[gameName])[userid].addEvent(e); // add event to the summary of the user
}

string StompProtocol::get_user_name(deque<std::string> &s)
{ // get the user name from deque
 string user_line = s.front();
 int index = user_line.find(":");
 string user_name = user_line.substr(index + 1);
 s.pop_front(); // remove user name
 return user_name;
}

#endif