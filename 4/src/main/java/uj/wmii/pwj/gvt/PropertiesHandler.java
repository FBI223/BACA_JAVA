//Marcin Sztukowski

package uj.wmii.pwj.gvt;

import java.nio.file.*;
import java.io.IOException;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

public class PropertiesHandler {
    private static final Path GVT_DIR = Paths.get(".gvt");
    private static final Path VERSIONS_DIR = GVT_DIR.resolve("versions");
    private static final Path FILES_DIR = GVT_DIR.resolve("files");
    private static final Path HEAD_DIR = GVT_DIR.resolve("head");
    private static final Path HEAD_VERSION_FILE = GVT_DIR.resolve("head_version_id");
    private static final Path CHECKOUT_VERSION_FILE = HEAD_DIR.resolve("checkout_version_id");

    private static ExitHandler exitHandler;

    public static void initializeExitHandler(ExitHandler handler) {
        exitHandler = handler;
    }

    public static void showLastNVersions(int n) throws IOException {
        int currentVersion = getCurrentVersion();
        int startVersion = Math.max(currentVersion - n + 1, 0);

        StringBuilder result = new StringBuilder();

        for (int version = currentVersion; version >= startVersion; version--)
        {
            Path versionFile = VERSIONS_DIR.resolve("version_" + version + ".properties");


            Properties props = new Properties();
            try (var in = Files.newInputStream(versionFile)) {
                props.load(in);
            }

            String defaultMessage = props.getProperty("default_message", "");
            String optionalMessage = props.getProperty("optional_message", "");

            result.append(version).append(": ");
            String commitMessage = !optionalMessage.isEmpty() ? optionalMessage : defaultMessage;
            String firstLine = commitMessage.split("\n")[0];
            result.append(firstLine).append("\n");
        }
        exitHandler.exit(0, result.toString() );

    }

    public static void showAllNVersions() throws IOException {
        int currentVersion = getCurrentVersion();
        StringBuilder result = new StringBuilder();

        for (int version = currentVersion; version >= 0 ; version--)
        {
            Path versionFile = VERSIONS_DIR.resolve("version_" + version + ".properties");


            Properties props = new Properties();
            try (var in = Files.newInputStream(versionFile)) {
                props.load(in);
            }

            String defaultMessage = props.getProperty("default_message", "");
            String optionalMessage = props.getProperty("optional_message", "");


            result.append(version).append(": ");
            String commitMessage = !optionalMessage.isEmpty() ? optionalMessage : defaultMessage;
            String firstLine = commitMessage.split("\n")[0];
            result.append(firstLine).append("\n");
        }
        exitHandler.exit(0, result.toString() );
    }

    public static String[] checkVersion(String versionNumber) throws IOException {
        Path versionFilePath = VERSIONS_DIR.resolve("version_" + versionNumber + ".properties");

        if (!Files.exists(versionFilePath)) {
            exitHandler.exit(60,"Invalid version number: " + versionNumber + '.');
            return null;
        }

        Properties props = new Properties();
        try (var in = Files.newInputStream(versionFilePath)) {
            props.load(in);
        }

        String defaultMessage = props.getProperty("default_message", "");
        String optionalMessage = props.getProperty("optional_message", "");

        return new String[]{defaultMessage, optionalMessage};
    }

    public static void showVersionDetails(String versionNumber) throws IOException {
        try {
            if (versionNumber.equals("") ) {
                versionNumber = Integer.toString(getCurrentVersion()) ;
            }


            String[] messages = checkVersion(versionNumber);
            String defaultMessage = messages[0];
            String optionalMessage = messages[1];

            StringBuilder versionInfo = new StringBuilder();
            versionInfo.append("Version: ").append(versionNumber).append("\n");


            if (!optionalMessage.trim().isEmpty()) {
                versionInfo.append(optionalMessage);
            } else if (!defaultMessage.trim().isEmpty() ) {
                versionInfo.append(defaultMessage);
            }

            exitHandler.exit(0,versionInfo.toString());

        } catch (IOException e) {
            e.printStackTrace();
            exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
        }
    }

    public static Properties loadVersion(int versionNumber) throws IOException {
        Properties props = new Properties();
        Path versionPath = VERSIONS_DIR.resolve("version_" + versionNumber + ".properties");

        try (var in = Files.newInputStream(versionPath, StandardOpenOption.READ)) {
            props.load(in);
        }
        return props;
    }

    public static int getCurrentVersion() throws IOException {
        return Integer.parseInt(Files.readString(HEAD_VERSION_FILE).trim());
    }

    public static void incrementCurrentVersion() throws IOException {
        int currentVersion = getCurrentVersion();
        currentVersion++;
        Files.writeString(HEAD_VERSION_FILE, String.valueOf(currentVersion));
    }

    public static int getCurrentVersionCheckout() throws IOException {
        return Integer.parseInt(Files.readString(CHECKOUT_VERSION_FILE).trim());
    }

    public static void incrementCurrentVersionCheckout() throws IOException {
        int currentVersion = getCurrentVersionCheckout();
        currentVersion++;
        Files.writeString(CHECKOUT_VERSION_FILE, String.valueOf(currentVersion));
    }

    public static void checkAndCopyHeadVersion() throws IOException {
        if (getCurrentVersion() != getCurrentVersionCheckout()) {
            checkout(Integer.toString(getCurrentVersion()));

            Files.copy(
                    HEAD_VERSION_FILE,
                    CHECKOUT_VERSION_FILE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    public static void init() throws IOException {

        if (Files.exists(GVT_DIR)) {
            exitHandler.exit(10, "Current directory is already initialized.");
            return;
        }


        Files.createDirectories(GVT_DIR);
        Files.createDirectories(VERSIONS_DIR);
        Files.createDirectories(FILES_DIR);
        Path headDir = GVT_DIR.resolve("head");
        Files.createDirectories(headDir);
        Files.writeString(HEAD_VERSION_FILE, "0");
        Files.writeString(CHECKOUT_VERSION_FILE, "0");
        saveVersion(0, "GVT initialized.", "", new HashMap<>());
        exitHandler.exit(0, "Current directory initialized successfully." );
    }

    public static void detach(String fileName, String optMessage) throws IOException {
        int currentVersion = getCurrentVersion();
        Properties latestProps = loadVersion(currentVersion);

        if (!latestProps.containsKey(fileName)) {
            exitHandler.exit(0, "File is not added to gvt. File: " + fileName);
            return;
        }

        checkAndCopyHeadVersion();


        Map<String, String> updatedFiles = new HashMap<>();
        latestProps.stringPropertyNames().stream()
                .filter(key -> !key.equals("default_message") && !key.equals("optional_message") && !key.equals("version") && !key.equals(fileName))
                .forEach(key -> updatedFiles.put(key, latestProps.getProperty(key)));

        saveVersion(currentVersion + 1, "File detached successfully. File: " + fileName, optMessage, updatedFiles);
        incrementCurrentVersion();
        incrementCurrentVersionCheckout();
        exitHandler.exit(0, "File detached successfully. File: " + fileName);
    }
    public static void commit(String fileName, String optMessage) throws IOException {
        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            exitHandler.exit(51, "File not found. File: " + fileName);
            return;
        }


        checkAndCopyHeadVersion();

        int currentVersion = getCurrentVersion();
        Properties latestProps = loadVersion(currentVersion);

        if (latestProps.containsKey(fileName)) {
            Map<String, String> updatedFiles = new HashMap<>();
            latestProps.stringPropertyNames().stream()
                    .filter(key -> !key.equals("default_message") && !key.equals("optional_message") && !key.equals("version"))
                    .forEach(key -> updatedFiles.put(key, latestProps.getProperty(key)));


            Path versionedFilePath = FILES_DIR.resolve(fileName).resolve("v" + (currentVersion + 1));
            Files.createDirectories(versionedFilePath.getParent());
            Files.copy(filePath, versionedFilePath, StandardCopyOption.REPLACE_EXISTING);
            updatedFiles.put(fileName, versionedFilePath.toString());

            Path headFilePath = HEAD_DIR.resolve(fileName);
            Files.copy(filePath, headFilePath, StandardCopyOption.REPLACE_EXISTING);

            saveVersion(currentVersion + 1, "File committed successfully. File: " + fileName, optMessage, updatedFiles);
            incrementCurrentVersion();
            incrementCurrentVersionCheckout();
            exitHandler.exit(0, "File committed successfully. File: " + fileName);
        } else {
            exitHandler.exit(0, "File is not added to gvt. File: " + fileName);
        }
    }

    public static void add(String fileName, String optMessage) throws IOException {
        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            exitHandler.exit(21, "File not found. File: " + fileName);
            return;
        }

        checkAndCopyHeadVersion();


        int currentVersion = getCurrentVersion();
        Properties latestProps = loadVersion(currentVersion);

        if (!latestProps.containsKey(fileName)) {
            Map<String, String> updatedFiles = new HashMap<>();
            latestProps.stringPropertyNames().stream()
                    .filter(key -> !key.equals("default_message") && !key.equals("optional_message") && !key.equals("version"))
                    .forEach(key -> updatedFiles.put(key, latestProps.getProperty(key)));

            Path versionedFilePath = FILES_DIR.resolve(fileName).resolve("v" + (currentVersion + 1));
            Files.createDirectories(versionedFilePath.getParent());
            Files.copy(filePath, versionedFilePath, StandardCopyOption.REPLACE_EXISTING);
            updatedFiles.put(fileName, versionedFilePath.toString());

            Path headFilePath = HEAD_DIR.resolve(fileName);
            Files.copy(filePath, headFilePath, StandardCopyOption.REPLACE_EXISTING);

            saveVersion(currentVersion + 1, "File added successfully. File: " + fileName, optMessage, updatedFiles);
            incrementCurrentVersion();
            incrementCurrentVersionCheckout();
            exitHandler.exit(0, "File added successfully. File: " + fileName);
        } else {
            exitHandler.exit(0, "File already added. File: " + fileName);
        }
    }

    public static void checkout(String versionNumber) throws IOException {
        Path versionFilePath = VERSIONS_DIR.resolve("version_" + versionNumber + ".properties");

        if (!Files.exists(versionFilePath)) {
            exitHandler.exit(60, "Invalid version number: " + versionNumber);
            return;
        }

        Properties props = new Properties();
        try (var in = Files.newInputStream(versionFilePath)) {
            props.load(in);
        }

        clearDirectory(HEAD_DIR);
        Files.writeString(CHECKOUT_VERSION_FILE, versionNumber, StandardOpenOption.CREATE );

        for (String key : props.stringPropertyNames()) {
            if (key.equals("default_message") || key.equals("optional_message") || key.equals("version")) {
                continue;
            }

            Path versionedFilePath = Path.of(props.getProperty(key));
            Path headTargetPath = HEAD_DIR.resolve(key);
            Path projectTargetPath = Paths.get("").resolve(key);


            if (Files.exists(versionedFilePath)) {
                Files.copy(versionedFilePath, headTargetPath, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(versionedFilePath, projectTargetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        exitHandler.exit(0, "Checkout successful for version: " + versionNumber);
    }

    public static void clearDirectory(Path directory) throws IOException {
        Files.walk(directory, 1)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static void saveVersion(int versionNumber, String defMessage, String optMessage, Map<String, String> filePaths) throws IOException {
        Properties props = new Properties();
        props.setProperty("version", String.valueOf(versionNumber));
        props.setProperty("default_message", defMessage);
        props.setProperty("optional_message", optMessage);

        //filePaths.forEach(props::setProperty);
        for (Map.Entry<String, String> entry : filePaths.entrySet()) {
            props.setProperty(entry.getKey(), entry.getValue());
        }


        Path versionPath = VERSIONS_DIR.resolve("version_" + versionNumber + ".properties");
        try (var out = Files.newOutputStream(versionPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            props.store(out, "Version Metadata for version " + versionNumber);
        }
    }

}

