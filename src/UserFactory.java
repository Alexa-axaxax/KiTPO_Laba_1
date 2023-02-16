import java.util.Arrays;
import java.util.List;

public class UserFactory{
    public List<String> getTypeNameList() {
           return Arrays.asList("PolarPoint", "Integer");
    }

    public UserType getBuilderByName(String name) {
        switch (name) {
            case "PolarPoint":
                return new PolarPoint();
            case "Integer":
                return new Integer();
            default:
                throw new IllegalArgumentException();
        }
    }
}