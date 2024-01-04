import java.io.*;

public class PassTwo {
    String LocationCounter = "TextFiles/LocationCounter.txt";
    String Output = "OutputFiles/outputCode.txt";
    int ProgramCounter;
    int BaseCounter;
    converter c1 = new converter();
    String[][] OpTab = c1.getOptab();
    String ObjectCode;
    PassOne p = new PassOne();
    String[][] SymTab = p.PassOne();
    void passTwo (){
        try(BufferedReader reader = new BufferedReader(new FileReader(LocationCounter));
            BufferedWriter write = new BufferedWriter(new FileWriter(Output))
        ){
            String line=reader.readLine();
            write.write(line);
            while ((line = reader.readLine())!=null) {
                String[] parts = line.split("_");
                ObjectCode = getObjectCode(parts[2],parts[3]);
                write.write(line+"_"+ObjectCode);
                write.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String getObjectCode(String instruction, String reference) {
        String objectCode = null;
        String opCode = null;
        String format = null;
        boolean format4;

        if (instruction.equals("BASE")){
            setBase(reference);
            return "no-object-code";
        }else if (instruction.equals("START")||instruction.equals("END")){
            return "no-object-code";
        }else if (instruction.equals("RESB")||instruction.equals("RESW")){
            return "no-object-code";
        }else if (instruction.equals("RSUB")){
            return "no-object-code";
        }else if(instruction.equals("BYTE")){
                //TODO: create a byte object code generator
        }


        for (int i = 0; i < 59; i++) {
            if(instruction.equals(OpTab[i][0])){
                opCode = OpTab[i][2];
                format = OpTab[i][1];
            } else if (instruction.contains("+")) {
                instruction = instruction.substring(1);
                format4 = true;
            }
        }
        if(opCode.equals(null)||format.equals(null)){
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
                objectCode = getFormat3();
                break;
            default:
                System.err.println("error\ninvalid instruction or reference");
        }
        return objectCode;
    }

    void setBase(String reference){
        int i = 0;
        while(SymTab[i][1]!=null){
            if (reference.equals(SymTab[i][1])){
                BaseCounter = Integer.parseInt(SymTab[i][0],16);
                return;
            }
            i++;
        }
    }

    private String getFormat2(String reference){
        String[] split;
        String code=null;
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
            }
        }
        return  code;
    }

    String addHexadecimal(String h1,String h2){
        int sum = Integer.parseInt(h1,16) + Integer.parseInt(h2,16);
        return String.format("%X",sum);
    }

    String getFormat3(){




    }
}
