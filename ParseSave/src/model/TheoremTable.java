package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TheoremTable {
    private final StringProperty  name;
    private final StringProperty  prover;
    private final IntegerProperty execution;
    private final BooleanProperty provable;
    private final StringProperty  family;
    private SimpleBooleanProperty checked = new SimpleBooleanProperty(false);

    public SimpleBooleanProperty checkedProperty() {
        return this.checked;
    }

    public java.lang.Boolean getChecked() {
        return this.checkedProperty().get();
    }

    public void setChecked(final java.lang.Boolean checked) {
        this.checkedProperty().set(checked);
    }

    /**
     * Constructor with some initial data.
     * 
     * @param id
     * @param firstName
     * @param lastName
     */
    public TheoremTable(String name, String prover, Integer execution,
            Integer provable, String family) {
        this.name = new SimpleStringProperty(name);
        this.prover = new SimpleStringProperty(prover);
        this.execution = new SimpleIntegerProperty(execution);
        this.provable = new SimpleBooleanProperty((provable == 1));
        this.family = new SimpleStringProperty(family);

    }

    public String getName() {
        return name.get();
    }

    public void setName(String Name) {
        this.name.set(Name);
    }

    public String getProver() {
        return prover.get();
    }

    public void setProver(String Prover) {
        this.prover.set(Prover);
    }

    public boolean getProvable() {
        return provable.get();
    }

    public String getFamily() {
        return family.get();
    }

    public Integer getExecution() {
        return execution.get();
    }

}
