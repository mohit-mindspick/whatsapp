#!/bin/bash

# Docker Build Script for WhatsApp Service
# This script builds the project and creates a Docker image

set -e

echo "Building WhatsApp Service..."

# Build the project first
echo "Step 1: Building the project with Gradle..."
./gradlew clean build -x test

# Check if JAR file exists
JAR_FILE=$(find build/libs -name "whatsapp-*.jar" | head -1)
if [ -z "$JAR_FILE" ]; then
    echo "Error: JAR file not found in build/libs/"
    exit 1
fi

echo "Found JAR file: $JAR_FILE"

# Build Docker image
echo "Step 2: Building Docker image..."
docker build --platform linux/amd64 --build-arg TARGETPLATFORM=linux/amd64 -t an-whatsapp-dev:latest .

echo "Docker image built successfully!"
echo "Image name: an-whatsapp-dev:latest"
echo ""

# Tag for ECR (optional)
if [ "$1" = "--ecr" ]; then
    ECR_REGISTRY="810278669336.dkr.ecr.ap-south-1.amazonaws.com"
    ECR_IMAGE="$ECR_REGISTRY/an-whatsapp-dev:latest"
    
    echo "Step 3: Tagging image for ECR..."
    docker tag an-whatsapp-dev:latest $ECR_IMAGE
    
    echo "ECR image tagged successfully!"
    echo "ECR image: $ECR_IMAGE"
    echo ""
    echo "Step 4: Logging in to ECR..."
    aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin $ECR_REGISTRY
    
    echo "Step 5: Pushing image to ECR..."
    docker push $ECR_IMAGE
    
    echo "Image pushed to ECR successfully!"
    echo ""
fi

echo "To run the container:"
echo "  docker run -p 9001:9001 an-whatsapp-dev:latest"
echo ""
echo "To run with environment variables:"
echo "  docker run -p 9001:9001 -e DB_HOST=your-db-host -e DB_PASSWORD=your-password an-whatsapp-dev:latest"
