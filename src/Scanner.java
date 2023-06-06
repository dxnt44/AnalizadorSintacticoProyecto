import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    private static final Map<String, TipoToken> palabrasReservadas;
    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and", TipoToken.Y);
        palabrasReservadas.put("class", TipoToken.CLASE);
        palabrasReservadas.put("else", TipoToken.ADEMAS);
        palabrasReservadas.put("false", TipoToken.FALSO);
        palabrasReservadas.put("for", TipoToken.PARA);
        palabrasReservadas.put("fun", TipoToken.FUN);
        palabrasReservadas.put("if", TipoToken.SI);
        palabrasReservadas.put("null", TipoToken.NULO);
        palabrasReservadas.put("or", TipoToken.O);
        palabrasReservadas.put("print", TipoToken.IMPRIMIR);
        palabrasReservadas.put("return", TipoToken.RETORNAR);
        palabrasReservadas.put("super", TipoToken.SUPER);
        palabrasReservadas.put("this", TipoToken.ESTE);
        palabrasReservadas.put("true", TipoToken.VERDADERO);
        palabrasReservadas.put("var", TipoToken.VAR);
        palabrasReservadas.put("while", TipoToken.MIENTRAS);
    }

    Scanner(String source){
        this.source = source + " ";
    }

    List<Token> scanTokens(){
        int estado = 0;
        char caracter = 0;
        String lexema = "";
        int inicioLexema = 0;

        for(int i=0; i<source.length(); i++){
            caracter = source.charAt(i);

            switch (estado){
                case 0:
                    if(caracter == '('){
                        tokens.add(new Token(TipoToken.PARENTESIS_IZQUIERDO, "(", i + 1));
                    }
                    else if(caracter == ')'){
                        tokens.add(new Token(TipoToken.PARENTESIS_DERECHO, ")", i + 1));
                    }
                    else if(caracter == '{'){
                        tokens.add(new Token(TipoToken.LLAVE_IZQUIERDA, "{", i + 1));
                    }
                    else if(caracter == '}') {
                        tokens.add(new Token(TipoToken.LLAVE_DERECHA, "}", i + 1));
                    }
                    else if(caracter == ','){
                        tokens.add(new Token(TipoToken.COMA, ",", i + 1));
                    }
                    else if(caracter == '.'){
                        tokens.add(new Token(TipoToken.PUNTO, ".", i + 1));
                    }
                    else if(caracter == ';'){
                        tokens.add(new Token(TipoToken.PUNTO_Y_COMA, ";", i + 1));
                    }
                    else if(caracter == '+'){
                        tokens.add(new Token(TipoToken.SUMA, "+", i + 1));
                    }
                    else if(caracter == '-'){
                        tokens.add(new Token(TipoToken.RESTA, "-", i + 1));
                    }
                    else if(caracter == '*'){
                        tokens.add(new Token(TipoToken.MULTIPLICACION, "*", i + 1));
                    }
                    else if(caracter == '/'){
                        tokens.add(new Token(TipoToken.DIVISION, "/", i + 1));
                    }
                    //else if(caracter == '!'){
                    //    tokens.add(new Token(TipoToken.NEGACION, "!", i + 1));
                    //}
                    else if (source.charAt(caracter-1) == '!') {
                        if (caracter < source.length() && source.charAt(caracter) == '=') {
                            tokens.add(new Token(TipoToken.DISTINTO, "!=", i + 2));
                        } else {
                            tokens.add(new Token(TipoToken.NEGACION, "!", i + 1));
                        }
                    }
                    else if(caracter == '='){
                        tokens.add(new Token(TipoToken.ASIGNACION, "=", i + 1));
                    }
                    else if(caracter == '<'){
                        tokens.add(new Token(TipoToken.MENOR_QUE, "<", i + 1));
                    }
                    else if(caracter == '>'){
                        tokens.add(new Token(TipoToken.MAYOR_QUE, ">", i + 1));
                    }
                    //else if(caracter.equals('>')){
                    //    tokens.add(new Token(TipoToken.MAYOR_QUE, ">", i + 1));
                    //}
                    else if(Character.isAlphabetic(caracter)){
                        estado = 1;
                        lexema = lexema + caracter;
                        inicioLexema = i;
                    }
                    break;

                case 1:
                    if(Character.isAlphabetic(caracter) || Character.isDigit(caracter) ){
                        lexema = lexema + caracter;
                    }
                    else{
                        TipoToken tt = palabrasReservadas.get(lexema);
                        if(tt == null){
                            tokens.add(new Token(TipoToken.IDENTIFICADOR, lexema, inicioLexema + 1));
                        }
                        else{
                            tokens.add(new Token(tt, lexema, inicioLexema + 1));
                        }

                        estado = 0;
                        i--;
                        lexema = "";
                        inicioLexema = 0;
                    }
                    break;
            }
        }
        tokens.add(new Token(TipoToken.EOF, "", source.length()));

        return tokens;
    }

}

