package graphql.language;


import java.util.ArrayList;
import java.util.List;

public class VariableReference extends AbstractNode implements Value {

    private String name;

    public VariableReference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<Node> getChildren() {
        return new ArrayList<Node>();
    }

    @Override
    public boolean isEqualTo(Node node) {
        if (this == node) return true;
        if (node == null || getClass() != node.getClass()) return false;

        VariableReference that = (VariableReference) node;

        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public String toString() {
        return "VariableReference{" +
                "name='" + name + '\'' +
                '}';
    }
}
