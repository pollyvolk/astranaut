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
package org.cqfn.astranaut.api;

import org.cqfn.astranaut.base.Node;
import org.cqfn.astranaut.exceptions.ProcessorException;
import org.cqfn.astranaut.utils.FilesReader;

/**
 * Converts a tree to a string that contains a JSON object.
 *
 * @since 0.2
 */
public class JsonDeserializer {

    /**
     * The name of the file that contains a JSON object.
     */
    private final String filename;

    /**
     * Constructor.
     * @param filename The file name
     */
    public JsonDeserializer(final String filename) {
        this.filename = filename;
    }

    /**
     * Converts the source string that contains a JSON object to a tree.
     * @return Root node of the created tree
     * @throws ProcessorException In case the operation fails
     */
    public Node deserialize() throws ProcessorException {
        final String file = this.filename;
        final String source = new FilesReader(file).readAsString(
            (FilesReader.CustomExceptionCreator<ProcessorException>) ()
                -> new ProcessorException() {
                    private static final long serialVersionUID = -2486266117492218703L;

                    @Override
                    public String getErrorMessage() {
                        return String.format(
                            "Could not read the file that contains source tree: %s",
                            file
                        );
                    }
                }
        );
        final org.cqfn.astranaut.handlers.json.JsonDeserializer deserializer =
            new org.cqfn.astranaut.handlers.json.JsonDeserializer(source);
        return deserializer.convert();
    }
}
