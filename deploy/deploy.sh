#!/bin/bash
DOCKER_USERNAME=${DOCKER_USERNAME}
IMAGE_TAG=${IMAGE_TAG}
DB_HOST=${DB_HOST}
DB_USERNAME=${DB_USERNAME}
DB_PASSWORD=${DB_PASSWORD}

IMAGE=${DOCKER_USERNAME}/skincare:${IMAGE_TAG}

CURRENT=$(docker ps --format '{{.Names}}' | grep -E 'skincare-blue|skincare-green')

if [ "$CURRENT" = "skincare-blue" ]; then
  NEXT="green"; NEXT_PORT=8082; PREV="blue"; PREV_PORT=8081
else
  NEXT="blue"; NEXT_PORT=8081; PREV="green"; PREV_PORT=8082
fi

docker pull $IMAGE

DOCKER_USERNAME=$DOCKER_USERNAME IMAGE_TAG=$IMAGE_TAG DB_HOST=$DB_HOST \
DB_USERNAME=$DB_USERNAME DB_PASSWORD=$DB_PASSWORD docker-compose up -d $NEXT

for i in {1..10}; do
  HEALTH=$(curl -s http://localhost:$NEXT_PORT/actuator/health | grep -o '"status":"UP"')
  if [ "$HEALTH" = '"status":"UP"' ]; then break; fi
  sleep 5
done

sudo sed -i "s/$PREV_PORT/$NEXT_PORT/" /etc/nginx/conf.d/skincare.conf
sudo nginx -s reload

docker-compose stop $PREV
