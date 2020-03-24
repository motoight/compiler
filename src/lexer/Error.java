package lexer;

public class Error {
    public String type;
    public String value;
    public int line_num;

    public static String[] errortype = {
            "无符号数输入错误",
            "字符串常量出错",
            "非法字符"
    };

    public Error(String type, String value, int line_num) {
        this.type = type;
        this.value = value;
        this.line_num = line_num;
    }

    @Override
    public String toString() {
        return "Error{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", line_num=" + line_num +
                '}';
    }

    public static void main(String[] args) {
        StringBuilder str = new StringBuilder("fgad");

        System.out.println(str.toString());
    }
}
