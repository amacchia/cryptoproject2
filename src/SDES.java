import java.util.Scanner;

public class SDES {
	
	private static final int[] key1Positions = {0, 6, 8, 3, 7, 2, 9, 5};
	private static final int[] key2Positions = {7, 2, 5, 4, 9, 1, 8, 0};
	private static final int[] initialPerm = {1, 5, 2, 0, 3, 7, 4, 6};
	private static final int[] invIP = {3, 0, 2, 4, 6, 1, 7, 5};
	private static final int[] EP = {3,0,1,2,1,2,3,0};
	private static final int[] P4 = {1,3,2,0};

	private static final boolean[][][] S0 = {{{false,true}, {false,false},{true,true}, {true,false}},
									         {{true,true},  {true,false}, {false,true},{false,false}},
									         {{false,false},{true,false}, {false,true},{true,true}},
										     {{true,true},  {false,true}, {true,true}, {true,false}}};

	private static final boolean[][][] S1 = {{{false,false},{false,true}, {true,false}, {true,true}},
									         {{true,false}, {false,false},{false,true}, {true,true}},
									         {{true,true},  {false,false},{false,true}, {false,false}},
									         {{true,false}, {false,true}, {false,false},{true,true}}};
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
	 * Utility function for performing s-box operation.
	 * @param x
	 * @param s
	 * @return
	 * @author Warren Devonshire
	 */
	public boolean[] sBox(boolean[] x, boolean[][][] s){
		int i = 0;//used for X0 and X3 Rows of SBox
		int j = 0;//used for X1 and X2 Columns of Sbox
		if(x[0]) i += 2;
		if(x[3]) i++;
		if(x[1]) j += 2;
		if(x[2]) j++;
		return s[i][j];
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
		for(int i= 0,j = rs; j < inp.length; i++, j++)
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

		for(int i = 0; i < y.length; i++, cnt++)
		{
			combine[cnt] = y[i];
		}
		return combine;
	}
	
	/**
	 * 
	 * @param inp takes boolean array of max size 8
	 * @return byte
	 * author Manoj George
	 */
	public byte bitArrayToByte(boolean[] inp)
	{
		String byteResult = "";
		int[] hold = new int[8];
		
		if (inp.length > 8) {
            System.exit(0);
            
        }
        else {
            int j = 7;
            for (int i = inp.length-1; i >= 1; i--) {
               if (inp[i] == true) { 
                    hold[j] = 1;
               }
               j--;
            }
        }
		for(int i = 0; i < hold.length; i++)
		{
			byteResult += hold[i];
		}
		
		byte out = Byte.parseByte(byteResult,2);
		if(inp[0] == true)
		{
			int tmp = out;
			int pow = (int) Math.pow(2, inp.length);
			tmp -= pow;
			out = (byte)tmp;
			
		}
		
		return new Byte(out);
	}
	
	/**
	 * Convert the given byte to a bit array, of the given size.
	 * @param b
	 * @param size The size of the resulting bit array. The operator >>> can be used for an unsigned right shift.
	 * @return boolean[]
	 * author Manoj George
	 */
	public boolean[] byteToBitArray(byte b, int size)
	{
		boolean[] temp = new boolean[size];
        for (int i = 0; i < size; i++) {
            
            if(temp[i] = (b & (1 << i)) == 0)
            {
            	temp[i] = false;
            }
            else
            {
            	temp[i] = true;
            }
        }

        
        boolean[] result = new boolean[size];
        for (int i = 0; i < temp.length; i++) {
            result[result.length-1-i] = temp[i];
        }
        return result;
		
	}


}
