#!/bin/bash
SERVER_DIR=/home/samith/WSO2/AppFactory/performance/fork/wso2greg-4.6.0/
DUMP_HOME_DIR=/home/samith/WSO2/AppFactory/performance/fork/Save

MY_PATH="`dirname \"$0\"`"
MY_PATH="`( cd \"$MY_PATH\" && pwd )`"

function restart_server(){
    if [ -e "${PIDFILE}" ]; then
      PID=`cat ${PIDFILE}`
    fi
    echo "Restarting server..."
    sh ${SERVER_DIR}/bin/wso2server.sh restart
    echo "Waiting 45s to restart the server.."
    sleep 45s
}

cd ${MY_PATH}
restart_server
echo "Build the project!.."
mvn clean install -Dmaven.test.skip=true
echo "Start tenant creation.."
mvn exec:java -Dexec.mainClass="org.wso2.appfactory.gregloadtest.Initializer" -Dexec.cleanupDaemonThreads="false"
echo "Completed!!!"
