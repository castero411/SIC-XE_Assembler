import java.io.*;

class PassOne{
    converter c1 = new converter();

    String[][] OpTab = c1.getOptab();
    String readFile = "TextFiles/file.txt";
    String LOCCTR = "TextFiles/LocationCounter.txt";
    String SymbolTable = "TextFiles/SymbolTable.txt";

    void PassOne(){
        try(BufferedReader reader = new BufferedReader(new FileReader(readFile));
            BufferedWriter writeLOCCTR = new BufferedWriter(new FileWriter(LOCCTR));
            BufferedWriter writeSymbol = new BufferedWriter(new FileWriter((SymbolTable)))
        ) {
            String line=reader.readLine();
            String[] part = line.split("_");
            int LocCounter = Integer.parseInt(part[2]);
            int opCodeN = 0;

            writeSymbol.write("0000_"+part[0]);
            writeLOCCTR.write("0000_"+part[0]+"_"+part[1]+"_"+part[2]);
            writeSymbol.newLine();
            writeLOCCTR.newLine();


            while ((line = reader.readLine())!=null) {
                String[] parts = line.split("_");


                writeLOCCTR.write(String.format("%04d_%s_%s_%s",opCodeN,parts[0],parts[1],parts[2]));
                if(!parts[0].equals("null")) {
                    writeSymbol.write(String.format("%04d_%s", opCodeN, parts[0]));
                    writeSymbol.newLine();
                }
                writeLOCCTR.newLine();
                opCodeN += getOPCode(parts[1],parts[2]);

            }
            System.out.println("location counter and symbol table done");


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    int getOPCode (String opcode,String ref){
        int num = 0;

        for (int i = 0; i < 59; i++) {
            if(opcode.contains("+")){
                num = 4;
            }else if(opcode.equals(OpTab[i][0])){
                num = Integer.parseInt(OpTab[i][1]);
            }else if(opcode.equals("BASE")){
                num =0;
            } else if (opcode.equals("RESB")) {
                num=Integer.parseInt(ref);
            } else if (opcode.equals("RESW")) {
                num=3*Integer.parseInt(ref);
            } else if (opcode.equals("BYTE")) {
                String ref2 = ref.substring(2, ref.length() - 1);
                num = (int) Math.ceil(ref2.length()/2);
                System.out.println(num);
            }
        }
        return  num;
    }



}