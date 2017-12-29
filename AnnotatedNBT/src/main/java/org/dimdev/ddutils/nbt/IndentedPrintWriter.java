package org.dimdev.ddutils.nbt;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;

public class IndentedPrintWriter extends PrintWriter { // TODO: verify indentation using a marking system when passing?

    private int indent = 0;
    private boolean startOfLine;

    public IndentedPrintWriter(Writer out) {
        super(out);
    }

    public void indent(int n) {
        if (indent + n < 0) throw new IllegalArgumentException("Can't set indentation to less than 0!");
        indent += n;
    }

    public void unindent(int n) {
        indent(-n);
    }

    public void indent() {
        indent(4);
    }

    public void unindent() {
        indent(-4);
    }

    @Override
    public void print(String s) {
        if (startOfLine) {
            super.print(String.join("", Collections.nCopies(indent, " ")));
        }
        super.print(s);
        startOfLine = false;
    }

    @Override
    public void println() {
        super.println();
        startOfLine = true;
    }
}