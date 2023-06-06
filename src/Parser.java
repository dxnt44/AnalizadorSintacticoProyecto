import javax.swing.*;
import java.beans.Expression;
import java.util.List;

public class Parser {

    private final List<Token> tokens;

    private final Token identificador = new Token(TipoToken.IDENTIFICADOR, "");
    private final Token and = new Token(TipoToken.Y, "Y");
    private final Token class_ = new Token(TipoToken.CLASE, "CLASE");
    private final Token else_ = new Token(TipoToken.ADEMAS, "ADEMAS");
    private final Token false_ = new Token(TipoToken.FALSO, "FALSO");
    private final Token for_ = new Token(TipoToken.PARA, "PARA");
    private final Token fun = new Token(TipoToken.FUN, "FUN");
    private final Token if_ = new Token(TipoToken.SI, "SI");
    private final Token null_ = new Token(TipoToken.SI, "NULO");
    private final Token or_ = new Token(TipoToken.O, "O");
    private final Token print_ = new Token(TipoToken.IMPRIMIR, "IMPRIMIR");
    private final Token return_ = new Token(TipoToken.RETORNAR, "RETORNAR");
    private final Token super_ = new Token(TipoToken.SUPER, "SUPER");
    private final Token this_ = new Token(TipoToken.ESTE, "ESTE");
    private final Token true_ = new Token(TipoToken.VERDADERO, "VERDADERO");
    private final Token var = new Token(TipoToken.VAR, "VAR");
    private final Token while_ = new Token(TipoToken.MIENTRAS, "MIENTRAS");
    private final Token parentesis_izq = new Token(TipoToken.PARENTESIS_IZQUIERDO, "(");
    private final Token parentesis_der = new Token(TipoToken.PARENTESIS_DERECHO, ")");
    private final Token llave_izq = new Token(TipoToken.LLAVE_IZQUIERDA, "{");
    private final Token llave_der = new Token(TipoToken.LLAVE_DERECHA, "}");
    private final Token coma = new Token(TipoToken.COMA, ",");
    private final Token punto = new Token(TipoToken.PUNTO, ".");
    private final Token punto_y_coma = new Token(TipoToken.PUNTO_Y_COMA, ";");
    private final Token suma = new Token(TipoToken.SUMA, "+");
    private final Token resta = new Token(TipoToken.RESTA, "-");
    private final Token multiplicacion = new Token(TipoToken.MULTIPLICACION, "*");
    private final Token division = new Token(TipoToken.DIVISION, "/");
    private final Token negacion = new Token(TipoToken.NEGACION, "!");
    private final Token asignacion = new Token(TipoToken.ASIGNACION, "=");
    private final Token igualdad = new Token(TipoToken.IGUALDAD, "==");
    private final Token distinto = new Token(TipoToken.DISTINTO, "!=");
    private final Token menor_que = new Token(TipoToken.MENOR_QUE, "<");
    private final Token mayor_que = new Token(TipoToken.MAYOR_QUE, ">");
    private final Token menor_o_igual_que = new Token(TipoToken.MENOR_O_IGUAL_QUE, "<=");
    private final Token mayor_o_igual_que = new Token(TipoToken.MAYOR_O_IGUAL_QUE, ">=");
    private final Token number = new Token(TipoToken.NUMERO, "NUMERO");
    private final Token string = new Token(TipoToken.CADENA, "CADENA");
    private final Token finCadena = new Token(TipoToken.EOF, "");

    private int i = 0;
    private boolean hayErrores = false;

    private Token preanalisis;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void parse() {
        i = 0;
        preanalisis = tokens.get(i);
        PROGRAM();

        if (!hayErrores && !preanalisis.equals(finCadena)) {
            System.out.println("Error en la posición " + preanalisis.posicion + ". No se esperaba el token " + preanalisis.tipo);
        } else if (!hayErrores && preanalisis.equals(finCadena)) {
            System.out.println("Consulta válida");
        }

        /*if(!preanalisis.equals(finCadena)){
            System.out.println("Error en la posición " + preanalisis.posicion + ". No se esperaba el token " + preanalisis.tipo);
        }else if(!hayErrores){
            System.out.println("Consulta válida");
        }*/
    }

    void PROGRAM(){
        DECLARATION();
    }

    void DECLARATION() {
        if (hayErrores) return;

        if (preanalisis.equals(class_)) {
            CLASS_DECL();
            DECLARATION();
        } else if (preanalisis.equals(fun)) {
            FUN_DECL();
            DECLARATION();
        } else if (preanalisis.equals(var)) {
            VAR_DECL();
            DECLARATION();
        } else if (preanalisis.equals(for_) || preanalisis.equals(if_) || preanalisis.equals(print_)
                || preanalisis.equals(return_) || preanalisis.equals(while_) || preanalisis.equals(llave_izq)
                || preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            STATEMENT();
            DECLARATION();
        }
    }

    void CLASS_DECL() {
        if (hayErrores) return;

        if (preanalisis.equals(class_)) {
            coincidir(class_);
            coincidir(identificador);
            CLASS_INHER();
            coincidir(llave_izq);
            FUNCTIONS();
            coincidir(llave_der);
        }
    }

    void CLASS_INHER() {
        if (hayErrores) return;

        if (preanalisis.equals(menor_que)) {
            coincidir(menor_que);
            coincidir(identificador);
        }
    }

    void FUN_DECL() {
        if (hayErrores) return;

        if (preanalisis.equals(fun)) {
            coincidir(fun);
            FUNCTION();
        } else {
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion + ". Error en la declaracion de clase.");
        }
    }

    void VAR_DECL() {
        if (hayErrores) return;

        if (preanalisis.equals(var)) {
            coincidir(var);
            coincidir(identificador);
            VAR_INIT();
            coincidir(punto_y_coma);
        }
    }

    void VAR_INIT() {
        if (hayErrores) return;

        if (preanalisis.equals(asignacion)) {
            coincidir(asignacion);
            EXPRESSION();
        }
    }

    void STATEMENT() {
        if (hayErrores) return;

        if (preanalisis.equals(for_)) {
            FOR_STMT();
        } else if (preanalisis.equals(if_)) {
            IF_STMT();
        } else if (preanalisis.equals(print_)) {
            PRINT_STMT();
        } else if (preanalisis.equals(return_)) {
            RETURN_STMT();
        } else if (preanalisis.equals(while_)) {
            WHILE_STMT();
        } else if (preanalisis.equals(llave_izq)) {
            BLOCK();
        } else if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            EXPRESSION_STMT();
        }
    }

    void EXPRESSION_STMT() {
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)) {
            EXPRESSION();
            coincidir(punto_y_coma);
        }
    }

    void FOR_STMT() {
        if (hayErrores) return;

        if (preanalisis.equals(for_)) {
            coincidir(for_);
            coincidir(parentesis_izq);
            FOR_STMT_1();
            FOR_STMT_2();
            FOR_STMT_3();
            coincidir(parentesis_der);
            STATEMENT();
        }
    }

    void FOR_STMT_1() {
        if (hayErrores) return;

        if (preanalisis.equals(var)) {
            VAR_DECL();
        } else if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)) {
            EXPRESSION_STMT();
        } else if (preanalisis.equals(punto_y_coma)) {
            coincidir(punto_y_coma);
        }
    }

    void FOR_STMT_2() {
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)) {
            EXPRESSION();
            coincidir(punto_y_coma);
        } else if (preanalisis.equals(punto_y_coma)) {
            coincidir(punto_y_coma);
        }
    }

    void FOR_STMT_3() {
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)) {
            EXPRESSION();
        }
    }

    void IF_STMT() {
        if (hayErrores) return;

        if (preanalisis.equals(if_)) {
            coincidir(if_);
            coincidir(parentesis_izq);
            EXPRESSION();
            coincidir(parentesis_der);
            STATEMENT();
            ELSE_STATEMENT();
        }
    }

    void ELSE_STATEMENT() {
        if (hayErrores) return;

        if (preanalisis.equals(else_)) {
            coincidir(else_);
            STATEMENT();
        }
    }

    void PRINT_STMT(){
        if (hayErrores) return;

        if(preanalisis.equals(print_)){
            coincidir(print_);
            EXPRESSION();
            coincidir(punto_y_coma);
        }
    }

    void RETURN_STMT(){
        if (hayErrores) return;

        if (preanalisis.equals(return_)){
            coincidir(return_);
            RETURN_EXP_OPC();
            coincidir(punto_y_coma);
        }
    }

    void RETURN_EXP_OPC(){
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            EXPRESSION();
        }
    }

    void WHILE_STMT(){
        if (hayErrores) return;

        if(preanalisis.equals(while_)){
            coincidir(while_);
            coincidir(parentesis_izq);
            EXPRESSION();
            coincidir(parentesis_der);
            STATEMENT();
        }
    }

    void BLOCK(){
        if (hayErrores) return;

        if(preanalisis.equals(llave_izq)){
            coincidir(llave_izq);
            BLOC_DECL();
            coincidir(llave_der);
        }
    }


    void BLOC_DECL(){
        if (hayErrores) return;

        if (preanalisis.equals(class_) || preanalisis.equals(fun) || preanalisis.equals(var)
                || preanalisis.equals(for_) || preanalisis.equals(if_) || preanalisis.equals(print_)
                || preanalisis.equals(return_) || preanalisis.equals(while_) || preanalisis.equals(llave_izq)
                || preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            DECLARATION();
            BLOC_DECL();
        }
    }

    void EXPRESSION(){
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)) {
            ASSIGNMENT();
        }
    }

    void ASSIGNMENT(){
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            LOGIC_OR();
            ASSIGNMENT_OPC();
        }
    }

    void ASSIGNMENT_OPC(){
        if (hayErrores) return;

        if (preanalisis.equals(asignacion)){
            coincidir(asignacion);
            EXPRESSION();
        }
    }

    void LOGIC_OR(){
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            LOGIC_AND();
            LOGIC_OR_2();
        }
    }


    void LOGIC_OR_2(){
        if (hayErrores) return;

        if (preanalisis.equals(or_)) {
            coincidir(or_);
            LOGIC_AND();
            LOGIC_OR_2();
        }
    }

    void LOGIC_AND(){
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            EQUALITY();
            LOGIC_AND_2();
        }
    }


    void LOGIC_AND_2(){
        if (hayErrores) return;

        if (preanalisis.equals(and)){
            coincidir(and);
            EQUALITY();
            LOGIC_AND_2();
        }
    }

    void EQUALITY(){
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            COMPARISON();
            EQUALITY_2();
        }
    }

    void EQUALITY_2(){
        if (hayErrores) return;

        if (preanalisis.equals(distinto)){
            coincidir(distinto);
            COMPARISON();
            EQUALITY_2();
        }else if (preanalisis.equals(igualdad)){
            coincidir(igualdad);
            COMPARISON();
            EQUALITY_2();
        }
    }

    void COMPARISON(){
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            TERM();
            COMPARISON_2();
        }
    }


    void COMPARISON_2(){
        if (hayErrores) return;

        if (preanalisis.equals(mayor_que)){
            coincidir(mayor_que);
            TERM();
            COMPARISON_2();
        }else if (preanalisis.equals(mayor_o_igual_que)){
            coincidir(mayor_o_igual_que);
            TERM();
            COMPARISON_2();
        }else if (preanalisis.equals(menor_que)){
            coincidir(menor_que);
            TERM();
            COMPARISON_2();
        }else if(preanalisis.equals(menor_o_igual_que)){
            coincidir(menor_o_igual_que);
            TERM();
            COMPARISON_2();
        }
    }


    void TERM(){
        if (hayErrores) return;

        if(preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            FACTOR();
            TERM_2();
        }
    }

    void TERM_2(){
        if (hayErrores) return;

        if (preanalisis.equals(resta)){
            coincidir(resta);
            FACTOR();
            TERM_2();
        }else if(preanalisis.equals(suma)){
            coincidir(suma);
            FACTOR();
            TERM_2();
        }
    }

    void FACTOR(){
        if (hayErrores) return;

        if(preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            UNARY();
            FACTOR_2();
        }
    }


    void FACTOR_2(){
        if (hayErrores) return;

        if (preanalisis.equals(division)){
            coincidir(division);
            UNARY();
            FACTOR_2();
        }else if (preanalisis.equals(multiplicacion)){
            coincidir(multiplicacion);
            UNARY();
            FACTOR_2();
        }
    }

    void UNARY(){
        if (hayErrores) return;

        if (preanalisis.equals(negacion)){
            coincidir(negacion);
            UNARY();
        }else if (preanalisis.equals(resta)){
            coincidir(resta);
            UNARY();
        }else if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_)){
            CALL();
        }
    }


    void CALL(){
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_)){
            PRIMARY();
            CALL_2();
        }
    }

    void CALL_2(){
        if (hayErrores) return;

        if (preanalisis.equals(parentesis_izq)){
            coincidir(parentesis_izq);
            ARGUMENTS_OPC();
            CALL_2();
        }else if(preanalisis.equals(punto)){
            coincidir(punto);
            coincidir(identificador);
            CALL_2();
        }
    }


    void CALL_OPC(){
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_)){
            CALL();
            coincidir(punto);
        }
    }

    void PRIMARY(){
        if (hayErrores) return;

        if (preanalisis.equals(true_)){
            coincidir(true_);
        }else if (preanalisis.equals(false_)){
            coincidir(false_);
        }else if (preanalisis.equals(null_)){
            coincidir(this_);
        }else if (preanalisis.equals(this_)){
            coincidir(this_);
        }else if (preanalisis.equals(number)){
            coincidir(number);
        }else if (preanalisis.equals(string)){
            coincidir(string);
        }else if (preanalisis.equals(identificador)){
            coincidir(identificador);
        }else if (preanalisis.equals(parentesis_izq)){
            coincidir(parentesis_izq);
            EXPRESSION();
            coincidir(parentesis_der);
        }else if (preanalisis.equals(super_)){
            coincidir(super_);
            coincidir(punto);
            coincidir(identificador);
        }
    }


    void FUNCTION(){
        if (hayErrores) return;

        if (preanalisis.equals(identificador)){
            coincidir(identificador);
            coincidir(parentesis_izq);
            PARAMETERS_OPC();
            coincidir(parentesis_der);
            BLOCK();
        }
    }

    void FUNCTIONS(){
        if (hayErrores) return;

        if (preanalisis.equals(identificador)){
            FUNCTION();
            FUNCTIONS();
        }
    }


    void PARAMETERS_OPC(){
        if (hayErrores) return;

        if (preanalisis.equals(identificador)){
            PARAMETERS();
        }
    }


    void PARAMETERS(){
        if (hayErrores) return;

        if (preanalisis.equals(identificador)){
            coincidir(identificador);
            PARAMETERS_2();
        }
    }


    void PARAMETERS_2(){
        if (hayErrores) return;

        if (preanalisis.equals(coma)){
            coincidir(coma);
            coincidir(identificador);
            PARAMETERS_2();
        }
     }


     void ARGUMENTS_OPC(){
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            ARGUMENTS();
        }
     }


     void ARGUMENTS(){
        if (hayErrores) return;

        if (preanalisis.equals(true_) || preanalisis.equals(false_) || preanalisis.equals(null_)
                || preanalisis.equals(this_) || preanalisis.equals(number) || preanalisis.equals(string)
                || preanalisis.equals(identificador) || preanalisis.equals(super_) || preanalisis.equals(negacion)
                || preanalisis.equals(resta)){
            EXPRESSION();
            ARGUMENTS_2();
        }
     }

     void ARGUMENTS_2(){
        if (hayErrores) return;

        if (preanalisis.equals(coma)){
            coincidir(coma);
            EXPRESSION();
            ARGUMENTS();
        }
     }


    void coincidir(Token t){
        if(hayErrores) return;

        if(preanalisis.tipo == t.tipo){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion + ". Se esperaba un  " + t.tipo);

        }
    }

}

