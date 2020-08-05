#!/bin/bash

# https://stackoverflow.com/questions/11176284/time-condition-loop-in-shell
# The variable STOP_TIME will store the seconds since 1970-01-01 00:00:00
# UTC, according to the date specified by -d "$2".

# Set some default values:
API_NODE_CONTAINER_NAME=
API_BROKER_CONTAINER_NAME=
STOP_TIME=unset
SLEEP_INTERVAL=unset
DOCKER_CONTAINERS=unset
START_TIME_EPOCH_SECONDS=unset
STOP_TIME_EPOCH_SECONDS=unset

usage()
{
  echo "Usage: 
    monitor-containers [ -a | --api-node API_NODE_CONTAINER_NAME ]
                       [ -b | --api-broker API_BROKER_CONTAINER_NAME ]
                       -e | --end-time 20:00 | 2020.12.30-08:00
                          # Recognized TIME formats:
                          #   hh:mm[:ss]
                          #   [YYYY.]MM.DD-hh:mm[:ss]
                          #   YYYY-MM-DD hh:mm[:ss]
                          #   [[[[[YY]YY]MM]DD]hh]mm[.ss]
                       -s | --sleep-interval (seconds) 60
                       container name(s)"
  exit 2
}

parse_and_validate_end_time()
{
    set -e
    STOP_TIME="$@"
    STOP_TIME=$(sed s/\'//g <<<$STOP_TIME)
    S=$(date -d "$STOP_TIME" "+%s")
    set +e 
}

PARSED_ARGUMENTS=$(getopt -a -n monitor-containers -o a:b:e:s: --long api-node:,api-broker:,end-time:,sleep-interval: -- "$@")
VALID_ARGUMENTS=$?
if [ "$VALID_ARGUMENTS" != "0" ]; then
  usage
fi

echo "$(date)::PARSED_ARGUMENTS: $PARSED_ARGUMENTS"
eval set -- "$PARSED_ARGUMENTS"
while :
do
  case "$1" in
    -a | --api-node)        API_NODE_CONTAINER_NAME="$2"    ; shift 2 ;;
    -b | --api-broker)      API_BROKER_CONTAINER_NAME="$2"  ; shift 2 ;;
    -e | --end-time)        parse_and_validate_end_time $2  ; shift 2 ;;
    -s | --sleep-interval)  SLEEP_INTERVAL="$2"             ; shift 2 ;;
    # -- means the end of the arguments; drop this, and break out of the while loop
    --) shift; break ;;
    # If invalid options were passed, then getopt should have reported an error,
    # which we checked as VALID_ARGUMENTS when getopt was called...
    *) echo "Unexpected option: $1 - this should not happen."
       usage ;;
  esac
done

DOCKER_CONTAINERS=($@)

if [[ ! -z "${API_BROKER_CONTAINER_NAME}" ]]
then
  echo "$(date)::API broker container to monitor   : ${API_BROKER_CONTAINER_NAME}"
  DOCKER_CONTAINERS+=("${API_BROKER_CONTAINER_NAME}")
else
  echo "$(date)::API broker container not specified"
fi

if [[ ! -z "${API_NODE_CONTAINER_NAME}" ]]
then
  echo "$(date)::API node container to monitor     : ${API_NODE_CONTAINER_NAME}"
  DOCKER_CONTAINERS+=("${API_NODE_CONTAINER_NAME}")
else
  echo "$(date)::API node container not specified"
fi

echo "$(date)::List of Symbol containers to monitor: ${DOCKER_CONTAINERS[@]}"

START_TIME_EPOCH_SECONDS=$(date "+%s")
STOP_TIME_EPOCH_SECONDS=$(date -d "$STOP_TIME" "+%s")

echo -e "\n$(date)::Start time                     : $(date)"
echo -e "$(date)::End time                       : $(date -d "$STOP_TIME")\n"

# Repeat the loop while the current date is less than STOP_TIME_EPOCH_SECONDS
while [ $(date "+%s") -lt ${STOP_TIME_EPOCH_SECONDS} ]; do
  sleep $SLEEP_INTERVAL
  for container in "${DOCKER_CONTAINERS[@]}"; do
    # Remove the single quotes from the container name string (it was returned by python with '')
    echo "$(date)::Processing docker container         : $container"
    container=$(sed s/\'//g <<<$container)
    echo "$(date)::Processing docker container         : $container"
    NODE_STATUS=$(docker inspect $container --format='{{.State.Status}}')
    NODE_EXITCODE=$(docker inspect $container --format='{{.State.ExitCode}}')
    echo "$(date):: container $container 
                    status     : ${NODE_STATUS}
                    exitcode   : ${NODE_EXITCODE}"
    if [[ ${NODE_STATUS} == "exited"  ]] && [[ ${NODE_EXITCODE} == "137" ]]; then
      
      # If it is the api-node-broker, then stop the api-node, start the broker, then start the api-node to avoid failure conditions
      if [[ ${container} == "${API_BROKER_CONTAINER_NAME}" ]]; then
        echo "$(date)::$container was killed by pumba. Stopping $API_NODE_CONTAINER_NAME and restarting both."
        docker stop "${API_NODE_CONTAINER_NAME}"
        sleep 5
        docker start "${API_BROKER_CONTAINER_NAME}"
        sleep 5
        docker start "${API_NODE_CONTAINER_NAME}"
      else
        echo "$(date)::$container was killed by pumba, restarting it."
        docker start "$container"
      fi
    fi
  done
done
