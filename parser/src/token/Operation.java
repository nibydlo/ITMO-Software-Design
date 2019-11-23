package token;

import visitor.TokenVisitor;

public class Operation implements Token {

    private final OperationType operationType;

    public Operation(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public void accept(TokenVisitor visitor) {

    }

    public enum OperationType {
        PLUS, MINUS, MUL, DIV
    }

    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String toString() {
        return operationType.toString();
    }
}
