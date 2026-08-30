package quorum.storage;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import quorum.QuorumException;
import quorum.model.Roster;

class FileRosterStorageTest {
    @TempDir
    private Path tempDir;

    @Test
    void constructor_nullFileWithValidCodec_throwsNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new FileRosterStorage(null, new TsvRosterCodec()));

        assertNull(exception.getMessage());
    }

    @Test
    void constructor_nullCodecWithValidFile_throwsNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new FileRosterStorage(tempDir.resolve("roster.tsv"), null));

        assertNull(exception.getMessage());
    }

    @Test
    void load_missingFile_returnsEmptyRosterWithoutDecoding() throws QuorumException {
        RecordingCodec codec = new RecordingCodec();
        RosterStorage storage = new FileRosterStorage(
                tempDir.resolve("missing").resolve("roster.tsv"), codec);

        Roster roster = storage.load();

        assertEquals(0, roster.size());
        assertTrue(roster.asList().isEmpty());
        assertEquals(0, codec.decodeCalls);
        assertNull(codec.decodedContent);
    }

    @Test
    void load_existingUtf8File_decodesExactContentAndReturnsCodecRoster()
            throws IOException, QuorumException {
        Path file = tempDir.resolve("roster.tsv");
        String content = "name\tzoneId\ttags\nZo\u00eb\tEurope/Paris\t\u00c9QUIPE\n";
        Files.writeString(file, content, UTF_8);
        RecordingCodec codec = new RecordingCodec();
        Roster expected = new Roster();
        codec.decodedRoster = expected;
        RosterStorage storage = new FileRosterStorage(file, codec);

        Roster actual = storage.load();

        assertSame(expected, actual);
        assertEquals(1, codec.decodeCalls);
        assertEquals(content, codec.decodedContent);
    }

    @Test
    void load_codecRejectsFile_wrapsSpecificExceptionWithNormalizedAbsolutePath()
            throws IOException {
        Path suppliedFile = tempDir.resolve("unused").resolve("..").resolve("roster.tsv");
        Path normalizedFile = suppliedFile.toAbsolutePath().normalize();
        Files.writeString(normalizedFile, "invalid", UTF_8);
        RecordingCodec codec = new RecordingCodec();
        QuorumException decodeFailure = new QuorumException("bad header");
        codec.decodeException = decodeFailure;
        RosterStorage storage = new FileRosterStorage(suppliedFile, codec);

        QuorumException exception = assertThrows(QuorumException.class, storage::load);

        assertEquals("Invalid roster file at " + normalizedFile + ": bad header",
                exception.getMessage());
        assertSame(decodeFailure, exception.getCause());
        assertEquals("invalid", codec.decodedContent);
    }

    @Test
    void load_pathPointsToDirectory_wrapsReadExceptionWithoutDecoding()
            throws IOException {
        Path directory = tempDir.resolve("roster.tsv");
        Files.createDirectory(directory);
        RecordingCodec codec = new RecordingCodec();
        RosterStorage storage = new FileRosterStorage(directory, codec);

        QuorumException exception = assertThrows(QuorumException.class, storage::load);

        IOException cause = assertInstanceOf(IOException.class, exception.getCause());
        assertEquals("Could not read roster file at " + directory.toAbsolutePath().normalize()
                + ": " + cause.getMessage(), exception.getMessage());
        assertEquals(0, codec.decodeCalls);
    }

    @Test
    void save_missingParentDirectories_writesExactUtf8ContentFromCodec()
            throws IOException, QuorumException {
        Path file = tempDir.resolve("nested").resolve("data").resolve("roster.tsv");
        RecordingCodec codec = new RecordingCodec();
        codec.encodedContent =
                "name\tzoneId\ttags\nZo\u00eb\tEurope/Paris\t\u00c9QUIPE\n";
        RosterStorage storage = new FileRosterStorage(file, codec);
        Roster roster = new Roster();

        storage.save(roster);

        assertSame(roster, codec.encodedRoster);
        assertEquals(codec.encodedContent, Files.readString(file, UTF_8));
    }

    @Test
    void save_existingLongerFile_replacesAndTruncatesItsContent()
            throws IOException, QuorumException {
        Path file = tempDir.resolve("roster.tsv");
        Files.writeString(file, "obsolete content that is longer", UTF_8);
        RecordingCodec codec = new RecordingCodec();
        codec.encodedContent = "\u65b0\n";
        RosterStorage storage = new FileRosterStorage(file, codec);

        storage.save(new Roster());

        assertEquals("\u65b0\n", Files.readString(file, UTF_8));
    }

    @Test
    void save_parentPathIsFile_wrapsSpecificDirectoryCreationException()
            throws IOException {
        Path parentFile = tempDir.resolve("not-a-directory");
        Files.writeString(parentFile, "content", UTF_8);

        assertSaveIoFailure(parentFile.resolve("roster.tsv"));
    }

    @Test
    void save_targetPathIsDirectory_wrapsSpecificWriteException() throws IOException {
        Path directory = tempDir.resolve("roster.tsv");
        Files.createDirectory(directory);

        assertSaveIoFailure(directory);
    }

    private void assertSaveIoFailure(Path file) {
        RecordingCodec codec = new RecordingCodec();
        codec.encodedContent = "encoded";
        RosterStorage storage = new FileRosterStorage(file, codec);
        Roster roster = new Roster();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                storage.save(roster));

        IOException cause = assertInstanceOf(IOException.class, exception.getCause());
        assertEquals("Could not save roster file at " + file.toAbsolutePath().normalize()
                + ": " + cause.getMessage(), exception.getMessage());
        assertSame(roster, codec.encodedRoster);
    }

    private static final class RecordingCodec implements RosterCodec {
        private String encodedContent;
        private Roster decodedRoster = new Roster();
        private QuorumException decodeException;
        private Roster encodedRoster;
        private String decodedContent;
        private int decodeCalls;

        @Override
        public String encode(Roster roster) {
            encodedRoster = roster;
            return encodedContent;
        }

        @Override
        public Roster decode(String content) throws QuorumException {
            decodeCalls++;
            decodedContent = content;
            if (decodeException != null) {
                throw decodeException;
            }
            return decodedRoster;
        }
    }
}
