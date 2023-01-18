#pragma once
#include <string>
using std::string;
#include <map>
#include "Event.h"
class Summary
{
private:
    string gameName;
    string teamAname;
    string teamBname;
    std::map<string,string> generalStats;
    std::map<string,string> teamAstats;
    std::map<string,string> teamBstats;
    std::map<string,string> reports; //time - report

public:
    Summary();
    void addEvent(Event event);
    void addEvent(string,string gameName);
    void addGeneralStats(std::map<string,string>);
    void addTeamStats(std::map<string,string> map,string team);
    void addreports(string discription,string time);
    string toString(string file);
    string toStringHashMap(std::map<string,string> h);
    string toStringReports(std::map<string,string> h);

};


	
