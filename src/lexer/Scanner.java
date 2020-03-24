package lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Scanner {
    private static int MAX_LENGTH = 1024;
    private char char_pt;
    private int line_pt;
    private String input;
    private List<Token> tokens;
    private List<Error> errors_info;




    public Scanner() {
        this.char_pt = 0;
        this.line_pt = 1;
//      this.input = new String[];
        this.tokens = new ArrayList<Token>();
        this.errors_info = new ArrayList<Error>();
    }


    public String readfile(String path){
        try {
            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[MAX_LENGTH];
            String rawinput = "";

            while(true){
                int flag = inputStream.read(bytes);
                rawinput = rawinput + new String(bytes,"GBK");
                if (flag < MAX_LENGTH){ break; }
            }

            return  rawinput;
//            this.input = rawinput.split("\n");
//            for (int i=0;i<input.length;i++){
//                System.out.println(input[i]);
//            }
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public char getNextchar(String input){
        if (char_pt>input.length()){
            char_pt++;
            return 0;
        }
        else{
            return input.charAt(char_pt++);
        }
    }

    public void lex(String path){
        try{
            String text = this.readfile(path);
            char ch ;
            // 当获取下一个字符返回值不为零，说明待分析串还未结束
            while((ch = getNextchar(text))!=0){
                if (ch== ' ' || ch== '\t' || ch=='\r'){continue;}
                // windows 文本格式的换行符是\r\n 先return， 再nextline，只在\n时将行号++
                if ( ch== '\n'){
                    line_pt++;
                    continue;
                }

                if (util.isAlpha(ch) || ch== '_'){
                    StringBuilder str = new StringBuilder();
                    do{
                        str.append(ch);
                        ch = getNextchar(text);

                    }while((util.isAlpha(ch) || ch== '_' || util.isDigit(ch)) && ch!=0);
                    String identifier = str.toString();
                    Token token;
                    if (util.isKeyWord(identifier)){
                        token = new Token(identifier.toUpperCase(),identifier,line_pt);
                    }else {
                        token = new Token("标识符",identifier,line_pt);
                    }
                    tokens.add(token);
                    // 碰到非标识符中字符，将字符指针回退一位，进入下次循环
                    this.char_pt--;
                    continue;
                }

                if (util.isDigit(ch)){
                    StringBuilder str = new StringBuilder();
                    int state =1;
                    boolean isfloat = false;
                    boolean isSci = false;

                    do {
                        str.append(ch);
                        ch = getNextchar(text);
                        if (util.isDigit(ch)){
                            state = util.digit_dfa_table[state][0];
                        }
                        else if (ch=='.'){
                            state = util.digit_dfa_table[state][1];
                            isfloat = true;
                        }
                        else if (ch=='E'||ch=='e'){
                            state = util.digit_dfa_table[state][2];
                            isSci = true;
                            isfloat = false;
                        }
                        else if (ch=='+' || ch=='-'){
                            state = util.digit_dfa_table[state][3];
                        }else{
                            // 输出字符不是该自动机接受的字符
                            break;
                        }
                        // state == -1说明此时处于自动机卡住了，跳出
                    }while(state!=-1);

                    //1,3,5是接受状态
                    if (state==1 || state==3 || state==6){
                        Token token;
                        if(isfloat) {
                            token = new Token("浮点数", str.toString(), line_pt);
                        }
                        if(isSci){
                            token = new Token("科学计数法",str.toString(),line_pt);
                        }
                        else{
                            token = new Token("整型数",str.toString(),line_pt);
                        }
                        tokens.add(token);
        // 此时说明此时不在接受状态，需要报错
                    }else {
                        errors_info.add(new Error(Error.errortype[0],str.toString(), line_pt));
                    }

                    // 结束数字识别，重新开始
                    this.char_pt--;
                    continue;
                }
                // 识别注释 单行双行
                if (ch== '/'){
                    StringBuilder str = new StringBuilder();
                    str.append(ch);
                    ch = getNextchar(text);
                    if(ch=='/'){
                        do{
                            str.append(ch);
                            ch = getNextchar(text);
                        }while(ch!='\r' && ch!=0);
                        Token token = new Token("单行注释", str.toString(),line_pt);
                        tokens.add(token);
                    }
                    else if(ch=='*'){
                        str.append(ch);
                        int state = 1;
                        do{
                            ch = getNextchar(text);
                            str.append(ch);
                            if (ch=='*'){
                                state =2;
                            }else if(ch=='/' && state==2){
                                state=3;
                            }else{
                                if (ch=='\n'){
                                    line_pt++;
                                }
                                state = 1;
                            }

                        }while (state!=3 && ch!=0);
                        Token token = new Token("多行注释", str.toString(),line_pt);
                        tokens.add(token);
                    }

                continue;
                }
                // 识别字符串常量
                if (ch== '\"'){
                    boolean hasMistake = false;
                    StringBuilder str = new StringBuilder();
                    do{
                        str.append(ch);
                        ch = getNextchar(text);
                        if (ch=='\r'||ch=='\n'){
                            line_pt++;
                            hasMistake = true;
                            break;
                        }
                    }while(ch!='\"' && ch!=0);


                    if(hasMistake){
                        errors_info.add(new Error(Error.errortype[1],str.toString(),line_pt));
                    }else {
                        // 将导致循环跳出的字符'\"'加入到str中
                        str.append(ch);
                        tokens.add(new Token("字符串常量",str.toString(),line_pt));
                    }
                    continue;
                }

                // 识别字符常量
                if (ch=='\''){
                    StringBuilder str = new StringBuilder();
                    int state = 1;
                    do{
                        str.append(ch);
                        ch = getNextchar(text);
                        if (ch=='\''){
                            state = util.char_dfa_table[state][0];
                        }
                        else if(ch=='\\'){
                            state = util.char_dfa_table[state][1];
                        }
                        else if(Character.isDefined(ch)){
                            state = util.char_dfa_table[state][2];
                        }
                        else if(ch!='\\' && ch!='\''){
                            state = util.char_dfa_table[state][3];
                        }
                        if (ch=='\n'||ch=='\r'){
                            state = -1;
                        }
                    }while(state!=4 && state!=-1 && ch!=0);

                    if (state==4){
                        str.append(ch);
                        tokens.add(new Token("字符常量",str.toString(),line_pt));
                    }else {
                        this.char_pt--;
                        errors_info.add(new Error(Error.errortype[2],str.toString(),line_pt));
                    }

                }

                // 识别界符
                if (util.isDelimiter(Character.toString(ch))){
                    tokens.add(new Token("界符",Character.toString(ch),line_pt));
                    continue;
                }

                // 识别操作符
                if (util.isOperator(Character.toString(ch))){
                    StringBuilder str = new StringBuilder(Character.toString(ch));
                    ch = getNextchar(text);
                    String temp = str.toString()+ch;
                    if (util.isOperator(temp)){
                        tokens.add(new Token("操作符",temp,line_pt));
                    }else{
                        tokens.add(new Token("操作符",str.toString(),line_pt));
                        char_pt--;
                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner();
//        scanner.readfile("test/code1");
        scanner.lex("test/input_code.txt");

        for(Token t:scanner.tokens){
            System.out.println(t.toString());
        }
        for (Error e : scanner.errors_info){
            System.out.println(e.toString());

        }
    }



}
