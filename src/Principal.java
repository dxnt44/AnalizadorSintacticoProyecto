import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Principal {
    static String nombrearchivo = "";
    static boolean existenErrores = false;

    public static void main(String[] args) throws IOException {
        ejecutarPrompt();
        ejecutarArchivoDeTexto("C:\\Users\\danie\\OneDrive\\Escritorio\\ISC\\5to\\Compiladores\\AnalizadorSintacticoPF\\src\\"+nombrearchivo);
    }

    private static void ejecutarPrompt() throws IOException{
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for(;;){
            System.out.print(">>> ");
            String linea = reader.readLine();


            if(linea.equals("archivo")) {
                System.out.print("Nombre >>> ");
                nombrearchivo = reader.readLine();
                break;
            }

            if(linea == null) break; // Presionar Ctrl + D
            ejecutar(linea);
            existenErrores = false;
        }
    }

    private static void ejecutarArchivoDeTexto(String nombreArchivo) {
        try {
            FileReader fileReader = new FileReader(nombreArchivo);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                ejecutar(linea);
            }

            bufferedReader.close();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    private static void ejecutar(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
/*
        for(Token token : tokens){
            System.out.println(token);
        }
*/
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    /*
    El método error se puede usar desde las distintas clases
    para reportar los errores:
    Interprete.error(....);
     */
    static void error(int linea, String mensaje){
        reportar(linea, "", mensaje);
    }

    private static void reportar(int linea, String donde, String mensaje){
        System.err.println(
                "[linea " + linea + "] Error " + donde + ": " + mensaje
        );
        existenErrores = true;
    }



}

