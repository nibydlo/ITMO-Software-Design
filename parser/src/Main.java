import token.Token;
import visitor.CalcVisitor;
import visitor.PrintVisitor;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws ParseException {
        String expression = args.length == 0 ? " " : String.join(" ", args);
        System.out.println("EXPRESSION: " + expression);

        Tokenizer tokenizer = new Tokenizer(new ByteArrayInputStream(expression.getBytes(StandardCharsets.UTF_8)));
        List<Token> tokens = tokenizer.tokenize();

        System.out.println(tokens.stream().map(Token::toString).collect(Collectors.toList()));
        ParserVisitor parserVisitor = new ParserVisitor(tokens);
        List<Token> polish = parserVisitor.parse();

        PrintVisitor printVisitor = new PrintVisitor(polish);
        String polishString = printVisitor.toString();
        System.out.println(polishString);

        CalcVisitor calcVisitor = new CalcVisitor(polish);
        Integer value = calcVisitor.calculate();
        System.out.println(value);
    }
}
