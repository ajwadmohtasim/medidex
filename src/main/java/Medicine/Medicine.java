package Medicine;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Medicine extends Generic {
    private final StringProperty name;
    private final String type;
    private final String companyName;
    private final String packsize;

    public Medicine(String name, String indicationType, String genericName, String dose, String sideEffects, String precautions, String modeOfAction, String companyName, String type, String packsize) {
        super(new SimpleStringProperty(indicationType), new SimpleStringProperty(genericName), dose, sideEffects, precautions, modeOfAction);
        this.name = new SimpleStringProperty(name);
        this.companyName = companyName;
        this.type = type;
        this.packsize = packsize;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getIndicationType() {
        return super.getIndicationType();
    }

    public String getType() {
        return type;
    }

    public String getPacksize() {
        return packsize;
    }

}
