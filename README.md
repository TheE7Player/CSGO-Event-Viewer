# CSGO Event Viewer
 A Java project to allow users to see what events you can use with [logic_eventlistener](https://developer.valvesoftware.com/wiki/Logic_eventlistener).

 This program is dependent on the parser [CSGO Event Parser](https://github.com/TheE7Player/CSGO_Event_Parser), which generates the events.json file *needed* to handle the events needed in order to run this program. (*Which is included with release*)

# Introducing multiple language support
The program now allows you to run the program in the language you want it to be (if it is supported).

As of the latest build (0.3) it supports the following:

 - English (UK & US)
 - Russian (Thank you **@DMax#2494**)

## How to launch the application in a different language?
With the provided .bat file (Windows), uncomment the second java line to load the Russian version...

### Launch in English
    java -jar CSGO_Event_Finder.jar
### Launch in Russian
Just add the argument **"-ru"** after **".jar"**
   `java -jar CSGO_Event_Finder.jar -ru`
# How to download this application
You can download it from [here](https://github.com/TheE7Player/CSGO-Event-Viewer/releases), which holds the latest edition of this program.

This program also requires Java 8 in order to run, so the latest version of Java is recommended.
