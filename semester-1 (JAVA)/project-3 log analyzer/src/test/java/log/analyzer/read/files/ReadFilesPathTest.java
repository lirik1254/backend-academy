package log.analyzer.read.files;

import log.analyzer.TestUtils;
import log.analyzer.nginx.parse.NginxFileParseData;
import log.analyzer.nginx.parse.NginxParseData;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ReadFilesPathTest {

    @BeforeAll
    public static void createSimplePathFile() throws IOException {
        try (FileWriter fileWriter = new FileWriter("simplePath.txt")) {
            fileWriter.write(TestUtils.CORRECT_LINE);
        }
    }

    @BeforeAll
    public static void createHardPathFiles() throws IOException {
        File directory1 = new File("directory1");
        File directory2 = new File("directory1", "directory2");
        File directory3 = new File("directory1/directory2", "directory3");
        File directory12 = new File("directory1", "directory12");
        File directory3After12 = new File("directory1/directory12", "directory3");
        directory1.mkdir();
        directory2.mkdir();
        directory3.mkdir();
        directory12.mkdir();
        directory3After12.mkdir();

        try (FileWriter fileWriter = new FileWriter("directory1/directory2/directory3/hardPath.txt")) {
            fileWriter.write(TestUtils.CORRECT_LINE);
        }
        try (FileWriter fileWriter = new FileWriter("directory1/directory2/directory3/hardPath2.txt")) {
            fileWriter.write(TestUtils.CORRECT_LINE);
        }
        try (FileWriter fileWriter = new FileWriter("directory1/directory12/directory3/hardPath3.txt")) {
            fileWriter.write(TestUtils.CORRECT_LINE);
        }
        try (FileWriter fileWriter = new FileWriter("directory1/badFile.txt")) {
            fileWriter.write(TestUtils.CORRECT_LINE);
        }
    }

    @AfterAll
    public static void deleteSimplePathFile() throws IOException {
        Files.delete(Path.of("simplePath.txt"));
    }

    @AfterAll
    public static void deleteHardPathFiles() throws IOException {
        FileUtils.deleteDirectory(new File("directory1"));
    }

    @Test
    public void readFromSimplePathTest() {
        NginxParseData nginxFileParseData = new NginxFileParseData("simplePath.txt");
        assertFalse(nginxFileParseData.getNginxList().isEmpty());
    }

    @Test
    public void readFromHardPathTest() {
        NginxParseData nginxFileParseData = new NginxFileParseData("directory1/**/directory3/*");
        assertEquals(nginxFileParseData.getNginxList().size(), 3);
    }

}
