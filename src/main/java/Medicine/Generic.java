package Medicine;

import javafx.beans.property.StringProperty;

public class Generic extends Indication {
    private final StringProperty genericName;
    private final String dose;

    private final String sideEffects;
    private final String precautions;
    private final String modeOfAction;

    public Generic(StringProperty indicationType, StringProperty genericName, String dose, String sideEffects, String precautions, String modeOfAction) {
        super(indicationType);
        this.genericName = genericName;
        this.dose = dose;
        this.sideEffects = sideEffects;
        this.precautions = precautions;
        this.modeOfAction = modeOfAction;
    }

    public String getGenericName() {
        return genericName.get();
    }

    public StringProperty genericNameProperty() {
        return genericName;
    }

    public String getDose() {
        return dose;
    }


    public String getSideEffects() {
        return sideEffects;
    }

    public String getPrecautions() {
        return precautions;
    }

    public String getModeOfAction() {
        return modeOfAction;
    }
}
