
public class SDES {
	
	private static final int[] key1Positions = {0, 6, 8, 3, 7, 2, 9, 5};
	private static final int[] key2Positions = {7, 2, 5, 4, 9, 1, 8, 0};
	private static final int[] initialPerm = {1, 5, 2, 0, 3, 7, 4, 6};
	private static final int[] invIP = {3, 0, 2, 4, 6, 1, 7, 5};
	private static final int[] EP = {3,0,1,2,1,2,3,0};
	private static final int[] P4 = {1,3,2,0};
	private static final int[][] S0 = {{0,0,0},{0,0,0},{0,0,0}};
	private static final int[][] S1 = {{0,0,0},{0,0,0},{0,0,0}};
	
	/**
	 * Encrypts a string using SDES.
	 * @param msg the string to encrypt
	 * @return the encrypted message
	 * @author Anthony Macchia
	 */
	public byte[] encrypt(String msg) {
		
		byte[] message = msg.getBytes();
		byte[] cipher = new byte[message.length];
		
		// Encrypt each byte in the message
		for (int i = 0; i < message.length; i++) {
			cipher[i] = encryptByte(message[i]);
		}
		 
		return cipher;
	}
	
	/**
	 * Decrypt a given byte array that was encrypted with SDES.
	 * @param cipher the encrypted cipher text
	 * @return a byte array of the plain text
	 * @author Anthony Macchia
	 */
	public byte[] decrypt(byte[] cipher) {
		
		byte[] message = new byte[cipher.length];
		
		// Decrypt each byte in the cipher text
		for (int i = 0; i < message.length; i++) {
			message[i] = decryptByte(cipher[i]);
		}
		 
		return message;
	}
	
	/**
	 * Encrypts a byte using SDES.
	 * @param b the byte to encrypt
	 * @return the encrypted byte
	 * @author Anthony Macchia
	 */
	public byte encryptByte(byte b) {
		// Convert byte to 8-bit array
		boolean[] bits = byteToBitArray(b, 8);
		
		// Initial permutation
		bits = expPerm(bits, initialPerm);
				
		// Round 1
		boolean[] k1 = expPerm(key, key1Positions);
		bits = f(bits, k1);		
		
		// Swap Nybbles
		bits = concat(rh(bits), lh(bits));
						
		// Round 2
		boolean[] k2 = expPerm(key, key2Positions);
		bits = f(bits, k2);
				
		// Inverse of Initial Permutation
		bits = expPerm(bits, invIP);
		
		return bitArrayToByte(bits);
	}		
	
	/**
	 * Decrypts a byte that was encrypted with SDES.
	 * @param b the byte to decrypt
	 * @return the decrypted byte
	 * @author Anthony Macchia
	 */
	public byte decryptByte(byte b) {
		// Convert byte to 8-bit array
		boolean[] bits = byteToBitArray(b, 8);
		
		// Initial permutation
		bits = expPerm(bits, initialPerm);
		
		// f2
		boolean[] k2 = expPerm(key, key2Positions);
		bits = f(bits, k2);
		
		// Swap Nybbles
		bits = concat(rh(bits), lh(bits));
		
		// f1
		boolean[] k1 = expPerm(key, key1Positions);
		bits = f(bits, k1);
		
		// Inverse of Initial Permutation
		bits = expPerm(bits, invIP);
		
		return bitArrayToByte(bits);
	}

	/**
	 * @param x
	 * @param k
	 * @return
	 * @author Warren Devonshire
	 */
	public boolean[] f(boolean[] x, boolean[] k){
		return concat(xor(lh(x), feistel(k, rh(x))), rh(x));
	}

	/**
	 * F(k,x) is a Feistel function F(k,x) = P4 (s0 (L (k xor EP(x))) || s1 (R (k xor EP(x)))
	 * @param k
	 * @param x
	 * @return
	 * @author Warren Devonshire
	 */
	public boolean[] feistel(boolean[] k, boolean[] x){
		boolean[] kXorEP = xor(k, expPerm(x, EP));
		return expPerm(concat(sBox(lh(kXorEP), S0), sBox(rh(kXorEP), S1)), P4);
	}

	/**
	 *
	 * @param x
	 * @param s
	 * @return
	 * @author Warren Devonshire
	 */
	public boolean[] sBox(boolean[] x, int[][] s){
		return new boolean[2];
	}

	/**
	 * Print a bit array to the standard output.
	 * @param inp the bit array to print
	 * @author Anthony Macchia
	 */
	public void show(boolean[] inp) {
		for (int i = 0; i < inp.length; i++) {
			boolean bit = inp[i];
			
			if (bit) {
				System.out.print(1);
			} else {
				System.out.print(0);
			}			
		 }
	}
	
	/**
	 * Print a byte array to the standard output.
	 * @param byteArray the byte array to print
	 * @author Anthony Macchia
	 */
	public void show(byte[] byteArray) {
		for (int i = 0; i < byteArray.length; i++) {
			System.out.print(byteArray[i]);
		 }
	}
}