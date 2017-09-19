# CSCI 4310 Object Oriented Programming. Fall 2017. 
## Project 2: Network stack simulator
<hr style="border: 0; height: 5px; background-color: black;"/>

## Introduction
This project implements a rough model of the TCP/IP network stack assuming very basic structure of each layer and minimal functionality in terms of how layers communicate one to another. The big goal is utilize the principles of Object Oriented Programming (OOP) to define **structure** and **behavior** of corresponding layers and the ability to combine them to form 3 kinds of network entities: (i) a *host*; (ii) a *route*r; and (iii) a *switch*. Lastly, the *application* class is to be introduced to run within the *host* network entity such that multiple application instances could communicate one to another by means of passing messages (network packets) through the stack to mimic actual network behavior.

There assumed to be 5 network layers in this project:
* **Application layer**. Used to host applications that require network communications. 
* **Transport layer**. Used to implement *transport* service which is usually considered in the context of *reliable* (relatively slow, in-order, and guaranteed delivery) **vs.** *unreliable* (relatively fast, out-of-order, and not guaranteed delivery) data exchange.
* **Network layer**. Used to provide means of **logical addressing** where network addresses of individual *hosts* and *networks* can be implemented. Any host can be configured to run on any specific network so its *network address is considered not permanent*. A process of network **routing** is introduced to allow cross-network communication where a *single host* or a *dedicated router* may have more than one instance of *network layer* to support network-to-network data exchange.  
* **Link layer**. Used to provide network connectivity between hosts (or routers) within the **same** *local network*. Each communicating host (or router) is associated with a permanent  **hardware address**. A process of *intra-network communication* is supoported by special devices called **network switches** that have multiple instances of *link layer* each of which is known to be connected (or not) to some network device (host or router) with a specific hardware address, so the switch decides how different intra-network devices should/can communicate. 
* **Physical layer**. Used to convert logical representation of network data into corresponding physical signals (voltages, frequencies, etc.), and transmit them across physical network (wires). 
 
The network exchange is implemented by means of passing messages between connected hosts. Network messages change while being transferred through the stack within the *sending* host (**encapsulation**) and then they change again while being passed though the stack of the *receiving* host (**decapsulation**). 

The main idea of *encapsulation* is to allow each layer (that receives a message from above neighbor) to add something to the message at the beginning (usually address info) and to the end (usually checksum used to validate correctness of data delivery at the time of message *decapsulation*) such that message grows in size and gets additional information. The encapsulated message is then forwarded to the next layer down below where encapsulation process is repeated. Physical layer does not implement encapsulation but instead forwards the "physical representation" of the message to the physical layer of the directly connected device. 

The *decapsulation* process pretty much reverses the *encapsualtion* while passing message from lower layers to the upper ones. Each layer (except application) decapsulates the message by removing and validating additional data (added by its sender counter-party) prior passing the message to the upper neighbor. In case when validation process detects some problems (for example, checksum of the message does not match the value provided by the sender) the message is not forwarded any further and is considered dropped by the current layer. 

As a message goes downward along the stack on the sender host and upwards on the receiving host we assume that:
 * An **upper layer calls a _method_ from the lower layer** while passing data as method's parameters and/or reading the returned data;
 * A **lower layer sends a _notification_ to the upper layer** when it has some data to be forwarded upwards. 
 
 ## Classes
 ### The Layers 
 There must be an abstract *layer* class considered as a parent to all members of the stack. That class should either implement a `Runnable` interface or extend `Thread` class which would allow for asynchronous implementation of the *notification system*. The `run()` method of the `Runnable` interface should have an infinite listening loop with each iteration having short periods of *working* mixed with relatively long periods of *sleeping*. During the active *working* periods a queue of notifications would be examined to check for the necessity to respond to a notification sent by the lower layer. Presence of a notification would result in some action to request the message data and to create another notification to be handled by the upper layer.

Every derived layer class should **at least** have it own:
* constructor;
* a method for data encapsulation;
* a method for data decapsulation;

The two exceptions are the **application** and the **physical** layers. The former should have no specific implementation (could derive it from the base layer class) of the *decapsulation* procedure and the latter should have no specific implementation of the *encapsulation* procedure. 
 
 ### The Message
 Message class represents an object that is passed from layer to layer. It has a data field to represent *raw* message data as well as placeholders to be *filled* and *evaluated* by individual layers during encapsulation/decapsulation process. An important aspect of the message class functionality is its ability to support serialization/deserialization process that mimics transformation of the "message content" into a "sequence of voltage pulses" by the physical interface of the sending host, and back to "message content" by the physical interface of the receiving host. Instead of voltage pulses we will consider an array of bytes to be transferred "along the wire".
 
 ### The Wire
The wire should be considered as a media connecting two network interfaces (see below for more details). It could be a connection between a host and a switch, or a connection between a switch and a router, and lastly, a connection between two switches. Despite it is possible, it is somewhat uncommon to create direct connection between host(s) and router(s) without a switch. 

A wire is characterized by an attribute that represents **quality of the connection** which is a combined measure of physical quality of the wire (frequency response, impedance, etc.) and its environment (electro-magnetic interference) that affects **probability of unmodified data delivery**. The default value of this parameter is 1 which means that data is delivered intact without in-wire modification. Values less than 1 would result in a random modification of some bytes in the byte array that represents the *message on the wire*. The smaller the value the more bytes are considered affected by the random process.
           
Another attribute of the wire class is the **data** itself (array of bytes) that is set by the sender and is read by the receiver. It is that array which is affected by the random process controlled by *quality of the connection* attribute. 

### The Network Interface
The network interface is an ordered collection of layer objects that represents one of the three network entities: 
* a **host**  that has all 5 layers ordered from top to bottom as *application*, *transport*, *network*, *link*, and *physical*. 
* a **router** that has the lower 3 layers, i.e. *network*, *link*, and *physical*.
* a **switch** that has the lower 2 layers, i.e. *link* and *physical*.
 
### The Network Entity
The network entity is the base class for the *host*, the *router*, and the *switch* classes that combines **one or more network interfaces of the same kind**. On top of this, each network entity may have additional data structures to represent its *dedicated function*. For example, a router needs a *routing table* and a *routing process* (see below for more details). A switch needs to maintain association between its network interfaces and other network entities that are plugged into those interfaces, etc. 
   
### The Network Topology
The topology of the network represents its **content** (set of entities) and its **connectivity** (set of wires). An exampled topology describing host-to-host connection via a switch would contain 3 entities (two hosts and a switch) in the former set and two wires in the latter set. It is important to note that a wire does not just connect two entities, it connects specific interfaces of two entities.

### The Application
The application class represents a primitive piece of network software that is running on a host and is willing to send/receive data from another application. Every application can be either a **server** (waiting for data exchange) or a **client** (initiating data exchange). Regardless to its kind, an application should be bound to a particular *network interface* which in turn would support network exchange to/from the connected network.      
 
### The Simulator
The simulator class is assumed to contain the `main()` method where *network topology* and a set of *applications* (possibly more than one application on a single host) are instantiated. The simulator  controls creation of data on client applications and its consumption (printing) on server applications. All intermediate processes are synchronized by network layers with respect to the rate of layer-to-layer notification mechanism without direct involvement of the simulator.  
     
## Processes
### Address Resolution Protocol (ARP)
The [address resolution protocol](https://en.wikipedia.org/wiki/Address_Resolution_Protocol) is a discovery mechanism that allows building up an ARP cache within **each** network interface implementing *network layer* (host or router). The ARP cache is a table of associations of **logical** (a.k.a. IP addresses, the ones known at the *network* layer) and **hardware** (a.k.a. MAC addresses, the ones known at the *link* layer) addresses of other network entities which are members of the same **local network**.   
    
### Routing
The routing process allows to decide which network interface (if many) to choose to forward each incoming network message. This process is usually implemented on routers but can also be implemented on hosts. The key part of this process is to consult the [routing table](https://en.wikipedia.org/wiki/Routing_table) that allows for identification of outgoing network interface on the basis of destination logical address contained in the message. Each router/host maintains a single routing table regardless to the number of installed interfaces.

## Addresses
### Logical (a.k.a. IP) address
By analogy to real networks a logical address will have a numerical presentation, but instead of 4 bytes we will use only 1. Below is a summary of the proposed logical addressing scheme
* Each network interface of a host/router containing *network* layer is considered to be a member of some network. 
* The network is identified by a **network address** and a **network mask** controlling the size of the network.
* A network address is a number in the range from 0 to 255
* A network mask is a number from 0 to 8 that describes how many bits in the associated network address are allocated to represent the network identity (see example below for details).
* The network *address* and the network *mask* are usually written as `address/mask` but are in fact represented by the two numbers. For example, pair `64/5` assumes that the 5 most-significant bits in the binary presentation of number 64, i.e. `01000` represent the *identity of the network* while the remaining 3 least-significant bits serve to identify particular hosts on that network. Since there are only 3 bits left for hosts identity, there could be 8 different combinations to represent up to 8 hosts on that network. A valid address on that network would be `67` (`01000` in the *network* part of the address and `011` in the *host* portion of the address). But `35` would be an incorrect address for network `64/5` because its first 5 most significant bits `00100` do not match given network identity `01000`.  

**Note:** In the context of this development we ignore the standard practice to reserve the *lowest* and *highest* host addresses to describe *network itself* and *broadcasting address* correspondingly.
 
### Hardware (a.k.a. MAC) address
By analogy to real networks our hardware address will be written in *hexadecimal* format and will be ranged from `00` to `FF` assuming 256 possible hardware addresses. We will also assume that the first hexadecimal digit represents a *vendor* while the second digit represents the *serial number* of the adapter manufactured by that vendor. In this case we allow up to 16 vendors and up to 16 hardware adapters manufactured by each vendor.  
