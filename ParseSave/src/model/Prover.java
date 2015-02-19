package model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class Prover {

    private final SimpleBooleanProperty selected;
    private final SimpleStringProperty  name;

    public Prover(boolean id, String name) {
        this.selected = new SimpleBooleanProperty(id);
        this.name = new SimpleStringProperty(name);
    }

    public boolean getSelected() {
        return this.selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String fName) {
        name.set(fName);
    }

    public SimpleBooleanProperty selectedProperty() {
        return selected;
    }

    @Override
    public String toString() {
        return getName();
    }
}
