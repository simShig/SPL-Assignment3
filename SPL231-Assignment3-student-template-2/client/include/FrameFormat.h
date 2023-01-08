#pragma once

#include <string>
#include <iostream>
#include <map>
#include <vector>
#include <list>
using std::string;
using std::list;

class FrameFormat
{
public:
FrameFormat(string stompCMD,list<list<string>> stompHDRS,string FrameBDY);

char EndOfMassage; // = '\0';

//delimiters for transfering to\from string
const string EndOfLine; // = "\n";
const string EndOfField; // = "\n\ff";    //end of field is also the end of some line


private:
string stompCMD;
list<list<string>> stompHDRS;
string FrameBDY;
};