
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Scanner;

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
