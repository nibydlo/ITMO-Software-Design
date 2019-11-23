package visitor;

import token.Brace;
import token.NumberToken;
import token.Operation;
import token.Token;

import java.util.List;
import java.util.Stack;

public class CalcVisitor implements TokenVisitor {

    private final List<Token> tokens;
    private Stack<Token> tokenStack;
    private Stack<Integer> argsCount;
    public CalcVisitor(List<Token> tokens) {
        this.tokens = tokens;
        tokenStack = new Stack<>();
        argsCount = new Stack<>();
    }

    @Override
    public void visit(NumberToken token) {

        tokenStack.push(token);
        argsCount.push(argsCount.peek() + 1);
        while (!argsCount.empty() && argsCount.peek() == 2) {
            argsCount.pop();
            argsCount.pop();
            argsCount.pop();
            if (!argsCount.empty()) {
                argsCount.push(argsCount.peek() + 1);
            } else{
                argsCount.push(1);
            }

            int b = ((NumberToken) tokenStack.pop()).getValue();
            int a = ((NumberToken) tokenStack.pop()).getValue();
            Operation oper = (Operation) tokenStack.pop();
            switch (oper.getOperationType()) {
                case DIV:
                    tokenStack.push(new NumberToken(a / b));
                    break;
                case MINUS:
                    tokenStack.push(new NumberToken(a - b));
                    break;
                case MUL:
                    tokenStack.push(new NumberToken(a * b));
                    break;
                case PLUS:
                    tokenStack.push(new NumberToken(a + b));
                    break;
            }
        }
    }

    @Override
    public void visit(Brace token) {

    }

    @Override
    public void visit(Operation token) {
        tokenStack.push(token);
        argsCount.push(0);
    }

    public int calculate() {
        tokens.forEach(token -> {
            if (token instanceof Operation) {
                visit((Operation) token);
            }
            if (token instanceof NumberToken) {
                visit((NumberToken) token);
            }
        });

        return ((NumberToken) tokenStack.pop()).getValue();
    }
}
