package com.example.database_version_management_system;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class VersionManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(VersionManagementSystemApplication.class, args);
    }

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }
}

@Component
class VersionManagementRunner implements CommandLineRunner {

    @Autowired
    private VersionService versionService;

    @Autowired
    private Scanner scanner;

    @Override
    public void run(String... args) {
        System.out.println("Welcome to the Version Management System!");

        String connectionURI = getPropertyValueFromUser("MongoDB connection URI: ");
        String databaseName = getPropertyValueFromUser("MongoDB database name: ");
        String collectionName = getPropertyValueFromUser("MongoDB collection name: ");

        versionService.setMongoDBDetails(connectionURI, databaseName, collectionName);

        while (true) {
            System.out.println("\nPlease select an option:");
            System.out.println("1. Create new version");
            System.out.println("2. View version history");
            System.out.println("3. Rollback to a previous version");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    versionService.createNewVersion();
                    break;
                case "2":
                    versionService.viewVersionHistory();
                    break;
                case "3":
                    versionService.rollbackToPreviousVersion();
                    break;
                case "4":
                    System.out.println("Exiting the Version Management System...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private String getPropertyValueFromUser(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}

@Component
class VersionService {

    private static List<String> versionHistory = new ArrayList<>();
    private static int lastVersionId = 0;
    private static int currentVersionId = 0;

    @Autowired
    private MongoTemplate mongoTemplate;

    private String connectionURI;
    private String databaseName;
    private String collectionName;

    @Autowired
    private Scanner scanner;

    public void setMongoDBDetails(String connectionURI, String databaseName, String collectionName) {
        this.connectionURI = connectionURI;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
    }

    public void createNewVersion() {
        System.out.print("Enter a description for the new version: ");
        String versionDescription = scanner.nextLine();

        // Create a new version document
        Document versionDocument = new Document();
        versionDocument.append("versionId", currentVersionId + 1);
        versionDocument.append("description", versionDescription);

        // Insert the version document into the collection
        mongoTemplate.insert(versionDocument, collectionName);

        // Update the version history and version ids
        versionHistory.add(versionDescription);
        lastVersionId = currentVersionId;
        currentVersionId++;

        System.out.println("New version created successfully!");
    }

    public void viewVersionHistory() {
        if (versionHistory.isEmpty()) {
            System.out.println("No version history available.");
        } else {
            System.out.println("Version History:");
            for (int i = 0; i < versionHistory.size(); i++) {
                System.out.println((i + 1) + ". " + versionHistory.get(i));
            }
        }
    }

    public void rollbackToPreviousVersion() {
        if (versionHistory.isEmpty()) {
            System.out.println("No version available to rollback.");
            return;
        }

        System.out.println("Select a version to rollback:");
        for (int i = 0; i < versionHistory.size(); i++) {
            System.out.println((i + 1) + ". " + versionHistory.get(i));
        }

        System.out.print("Enter the version number: ");
        int versionNumber = Integer.parseInt(scanner.nextLine());

        // Delete the versions after the selected version
        int rollbackVersionId = versionNumber;
        int versionsToDelete = currentVersionId - rollbackVersionId;
        for (int i = 0; i < versionsToDelete; i++) {
            mongoTemplate.remove(new Query(Criteria.where("versionId").is(rollbackVersionId + i + 1)), collectionName);
        }

        // Update the version history and version ids
        versionHistory.subList(versionNumber, versionHistory.size()).clear();
        lastVersionId = rollbackVersionId - 1;
        currentVersionId = rollbackVersionId;

        System.out.println("Rollback successful!");
    }
}
