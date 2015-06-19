#!/bin/bash
mvn clean install -Dmaven.test.skip=true
array_tenants_to_load=(10 20 50 100 200 500)
array_apps_to_load=(1 5 10)
for num_of_apps in "${array_apps_to_load[@]}"
do
    for num_of_tenants in "${array_tenants_to_load[@]}"
    do
        ARGS=${num_of_tenants}" "${num_of_apps}
        echo "Tenants Apps: "${ARGS}
        mvn exec:java -Dexec.mainClass="org.wso2.appfactory.gregloadtest.Client" -Dexec.cleanupDaemonThreads="false" \
        -Dexec.args="${ARGS}"
    done
    echo "Done: "${num_of_tenants}
done
