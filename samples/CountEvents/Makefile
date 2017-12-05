#
# *******************************************************************************
# * Copyright (C)2017, International Business Machines Corporation and *
# * others. All Rights Reserved. *
# *******************************************************************************
#

sc=${STREAMS_INSTALL}/bin/sc
args=-t ${STREAMS_INSTALL}/toolkits --data-directory=data
ns=com.ibm.streamsx.iot.sample.countevents
main=CountEvents
outputdir=./output

all: compile

compile: 
	${sc} ${args} --output-directory ${outputdir}/ -M ${ns}::${main}

clean:
	rm -rf ${outputdir}
