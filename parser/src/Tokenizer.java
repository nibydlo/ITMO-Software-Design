import token.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private InputStream is;
    private int curChar;
    private int curPos;
    private Token curToken;

    public Tokenizer(InputStream is) throws ParseException {
        this.is = is;
        curPos = 0;
        nextChar();
    }

    private boolean isBlank(int c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }

    private void nextChar() throws ParseException {
        curPos++;
        try {
            curChar = is.read();
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), curPos);
        }
        while (isBlank(curChar)) {
            nextChar();
        }
    }


    private String cutNumber(int startChar) throws IOException, ParseException {
        StringBuilder res = new StringBuilder(Character.toString((char) curChar));
        nextChar();
        while (Character.isDigit(curChar)) {
            res.append((char) curChar);
            nextChar();
        }
        return new String(res);
    }

    public void nextToken() throws ParseException {
        while (isBlank(curChar)) {
            nextChar();
        }
        switch (curChar) {
            case '(':
                nextChar();
                curToken = new Brace(Brace.BraceType.LEFT);
                break;
            case ')':
                nextChar();
                curToken = new Brace(Brace.BraceType.RIGHT);
                break;
            case '+':
                nextChar();
                curToken = new Operation(Operation.OperationType.PLUS);
                break;
            case '-':
                nextChar();
                curToken = new Operation(Operation.OperationType.MINUS);
                break;
            case '*':
                nextChar();
                curToken = new Operation(Operation.OperationType.MUL);
                break;
            case '/':
                nextChar();
                curToken = new Operation(Operation.OperationType.DIV);
                break;
            case -1:
                curToken = new EndToken();
                break;
            default:
                if (Character.isDigit(curChar)) {
                    try {
                        curToken = new NumberToken(Integer.parseInt(cutNumber(curChar)));
                    } catch (IOException e) {
                        throw new ParseException("Illegal Character" + (char) curChar, curPos);
                    }
                } else {
                    throw new ParseException("Illegal Character " + (char) curChar, curPos);
                }
        }
    }

    private Token getCurToken() {
        return curToken;
    }

    private int getCurPos() {
        return curPos;
    }

    public List<Token> tokenize () throws ParseException {
        List<Token> res = new ArrayList<>();
        nextToken();
        while (!(curToken instanceof EndToken)) {
            res.add(curToken);
            nextToken();
        }
        res.add(new EndToken());
        return res;
    }
}
