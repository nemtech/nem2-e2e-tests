#!/bin/bash

# https://stackoverflow.com/questions/11176284/time-condition-loop-in-shell
# The variable STOP_TIME will store the seconds since 1970-01-01 00:00:00
# UTC, according to the date specified by -d "$2".

# $ ./start-chaos-kill-test.sh ... "1 hour 4 minutes 3 seconds" ...
# $ ./start-chaos-kill-test.sh ... "tomorrow 8:00am" ...
# $ ./start-chaos-kill-test.sh ... "monday 8:00am" ...

# set -e to exit if the time provided by argument 2 is not valid for date.
set -e
STOP_TIME=$2
set +e

CHAOS_COMPOSE_FILE=$1
echo -e "Using chaos docker-compose file: $CHAOS_COMPOSE_FILE"
SYMBOL_COMPOSE_FILE=$(python3 utils.py get_relative_file_path --file_name=docker-compose-auto-recovery.yml --target_dir=catapult-service-bootstrap)
SPAMMER_COMPOSE_FILE=$(python3 utils.py get_relative_file_path --file_name=docker-compose-spammer.yml --target_dir=catapult-spammer)

# CHAOS_LOG_FILE=chaos-logs/$CHAOS_COMPOSE_FILE.$(date +"%d.%m.%Y-%H.%M.%S").log
# Launch the symbol server and pumba containers
echo -e 'Starting Symbol server...'
docker-compose -f ${SYMBOL_COMPOSE_FILE} -f ${CHAOS_COMPOSE_FILE} up -d

# Call the python script
DOCKER_CONTAINERS=($(python3 utils.py get_docker_container_names --compose_file=$CHAOS_COMPOSE_FILE))
echo "List of Symbol Docker containers to be killed: ${DOCKER_CONTAINERS[@]}"
sleep 10
docker ps
echo 'Finished starting up catapult server.'

echo 'Getting private key and generation hash...'
# Get the private key and generation hash from the started symbol server
export PRIVATE_KEY=$(python3 utils.py get_first_user_private_key)
export GENERATION_HASH=$(curl localhost:3000/node/info | jq --raw-output '.networkGenerationHash')
export TRANSACTIONS_PER_SECOND=$3

START_TIME_EPOCH_SECONDS=$(date "+%s")
STOP_TIME_EPOCH_SECONDS=$(date -d "$STOP_TIME" "+%s")

echo -e "\nStart time: $(date)"
echo -e "End time: $(date -d "$STOP_TIME")\n"

# Calculate number of accounts based on end time and transaction rate. 
NUM_ACCOUNTS=$(python3 utils.py injected-transactions --start-epoch=$START_TIME_EPOCH_SECONDS --end-epoch=$STOP_TIME_EPOCH_SECONDS --transaction-rate=$3 --time-offset=30)
export NUMBER_OF_ACCOUNTS=$NUM_ACCOUNTS

echo 'Creating .env file with private key and generation hash from the catapult config...'
envsubst < ../catapult-spammer/cmds/bootstrap/spammer/spammer.env > ../catapult-spammer/cmds/bootstrap/.env
echo 'printing ../catapult-spammer/cmds/bootstrap/.env file contents...'
cat ../catapult-spammer/cmds/bootstrap/.env

TRANSACTION_COUNT_BEFORE=$(python3 symbol_data.py count-transactions)
printf "\n\nNumber of transactions in Symbol mongo db before the test: $TRANSACTION_COUNT_BEFORE\n"

docker-compose -f ${SPAMMER_COMPOSE_FILE} up -d --build
echo "Printing spammer container logs..." && sleep 10
docker-compose -f ${SPAMMER_COMPOSE_FILE} logs spammer
# Properly check if spammer container started and is actually sending transactions. 
# If not, exit, since there is no point in continuing
# Also, could improve this by monitoring the spammer container too to check it has not exited
echo "Spammer started; entering peer containers monitoring loop..."


# Repeat the loop while the current date is less than STOP_TIME_EPOCH_SECONDS
while [ $(date "+%s") -lt ${STOP_TIME_EPOCH_SECONDS} ]; do
  printf "\n"
  sleep 60
  for container in "${DOCKER_CONTAINERS[@]}"; do
    # Remove the single quotes from the container name string (it was returned by python with '')
    container=$(sed s/\'//g <<<$container)
    echo "Processing docker container: $container"
    NODE_STATUS=$(docker inspect $container --format='{{.State.Status}}')
    NODE_EXITCODE=$(docker inspect $container --format='{{.State.ExitCode}}')
    echo "container $container status: ${NODE_STATUS}; exitcode: ${NODE_EXITCODE}"
    if [ ${NODE_STATUS} == "exited"  ] && [ ${NODE_EXITCODE} == "137" ]; then
      echo "$container was randomly killed by pumba, restarting it."
      docker start $container
    fi
  done
done

# Now query the catapult mongo db and check the count of transactions
TRANSACTION_COUNT_AFTER=$(python3 symbol_data.py count-transactions)
printf "\n\nNumber of transactions in Symbol mongo db after the test: $TRANSACTION_COUNT_AFTER\n"

EXIT_CODE=$(python3 utils.py assert-total-transactions --before=$TRANSACTION_COUNT_BEFORE --injected=$NUM_ACCOUNTS --after=$TRANSACTION_COUNT_AFTER)

# Stop spammer
docker-compose -f ${SPAMMER_COMPOSE_FILE} down

# Stop symbol and pumba
docker-compose -f ${SYMBOL_COMPOSE_FILE} -f ${CHAOS_COMPOSE_FILE} down --remove-orphans

# Delete all data and settings created by symbol for a clean start next time
# passwordless sudo user will be created on the chaos net instances for below to work
# cd ../catapult-service-bootstrap && sudo ./cmds/clean-all && cd ../chaos-tests

printf "\nTransactions before = $TRANSACTION_COUNT_BEFORE\n"
printf "Transactions injected = $NUM_ACCOUNTS\n"
printf "Transactions after = $TRANSACTION_COUNT_AFTER\n\n"

if [ $EXIT_CODE -eq 0 ]
then
  printf "\nWoohoo! Hurray!! Test passed!!!\n"
else
  printf "\nOh dear! Not sure what went wrong, but the test failed.\n\n"
fi

exit $EXIT_CODE