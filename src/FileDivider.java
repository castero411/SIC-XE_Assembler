import java.io.*;

public class FileDivider {
    private String inputFile = "TextFiles/code.txt";
    private String outputFile = "TextFiles/file.txt";

    FileDivider(String in,String out){
        inputFile = in;
        outputFile = out;
    }
    FileDivider(){

    }

    public void FileDividing(){

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer1 = new BufferedWriter(new FileWriter(outputFile))
        ) {
            String line;
            while ((line = reader.readLine())!=null){
                String [] parts = line.split(" ");
                String label = "";
                String instruction = "";
                String reference = "";

                if(parts.length == 1){
                    label = "null";
                    instruction = parts[0];
                    reference = "null";
                }else if (parts.length == 2){
                    label = "null";
                    instruction = parts[0];
                    reference = parts[1];
                }else if (parts.length == 3){
                    label = parts[0];
                    instruction = parts[1];
                    reference = parts[2];
                }
                writer1.write(label+"_"+instruction+"_"+reference);
                writer1.newLine();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

