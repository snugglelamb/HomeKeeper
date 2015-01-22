import serial, sys, string
import httplib
import ast
from push_gcm import RemoteAlert

# Domain you want to post to: localhost would be an emoncms installation on your own laptop
# this could be changed to emoncms.org to post to emoncms.org
domain = "localhost"
#domain = "emoncms.org"

# Location of emoncms in your server, the standard setup is to place it in a folder called emoncms
# To post to emoncms.org change this to blank: ""
emoncmspath = "emoncms"

# Write apikey of emoncms account
#localhost
apikey_write = "xxx"
apikey_read = "yyy"


# Node id youd like the emontx to appear as
temp_id = 10
dis_id = 11

conn = httplib.HTTPConnection(domain)

# Set this to the serial port of your emontx and baud rate, 9600 is standard emontx baud rate
ser = serial.Serial('/dev/ttyACM3', 9600)

# Send to emoncms for threshold
threshold = ['200','2000','200'] #temp, light, distance
thres_id = [13,14,15]
thres_val = [float(i) for i in threshold]
get_thres_id = [5,9,10] # 5 for temp, 6 for light, 7 for distance
# current get 
flag = 10
sensor_name = ["temperature","light","distance"]

for i in range(len(threshold)):
  csv = threshold[i]
  # csv = ",".join(threshold)
  print sensor_name[i]
  conn.request("GET", "/"+emoncmspath+"/input/post.json?apikey="+apikey_write+"&node="+str(thres_id[i])+"&csv="+csv)
  response = conn.getresponse()
  print response.read()

#prepare to send alert
ra = RemoteAlert()
dev_id = 'xxx'

print "Initial Readout:\n"
print ser.readline()
print ser.readline()
print "Finish Initialization.\n"

while 1:

  # Read in line of readings from arduino serial
  linestr = ser.readline()
  linestr = linestr.rstrip()
  
  print linestr

  if "dis" in linestr:
    dis = linestr.split(' ')[1]
    arr = [dis]
    node_id = dis_id
    flag = 2

  # Remove the new line at the end
  elif "temp" in linestr:
    temp = linestr.split(' ')[1]
    arr = [temp]
    node_id = temp_id
    flag = 0

  else:
    light = linestr.split(' ')[1]
    arr = [light]
    node_id = 12
    flag = 1

#  print linestr
  # Create csv string
  print arr
  csv = ",".join(arr)

  # Send to emoncms
  conn.request("GET", "/"+emoncmspath+"/input/post.json?apikey="+apikey_write+"&node="+str(node_id)+"&csv="+csv)
  response = conn.getresponse()
  print response.read()
  
  # read threshold
  conn.request("GET", "/"+emoncmspath+"/feed/value.json?apikey="+apikey_read+"&id="+str(get_thres_id[flag]))
  response = conn.getresponse()
  try:
    get_str = ast.literal_eval(response.read())
    print get_str
    thres_val[flag] = float(get_str)
  except ValueError, e:
    print "error:",e
  
  # check alarm
  try:
    arr_val = [float(i) for i in arr]
    print "sent:", arr_val
  except ValueError, e:
    print "error:",e
    
  if any( item > thres_val[flag] for item in arr_val):
    # send alert to device
    alert = "[!_ALARM_!] "+ str(sensor_name[flag])+ " sensor above threshold at " + str(thres_val[flag])
    print ra.send(dev_id,alert)
    print alert
#    ser.write('1') #Alarm will trigger local LED 
#  else:
#    ser.write('100') #Alarm will turn off
