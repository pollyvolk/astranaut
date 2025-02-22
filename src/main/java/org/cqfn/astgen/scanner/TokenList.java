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
package org.cqfn.astgen.scanner;

import java.util.Iterator;

/**
 * A list contains tokens.
 *
 * @since 1.0
 */
public abstract class TokenList implements Iterable<Token> {
    /**
     * Returns the size of the list.
     * @return The size
     */
    public abstract int size();

    /**
     * Returns token by its index.
     * @param index Index
     * @return A token
     * @throws IndexOutOfBoundsException If index is negative or greater than size - 1
     */
    public abstract Token get(int index) throws IndexOutOfBoundsException;

    @Override
    public final Iterator<Token> iterator() {
        return new TokenListIterator(this);
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int index = 0; index < this.size(); index = index + 1) {
            builder.append(this.get(index).toString());
        }
        return builder.toString();
    }
}
