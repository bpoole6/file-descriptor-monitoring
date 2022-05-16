#!/bin/bash


function publish(){
        PID=$1
        NAME=$2
        COUNT=$(ls ${PROC_MOUNT_POINT}/${PID}/fd -f | wc -l) #$PROC_MOUNT_POINT is an environment variable set in the task definition
        TASK_ID=$(docker inspect --format '{{index  .Config.Labels "com.amazonaws.ecs.task-arn"}}' $NAME |  rev | cut -d '/' -f 1 | rev)
        ENVIRONENT=$(docker inspect --format '{{index  .Config.Labels "testApp.env"}}' $NAME |  rev | cut -d '/' -f 1 | rev)
        SERVICE_NAME=$(docker inspect --format '{{index  .Config.Labels "testApp.service"}}' $NAME |  rev | cut -d '/' -f 1 | rev)
        ENABLE_FD_MONITORING=$(docker inspect --format '{{index  .Config.Labels "testApp.enable.fd.monitoring"}}' $NAME |  rev | cut -d '/' -f 1 | rev) || false
        if [ "$ENABLE_FD_MONITORING" = "true" ]; then
                aws cloudwatch put-metric-data --metric-name Open-Files --namespace ECS/ContainerMonitoring --unit Count --value ${COUNT}\
                 --dimensions Environment=${ENVIRONENT},Service=${SERVICE_NAME},TaskId=${TASK_ID} --storage-resolution 1
        fi;
        return $COUNT
}
while :
do
  pids=()
  for i in $(docker ps -f label=testApp.env --format {{.Names}}); do
          PID=$(docker inspect --format '{{.State.Pid}}' $i)
          publish $PID $i &
          pids+=($!)

  done

  for i in ${pids}; do
          wait $i
  done
  echo done
sleep .9
done
echo done
