
# Simulate Replicas Assignment Project

This Java project demonstrates a simulation of default Kafka replica assignments with optional rack awareness.
It doesn't take into account arguments supplied to kafka-topics.sh script (ex: --replica-assignment)
or any use of tools like Kafka's `kafka-reassign-partitions.sh` script, confluent self balancing or confluent auto data balancer.
## Requirements

- Java JDK 8 or later
- Gradle

## Building the Project

To build the project and create an executable jar, run the `start.sh` script from the command line:

```
./start.sh
```

This script automates the build process and execution of the application.

## Usage

```
./start.sh numPartitions replicationFactor numBrokers [--rackAware brokerId,rack ...]
```

- `numPartitions`: The number of partitions.
- `replicationFactor`: The replication factor for each partition.
- `numBrokers`: The number of brokers.
- `--rackAware`: (Optional) Followed by space-separated brokerId,rack pairs to simulate rack-aware replica assignment.

### Example

```
./start.sh 10 3 5 --rackAware 0,r1 1,r3 2,r3 3,r2 4,r2 5,r1
```

#### This example simulates replica assignment for 3 partitions, a replication factor of 4, and 6 brokers with specified rack information.
and displays the following:
Configuration:
---------------
numPartitions: 3
replicationFactor: 4
numBrokers: 6
Rack Awareness: true
Broker 0 -> Rack: r1
Broker 1 -> Rack: r1
Broker 2 -> Rack: r1
Broker 3 -> Rack: r2
Broker 4 -> Rack: r2
Broker 5 -> Rack: r2
---------------
Simulating Replica Assignment for 3 partitions, replication factor of 4 and 6 brokers

With Rack Awareness:
Partition 0 -> Replicas: [0, 1, 3, 5]
Partition 1 -> Replicas: [0, 1, 3, 4]
Partition 2 -> Replicas: [1, 2, 3, 4]

Rack: r1
+--------+--------+--------+
|broker 0|broker 1|broker 2|
+--------+--------+--------+
|p0      |p0      |        |
|p1      |p1      |        |
|        |p2      |p2      |

Rack: r2
+--------+--------+--------+
|broker 3|broker 4|broker 5|
+--------+--------+--------+
|p0      |        |p0      |
|p1      |p1      |        |
|p2      |p2      |        |


#### An example without Rack Awareness:
```
./start.sh 10 3 5
```
will display:
Configuration:
---------------
numPartitions: 10
replicationFactor: 3
numBrokers: 5
Rack Awareness: false
---------------
Simulating Replica Assignment for 10 partitions, replication factor of 3 and 5 brokers
Without Rack Awareness:
Partition 0 -> Replicas: [1, 2, 4]
Partition 1 -> Replicas: [0, 2, 3]
Partition 2 -> Replicas: [1, 3, 4]
Partition 3 -> Replicas: [0, 2, 4]
Partition 4 -> Replicas: [0, 1, 3]
Partition 5 -> Replicas: [2, 3, 4]
Partition 6 -> Replicas: [0, 3, 4]
Partition 7 -> Replicas: [0, 1, 4]
Partition 8 -> Replicas: [0, 1, 2]
Partition 9 -> Replicas: [1, 2, 3]

Rack: default-rack
+--------+--------+--------+--------+--------+
|broker 0|broker 1|broker 2|broker 3|broker 4|
+--------+--------+--------+--------+--------+
|        |p0      |p0      |        |p0      |
|p1      |        |p1      |p1      |        |
|        |p2      |        |p2      |p2      |
|p3      |        |p3      |        |p3      |
|p4      |p4      |        |p4      |        |
|        |        |p5      |p5      |p5      |
|p6      |        |        |p6      |p6      |
|p7      |p7      |        |        |p7      |
|p8      |p8      |p8      |        |        |
|        |p9      |p9      |p9      |        |


## Implementation Details

The program, `SimulateReplicasAssignment`, processes the input parameters to simulate the assignment of Kafka replicas either with or without rack awareness based on the provided arguments. 
It uses AdminUtils.assignReplicasToBrokers() method to simulate the replica assignment.

## Note

Make sure to adjust the path in `start.sh` to match the jar file name basing on your build configuration.
