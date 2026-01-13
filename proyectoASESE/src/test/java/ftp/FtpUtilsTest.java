package ftp;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FtpUtilsTest {
    @ParameterizedTest
    @MethodSource("dataForGetParentPath")
    void testParentPath(String path, String expected) {
        assertEquals(expected, FtpUtils.getParentPath(path));
    }

    @ParameterizedTest
    @MethodSource("dataForGetUsernamePath")
    void testGetUsernameFromPath(String path, String expected) {
        assertEquals(expected, FtpUtils.getUsernameFromPath(path));
    }

    @ParameterizedTest
    @MethodSource("dataForGetChildPath")
    void testGetChildPath(String path, String expected) {
        assertEquals(expected, FtpUtils.getChildPath(path));
    }

    static Object[][] dataForGetParentPath() {
        return new Object[][] {
                {"", ""},
                {"/", "/"},
                {"/ftp", "/"},
                {"/ftp/", "/"},
                {"/ftp/pepe", "/ftp"},
                {"/ftp/pepe/", "/ftp"},
        };
    }

    static Object[][] dataForGetUsernamePath() {
        return new Object[][] {
                {"", null},
                {"/", null},
                {"/ftp", null},
                {"/ftp/", null},
                {"/not", null},
                {"/not/the/ftp", null},
                {"/ftp/pepe", "pepe"},
                {"/ftp/pepe/", "pepe"},
                {"/ftp/pepe/some", "pepe"},
                {"/ftp/pepe/some/", "pepe"},
                {"/ftp/pepe/some/file", "pepe"},
                {"/ftp/pepe/some/file/", "pepe"},
        };
    }

    static Object[][] dataForGetChildPath() {
        return new Object[][] {
                {"", ""},
                {"/", "/"},
                {"/ftp", "ftp"},
                {"/ftp/", "ftp"},
                {"/ftp/pepe", "pepe"},
                {"/ftp/pepe/", "pepe"},
                {"/ftp/pepe/some", "some"},
                {"/ftp/pepe/some/", "some"},
                {"/ftp/pepe/some/file", "file"},
                {"/ftp/pepe/some/file/", "file"},
                {"/file/with some/ spaces", " spaces"},
                {"/file/with some/ spaces   ", " spaces   "},
        };
    }
}
