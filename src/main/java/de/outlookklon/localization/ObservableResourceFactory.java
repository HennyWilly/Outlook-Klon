package de.outlookklon.localization;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @see <a href="https://stackoverflow.com/a/32468810">Stackoverflow</a>
 */
public class ObservableResourceFactory {

    private final String bundleName;
    private final ObjectProperty<ResourceBundle> resources;

    public ObservableResourceFactory(String bundleName) {
        this.bundleName = bundleName;
        this.resources = new SimpleObjectProperty<>();

        setResources(ResourceBundle.getBundle(bundleName));
    }

    public ReadOnlyObjectProperty<ResourceBundle> resourcesProperty() {
        return resources;
    }

    public final ResourceBundle getResources() {
        return resourcesProperty().get();
    }

    public final void setLanguage(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
        setResources(bundle);
    }

    private void setResources(ResourceBundle resource) {
        resources.set(resource);
    }

    public StringBinding getStringBinding(String key) {
        return new StringBinding() {
            {
                bind(resourcesProperty());
            }

            @Override
            public String computeValue() {
                return getResources().getString(key);
            }
        };
    }
}
