#HomeKeeper
    CIS 542 project
    Team Member: Zhi Li, Huayi Guo

##Intro
The aim is to build a remote home monitor system that could be accessed by user anywhere they want via our Android app. 

**System Structure**

![alt text][system]
[system]: https://github.com/snugglelamb/HomeKeeper/blob/master/screenshot/p0.png "System Structure"


**Hardware**
* Hardwares to be setted up at home include a raspberry pi with wifi dongle, an Arduino DUE board, a thermometer, a dimmer and a distance sensor.

**Software**
* A python DAQ is running on rpi to read data from Arduino via serial port, then the data is sent to emoncms cloud server and read by android devices running our app for data visualization. 

**Alarm Setting**
* If the data read from Arduino exceeds the threshold set, alarm messages will be sent to users via notifications (RemoteAlert) and emails containing detail information. Also, user could change the current threshold setup on his device and send to emoncms cloud, then the settings will be read and applied by local server on rpi, thus bi­directional communication is established.

**App Screenshot**

|                |                |
|:--------------:| --------------:|
|![alt text][p1] |![alt text][p2] |
| ![alt text][p3]|![alt text][p4] |

[p1]: https://github.com/snugglelamb/HomeKeeper/blob/master/screenshot/p1.png "p1"
[p2]: https://github.com/snugglelamb/HomeKeeper/blob/master/screenshot/p2.png "p2"
[p3]: https://github.com/snugglelamb/HomeKeeper/blob/master/screenshot/p3.png "p3"
[p4]: https://github.com/snugglelamb/HomeKeeper/blob/master/screenshot/p4.png "p4"

##Steps to setup
* Setup Raspbian 

    We followed the standard routine of setting up raspberry pi system using a 16GB SD card. Considering the functionality to implement, we choose the Raspbian image instead of NOOBS and others. First we reformat SD card, then download from [Raspbian Doc](http://www.raspberrypi.org/downloads a)

* Wifi setup 

    Then, in order to ssh over to pi, we setted up the wifi dongle following the instructions in the following two links. [Config Settings](http://www.raspberrypi.org/forums/viewtopic.php?f=28&t=72282)

* Setup partition the disk

    Specifically, we created a directory /home/pi/data that will be a mount point for the read and write data partition. Also, we set the pi to reboot in read­only mode, in order to increase the lifespan of current SD card. 

* Enable Security Assurance 
 
    In order to raise the security standard of current application, we added the following packages.
    1. ufw
      We setuped ufw to control your server access rules. sudo ufw allow 80/tcp
      sudo ufw allow 443/tcp
      sudo ufw allow 22/tcp
      sudo ufw enable
    2. secure mySQL
      we choose to use mysql_secure_installation
    3. secure ssh
      disabled root login
      sudo nano /etc/ssh/sshd_config

* Setup Emoncms
  
    For this application, in case of server, we choose to use emoncms server, which is an open source server source provided by [emoncms](http://openenergymonitor.org/emon/). OpenEnergyMonitor is a project to develop open­source energy monitoring tools to help us relate to our use of energy, our energy systems and the challenge of sustainable energy. Developer could program to store sensor data in local database and post to cloud server held by emoncms.org, and the android app could fetch sensor data from the cloud server as well as set alarm threshold to the cloud server then read by the local server.

    In order to build and run the server, we follow the instructions in the following link, basically we setuped the emonhub as mediator between sensor hardware and emoncms server, setted up the website using php, and created scripts to enable and disable local emoncms server. The example of local server webpage is shown on the right. [Official ReadMe](https://github.com/emoncms/emoncms/blob/bufferedwrite/docs/install.md)

* Set Arduino on Pi
    In order to listen Arduino output via serial port on raspberry pi, we compiled and loaded the following linux kernel modules (cdc­acm, usbserial, ftdi_sio), which could be found in the pi’s root directory. The source codes were adapted locally from online source http://openenergymonitor.org/emon/node/488 on emoncs forum.

* Setup Python DAQ
    First enable the local server via localemoncms­enable.
    Then run the python DAQ program "read_data_internet.py" under script directory, which is designed for data logging into cloud server held by emoncms.org, so that the data could be fetched as long as the pi is connected to internet. The "read_data_local.py" is written for data logging into the local server and database. 
