package visitor;

import token.Brace;
import token.NumberToken;
import token.Operation;
import token.Token;

import java.util.List;

public class PrintVisitor implements TokenVisitor {

    private StringBuilder stringBuilder = new StringBuilder();
    private final List<Token> tokens;

    public PrintVisitor(List<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public void visit(NumberToken token) {
        stringBuilder.append(token);
    }

    @Override
    public void visit(Brace token) {
        stringBuilder.append(token);
    }

    @Override
    public void visit(Operation token) {
        stringBuilder.append(token);
    }

    public String toString() {
        tokens.forEach(token -> stringBuilder.append(token).append(" "));
        return stringBuilder.toString();
    }
}
