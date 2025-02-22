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

import org.cqfn.astgen.exceptions.ExpectedUniqueNumbers;
import org.cqfn.astgen.exceptions.ParserException;
import org.cqfn.astgen.exceptions.UnexpectedNumberUsed;
import org.cqfn.astgen.exceptions.UnknownSymbol;
import org.cqfn.astgen.rules.Transformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link TransformationParser} class.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public class TransformationParserTest {
    /**
     * Test case: simple transformation.
     */
    @Test
    public void simpleTransformation() {
        final boolean result = this.run("A -> B");
        Assertions.assertTrue(result);
    }

    /**
     * Test case: complex transformation.
     */
    @Test
    public void complexTransformation() {
        final boolean result = this.run(
            "singleExpression(identifier(literal<#13>)) -> Identifier<#13>"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: more complex transformation.
     */
    @Test
    public void moreComplexTransformation() {
        final boolean result = this.run(
            "singleExpression(#1, literal<\"+\">, #2) -> Addition(#1, #2)"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: DSL line with exception.
     */
    @Test
    public void wrongSyntax() {
        final boolean result = this.run("A -> 123", UnknownSymbol.class);
        Assertions.assertTrue(result);
    }

    /**
     * Test case: DSL line with duplicated child hole numbers in a left part.
     */
    @Test
    public void duplicatedChildHoleNumbersInLeft() {
        final boolean result = this.run(
            "A(#1, #1) -> B(#3)",
            ExpectedUniqueNumbers.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: DSL line with duplicated data hole numbers in a left part.
     */
    @Test
    public void duplicatedDataHoleNumbersInLeft() {
        final boolean result = this.run(
            "A(B<#1>, B<#1>) -> C<#1>",
            ExpectedUniqueNumbers.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: DSL line with unexpected child hole number in right part.
     */
    @Test
    public void wrongChildHoleNumbersInRight() {
        final boolean result = this.run(
            "A(#1, #2) -> B(#3)",
            UnexpectedNumberUsed.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: DSL line with unexpected data hole number in right part.
     */
    @Test
    public void wrongDataHoleNumbersInRight() {
        final boolean result = this.run(
            "A(#1, B<#2>) -> B<#3>",
            UnexpectedNumberUsed.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: DSL line with duplicated child hole numbers in a right part.
     */
    @Test
    public void duplicatedChildHoleNumbersInRight() {
        final boolean result = this.run(
            "A(#1, #2) -> B(#1, #1)",
            ExpectedUniqueNumbers.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: DSL line with duplicated data hole numbers in a right part.
     */
    @Test
    public void duplicatedDataHoleNumbersInRight() {
        final boolean result = this.run(
            "A(B<#1>, C<#2>) -> D(E<#1>, E<#1>)",
            ExpectedUniqueNumbers.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: DSL line with a child hole number in a right part which duplicates
     *  a data hole number.
     */
    @Test
    public void duplicatedDataHoleNumberByChildHoleNumberInRight() {
        final boolean result = this.run(
            "A(B<#1>, C(#2)) -> D(E<#1>, F(#2), G(#1))",
            ExpectedUniqueNumbers.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: DSL line with a data hole number in a right part which duplicates
     *  a child hole number.
     */
    @Test
    public void duplicatedChildHoleNumberByDataHoleNumberInRight() {
        final boolean result = this.run(
            "A(B<#1>, C(#2)) -> D(E<#1>, F(#2), G<#2>)",
            ExpectedUniqueNumbers.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Runs the literal parser.
     * @param source Source string
     * @return Test result ({@code true} means no exceptions)
     */
    private boolean run(final String source) {
        boolean oops = false;
        try {
            final Transformation rule = new TransformationParser(source).parse();
            final String text = rule.toString();
            Assertions.assertEquals(source, text);
        } catch (final ParserException ignored) {
            oops = true;
        }
        return !oops;
    }

    /**
     * Runs the literal parser and expects an exception.
     * @param source Source string
     * @param type Error type
     * @param <T> Exception class
     * @return Test result ({@code true} means parser throw expected exception)
     */
    private <T> boolean run(final String source, final Class<T> type) {
        boolean oops = false;
        try {
            new TransformationParser(source).parse();
        } catch (final ParserException error) {
            Assertions.assertInstanceOf(type, error);
            oops = true;
        }
        return oops;
    }
}
