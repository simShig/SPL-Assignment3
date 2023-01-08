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

// TODO: implement the methods

string FrameFormat::FrameBody()
{
    return nullptr;
}

char FrameFormat::EndOfMessage(){
    return '\0';
}



