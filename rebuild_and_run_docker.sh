IMAGE_NAME="comm-board-service"

CONTAINER_NAME="comm-board-service"

PORT_MAPPING="6000:6000"

echo "Stopping existing container..."
docker stop $CONTAINER_NAME

echo "Removing existing container..."
docker rm $CONTAINER_NAME

mvn clean package

echo "Building new Docker image..."
docker build -t $IMAGE_NAME .

echo "Running new container..."
docker run --name $CONTAINER_NAME -p $PORT_MAPPING $IMAGE_NAME
