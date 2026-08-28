package quorum.storage;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import quorum.QuorumException;
import quorum.model.Roster;

/** Stores an encoded roster in a local file. */
public class FileRosterStorage implements RosterStorage {
    private final Path file;
    private final RosterCodec codec;

    /** Creates file storage at the given path using the given text codec. */
    public FileRosterStorage(Path file, RosterCodec codec) {
        this.file = Objects.requireNonNull(file).toAbsolutePath().normalize();
        this.codec = Objects.requireNonNull(codec);
    }

    @Override
    public Roster load() throws QuorumException {
        if (Files.notExists(file)) {
            return new Roster();
        }

        try {
            return codec.decode(Files.readString(file, UTF_8));
        } catch (QuorumException e) {
            throw new QuorumException(
                    "Invalid roster file at " + file + ": " + e.getMessage(), e);
        } catch (IOException e) {
            throw new QuorumException(
                    "Could not read roster file at " + file + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void save(Roster roster) throws QuorumException {
        String content = codec.encode(roster);

        Path directory = file.getParent();
        try {
            Files.createDirectories(directory);
            // TODO: Use a temporary file and atomic replacement to prevent partial writes.
            Files.writeString(file, content, UTF_8);
        } catch (IOException e) {
            throw new QuorumException(
                    "Could not save roster file at " + file + ": " + e.getMessage(), e);
        }
    }
}
