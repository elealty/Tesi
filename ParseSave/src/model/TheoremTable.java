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
    private final StringProperty  machine;

    public TheoremTable(String name, String prover, Integer execution,
            Integer provable, String family, String machine) {
        this.name = new SimpleStringProperty(name);
        this.prover = new SimpleStringProperty(prover);
        this.execution = new SimpleIntegerProperty(execution);
        this.provable = new SimpleBooleanProperty((provable == 1));
        this.family = new SimpleStringProperty(family);
        this.machine = new SimpleStringProperty(machine);
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
        return this.provable.get();
    }

    public String getFamily() {
        return this.family.get();
    }

    public Integer getExecution() {
        return this.execution.get();
    }

    public String getMachine() {
        return this.machine.get();
    }

    public void setMachine(String machine) {
        this.machine.set(machine);
    }
}
