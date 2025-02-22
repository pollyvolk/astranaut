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
package org.cqfn.astgen.codegen.java;

import org.cqfn.astgen.utils.StringUtils;

/**
 * The Java type (interface or class).
 *
 * @since 1.0
 */
public interface Type extends Entity {
    /**
     * Returns brief description of the type.
     * @return The description
     */
    String getBrief();

    /**
     * Returns version of the implementation.
     * @return The version
     */
    String getVersion();

    /**
     * Specifies the version of the implementation.
     * @param version The version
     */
    void setVersion(String version);

    /**
     * Generates the type header.
     * @param builder String builder where to generate
     * @param indent Indentation
     */
    default void generateHeader(final StringBuilder builder, final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        builder.append(tabulation)
            .append("/**\n")
            .append(tabulation)
            .append(" * ")
            .append(this.getBrief())
            .append(".\n")
            .append(tabulation)
            .append(" *\n")
            .append(tabulation)
            .append(" * @since ")
            .append(this.getVersion())
            .append('\n')
            .append(tabulation)
            .append(" */\n");
    }
}
