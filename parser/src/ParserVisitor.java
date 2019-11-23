import token.Brace;
import token.NumberToken;
import token.Operation;
import token.Token;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParserVisitor {

    private final List<Token> tokens;
    private int curPos;

    public ParserVisitor(List<Token> tokens) {
        this.tokens = tokens;
        curPos = 0;
    }

    private List<Token> F() throws ParseException {
        if (tokens.get(curPos) instanceof NumberToken) {
            curPos++;
            List<Token> res = new ArrayList<>();
            res.add(tokens.get(curPos - 1));
            return res;
        }
        if (tokens.get(curPos) instanceof Operation &&
                ((Operation) tokens.get(curPos)).getOperationType() == Operation.OperationType.MINUS
        ) {
            curPos++;
            List<Token> start = List.of(new Operation(Operation.OperationType.MUL), new NumberToken(-1));
            List<Token> cont = F();
            return Stream.concat(start.stream(), cont.stream()).collect(Collectors.toList());
        }
        if (tokens.get(curPos) instanceof Brace &&
                ((Brace) tokens.get(curPos)).getBraceType() == Brace.BraceType.LEFT)
        {
            curPos++;
            List<Token> res = E();
            if (!(tokens.get(curPos) instanceof Brace)
                    || ((Brace) tokens.get(curPos)).getBraceType() == Brace.BraceType.RIGHT)
            {
                throw new ParseException(") expected", curPos);
            }
            return res;
        }
        throw new AssertionError();
    }

    private List<Token> T() throws ParseException {
        List<Token> args = F();
        List<Token> opers = new ArrayList<>();
        do {
            if (tokens.get(curPos) instanceof Operation &&
                    (((Operation) tokens.get(curPos)).getOperationType() == Operation.OperationType.MUL ||
                            ((Operation) tokens.get(curPos)).getOperationType() == Operation.OperationType.DIV))
            {
                opers.add(tokens.get(curPos));
                curPos++;
                List<Token> f = F();
                args.addAll(f);
            } else {
                Collections.reverse(opers);
                return Stream.concat(opers.stream(), args.stream()).collect(Collectors.toList());
            }
        } while (true);
    }

    private List<Token> E() throws ParseException {
        List<Token> args = T();
        List<Token> opers = new ArrayList<>();
        do {
            if (tokens.get(curPos) instanceof Operation &&
                    (((Operation) tokens.get(curPos)).getOperationType() == Operation.OperationType.PLUS ||
                            ((Operation) tokens.get(curPos)).getOperationType() == Operation.OperationType.MINUS))
            {
                opers.add(tokens.get(curPos));
                curPos++;
                args.addAll(T());
            } else {
                Collections.reverse(opers);
                return Stream.concat(opers.stream(), args.stream()).collect(Collectors.toList());
            }
        } while (true);
    }

    public List<Token> parse() throws ParseException {
        return E();
    }
}
