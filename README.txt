Bytewalla - Android DTN Reference implementation
================================================
This is a Delay Tolerant Networking implementation on the Android platform.
This implementation is written in Java and highly inspired by the design of
DTN2 software version 2.6 developed in C++ by the DTN research group 
[http://www.dtnrg.org/wiki/Code].


Bytewalla - CSD Fall 2009 Project
=================================
This implementation is one of the deliverables of the Bytewalla project. 
Bytewalla project is a project from Communication System Design(CSD) course in
 Telecommunication Systems Laboratory (TSLab), Royal Institute of Technology, Sweden.
More information about Bytewalla can be found at [http://www.tslab.ssvl.kth.se/csd/projects/092106/]
More information about CSD can be found at [http://www.tslab.ssvl.kth.se/csd/]

Standards & Compatiblity
========================
This implementation followed the Bundle Protocol specification (RFC5050) and have been tested communicating 
with DTN2( Reference Implementation done by DTN Research Group). The TCP convergence layer is based on the Delay Tolerant Networking 
TCP Convergence Layer Protocol draft version 2. 

Official RFC5050 specification can be found at [http://tools.ietf.org/html/rfc5050].
Official Delay Tolerant Networking TCP Convergence Layer Protocol specification can be found at [http://tools.ietf.org/html/draft-irtf-dtnrg-tcp-clayer-02].

Bytewalla Folder Structure
==========================
src/		 - main source folder. All the source code for the Android platform is here.
assets/      - Android Asset folder [http://developer.android.com/guide/topics/resources/index.html].
             - The main dtn configuration file (dtn.config.xml) and its XML Schema (dtn.config.xsd) are here.          
res/         - Android Resources folder [http://developer.android.com/guide/topics/resources/index.html].
res/drawable - The pictures used in the user interfaces are stored here
res/layout   - The layout of DTN user interfaces including DTNManager, DTNConfigEditor, DTNSend, and DTNReceiveare are here.  
res/values   - The development parameters (strings.xml) stored here
bin/         - generated binary location which is suitable to run on the Android device or Emulator kept here 

Developer Guide
===============
  We used Eclispe with ADT plugin [http://developer.android.com/guide/developing/eclipse-adt.html] as a main development tool. 
As a result, continuing development with the tool is recommended but not mandatory. 

Documentation
=============
	Documentations for this software are available online from the project website.
1. Installation guide can be found at [http://www.tslab.ssvl.kth.se/csd/projects/092106/sites/default/files/Bytewalla_Installation_Guide.pdf].
2. User manual can be found at [http://www.tslab.ssvl.kth.se/csd/projects/092106/sites/default/files/Bytewalla_User_Manual.pdf].
3. Screenshots can be found at [http://www.tslab.ssvl.kth.se/csd/projects/092106/screenshot].
4. Javadoc for this software can be found at [http://www.tslab.ssvl.kth.se/csd/projects/092106/sites/default/files/Bytewalla%20Javadoc%20of%20AndroidDTN%20v1.0%20(2009.12.31).zip].
5. The system requirement to run this software can be found at [http://www.tslab.ssvl.kth.se/csd/projects/092106/sites/default/files/Bytewalla_System_Requirement.pdf].
6. This software have been tested an integration with Postfix email system. The system design architecture document
for the integration can be found at [http://www.tslab.ssvl.kth.se/csd/projects/092106/sites/default/files/Bytewalla%20System%20Architecture%20Design%20v1.0%202009.09.15.pdf].
The network setup documents before the integration can be done can be found at [http://www.tslab.ssvl.kth.se/csd/projects/092106/networkdocs].
7. Technical demonstration video can be found at [http://www.tslab.ssvl.kth.se/csd/projects/092106/tech_video]


Working teams
=============
Rerngvit Yanggratoke ( rerngvit@kth.se )
Sharjeel Ahmed (sharjeel@kth.se)
Maria Jose Peroza Marval (mjperoza@gmail.com)
Abdullah Azfar (abdullah_azfar@yahoo.com)