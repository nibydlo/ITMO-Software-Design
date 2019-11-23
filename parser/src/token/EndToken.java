package token;

import visitor.TokenVisitor;

public class EndToken implements Token {
    @Override
    public void accept(TokenVisitor visitor) {

    }

    @Override
    public String toString() {
        return "END";
    }
}
