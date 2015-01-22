import serial, sys, string
import httplib
import ast
from time import sleep
from push_gcm import RemoteAlert
import smtplib

# Domain you want to post to: localhost would be an emoncms installation on your own laptop
# this could be changed to emoncms.org to post to emoncms.org
# domain = "localhost"
domain = "emoncms.org"

# Location of emoncms in your server, the standard setup is to place it in a folder called emoncms
# To post to emoncms.org change this to blank: ""
# emoncmspath = "emoncms"
emoncmspath = ""

# Write apikey of emoncms account
# #localhost
# apikey_write = "xxx"
# apikey_read = "yyy"
#emoncms.org
apikey_write = "xxx"
apikey_read = "yyy"

# Node id youd like the emontx to appear as
temp_id = 10
light_id = 11
dis_id = 12

conn = httplib.HTTPConnection(domain)

# Set this to the serial port of your emontx and baud rate, 9600 is standard emontx baud rate
ser = serial.Serial('/dev/ttyACM0', 9600)

# Send to emoncms for threshold
threshold = ['35','1200','20'] #temp, light, distance
thres_id = [13,14,15]
thres_val = [float(i) for i in threshold]
get_thres_id = [63052,63053,63054] # 0 for temp, 1 for light, 2 for distance
alarm_count = [0,0,0]
# current get 
flag = 100
sensor_name = ["temperature","light","distance"]

for i in range(len(threshold)):
  csv = threshold[i]
  # csv = ",".join(threshold)
  print sensor_name[i],"initial threshold set."
  conn.request("GET", "/input/post.json?apikey="+apikey_write+"&node="+str(thres_id[i])+"&csv="+csv)
  response = conn.getresponse()
  print response.read()

#prepare to send alert
ra = RemoteAlert()
dev_id = 'XXX'
dev_id_2 = 'YYY'

#send alert email
smtpUser = 'abc@gmail.com'
smtpPass = 'abc'
toAdd = 'abc@gmail.com'
fromAdd = smtpUser
subject = '[Alert]From HomeKeeper'
header = 'To: '+ toAdd + '\n' + 'From: '+ fromAdd + '\n' + 'Subject: '+ subject
body = 'Please check will HomeKeeper App.\n'

# s = smtplib.SMTP('smtp.gmail.com',587)
# s.ehlo()
# s.starttls()
# s.ehlo()
# s.login(smtpUser,smtpPass)
# s.sendmail(fromAdd,toAdd,header+'\n\n'+body)
# s.quit()

# in case of corrupted data in buffer
print "Initial Readout:\n"
print ser.readline()
print ser.readline()
print ser.readline()
print "Finish Initialization.\n"

while 1:
  # sleep for 1s
  sleep(1)

  # Read in line of readings from arduino serial
  linestr = ser.readline()
  linestr = linestr.rstrip()
  
  print linestr
  # arr = []
  node_id = 0
  
  if "dis" in linestr or "cm" in linestr:
    dis = linestr.split(' ')[1]
    # arr = [dis]
    csv = dis
    node_id = dis_id
    flag = 2

  # Remove the new line at the end
  elif "light" in linestr or "lux" in linestr:
    light = linestr.split(' ')[1]
    # arr = [light]
    csv = light
    node_id = light_id
    flag = 1

  # else:
  elif "temp" in linestr or "Celsius" in linestr:
    temp = linestr.split(' ')[1]
    csv = temp
    # arr = [temp]
    node_id = temp_id
    flag = 0
      
  # Create csv string
  # print csv
  # csv = ",".join(arr)
  # csv = arr[0].rstrip()
  
  # Send to emoncms
  conn.request("GET","/input/post.json?apikey="+apikey_write+"&node="+str(node_id)+"&csv="+str(csv))
  response = conn.getresponse()
  print "response:", response.read()
  print "HTTP status:",response.status, response.reason
  print "\n"
  
  # read threshold
  conn.request("GET", "/feed/value.json?apikey="+apikey_read+"&id="+str(get_thres_id[flag]))
  response = conn.getresponse()
  try:
    get_str = ast.literal_eval(response.read())
    # print get_str
    thres_val[flag] = float(get_str)
  except ValueError, e:
    print "error:",e
  
  # check alarm
  try:
    csv_val = float(csv)
    # print "sent:", csv_val
  except ValueError, e:
    print "error:",e
    
  if csv_val > thres_val[flag]:
    alarm_count[flag] = alarm_count[flag] + 1
    if (alarm_count[flag]>2):
      alert = "[!_ALARM_!] "+ str(sensor_name[flag])+ " sensor above threshold at " + str(thres_val[flag])
      print alert
  
      # send alert to device at most 3 times
      if (alarm_count[flag]<=5):
        ra.send(dev_id,alert) 
        ra.send(dev_id_2,alert) 
        # send email
        s = smtplib.SMTP('smtp.gmail.com',587)
        s.ehlo()
        s.starttls()
        s.ehlo()
        s.login(smtpUser,smtpPass)
        s.sendmail(fromAdd,toAdd,header+'\n\n'+body+alert)
        s.quit()
  else:
    alarm_count[flag] = 0
