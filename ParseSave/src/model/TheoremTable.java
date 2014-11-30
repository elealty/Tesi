package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TheoremTable {
    private final IntegerProperty id;
    private final StringProperty  name;
    private final IntegerProperty execution;
    private final BooleanProperty provable;

    /**
     * Constructor with some initial data.
     * 
     * @param id
     * @param firstName
     * @param lastName
     */
    public TheoremTable(Integer id, String name, Integer execution,
            Integer provable) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.execution = new SimpleIntegerProperty(execution);
        this.provable = new SimpleBooleanProperty((provable == 1));

    }

    public String getName() {
        return name.get();
    }

    public void setName(String Name) {
        this.name.set(Name);
    }

    public int getExcution() {
        return execution.get();
    }

    public void setExcution(Integer execution) {
        this.execution.set(execution);
    }

    public int getId() {
        return id.get();
    }

    public boolean getProvable() {
        return provable.get();
    }

}
