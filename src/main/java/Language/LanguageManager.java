package Language;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

public class LanguageManager {
    private static LanguageManager instance;
    private ResourceBundle resourceBundle;
    private Locale currentLocale;
    private StringProperty languageProperty = new SimpleStringProperty();

    // Language change listeners
    public interface LanguageChangeListener {
        void onLanguageChanged();
    }

    private java.util.List<LanguageChangeListener> listeners = new java.util.ArrayList<>();

    private LanguageManager() {
        // Default to English with fallback
        setLanguageWithFallback("en");
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public void setLanguage(String languageCode) {
        setLanguageWithFallback(languageCode);
    }

    private void setLanguageWithFallback(String languageCode) {
        try {
            switch (languageCode.toLowerCase()) {
                case "nepali":
                case "ne":
                    currentLocale = new Locale("ne", "NP");
                    break;
                case "english":
                case "en":
                default:
                    currentLocale = new Locale("en", "US");
                    break;
            }

            System.out.println("Attempting to load locale: " + currentLocale);

            // Try multiple resource bundle locations
            String[] bundleNames = {
                    "Language.messages",
                    "messages",
                    "Language/messages"
            };

            resourceBundle = null;
            for (String bundleName : bundleNames) {
                try {
                    resourceBundle = ResourceBundle.getBundle(bundleName, currentLocale);
                    System.out.println("Successfully loaded bundle: " + bundleName);
                    break;
                } catch (MissingResourceException e) {
                    System.out.println("Failed to load bundle: " + bundleName);
                }
            }

            if (resourceBundle == null) {
                throw new MissingResourceException("No resource bundle found", "messages", "");
            }

            languageProperty.set(languageCode);

            // Debug the loaded bundle
            debugResourceBundle();

            notifyLanguageChange();
            System.out.println("Language successfully changed to: " + currentLocale.getDisplayName());

        } catch (Exception e) {
            System.err.println("Error loading resource bundle: " + e.getMessage());
            e.printStackTrace();
            // Fallback logic...
        }
    }

    private void createEmptyResourceBundle() {
        // Create a minimal resource bundle to prevent null pointer exceptions
        currentLocale = new Locale("en", "US");
        // We'll return the key itself if no resource bundle is available
        resourceBundle = null;
        System.out.println("Using fallback mode - keys will be returned as-is");
    }

    public String getString(String key) {
        if (key == null) {
            return "NULL_KEY";
        }

        if (resourceBundle == null) {
            System.err.println("ResourceBundle is null, returning key: " + key);
            return key; // Return the key itself as fallback
        }

        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            System.err.println("Missing translation key: " + key);
            return key; // Return the key itself if translation is missing
        } catch (Exception e) {
            System.err.println("Error getting string for key '" + key + "': " + e.getMessage());
            return key;
        }
    }

    public String getString(String key, Object... args) {
        if (key == null) {
            return "NULL_KEY";
        }

        try {
            String pattern = getString(key); // This already handles null resourceBundle
            if (args != null && args.length > 0) {
                return java.text.MessageFormat.format(pattern, args);
            }
            return pattern;
        } catch (Exception e) {
            System.err.println("Error formatting string for key '" + key + "': " + e.getMessage());
            return key;
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale != null ? currentLocale : new Locale("en", "US");
    }

    public void addLanguageChangeListener(LanguageChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeLanguageChangeListener(LanguageChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyLanguageChange() {
        for (LanguageChangeListener listener : listeners) {
            try {
                if (listener != null) {
                    listener.onLanguageChanged();
                }
            } catch (Exception e) {
                System.err.println("Error notifying language change listener: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public StringProperty languageProperty() {
        return languageProperty;
    }

    // Method to check if resource bundle is loaded properly
    public boolean isResourceBundleLoaded() {
        return resourceBundle != null;
    }

    // Method to get available languages
    public String[] getAvailableLanguages() {
        return new String[]{"English", "Nepali"};
    }
    public void debugResourceBundle() {
        System.out.println("Current Locale: " + currentLocale);
        System.out.println("Resource Bundle: " + (resourceBundle != null ? "Loaded" : "NULL"));

        if (resourceBundle != null) {
            try {
                // Test a few keys
                System.out.println("nav.dashboard = " + resourceBundle.getString("nav.dashboard"));
                System.out.println("language.nepali = " + resourceBundle.getString("language.nepali"));
            } catch (Exception e) {
                System.err.println("Error reading from resource bundle: " + e.getMessage());
            }
        }

        // Check if resource bundle file exists
        try {
            java.net.URL resourceUrl = getClass().getClassLoader()
                    .getResource("Language/messages_ne_NP.properties");
            System.out.println("Nepali resource file found: " + (resourceUrl != null));
            if (resourceUrl != null) {
                System.out.println("Resource URL: " + resourceUrl.toString());
            }
        } catch (Exception e) {
            System.err.println("Error checking resource file: " + e.getMessage());
        }
    }

}