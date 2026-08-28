package quorum.command;

import quorum.model.Tag;

/** Holds the participant index and normalized tag for a tag mutation. */
public record TagRequest(int index, Tag tag) {
}
