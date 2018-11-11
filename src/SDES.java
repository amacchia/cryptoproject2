import java.util.Scanner;

public class SDES {
	
	private static final int[] key1Positions = {0, 6, 8, 3, 7, 2, 9, 5};
	private static final int[] key2Positions = {7, 2, 5, 4, 9, 1, 8, 0};
	private static final int[] initialPerm = {1, 5, 2, 0, 3, 7, 4, 6};
	private static final int[] invIP = {3, 0, 2, 4, 6, 1, 7, 5};
	private static final int[] EP = {3,0,1,2,1,2,3,0};
	private static final int[] P4 = {1,3,2,0};
	private static final int[][] S0 = {{0,0,0},{0,0,0},{0,0,0}};
	private static final int[][] S1 = {{0,0,0},{0,0,0},{0,0,0}};
	boolean[] key = new boolean[10];

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


	//Manoj Starts here
	/**
	 * Expand and/or permute and/or select from the bit array, inp, producing an expanded/permuted/selected bit array.
	 * @param inp A bit array represented as booleans, true=1, false=0.
	 * @param epv An expansion and/or permutation and/or selection vector; all numbers in epv must be in the range 0..inp.length-1, i.e. they must be valid subscripts for inp.
	 * @return The permuted/expanded/selected bit array, or null if there is an error.
	 * @author Manoj George
	 */
	public boolean[] expPerm(boolean[] inp, int[] epv)
	{
		boolean[] out = new boolean[epv.length];

		for(int i = 0; i< epv.length; i++)
		{
			out[i] = inp[epv[i]];
		}
		return out;
	}

	/**
	 * @param inp An array of bytes, hopefully storing the codes of printable characters.
	 * @return The characters as a String.
	 * @author Manoj George
	 */
	public String byteArrayToString(byte[] inp)
	{

		String result = "";
		if(inp.length > 0)
		{
			for(int i = 0; i < inp.length; i++)
			{
				result = result + inp[i] +" " ;
			}
		}
		return result;
	}

	/**
	 * Get a 10 bit key from the keyboard, such as 1010101010. Store it as an array of booleans in a field.
	 * @param scan
	 * @author Manoj George
	 */
	public void getKey10(Scanner scan)
	{
		System.out.println("Please enter your key a single bit at a time:");
		for (int i = 0; i < 10; i++) {
            int bit = scan.nextInt();
            if (bit == 1) {
                key[i] = true;
            }
            else if (bit != 0) {
                System.out.println("Only ones and zero's allowed");
                i--;

            }
        }
	}

	/**
	 * Left half of x, L(x)
	 * @param inp
	 * @return a bit array which is the left half of the parameter, inp.
	 * @author Manoj George
	 */
	public boolean[] lh(boolean [] inp)
	{
		int ls = inp.length/2;
		boolean[] half = new boolean[ls];
		for(int i = 0; i < ls; i++)
		{
			half[i] = inp[i];
		}
		return half;

	}

	/**
	 * Right half of x, R(x)
	 * @param inp
	 * @return a bit array which is the right half of the parameter, inp.
	 * @author Manoj George
	 */
	public boolean[] rh(boolean[] inp)
	{

		int rs = inp.length/2;
		boolean[] half = new boolean[rs];
		for(int i= 0,j = rs; i < inp.length; i++, j++)
		{
			half[i] = inp[j];
		}
		return half;

	}

	/**
	 * Exclusive OR
	 * @param x
	 * @param y
	 * @author Manoj George
	 */
	public boolean[] xor(boolean[] x, boolean[] y)
	{
		if(x.length != y.length)
		{
			return null;
		}

		boolean[] xOR = new boolean[x.length];
		for(int i = 0; i< x.length; i++)
		{
			if(x[i] == y[i])
			{
				xOR[i] = false;
			}
			else
			{
				xOR[i] = true;
			}
		}

		return xOR;
	}

	/**
	 * Concatenate the two bit arrays, x || y
	 * @param x
	 * @param y
	 * @return the concatenation of x and y
	 * @author Manoj George
	 */
	public boolean[] concat(boolean[] x, boolean[] y)
	{
		boolean[] combine = new boolean[x.length + y.length];


		for(int i = 0; i < x.length; i++)
		{
			combine[i] = x[i];
		}

		int cnt = x.length;

		for(int i = 0; i < combine.length; i++, cnt++)
		{
			combine[cnt] = y[i];
		}
		return combine;
	}


}