#!/bin/bash
WEBAPP_SRC=/home/samith/WSO2/AppFactory/performance/fork/wso2greg-4.6.0/repository/resources/tenantwebapps/javaapp11-default-SNAPSHOT.war
DESTINATION_DIR=/home/samith/WSO2/AppFactory/performance/fork/wso2greg-4.6.0/repository/tenants
NUM_OF_TENANTS=2000
RESET_CLR='\033[00;00m'
RED="\033[33;31m"
GREEN="\033[33;32m"
MAGENTA="\033[33;35m"

for i in  $(seq 1 ${NUM_OF_TENANTS});
do
    DIR_TO_COPY=${DESTINATION_DIR}/${i}/webapps
    mkdir -p ${DIR_TO_COPY}
    echo -en "${MAGENTA}Cpoying for tenant:${RESET_CLR} "${i}" ... "
    cp ${WEBAPP_SRC} ${DIR_TO_COPY}/
    [ $? -eq 0 ] && echo -e "${GREEN}[DONE]${RESET_CLR}" || echo -e "${RED}[FAILED]${RESET_CLR}";
done