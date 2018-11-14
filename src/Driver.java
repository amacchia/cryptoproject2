import java.util.Scanner;

public class Driver {

	public static void main(String[] args) {
		 String msg = "Hello World";
		 String msg2 = "Math is fun!";
		 String msg3 = "Cryptography is cool!";
		 
		 byte[] cipher = {-115, -17, -47, -113, -43, -47, 15, 84, -43, -113, -17, 84,
				 -43, 79, 58, 15, 64, -113, -43, 65, -47, 127, 84, 64, -43, -61, 79,
				 -43, 93, -61, -14, 15, -43, -113, 84, -47, 127, -43, 127, 84, 127, 10, 84, 15, 64, 43};
		 
		 SDES sdes = new SDES();
		 
		 Scanner scan = new Scanner(System.in);
		 // Key: 0111 1111 01 
		 sdes.getKey10(scan);
		 		 
		 System.out.println("Byte array of cipher text:");
		 sdes.show(cipher);
		 byte[] plain = sdes.decrypt(cipher);
		 System.out.println("\nDecrpyted Cipher Text:");
		 System.out.println(new String(plain));
		 
		 
		 byte[] encr = sdes.encrypt(msg);
		 System.out.println("\n\nEncrpyted Message:");
		 sdes.show(encr);
		 byte[] decr = sdes.decrypt(encr);
		 System.out.println("\nDecrpyted Message:");
		 System.out.println(new String(decr));
		 
		 byte[] encr2 = sdes.encrypt(msg2);
		 System.out.println("\nEncrpyted Message2:");
		 sdes.show(encr2);
		 byte[] decr2 = sdes.decrypt(encr2);
		 System.out.println("\nDecrpyted Message2:");
		 System.out.println(new String(decr2));
		 
		 byte[] encr3 = sdes.encrypt(msg3);
		 System.out.println("\nEncrpyted Message3:");
		 sdes.show(encr3);
		 byte[] decr3 = sdes.decrypt(encr3);
		 System.out.println("\nDecrpyted Message3:");
		 System.out.println(new String(decr3));
		 
		 System.out.println("\nShow a bit array: ");
		 boolean[] bits = {false, true, true, false, true, false, false, true};
		 sdes.show(bits);
	}

}
