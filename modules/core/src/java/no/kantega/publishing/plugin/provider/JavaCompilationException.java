package no.kantega.publishing.plugin.provider;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.List;

/**
 *
 */
public class JavaCompilationException extends RuntimeException {
    private final List<Diagnostic<? extends JavaFileObject>> diagnostics;

    public JavaCompilationException(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        this.diagnostics = diagnostics;
    }

    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
        return diagnostics;
    }
}
