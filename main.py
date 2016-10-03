from pubnub import Pubnub                       #importing pubnub into python
import RPi.GPIO as GPIO                         #importing GPIO
from time import sleep, localtime               #importing sleep and location
import datetime                                 #importing datetime 
import ephem                                    #importing the ephem library for calculating the position of the sun and moon based on the user input of date and location
import threading                                #importing threading which is newer version and provides much more light-level support for threads
import schedule                                 #importing schedule for scheduling task
 
GPIO.setwarnings(False)                         #to disable the warning which says this GPIO has been occupied. This is option provided by sytem while launching the application

GPIO.setmode(GPIO.BCM)                          # "Broadcom SOC channel" numbering. Alternative is to use GPIO.BOARD 

pir_pin = 18                                    #Initializing the GPIO port 18 in BCM numbering to motion sensor
light_pin = 23                                  #Initializing the GPIO port 23 in BCM numbering to light
pir_pwr = 24                                    #Initializing the GPIO port 24 in BCM numbering to motion sensor power from the relay

GPIO.setup(pir_pin, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)  #setting pin 18 as an input source. pull_up_down=GPIO.PUD_DOWN is neccessary to turn off motion sensor
GPIO.setup(light_pin, GPIO.OUT)                           #setting pin 23 as output pin to send signal to
GPIO.setup(pir_pwr, GPIO.OUT)                             #setting pin 24 as output.It is necessary so that power can be cut off to motion sensor when we want to switch it off

pubnub = Pubnub(publish_key="pub-c-09bfa5fa-e87b-4902-8b46-b72f0f4df755", subscribe_key="sub-c-8c48d8d4-ed94-11e5-8112-02ee2ddab7fe") #keys to communicate in pubnub.
FLAG1 = False                                                                                                                         #keys should be same in android and this file
FLAG2 = False
FLAG3 = False

def updateView():                                                                           #creating a function called updateView
  print pubnub.publish(channel='pirStatus', message=not GPIO.input(pir_pwr))                #telling the GPIO to send power to turn on motion sensor 
  print pubnub.publish(channel='lightStatus', message=not GPIO.input(light_pin))            #telling the GPIO to send power to turn on light bulb
  print pubnub.publish(channel='geoStatus', message=FLAG1)
  print pubnub.publish(channel='schStatus', message=FLAG2)

def initCallback(message,channel):                            
  updateView()                                                                              #calling the updateView function to set the default configuration

def autoCallback(message, channel):
  global FLAG1                                                                 #******************************************************************************************
  global FLAG2                                                                        # Relay works in negative logic. If user supply 1(HIGH) then it turns off the power
  global FLAG3                                                                        # Similarly if 0(LOW) is supplied then it supply power to the circuit
  print(message)                                                               #*****************************************************************************************
  if message=="L0":                                                            
    print "light off"                                                          
    GPIO.output(light_pin,GPIO.HIGH)                                           #GPIO.HIGH means the to turn on that light.It tells circuit to provide power to turn on the light
  elif message=="L1":                                                          
    print "Light On"
    GPIO.output(light_pin,GPIO.LOW)                                            #GPIO.LOW means pin is no longer supplying power.
  elif message == "P0":
    print "sensor off"
    GPIO.output(pir_pwr,GPIO.HIGH)
    FLAG3 = False
  elif message == "P1":
    print "Sensor On"
    FLAG3 = True
    GPIO.output(pir_pwr,GPIO.LOW)
    t = threading.Thread(target=motionTrigger)                                  #targets to the function motionTrigger 
    t.start()                                                                   #starts the new thread
  elif message == "S0":
    print "Scheduler Off"
    FLAG2 = False
  elif message == "S1":
    print "Scheduler On"
    FLAG2 = True
  elif message == "G0":
    print "Geo Trigger Off"
    FLAG1 = False
  elif message == "G1":
    print "Geo Trigger On"
  
def schCallback(msg, channel):
  print(msg)
  scheduler(msg["START_HOUR"],msg["START_MINUTE"], msg["END_HOUR"],msg["END_MINUTE"])             #inputs from the android interface from scheduler feature 

def locationCallback(message,channel):                                                            
  global FLAG1
  print message
  FLAG1 = False
  sleep(4)
  t = threading.Thread(target=geoFencing,args=(str(message["latitude"]),str(message["longitude"]),))    #target to geoFencing function and put the input latitude and longitude 
  t.start()



def error(message):
  print("ERROR : " + str(message))
  
  
def connect(message):
  print("CONNECTED")
  
def reconnect(message):
  print("RECONNECTED")
  
  
def disconnect(message):
  print("DISCONNECTED")
  
def geoFencing(latitude, longitude):                              #the latitude and longitude value that we get from android is passed here
  global FLAG1
  FLAG1 = True
  flag = 0
  while FLAG1:
    o=ephem.Observer()                                            
    o.lat=latitude                                                #takes latitude value
    o.long=longitude                                              #takes longitude value
    s=ephem.Sun()  
    s.compute()                                                   #computes the value
    sr_next = ephem.localtime(o.next_rising(s))                   #stores the value of next sunrise to sr_next
    ss_next = ephem.localtime(o.next_setting(s))                  #stores the value of next sunset to ss_next
         
    if sr_next > ss_next:                             
      print "Turn off lights"   
      GPIO.output(light_pin,GPIO.HIGH)
      if flag==0:
        updateView()
        flag = 1
    else:
      print "Turn on lights"
      GPIO.output(light_pin,GPIO.LOW)
      if flag == 1:
        updateView()
        flag = 0

    sleep(5)

def scheduler(startH,startM,stopH,stopM):                     #input from the scheduler feature from android is passed here
  global FLAG2    
  def job():                                                  #define a job
    GPIO.output(light_pin, GPIO.LOW)                          
    updateView()                                              #pass the updated value to updateView()
    print "turn on light"

  def job1():                                                 #creating another job. In similar ways many jobs can be created in simultaneous way.
    global FLAG2
    GPIO.output(light_pin, GPIO.HIGH)
    FLAG2 = False
    updateView()
    print "turn off light"
  
  start_time = "{0:02d}:{1:02d}".format(startH, startM)       #
  stop_time = "{0:02d}:{1:02d}".format(stopH, stopM)
  print start_time                                            #prints the time 
  print stop_time
  schedule.every().day.at(start_time).do(job)                #jobs are performed in sequence one after another 
  schedule.every().day.at(stop_time).do(job1)

  while FLAG2:
    schedule.run_pending()
    sleep(1)          
  print "Exit schedule"
  schedule.clear()                                            #clears all the scheduled job there are

def motionTrigger():
  while FLAG3:
    if(GPIO.input(pir_pin)):
      print "pir trigger"
      GPIO.output(light_pin,GPIO.LOW)
      updateView()
    else:
      GPIO.output(light_pin,GPIO.HIGH)
      updateView()
    sleep(1)
  GPIO.output(light_pin,GPIO.HIGH)
  updateView()

pubnub.subscribe(channels='automation', callback=autoCallback, error=error,                       #subscribe to the channel means to recive the data from the publisher which is 
                 connect=connect, reconnect=reconnect, disconnect=disconnect)                     # android device in this case

pubnub.subscribe(channels='scheduler', callback=schCallback, error=error,
                 connect=connect, reconnect=reconnect, disconnect=disconnect)

pubnub.subscribe(channels='location', callback=locationCallback, error=error,
                 connect=connect, reconnect=reconnect, disconnect=disconnect)

pubnub.subscribe(channels='init', callback=initCallback, error=error,
                 connect=connect, reconnect=reconnect, disconnect=disconnect)