package lexer;

import java.util.Arrays;
import java.util.HashSet;

/*
 这个包是用来提供词法分析中一些常用的功能函数，包括：
    1、关键词，界符，操作符的定义
    2、字母，数字，下划线的判定函数
    3、可能是dfa初始化函数
    4、等等
 */
public class util {


    public static String[] keywords = { "auto", "double", "int", "struct",
            "break", "else", "long", "switch", "case", "enum", "register",
            "typedef", "char", "extern", "return", "union", "const", "float",
            "short", "unsigned", "continue", "for", "signed", "void",
            "default", "goto", "sizeof", "volatile", "do", "if", "while",
            "static", "main", "String"};

    public static String[] operator = { "+","-","*","/","%","!","=","<",">","|","&",
            "!=","==",">=","<=","&&","||","++","--"};


    private static String[] delimiters = {
            ",", ";", "[", "]", "{", "}", "(", ")"
    };

    private static HashSet<String> keywords_set = new HashSet<String>(Arrays.asList(keywords));

    private static HashSet<String> operator_set = new HashSet<String>(Arrays.asList(operator));

    private static HashSet<String> delimiter_set = new HashSet<String>(Arrays.asList(delimiters));



    public static Boolean isDigit(char ch){
        return ch>='0' && ch<= '9';
    }

    public static Boolean isAlpha(char ch){
        return (ch>='a' && ch<='z') || (ch>='A' && ch<='Z');
    }

    public static Boolean isKeyWord(String str){
        return keywords_set.contains(str);
    }

    public static Boolean isOperator(String op){
        return operator_set.contains(op);
    }

    public static Boolean isDelimiter(String deli){
        return  delimiter_set.contains(deli);
    }

//    private static int comment_dfa(){
//
//    }

    // 一共有0-6个状态，接受状态是1,3,6
    /*
    对表的说明
    可接受的字符有四类： d，., E/e, +/-
        d  .  E/e  +/-
     0  1  -1  -1  -1
     1  1   2   4  -1
     2  3  -1  -1  -1
     3  3  -1   4  -1
     4  6  -1  -1   5
     5  6  -1  -1  -1
     6  6  -1  -1  -1
     */
    public static int[][] digit_dfa_table = new int[][]{
            {1,-1,-1,-1},
            {1,2,4,-1},
            {3,-1,-1,-1},
            {3,-1,4,-1},
            {6,-1,-1,5},
            {6,-1,-1,-1},
            {6,-1,-1,-1}
    };
    // 接受状态是4

    /*
    对表的说明
    可接受的字符有四类： a, b, '\\', '\''  a是在Character中有定义的字符， b是除'\\','\''之外的合法字符
        '\''  '\\''  a  b
     0   1   -1  -1  -1
     1  -1   -1   2   3
     2   4   -1  -1  -1
     3  -1   -1  -1   2
     4  -1   -1  -1  -1
     */
    public static int[][] char_dfa_table = new int[][]{
            {1,-1,-1,-1},
            {-1,3,2,-1},
            {4,-1,-1,-1},
            {-1,-1,-1,2},
            {-1,-1,-1,-1}
    };



}
