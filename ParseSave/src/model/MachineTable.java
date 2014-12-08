/**
 * 
 */
package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author eleonora
 */
public class MachineTable {
    private StringProperty        name;
    private SimpleIntegerProperty id;

    public MachineTable(Integer id, String name) {
        this.setId(new SimpleIntegerProperty(id));
        this.setName(new SimpleStringProperty(name));
    }

    public void setName(StringProperty name) {
        this.name = name;
    }

    public String getName() {
        return name.get();
    }

    public int getId() {
        return id.get();
    }

    public void setId(SimpleIntegerProperty id) {
        this.id = id;
    }

}
