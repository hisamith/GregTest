#!/bin/bash
SERVER_DIR=/home/samith/WSO2/AppFactory/performance/fork/wso2greg-4.6.0/

MY_PATH="`dirname \"$0\"`"
MY_PATH="`( cd \"$MY_PATH\" && pwd )`"
cd ${MY_PATH}
echo "Build the project!.."
mvn clean install -Dmaven.test.skip=true
array_tenants_to_load=(10 20 50 100)
array_apps_to_load=(1 5 10)
PIDFILE=${SERVER_DIR}/wso2carbon.pid

function restart_server(){
    if [ -e "${PIDFILE}" ]; then
      PID=`cat ${PIDFILE}`
    fi
    echo "Restarting server..."
    sh ${SERVER_DIR}/bin/wso2server.sh restart
    echo "Waiting 45s to restart the server.."
    sleep 45s
}

echo "Starting the load test.."
for num_of_apps in "${array_apps_to_load[@]}"
do
    restart_server
    echo "starting get the results for number of apps: "${num_of_apps}
    for num_of_tenants in "${array_tenants_to_load[@]}"
    do
        ARGS=${num_of_tenants}" "${num_of_apps}
        echo "Tenants Apps: "${ARGS}
        mvn exec:java -Dexec.mainClass="org.wso2.appfactory.gregloadtest.Client" -Dexec.cleanupDaemonThreads="false" \
        -Dexec.args="${ARGS}"
    done
    echo "Done for number of apps: "${num_of_apps}
done
echo "Completed!!!"
