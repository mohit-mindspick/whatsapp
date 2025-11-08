#!/bin/bash
echo "Starting location service with PostgreSQL database..."
./gradlew bootRun --args='--spring.profiles.active=postgres'
