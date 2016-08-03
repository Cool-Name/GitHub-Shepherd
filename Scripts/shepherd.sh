#!/bin/bash
# Shepherd
#
# description: 	Script to run the Puller on startup
#		and close on shutdown.

case $1 in
    start)
        /bin/bash /usr/local/bin/shepherd-start.sh
    ;;
    stop)
        /bin/bash /usr/local/bin/shepherd-stop.sh
    ;;
    restart)
        /bin/bash /usr/local/bin/shepherd-stop.sh
        /bin/bash /usr/local/bin/shepherd-start.sh
    ;;
esac
exit 0
