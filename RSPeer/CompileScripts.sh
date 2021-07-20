#!/bin/bash
# Compile RSPeer Scripts
#
#	compiles all .java files in the Git repo named "RSPeer"
#
#		compiles to a local folder "out" (.gitignored)
#		also to the RSPeer /scripts folder for the client
#

echo " "
echo "Compiling All .java files to output location: \"/home/ec2-user/RSPeer/scripts\"..."
javac -cp /home/ec2-user/.rspeer/2.18.jar $(find ./scripts/* | grep .java) \
	-d /home/ec2-user/RSPeer/scripts

#javac -cp /home/ec2-user/.rspeer/2.18.jar scripts/*.java \
#	-d /home/ec2-user/RSPeer/scripts
	
echo "Compile Success!"
echo " "
