package com.ankamagames.dofus.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.ankamagames.dofus.network.types.version.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class FilesUtils {

    private static final Logger log = Logger.getLogger(FilesUtils.class);

    private static String outputDirectory = "output/";

    /**
     * Private constructor.
     */
    private FilesUtils() {

    }

    /**
     * Get data out of the MessageNameId json file
     *
     * @return The data
     */
    public static Map<String, String> parseMessageNameId() {
        InputStream input = FilesUtils.class.getClassLoader().getResourceAsStream("MessageNameId.json");
        try {
            String result = IOUtils.toString(input, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
            };
            return mapper.readValue(result, typeRef);
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }

    /**
     * Get data out of the TypeNameId json file
     *
     * @return The data
     */
    public static Map<String, String> parseTypeNameId() {
        InputStream input = FilesUtils.class.getClassLoader().getResourceAsStream("TypeNameId.json");
        try {
            String result = IOUtils.toString(input, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
            };
            return mapper.readValue(result, typeRef);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }


    /**
     * Get data out of the Version json file
     *
     * @return The data
     * @throws IOException Exception if the process fail
     */
    public static Version getVersion() throws IOException {
        InputStream input = FilesUtils.class.getClassLoader().getResourceAsStream("Version.json");
        String result = IOUtils.toString(input, "UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result, Version.class);
    }


    /**
     * Write content in a file
     *
     * @param content The content to write in a file
     * @param path    The path to the file
     */
    public static void writeFile(String content, String path) throws IOException {
        File file = new File(Paths.get(outputDirectory + path).getParent().toString());
        if (!file.exists()) file.mkdirs();
        Files.write(Paths.get(outputDirectory + path), content.getBytes());

    }

    /**
     * Read file
     *
     * @param url Url of the file
     * @return File as a String
     */
    public static String readFile(URL url) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(url.toURI())));
    }

    public static void setOutputDirectory(String outputDirectory) {
        if (!outputDirectory.endsWith("/")) {
            outputDirectory += "/";
        }

        FilesUtils.outputDirectory = outputDirectory;
    }
}
