import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tree {
    private String node;
    private List<Tree> children;

    public Tree(String node, Tree... children) {
        this.node = node;
        this.children = Arrays.asList(children);
    }

    public Tree(String node) {
        this.node = node;
        this.children = new ArrayList<>();
    }

    @Override
    public String toString() {
        if (children == null || children.size() == 0) {
            if (node.equals("e"))
                return "";
            return node;
        }
        StringBuilder sb = new StringBuilder();
        for (Tree t : children) sb.append(t.toString());
        return sb.toString();
    }
}
