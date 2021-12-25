import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

public class AssemblyToHex {

    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while ((line = br.readLine()) != null) {
            String binary = "00";
            String isa[] = line.split(" ");
            String opcode = isa[0];

            if (opcode.equals("AND") || opcode.equals("ADD") || opcode.equals("OR") || opcode.equals("XOR")) {
                String secondPart[] = isa[1].split(",");
                String dst = secondPart[0], src1 = secondPart[1], src2 = secondPart[2];
                if (opcode.equals("ADD")) {
                    binary += "000" + registerToBinString(dst) + registerToBinString(src1) + registerToBinString(src2);
                    System.out.println(binary);
                } else if (opcode.equals("AND")) {
                    binary += "001" + registerToBinString(dst) + registerToBinString(src1) + registerToBinString(src2);
                    System.out.println(binary);
                } else if (opcode.equals("OR")) {
                    binary += "010" + registerToBinString(dst) + registerToBinString(src1) + registerToBinString(src2);
                    System.out.println(binary);
                } else if (opcode.equals("XOR")) {
                    binary += "011" + registerToBinString(dst) + registerToBinString(src1) + registerToBinString(src2);
                    System.out.println(binary);
                }

            } else if (opcode.equals("ADDI") || opcode.equals("ANDI") || opcode.equals("ORI")
                    || opcode.equals("XORI")) {
                String secondPart[] = isa[1].split(",");
                String dst = secondPart[0], src = secondPart[1], imm = secondPart[2];

            } else if (opcode.equals("JUMP")) {
                String address = isa[1];

            } else if (opcode.equals("BEQ") || opcode.equals("BLT") || opcode.equals("BGT") || opcode.equals("BLE")
                    || opcode.equals("BGE")) {
                String secondPart[] = isa[1].split(",");
                String op1 = secondPart[0], op2 = secondPart[1], address = secondPart[2];

            } else if (opcode.equals("LD")) {
                String secondPart[] = isa[1].split(",");
                String dst = secondPart[0], address = secondPart[1];

            } else if (opcode.equals("ST")) {
                String secondPart[] = isa[1].split(",");
                String src = secondPart[0], address = secondPart[1];

            } else {
                System.out.println("Error: Invalid opcode!");
                System.exit(1);
            }
        }
    }

    public static String toBinary(int num, int bits) {
        String binary = "";
        for (int i = 0; i < bits; i++) {
            binary = (num & (1 << i)) != 0 ? "1" + binary : "0" + binary;
        }
        return binary;
    }

    public static String registerToBinString(String register) {
        int registerNum = Integer.parseInt(register.substring(1));
        String rString = toBinary(registerNum, 4);
        return rString;
    }
}