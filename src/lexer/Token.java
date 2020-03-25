package lexer;

public class Token {
    private String type;
    private String value;
    private int line_num;

    public Token(String type, String value, int line_num) {
        this.type = type;
        this.value = value;
        this.line_num  = line_num;

    }

//    @Override
//    public String toString() {
//        return "<" +
//                type +", "+value
//                + ">";
//    }


    public String getType() {
        return type;
    }


    public String getValue() {
        return value;
    }


    public int getLine_num() {
        return line_num;
    }


    @Override
    public String toString() {
        return "Token{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", line_num=" + line_num +
                '}';
    }
}
