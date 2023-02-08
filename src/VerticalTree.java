import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.Random;

public class VerticalTree<T extends UserType> {

    private VerticalTreeNode<T> root;

    public VerticalTree() {
        root = null;
    }

    public void setRoot(VerticalTreeNode<T> root) {
        this.root = root;
    }

    @JsonIgnore
    public int getSize() {
        return root == null ? 0 : root.getSubtreeSize();
    }

    public void add(T v) {
        if (root == null) {
            root = new VerticalTreeNode<>(v);
        } else {
            root.add(v);
        }
    }

    public T get(int index) {
        if (root == null || index < 0 || index >= root.getSubtreeSize()) {
            throw new IndexOutOfBoundsException();
        }

        VerticalTreeNode<T> curr = root.copy();
        for(int i = 0; i < index; i++) {
            curr = curr.upSift();
        }
        return curr.getData();
    }

    public T remove(int index) {
        if (root == null || index < 0 || index >= root.getSubtreeSize()) {
            throw new IndexOutOfBoundsException();
        }
        int size = root.getSubtreeSize();
        VerticalTreeNode<T> curr = root.copy();
        root = null;
        T result = null;
        for(int i = 0; i < size; i++) {
            if (i == index) {
                result = curr.getData();
            }
            else {
                add(curr.getData());
            }
            curr = curr.upSift();
        }
        return result;
    }

    public void balance() {
        VerticalTreeNode<T> curr = root.copy();
        root = null;
        while (curr != null) {
            addBalanced(curr.getData());
            curr = curr.upSift();
        }
    }

    public void forEach(DoWith d) {
        VerticalTreeNode<T> curr = root.copy();
        while (curr != null) {
            d.doWith(curr.getData());
            curr = curr.upSift();
        }
    }

    @Override
    public String toString() {
        if (root == null) {
            return "[EMPTY]";
        }
        else {
            StringBuilder builder = new StringBuilder();
            root.toString(builder, 0);
            return builder.toString();
        }
    }

    private void addBalanced(T v) {
        if (root == null) {
            root = new VerticalTreeNode<>(v);
        } else {
            root.addBalanced(v);
        }
    }


    public static void main(String[] args) {
        VerticalTree<Integer> tree = new VerticalTree<>();
        Random random = new Random(1);
        int n = 20;
        int limit = 100;
        for (int i = 0; i < n; i++) {
            Integer value = new Integer(random.nextInt(limit));
            System.out.println("Inserted: " + value);
            tree.add(value);
        }
        System.out.println();

        System.out.println("Sorted:");
        tree.forEach(v -> System.out.println(v.toString()));
        System.out.println();

        System.out.println("Indexes:");
        for (int i = 0; i < n; i++) {
            System.out.println("Element #" + i + ": " + tree.get(i));
        }
        System.out.println();


        System.out.println("Structure:");
        System.out.println(tree);

        tree.balance();
        System.out.println("Structure after balancing:");
        System.out.println(tree);


        System.out.println();
        int toRemove = 5;
        System.out.println("Removing elements:");
        for(int i = 0; i<toRemove; i++) {
            int index = random.nextInt(tree.getSize());
            System.out.println("Removing element at index " + index + " ... :" + tree.remove(index));

            System.out.println(tree);
        }
    }

    public String serialize() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            return mapper.writeValueAsString(this);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends UserType> VerticalTree<T> deserialize(String s, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.registerModule(new SimpleModule().addAbstractTypeMapping(UserType.class, clazz));
            return mapper.readValue(s, new TypeReference<VerticalTree<T>>() {});
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
