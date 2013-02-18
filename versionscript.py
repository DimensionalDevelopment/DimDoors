import sys
import json
import string


argversion = sys.argv

versionlist = argversion[1].split('R')

mcversion = versionlist[0]
mfrversion = versionlist[1]
if(len(versionlist) > 2):
	mfrversion = versionlist[1]+"R"+versionlist[2]

mcmod=open('mcmod.info', 'r')

data = json.load(mcmod)

for mod in data:
	for attribute, value in mod.iteritems():
		if (attribute == 'version'):
			mod[attribute] = mfrversion
		if (attribute == 'mcversion'):
			mod[attribute] = mcversion
			
mcmod=open('mcmod.info', 'w')
json.dump(data, mcmod, indent=4)

print (mfrversion)