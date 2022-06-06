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

package org.cqfn.astranaut.exceptions;

/**
 * Exception "Unexpected hole number".
 *
 * @since 1.0
 */
public final class UnexpectedNumberUsed extends ParserException {
    private static final long serialVersionUID = -770673234650552171L;

    /**
     * The unexpected hole number.
     */
    private final int number;

    /**
     * Constructor.
     * @param number An unexpected hole number
     */
    public UnexpectedNumberUsed(final int number) {
        super();
        this.number = number;
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append("Unexpected hole number, was not used in the left part: '#")
            .append(this.number)
            .append('\'')
            .toString();
    }
}