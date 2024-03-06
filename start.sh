#!/bin/bash


usage() {
    echo "Usage: $0 numPartitions replicationFactor numBrokers [--rackAware brokerId,rack ...]"
    echo "  numPartitions: The number of partitions."
    echo "  replicationFactor: The replication factor for each partition."
    echo "  numBrokers: The number of brokers."
    echo "  --rackAware: Optional flag followed by space-separated brokerId,rack pairs to simulate rack-aware replica assignment."
    exit 1
}
# Build the project and create the shadow jar
./gradlew shadowJar

# Execute the jar with command-line arguments
java -jar build/libs/replicaPlacementDemo-1.0-SNAPSHOT-all.jar "$@"