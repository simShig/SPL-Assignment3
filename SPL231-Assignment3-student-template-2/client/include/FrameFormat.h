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
private:

public:

string stompCommand; // = nullptr;


list<list<string>> stompHeaders ; //= new list<list<string>>(); //list<list(headerName,headerValue)
string FrameBody; // = nullptr;
char EndOfMassage; // = '\0';

//delimiters for transfering to\from string
const string EndOfLine; // = "\n";
const string EndOfField; // = "\n\ff";    //end of field is also the end of some line


};