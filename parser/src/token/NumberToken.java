package token;

import visitor.TokenVisitor;

public class NumberToken implements Token {

    private final int value;

    public NumberToken(int value) {
        this.value = value;
    }

    @Override
    public void accept(TokenVisitor visitor) {

    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "NUMBER(" + value + ")";
    }
}
