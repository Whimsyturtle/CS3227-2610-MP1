package quorum.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import quorum.ui.Ui;

/** Provides command tests with a UI that performs no console I/O. */
abstract class SilentUi extends Ui {
    SilentUi() {
        super(InputStream.nullInputStream(),
                new PrintStream(OutputStream.nullOutputStream()));
    }
}
