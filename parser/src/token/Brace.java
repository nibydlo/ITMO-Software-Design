package token;

import visitor.TokenVisitor;

public class Brace implements Token{

    private final BraceType braceType;

    public Brace (BraceType braceType) {
        this.braceType = braceType;
    }

    public BraceType getBraceType() {
        return braceType;
    }

    @Override
    public void accept(TokenVisitor visitor) {

    }

    public enum BraceType {LEFT, RIGHT}

    @Override
    public String toString() {
        return braceType == BraceType.RIGHT ? "RIGHT" : "LEFT";
    }
}
