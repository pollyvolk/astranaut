/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astgen.interpreter;

import java.io.File;
import org.cqfn.astgen.base.Node;
import org.cqfn.astgen.exceptions.DestinationNotSpecified;
import org.cqfn.astgen.exceptions.InterpreterCouldNotWriteFile;
import org.cqfn.astgen.exceptions.InterpreterException;
import org.cqfn.astgen.exceptions.SourceNotSpecified;
import org.cqfn.astgen.rules.Program;
import org.cqfn.astgen.utils.FilesReader;

/**
 * The interpreter that loads the syntax tree in Json format,
 * then applies DSL rules and saves the result to a file.
 *
 * @since 1.0
 */
public class Interpreter {
    /**
     * The name of the source file.
     */
    private final File source;

    /**
     * The name of the destination file.
     */
    private final File destination;

    /**
     * The DSL program.
     */
    private final Program program;

    /**
     * Constructor.
     * @param source The source file
     * @param destination The destination file
     * @param program The program
     */
    public Interpreter(final File source, final File destination, final Program program) {
        this.source = source;
        this.destination = destination;
        this.program = program;
    }

    /**
     * Runs the interpreter.
     * @throws InterpreterException Can't execute the program for some reasons
     */
    public void run() throws InterpreterException {
        if (this.source == null) {
            throw SourceNotSpecified.INSTANCE;
        }
        if (this.destination == null) {
            throw DestinationNotSpecified.INSTANCE;
        }
        final Node unprocessed = new JsonDeserializer(
            new FilesReader(this.source.getPath()).readAsString(
                (FilesReader.CustomExceptionCreator<InterpreterException>) ()
                    -> new InterpreterException() {
                        @Override
                        public String getErrorMessage() {
                            return String.format(
                                "Could not read the file that contains source syntax tree: %s",
                                Interpreter.this.source.getPath()
                            );
                        }
                    }
            )
        ).convert();
        final Adapter adapter = new Adapter(this.program.getTransformations());
        final Node processed = adapter.convert(unprocessed);
        if (!new JsonSerializer(processed).serializeToFile(this.destination.getPath())) {
            throw new InterpreterCouldNotWriteFile(this.destination.getPath());
        }
    }
}
