import java.io.*;
import java.util.logging.XMLFormatter;

public class PassTwo {
    String LocationCounter = "TextFiles/LocationCounter.txt";
    String Output = "OutputFiles/outputCode.txt";
    String SymbolTable = "TextFiles/SymbolTable.txt";
    String programCounter = "0";
    String base = "0";
    converter c1 = new converter();
    String[][] OpTab = c1.getOptab();
    String ObjectCode;
    void passTwo (){
        try(BufferedReader reader = new BufferedReader(new FileReader(LocationCounter));
            BufferedWriter write = new BufferedWriter(new FileWriter(Output))
        ){
            String line=reader.readLine();
            write.write(line);
            write.newLine();
            while ((line = reader.readLine())!=null) {
                String[] parts = line.split("_");
                ObjectCode = getObjectCode(parts[2],parts[3]);
                write.write(line+"---->"+ObjectCode);
                write.newLine();
                System.out.println("it works");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String getObjectCode(String instruction, String reference) {
        String objectCode = null;
        String opCode = null;
        String format = null;
        boolean format4 = false;

        if (instruction.equals("BASE")){
            setBase(reference);
            return "no-object-code";
        }else if (instruction.equals("START")||instruction.equals("END")){
            return "no-object-code";
        }else if (instruction.equals("RESB")||instruction.equals("RESW")){
            return "no-object-code";
        }else if (instruction.equals("RSUB")){
            return "no-object-code";
        }else if(instruction.equals("BYTE")||instruction.equals("WORD")){
                //TODO: create a byte object code generator
                //use split with '
            return "to be done";
        }


        for (int i = 0; i < 59; i++) {
            if(instruction.equals(OpTab[i][0])){
                opCode = OpTab[i][2];
                format = OpTab[i][1];
                programCounter = addHexadecimal(programCounter,format);
            } else if (instruction.contains("+")) {
                instruction = instruction.substring(1);
                format4 = true;
            }
        }
        System.out.println(instruction);
        if(opCode==null){
            System.err.println("error happened\nplease check your code");
            System.exit(0);
        }

        switch (format){
            case "1":
                objectCode = opCode;
                break;
            case "2":
                objectCode = opCode + getFormat2(reference);
                break;
            case "3":
                objectCode = getFormat3(opCode,reference,format4);
                break;
            default:
                System.err.println("error\ninvalid instruction or reference");
        }
        return objectCode;
    }

    String getFormat2(String reference){
        String[] split;
        String code="";
        if (reference.contains(",")){
            split = reference.split(",");
            for(int i = 0; i < 2; i++){
                if(split[i].equals("A")){
                    code+="0";
                } else if(split[i].equals("X")){
                    code+="1";
                } else if(split[i].equals("L")){
                    code+="2";
                } else if(split[i].equals("B")){
                    code+="3";
                } else if(split[i].equals("S")){
                    code+="4";
                } else if(split[i].equals("Y")){
                    code+="5";
                } else if(split[i].equals("T")){
                    code+="6";
                }else{
                    System.err.println("invalid register");
                    System.exit(0);
                }
            }

        }else{
            if(reference.equals("A")){
                code+="0";
            } else if(reference.equals("X")){
                code+="1";
            } else if(reference.equals("L")){
                code+="2";
            } else if(reference.equals("B")){
                code+="3";
            } else if(reference.equals("S")){
                code+="4";
            } else if(reference.equals("Y")){
                code+="5";
            } else if(reference.equals("T")){
                code+="6";
            } else{
                System.err.println("invalid register");
                System.exit(0);
            }
            code+="0";
        }
        return  code;
    }

     String getFormat3(String opCode,String reference,boolean format4){

        if(opCode.isEmpty()||reference.isEmpty()){
            System.err.println("error/nempty reference");
            System.exit(0);
        }

        String objectCode = "";
        String TargetAddress = "";
        String xpbe = "0";
        String displacement = "";

        if (reference.contains("#")) {
            reference=reference.substring(1);
            xpbe = "0";
            objectCode = addHexadecimal(opCode, "1");

        } else if (reference.contains(",X")) {
            xpbe = "8";
            reference = reference.split(",")[0];

        } else if (reference.contains("@")) {
            xpbe="0";
            reference = reference.substring(1);
            objectCode = addHexadecimal(opCode, "2");
        } else {
            objectCode = addHexadecimal(opCode, "3");
            xpbe="0";
        }

        //get reference displacement
        TargetAddress = getReference(reference);

        if (TargetAddress.isEmpty()){
            System.err.println("wrong reference entered");
        }

        if (reference.matches(".*\\d.*")) {
            TargetAddress = reference;

        }
        displacement = getDisplacement(TargetAddress)[0];
        xpbe = addHexadecimal(getDisplacement(TargetAddress)[1],xpbe);

        if (displacement.length() >= 3) {
            displacement = displacement.substring(displacement.length() - 3);
        }

        displacement = String.format("%03X",Integer.parseInt(displacement,16));


        if (format4) {
            xpbe = addHexadecimal(xpbe, "1");
            displacement = "00" + displacement;
        }

        if(objectCode.length()==1){
            objectCode="0"+objectCode;
        }

        return objectCode+xpbe+displacement;
    }

    String[] getDisplacement(String TargetAddress){
        String[] displacement = new String[2];

        if (Integer.parseInt(TargetAddress,16)<2047){
            displacement[0] = minusHexaDecimal(TargetAddress,programCounter);
            displacement[1] = "4"; // add to the flag bits programCounter flag
        }else{
            displacement[0] = minusHexaDecimal(TargetAddress,base);
            displacement[1] = "2"; // add to the flag bits base flag
        }

        return displacement;
    }


    String getReference(String reference){//working as intended
        String displacement = "";

        try (BufferedReader read = new BufferedReader(new FileReader(SymbolTable))){
            String line ;
            while ((line = read.readLine())!=null){
                String parts[] = line.split("_");
                if(parts[1].equals(reference)){
                    displacement = parts[0];
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (displacement.equals("")){
            displacement = "000";
        }

        return displacement;
    }


    String addHexadecimal(String h1,String h2){
        int sum = Integer.parseInt(h1,16) + Integer.parseInt(h2,16);
        return String.format("%X",sum);
    }
    String minusHexaDecimal(String h1,String h2){
        int sum = Integer.parseInt(h1,16) - Integer.parseInt(h2,16);
        return String.format("%X",sum);
    }


    void setBase(String reference){
        try(BufferedReader read = new BufferedReader(new FileReader("TextFiles/SymbolTable.txt"))) {

            String line;
            while ((line = read.readLine())!=null){
                String[] parts = line.split("_");
                if(parts[1].equals(reference)) {
                    base =  parts[0];
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
