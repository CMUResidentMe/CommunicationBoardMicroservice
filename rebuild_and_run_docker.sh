#!/bin/bash

# Name of Docker image
IMAGE_NAME="comm-board-service"

# Name of Docker container
CONTAINER_NAME="comm-board-service"

# Port mapping (host:container)
PORT_MAPPING="6000:6000"

# Stop the currently running container
echo "Stopping existing container..."
docker stop $CONTAINER_NAME

# Remove the stopped container
echo "Removing existing container..."
docker rm $CONTAINER_NAME

# Rebuild the Docker image with no cache
echo "Building new Docker image..."
docker build -t $IMAGE_NAME:latest . --no-cache

# Run a new container from the rebuilt image
echo "Running new container..."
docker run --name $CONTAINER_NAME -p $PORT_MAPPING $IMAGE_NAME
