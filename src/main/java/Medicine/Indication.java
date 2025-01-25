package Medicine;
import javafx.beans.property.StringProperty;

public class Indication {
    private final StringProperty indicationType;
    public Indication(StringProperty indicationType) {
        this.indicationType = indicationType;
    }
    public String getIndicationType() {
        return indicationType.get();
    }
    public StringProperty indicationTypeProperty() {
        return indicationType;
    }
}
