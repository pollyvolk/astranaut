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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cqfn.astgen.rules.Statement;
import org.cqfn.astgen.rules.Transformation;

/**
 * Tree converter built on a set of rules described in DSL.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.CloseResource")
public class Adapter extends org.cqfn.astgen.base.Adapter {
    /**
     * Constructor.
     * @param statements The list of transformation statements
     */
    public Adapter(final List<Statement<Transformation>> statements) {
        super(Collections.unmodifiableList(Adapter.init(statements)), Factory.INSTANCE);
    }

    /**
     * Initialises the list of converters.
     * @param statements The list of transformation statements
     * @return List of converters
     */
    private static List<org.cqfn.astgen.base.Converter> init(
        final List<Statement<Transformation>> statements) {
        final List<org.cqfn.astgen.base.Converter> result = new ArrayList<>(statements.size());
        for (final Statement<Transformation> statement : statements) {
            final Transformation rule = statement.getRule();
            result.add(new Converter(rule));
        }
        return result;
    }
}
