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
package org.cqfn.astgen.parser;

import org.cqfn.astgen.exceptions.BaseException;
import org.cqfn.astgen.exceptions.ExceptionWithLineNumber;
import org.cqfn.astgen.exceptions.ParserException;
import org.cqfn.astgen.rules.Program;

/**
 * Parses the whole DSL program.
 *
 * @since 1.0
 */
public class ProgramParser {
    /**
     * Source string.
     */
    private final String source;

    /**
     * Constructor.
     * @param source The source string.
     */
    public ProgramParser(final String source) {
        this.source = source;
    }

    /**
     * Parses the whole DSL program.
     * @return Parsed program.
     * @throws BaseException If source can't be parsed
     */
    public Program parse() throws BaseException {
        final String[] lines = this.source.split(";");
        final Program program = new Program();
        final StatementParser parser = new StatementParser(program);
        int number = 1;
        for (final String line : lines) {
            final String dsl = line.trim();
            if (!dsl.isEmpty()) {
                try {
                    parser.parse(dsl);
                } catch (final ParserException error) {
                    throw new ExceptionWithLineNumber(error, number);
                }
            }
            number = number + 1;
        }
        return program;
    }
}
