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

import org.cqfn.astgen.exceptions.ParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Scanner} and {@link Bracket} classes.
 *
 * @since 1.0
 */
public class BracketsTest {
    /**
     * Source code.
     */
    private static final String SOURCE = "{ one, [ two ], <t(h)ree> }";

    /**
     * Output example.
     */
    private static final String EXPECTED = "{[]<()>}";

    /**
     * Test scanner with string contains brackets.
     */
    @Test
    public void brackets() {
        final Scanner scanner = new Scanner(BracketsTest.SOURCE);
        final StringBuilder result = new StringBuilder();
        boolean oops = false;
        try {
            Token token = null;
            do {
                token = scanner.getToken();
                if (token instanceof Bracket) {
                    result.append(token.toString());
                }
            } while (!(token instanceof Null));
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertEquals(result.toString(), BracketsTest.EXPECTED);
    }
}
