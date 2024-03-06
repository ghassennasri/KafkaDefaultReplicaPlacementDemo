package org.example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.kafka.admin.AdminUtils;
import org.apache.kafka.admin.BrokerMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SimulateReplicasAssignment{
    private static final Logger logger = LogManager.getLogger(SimulateReplicasAssignment.class);

    public static void main(String[] args) {
        if (args.length < 3) {
            logger.info("Usage: java SimulateReplicasAssignment numPartitions replicationFactor numBrokers [--rackAware brokerId,rack ...]");
            return;
        }
        //afficher la configuration table
        logger.info("Configuration:");
        logger.info("---------------");
        logger.info("numPartitions: {}", args[0]);
        logger.info("replicationFactor: {}", args[1]);
        logger.info("numBrokers: {}", args[2]);
        if (args.length > 3 && "--rackaware".equalsIgnoreCase(args[3])) {
            logger.info("Rack Awareness: true");
            for (int i = 4; i < args.length; i++) {
                String[] parts = args[i].split(",");
                if (parts.length == 2) {
                    int brokerId = Integer.parseInt(parts[0]);
                    String rack = parts[1];
                    logger.info("Broker {} -> Rack: {}", brokerId, rack);
                }
            }
        } else {
            logger.info("Rack Awareness: false");
        }
        logger.info("---------------");


        int numPartitions = Integer.parseInt(args[0]);
        int replicationFactor = Integer.parseInt(args[1]);
        int numBrokers = Integer.parseInt(args[2]);
        List<BrokerMetadata> brokerMetadatas = new ArrayList<>();
        logger.info("Simulating Replica Assignment for {} partitions, replication factor of {} and {} brokers", numPartitions, replicationFactor, numBrokers);

        // if rack awareness is specified
        if (args.length > 3 && "--rackaware".equalsIgnoreCase(args[3])) {
            for (int i = 4; i < args.length; i++) {
                String[] parts = args[i].split(",");
                if (parts.length == 2) {
                    int brokerId = Integer.parseInt(parts[0]);
                    String rack = parts[1];
                    brokerMetadatas.add(new BrokerMetadata(brokerId, Optional.of(rack)));
                }
            }
            logger.info("\nWith Rack Awareness:");
            
        } else {
            // remplir la liste des brokers sans rack
            for (int i = 0; i < numBrokers; i++) {
                brokerMetadatas.add(new BrokerMetadata(i, Optional.empty()));
            }
            logger.info("Without Rack Awareness:");
        }
        assignReplicas(brokerMetadatas, numPartitions, replicationFactor);

    }

    private static void assignReplicas(List<BrokerMetadata> brokerMetadatas, int nPartitions, int replicationFactor) {
        // Placeholder for the actual AdminUtils call
        Map<Integer, List<Integer>> assignment = AdminUtils.assignReplicasToBrokers(brokerMetadatas, nPartitions,
                replicationFactor);
        assignment.forEach((partitionId, replicas) -> logger.info("Partition {} -> Replicas: {}", partitionId, replicas));
        formatAndDisplayAssignment(assignment, brokerMetadatas);
    }



    public static void formatAndDisplayAssignment(Map<Integer, List<Integer>> assignment, List<BrokerMetadata> brokerMetadatas) {
        // Group brokers by rack maintaining sort order by broker id
        Map<String, List<BrokerMetadata>> brokersByRack = brokerMetadatas.stream()
                .sorted(Comparator.comparingInt(b -> b.id))
                .collect(Collectors.groupingBy(
                        b -> b.rack.orElse("default-rack"),
                        LinkedHashMap::new, // LinkedHashMap to maintain order
                        Collectors.toList()));

        // maximum length for rack and broker formatting
        int maxRackLength = brokersByRack.keySet().stream().mapToInt(String::length).max().orElse(12) + 4; // 4  espace supplémentaires
        int maxBrokerIdLength = brokerMetadatas.stream().mapToInt(b -> Integer.toString(b.id).length()).max().orElse(1) + 7; // "broker ".length() + 1

        brokersByRack.forEach((rack, brokers) -> {
            String border = printBorder(brokers.size(), maxBrokerIdLength);
            logger.info("\nRack: {}", rack);
            logger.info(border);
            // afficahage Broker ids
            String brokerLine = "|";
            for (BrokerMetadata broker : brokers) {
                brokerLine += String.format("%-" + maxBrokerIdLength + "s|", "broker " + broker.id);
            }
            logger.info(brokerLine);
            logger.info(border);

            // pour chaque partition,si elle est dans le rack, on affiche p+partition sinon on affiche rien
            assignment.forEach((partition, replicas) -> {
                StringBuilder row = new StringBuilder("|");
                brokers.forEach(broker -> {
                    String cell = replicas.contains(broker.id) ? "p" + partition : "";
                    row.append(String.format("%-" + maxBrokerIdLength + "s|", cell));
                });
                logger.info(row);
            });

            printBorder(brokers.size(), maxBrokerIdLength);
        });
    }

    private static String printBorder(int numBrokers, int maxBrokerIdLength) {
        StringBuilder border = new StringBuilder();
        for (int i = 0; i < numBrokers; i++) {
            border.append("+");
            for (int j = 0; j < maxBrokerIdLength; j++) {
                border.append("-");
            }
        }
        border.append("+");
        return border.toString();
    }
 

    

}
