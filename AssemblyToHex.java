import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;

// This program conversts assembly code to hexadecimal.
public class AssemblyToHex {

    public static void main(String[] args) throws IOException {
        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            writer.write("v2.0 raw\n");

            String line;
            // Read each line of the file.
            while ((line = reader.readLine()) != null) {
                String binary = "00";
                String isa[] = line.split(" ");
                String opcode = isa[0];

                if (opcode.equals("AND") || opcode.equals("ADD") || opcode.equals("OR") || opcode.equals("XOR")) {
                    String secondPart[] = isa[1].split(",");
                    String dst = secondPart[0], src1 = secondPart[1], src2 = secondPart[2];
                    if (opcode.equals("ADD")) {
                        binary += "000";
                    } else if (opcode.equals("AND")) {
                        binary += "001";
                    } else if (opcode.equals("OR")) {
                        binary += "010";
                    } else if (opcode.equals("XOR")) {
                        binary += "011";
                    }
                    binary += registerToBinString(dst) + registerToBinString(src1) + "000" + registerToBinString(src2);
                } else if (opcode.equals("ADDI") || opcode.equals("ANDI") || opcode.equals("ORI")
                        || opcode.equals("XORI")) {
                    String secondPart[] = isa[1].split(",");
                    String dst = secondPart[0], src = secondPart[1], imm = secondPart[2];
                    if (opcode.equals("ADDI")) {
                        binary += "000";
                    } else if (opcode.equals("ANDI")) {
                        binary += "001";
                    } else if (opcode.equals("ORI")) {
                        binary += "010";
                    } else if (opcode.equals("XORI")) {
                        binary += "011";
                    }
                    binary += registerToBinString(dst) + registerToBinString(src) + "1" + decimalToTwosComp(imm, 6);
                } else if (opcode.equals("JUMP")) {
                    String address = isa[1];
                    binary += "110" + decimalToTwosComp(address, 15);
                } else if (opcode.equals("BEQ") || opcode.equals("BLT") || opcode.equals("BGT") || opcode.equals("BLE")
                        || opcode.equals("BGE")) {
                    String secondPart[] = isa[1].split(",");
                    String op1 = secondPart[0], op2 = secondPart[1], address = secondPart[2];
                    binary += "111";
                    if (opcode.equals("BEQ")) {
                        binary += "010";
                    } else if (opcode.equals("BLT")) {
                        binary += "100";
                    } else if (opcode.equals("BGT")) {
                        binary += "001";
                    } else if (opcode.equals("BLE")) {
                        binary += "110";
                    } else if (opcode.equals("BGE")) {
                        binary += "011";
                    }
                    binary += registerToBinString(op1) + registerToBinString(op2) + decimalToTwosComp(address, 4);
                } else if (opcode.equals("LD")) {
                    String secondPart[] = isa[1].split(",");
                    String dst = secondPart[0], address = secondPart[1];
                    binary += "100" + registerToBinString(dst) + toBinary(Integer.parseInt(address), 11);
                } else if (opcode.equals("ST")) {
                    String secondPart[] = isa[1].split(",");
                    String src = secondPart[0], address = secondPart[1];
                    binary += "101" + registerToBinString(src) + toBinary(Integer.parseInt(address), 11);
                } else {
                    System.out.println("Error: Invalid opcode!");
                    System.exit(1);
                }
                writer.write(binaryToHexString(binary) + "\n");
                System.out.println(binaryToHexString(binary));
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
            System.exit(1);
        }
    }

    // This method converts a binary string to a hex string.
    public static String binaryToHexString(String binary) {
        String hex = "";
        for (int i = 0; i < binary.length(); i += 4) {
            String temp = binary.substring(i, i + 4);
            int tempInt = Integer.parseInt(temp, 2);
            hex += Integer.toHexString(tempInt).toUpperCase();
        }
        return hex;
    }

    // This method converts a number to binary string according to the given length.
    public static String toBinary(int num, int bits) {
        String binary = "";
        for (int i = 0; i < bits; i++) {
            binary = (num & (1 << i)) != 0 ? "1" + binary : "0" + binary;
        }
        return binary;
    }

    // This method converts a register name to binary string.
    public static String registerToBinString(String register) {
        int registerNum = Integer.parseInt(register.substring(1));
        String rString = toBinary(registerNum, 4);
        return rString;
    }

    // This method converts a decimal to twos complemented binary string.
    public static String decimalToTwosComp(String number, int bits) {
        int num = Integer.parseInt(number);
        // If number is zero or positive, return the binary string of the number.
        if (num >= 0) {
            return toBinary(num, bits);
        }
        // If the number is negative, return the twos complement of the number.
        else {
            String binary = toBinary(Math.abs(num), bits);
            return twosComplement(binary);
        }
    }

    // This method converts a binary string to twos complemented binary string.
    public static String twosComplement(String bin) {
        String twos = "", ones = "";

        // First, flip the bits of the binary string.
        for (int i = 0; i < bin.length(); i++) {
            ones += flip(bin.charAt(i));
        }

        // Then, add 1 to the ones string.
        StringBuilder builder = new StringBuilder(ones);
        boolean b = false;
        for (int i = ones.length() - 1; i > 0; i--) {
            if (ones.charAt(i) == '1') {
                builder.setCharAt(i, '0');
            } else {
                builder.setCharAt(i, '1');
                b = true;
                break;
            }
        }

        // If the first bit is 1, add 1 to the twos string.
        if (!b)
            builder.append("1", 0, 7);

        twos = builder.toString();

        return twos;
    }

    // Returns '0' for '1' and '1' for '0'
    public static char flip(char c) {
        return (c == '0') ? '1' : '0';
    }
}