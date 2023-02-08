import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VerticalTreeNode<T extends UserType> {
    private static Random random = new Random();

    private T data;
    private int subtreeSize;
    private List<VerticalTreeNode<T>> children;

    public VerticalTreeNode() {}

    public VerticalTreeNode(T data) {
        this.data = data;
        this.subtreeSize = 1;
        this.children = new ArrayList<>();
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public int getSubtreeSize() {
        return subtreeSize;
    }

    public void add(T v) {
        if( v.getTypeComparator().compare(data, v) > 0) {
            T sw = data;
            data = v;
            v = sw;
        }

        int childIndex = random.nextInt(children.size() + 1);
        if (childIndex == children.size()) {
            children.add(new VerticalTreeNode<>(v));
        }
        else {
            children.get(childIndex).add(v);
        }
        subtreeSize++;
    }

    public void addBalanced(T v) {
        if( v.getTypeComparator().compare(data, v) > 0) {
            T sw = data;
            data = v;
            v = sw;
        }

        if (children.size() < 2) {
            children.add(new VerticalTreeNode<>(v));
        }
        else if (children.get(0).subtreeSize > children.get(1).subtreeSize) {
            children.get(1).addBalanced(v);
        }
        else {
            children.get(0).addBalanced(v);
        }
        subtreeSize++;
    }

    public VerticalTreeNode<T> copy() {
        VerticalTreeNode<T> result = new VerticalTreeNode<>(data);
        result.subtreeSize = subtreeSize;
        for(VerticalTreeNode<T> child : children) {
            result.children.add(child.copy());
        }
        return result;
    }

    public VerticalTreeNode<T> upSift() {
        if (children.isEmpty()) {
            return null;
        }

        int minIndex = -1;
        for(int i = 0; i<children.size(); i++) {
            VerticalTreeNode<T> child = children.get(i);
            if (minIndex == -1 || data.getTypeComparator().compare(children.get(minIndex).getData(), child.getData()) > 0) {
                minIndex = i;
            }
        }
        data = children.get(minIndex).getData();
        VerticalTreeNode<T> siftedChild = children.get(minIndex).upSift();
        if (siftedChild == null) {
            children.remove(minIndex);
        }

        return this;
    }

    public void toString(StringBuilder builder, int depth) {
        if (depth > 0) {
            for (int i = 0; i < depth - 1; i++) {
                builder.append("\t");
            }
            builder.append("|-- ");
        }
        builder.append(data.toString()).append(System.lineSeparator());
        for (VerticalTreeNode<T> child : children) {
            child.toString(builder, depth + 1);
        }
    }
}
