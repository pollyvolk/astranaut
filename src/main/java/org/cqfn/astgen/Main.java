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

package org.cqfn.astgen;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import org.cqfn.astgen.analyzer.PreparedEnvironment;
import org.cqfn.astgen.codegen.java.Environment;
import org.cqfn.astgen.codegen.java.License;
import org.cqfn.astgen.codegen.java.ProgramGenerator;
import org.cqfn.astgen.codegen.java.TaggedChild;
import org.cqfn.astgen.exceptions.BaseException;
import org.cqfn.astgen.interpreter.Interpreter;
import org.cqfn.astgen.parser.ProgramParser;
import org.cqfn.astgen.rules.Program;
import org.cqfn.astgen.utils.FilesReader;
import org.cqfn.astgen.utils.cli.ActionConverter;
import org.cqfn.astgen.utils.cli.DestinationFileConverter;
import org.cqfn.astgen.utils.cli.LicenseValidator;
import org.cqfn.astgen.utils.cli.PackageValidator;
import org.cqfn.astgen.utils.cli.ProjectRootValidator;
import org.cqfn.astgen.utils.cli.RulesFileConverter;
import org.cqfn.astgen.utils.cli.SourceFileConverter;

/**
 * Main class.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.ImmutableField")
public final class Main {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    /**
     * The action.
     */
    @Parameter(
        names = { "--action", "-a" },
        converter = ActionConverter.class,
        required = true,
        description = "The action: 'generate' or 'convert'"
    )
    private Action action;

    /**
     * The file contains DSL rules.
     */
    @Parameter(
        names = { "--rules", "--dsl", "-r" },
        converter = RulesFileConverter.class,
        required = true,
        description = "Text file with DSL descriptions"
    )
    private File dsl;

    /**
     * The name of file that contains license header.
     */
    @Parameter(
        names = { "--license", "-l" },
        validateWith = LicenseValidator.class,
        arity = 1,
        description = "The name of file that contain license header"
    )
    private String license;

    /**
     * The root of the target project.
     */
    @Parameter(
        names = { "--output", "-o" },
        validateWith = ProjectRootValidator.class,
        arity = 1,
        description = "Output path, i.e. the folder where generated files are stored"
    )
    private String path;

    /**
     * The package of the generated file.
     */
    @Parameter(
        names = { "--package", "-p" },
        validateWith = PackageValidator.class,
        arity = 1,
        description = "Name of the package of generated files"
    )
    private String rootpkg;

    /**
     * The name of the package that contains the 'Node' base interface.
     */
    @Parameter(
        names = { "--base", "-b" },
        validateWith = PackageValidator.class,
        arity = 1,
        description = "The name of the package that contains the 'Node' base interface"
    )
    private String basepkg;

    /**
     * Specify the version of the implementation.
     */
    @Parameter(
        names = { "--version", "-v" },
        arity = 1,
        description = "Specify the version of the implementation"
    )
    private String version;

    /**
     * The file that contains source syntax tree in the JSON format.
     */
    @Parameter(
        names = { "--source", "--src", "-s" },
        converter = SourceFileConverter.class,
        description = "The file that contains source syntax tree in the JSON format"
    )
    private File source;

    /**
     * The file for saving resulting syntax tree in the JSON format.
     */
    @Parameter(
        names = { "--destination", "--dst", "-d" },
        converter = DestinationFileConverter.class,
        description = "The file for saving resulting syntax tree in the JSON format"
    )
    private File destination;

    /**
     * Test mode.
     */
    @Parameter(
        names = "--test",
        description = "Test mode (no files will be written to the file system)"
    )
    private boolean test;

    /**
     * The help option.
     */
    @Parameter(names = "--help", help = true)
    private boolean help;

    /**
     * Private constructor with default values.
     */
    private Main() {
        this.help = false;
        this.license = "LICENSE.txt";
        this.path = "generated";
        this.rootpkg = "org.uast";
        this.basepkg = "org.uast.uast.base";
        this.version = "";
    }

    /**
     * The main function. Parses the command line.
     * @param args The command-line arguments
     * @throws BaseException If fails
     */
    public static void main(final String... args) throws BaseException {
        final Main main = new Main();
        final JCommander jcr = JCommander.newBuilder()
            .addObject(main)
            .build();
        jcr.parse(args);
        if (main.help) {
            jcr.usage();
            return;
        }
        main.run();
    }

    /**
     * Runs actions.
     * @throws BaseException If fails
     */
    private void run() throws BaseException {
        final String rules = this.dsl.getPath();
        try {
            final String code = new FilesReader(rules).readAsString(
                (FilesReader.CustomExceptionCreator<BaseException>) () -> new BaseException() {
                    @Override
                    public String getInitiator() {
                        return "Main";
                    }

                    @Override
                    public String getErrorMessage() {
                        return String.format("Could not read DSL file: %s", rules);
                    }
                }
            );
            final ProgramParser parser = new ProgramParser(code);
            final Program program = parser.parse();
            if (this.action == Action.GENERATE) {
                final Environment base = new EnvironmentImpl();
                final Map<String, Environment> env = new TreeMap<>();
                env.put("", new PreparedEnvironment(base, program.getVertices(), ""));
                for (final String language : program.getNamesOfAllLanguages()) {
                    env.put(
                        language,
                        new PreparedEnvironment(base, program.getVertices(), language)
                    );
                }
                final ProgramGenerator generator = new ProgramGenerator(this.path, program, env);
                generator.generate();
            } else if (this.action == Action.CONVERT) {
                new Interpreter(this.source, this.destination, program).run();
            }
        } catch (final BaseException exc) {
            LOG.severe(String.format("%s, %s", exc.getInitiator(), exc.getErrorMessage()));
            throw exc;
        }
    }

    /**
     * Environment implementation.
     *
     * @since 1.0
     */
    private class EnvironmentImpl implements Environment {
        /**
         * The license.
         */
        private final License license;

        /**
         * Constructor.
         */
        EnvironmentImpl() {
            this.license = new License(Main.this.license);
        }

        @Override
        public License getLicense() {
            return this.license;
        }

        @Override
        public String getVersion() {
            return Main.this.version;
        }

        @Override
        public String getRootPackage() {
            return Main.this.rootpkg;
        }

        @Override
        public String getBasePackage() {
            return Main.this.basepkg;
        }

        @Override
        public boolean isTestMode() {
            return Main.this.test;
        }

        @Override
        public String getLanguage() {
            return "";
        }

        @Override
        public List<String> getHierarchy(final String name) {
            return Collections.singletonList(name);
        }

        @Override
        public List<TaggedChild> getTags(final String type) {
            return Collections.emptyList();
        }

        @Override
        public Set<String> getImports(final String type) {
            return Collections.emptySet();
        }
    }
}
