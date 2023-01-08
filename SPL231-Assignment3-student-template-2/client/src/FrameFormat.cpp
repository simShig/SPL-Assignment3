#include "../include/event.h"
#include "../include/FrameFormat.h"
#include "../include/json.hpp"
#include <iostream>
#include <fstream>
#include <string>
#include <list>
#include <map>
#include <vector>
#include <sstream>
using json = nlohmann::json;
using std::string;
using std::list;

// TODO: implement
char EndOfMassage= '\0';

//delimiters for transfering to\from string
const string EndOfLine= "\n";
const string EndOfField= "\n\ff";    //end of field is also the end of some line


FrameFormat::FrameFormat(string stompCMD,list<list<string>> stompHeaders,string FrameBody):
    stompCommand(stompCMD),stompHeaders(new list<list<string>>()),string FrameBody(frameBody)
{
    stompHeaders = list<list(headerName,headerValue)>;
}

//---------Rule of 5 needed??