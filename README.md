# BlackFalconAPI

> The BlackFalconAPI is a translator between the game and a client. It has been created to make easier the developpement of any bot/IA that want to interact with the game.

The API works with two different sockets :

- The first one is a simple **socket** and will be used to connect with the game. 
- The second is a **websocket** that will be used to connect with the client. 

The API will receive packets from the game that will be translated and transferred to the client in JSON. The client will also be able to command to the API (connect, move, fight, ...) in JSON, it will be translated and sent to the game. 

All antibot solutions that can be managed by the API will be implemented (LatencyFrame, RDM, ...). 

[![STRUCTURE](https://trello-attachments.s3.amazonaws.com/5ce57f181041ba0b5ae4c693/5ce960f68adc4307dec113cc/3727b230292b52f744aa3a6ef01e1077/API.svg)]()

## Usage 

The API will need the current version of the game as well as the current java classes for the game (translated directly from the game). 

There will soon be a ProtocolBuilder to create theses classes and a VersionExtrator available on BlackFalcon.

## Commands 

> Commands that can be send to the API (connect, move, ..).

*In progress*

## Update automatisation 

In order to be as efficient as possible there will be a jenkins job to automate the build of the API. It will take the updated VersionExtractor, ProtocolBuilder and API repositories to create be fully functionnal and ready to use jar file. 

Also, if the game developpers update their game in ways that makes our API broken, we will be quickly notified. 

## License

[![License](http://img.shields.io/:license-mit-blue.svg?style=flat-square)](http://badges.mit-license.org)

- **[MIT license](http://opensource.org/licenses/mit-license.php)**
