package de.outlook_klon.gui.helpers;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Erweitert die FocusTraversalPolicy-Klasse zum einfachen Wandern per Tab-Taste
 * �ber alle Steuerelemente in der Reihenfolge, die in der Liste beschrieben
 * ist.
 */
public class ListFocusTraversalPolicy extends FocusTraversalPolicy {

    private final List<Component> order;

    /**
     * Erstellt eine neue Instanz mit den �bergebenen Komponenten.
     *
     * @param order Liste aller zu durchlaufenden Komponenten.
     */
    public ListFocusTraversalPolicy(List<Component> order) {
        this.order = new ArrayList<>(order.size());
        this.order.addAll(order);
    }

    @Override
    public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
        int vectorIndex = order.indexOf(aComponent) + 1;
        if (vectorIndex >= order.size()) {
            vectorIndex = 0;
        }
        return order.get(vectorIndex);
    }

    @Override
    public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
        int vectorIndex = order.indexOf(aComponent) - 1;
        if (vectorIndex < 0) {
            vectorIndex = order.size() - 1;
        }
        return order.get(vectorIndex);
    }

    @Override
    public Component getDefaultComponent(Container focusCycleRoot) {
        return getFirstComponent(focusCycleRoot);
    }

    @Override
    public Component getLastComponent(Container focusCycleRoot) {
        if (order.isEmpty()) {
            throw new NoSuchElementException("The component list is empty");
        }

        return order.get(order.size() - 1);
    }

    @Override
    public Component getFirstComponent(Container focusCycleRoot) {
        if (order.isEmpty()) {
            throw new NoSuchElementException("The component list is empty");
        }

        return order.get(0);
    }
}
