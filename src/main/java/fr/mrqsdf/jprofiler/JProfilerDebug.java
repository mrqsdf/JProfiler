package fr.mrqsdf.jprofiler;

/**
 * Global debug configuration for JProfiler
 */
public class JProfilerDebug {
    
    private static boolean debugEnabled = false;
    private static boolean logEvents = false;
    private static boolean logUpdates = false;
    private static boolean logBindings = false;

    /**
     * Enable or disable all debug output
     */
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
        logEvents = enabled;
        logUpdates = enabled;
        logBindings = enabled;
    }

    /**
     * Enable or disable event logging specifically
     */
    public static void setLogEvents(boolean enabled) {
        logEvents = enabled;
    }

    /**
     * Enable or disable update logging specifically
     */
    public static void setLogUpdates(boolean enabled) {
        logUpdates = enabled;
    }

    /**
     * Enable or disable binding logging specifically
     */
    public static void setLogBindings(boolean enabled) {
        logBindings = enabled;
    }

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    public static boolean shouldLogEvents() {
        return logEvents;
    }

    public static boolean shouldLogUpdates() {
        return logUpdates;
    }

    public static boolean shouldLogBindings() {
        return logBindings;
    }

    /**
     * Log a debug message if debug is enabled
     */
    public static void log(String message) {
        if (debugEnabled) {
            System.out.println("[JProfiler Debug] " + message);
        }
    }

    /**
     * Log an event if event logging is enabled
     */
    public static void logEvent(String message) {
        if (logEvents) {
            System.out.println("[JProfiler Event] " + message);
        }
    }

    /**
     * Log an update if update logging is enabled
     */
    public static void logUpdate(String message) {
        if (logUpdates) {
            System.out.println("[JProfiler Update] " + message);
        }
    }

    /**
     * Log a binding operation if binding logging is enabled
     */
    public static void logBinding(String message) {
        if (logBindings) {
            System.out.println("[JProfiler Binding] " + message);
        }
    }
}
