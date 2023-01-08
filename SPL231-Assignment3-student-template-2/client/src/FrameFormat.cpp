#include "../include/event.h"
#include "../include/FrameFormat.h"
#include "../include/json.hpp"
#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <vector>
#include <sstream>
using json = nlohmann::json;
using std::string;
using std::list;
//////hiiiiiiiiiiiii

// TODO: implement
char EndOfMassage= '\0';

//delimiters for transfering to\from string
const string EndOfLine= "\n";
const string EndOfField= "\n\ff";    //end of field is also the end of some line


FrameFormat::FrameFormat(string stompCMD,list<list<string>> stompHDRS, string FrameBDY) :
    stompCommand(stompCMD),
    stompHeaders(new stompHDRS),
    string FrameBody(FrameBDY)
{
    //stompHeaders = list<list(headerName,headerValue)>;
}

//---------Rule of 5 needed??