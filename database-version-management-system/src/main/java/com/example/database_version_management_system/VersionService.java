package com.example.database_version_management_system;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class VersionService {

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
