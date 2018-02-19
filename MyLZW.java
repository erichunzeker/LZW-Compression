/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class MyLZW {
    private static final int R = 256;               // number of input chars
    private static int L = 512;                     // number of codewords = 2^W
    private static int LMAX = 65536;                // number of codewords = 2^W
    private static int W = 9;                       // codeword width
    private static final double threshold = 1.1;    // ratio threshold

    public static void compress(String mode)
    {
        /////////// ***DO THIS FOR ALL MODES*** ///////////

        String input = BinaryStdIn.readString();

        TST<Integer> st = new TST<Integer>();

        double uncompressed = 0;
        double compressed = 0;
        double oldRatio = 0;
        double newRatio;

        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        /////////// ***DO THIS FOR ALL MODES*** ///////////


        if(mode.equalsIgnoreCase("n"))              //do nothing mode
        {
            BinaryStdOut.write('n');

            while (input.length() > 0) {
                String s = st.longestPrefixOf(input);  // Find max prefix match s.
                BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
                int t = s.length();

                if (t < input.length() && code < L)    // Add s to symbol table.
                    st.put(input.substring(0, t + 1), code++);

                else if(t < input.length() && W < 16)
                {
                    W++;
                    L *= 2;
                    st.put(input.substring(0, t + 1), code++);
                }
                input = input.substring(t);            // Scan past s in input.
            }
        }
        else if(mode.equalsIgnoreCase("r"))         //reset mode
        {
            BinaryStdOut.write('r');

            while (input.length() > 0) {
                String s = st.longestPrefixOf(input);  // Find max prefix match s.
                BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
                int t = s.length();
                if (t < input.length() && code < L)    // Add s to symbol table.
                    st.put(input.substring(0, t + 1), code++);
                else if(t < input.length() && W < 16)
                {
                    W++;
                    L *= 2;
                    st.put(input.substring(0, t + 1), code++);
                }
                else if(t < input.length() && W == 16 && L == LMAX)
                {
                    W = 9;
                    L = 512;
                    st = new TST<Integer>();
                    for (int i = 0; i < R; i++)
                        st.put("" + (char) i, i);
                    code = R+1;
                    st.put(input.substring(0, t + 1), code++);

                }
                input = input.substring(t);            // Scan past s in input.
            }
        }

        else if(mode.equalsIgnoreCase("m"))        //monitor mode
        {
            BinaryStdOut.write('m');

            while (input.length() > 0) {
                String s = st.longestPrefixOf(input);  // Find max prefix match s.
                BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
                int t = s.length();
                uncompressed += t * 8;
                compressed += W;



                if (t < input.length() && code < L)    // Add s to symbol table.
                    st.put(input.substring(0, t + 1), code++);

                else if(t < input.length() && W < 16)
                {
                    W++;
                    L *= 2;
                    st.put(input.substring(0, t + 1), code++);
                }

                else
                {
                    newRatio = uncompressed/compressed;

                    if(oldRatio == 0)
                        oldRatio = newRatio;

                    else if(oldRatio/newRatio > threshold)
                    {
                        W = 9;
                        L = 512;

                        oldRatio = 0;

                        st = new TST<Integer>();

                        for (int i = 0; i < R; i++)
                            st.put("" + (char) i, i);
                        code = R+1;
                        st.put(input.substring(0, t + 1), code++);
                    }
                }

                input = input.substring(t);            // Scan past s in input.*/
            }
        }

        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 



    public static void expand()
    {
        String[] st = new String[LMAX]; //sum of codewords from size 9 to 16 inclusive
        int i; // next available codeword value
        char type = BinaryStdIn.readChar();
        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF
        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        double uncompressed = 0;
        double compressed = 0;
        double oldRatio = 0;
        double newRatio;

        while (true)
        {
            if(type == 'n')
            {
                BinaryStdOut.write(val);

                if( i >= L && W < 16)
                {
                    W++;
                    L *= 2;
                }

                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                String s = st[codeword];
                if (i == codeword)
                    s = val + val.charAt(0);   // special case hack
                if (i < L)
                    st[i++] = val + s.charAt(0);
                val = s;
            }

            else if(type == 'r')
            {
                BinaryStdOut.write(val);
                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                String s = st[codeword];
                if (i == codeword)
                    s = val + val.charAt(0);   // special case hack
                if (i < L - 1)
                    st[i++] = val + s.charAt(0);
                else if (W < 16)
                {
                    W++;
                    L *= 2;
                    st[i++] = val + s.charAt(0);
                }
                else if(W == 16 && L == LMAX)
                {
                    st = new String[LMAX];
                    W = 9;
                    L = 512;
                    for (i = 0; i < R; i++)
                        st[i] = "" + (char) i;
                    st[i++] = "";
                }
                val = s;
            }

            else if(type == 'm')
            {
                BinaryStdOut.write(val);
                int t = val.length();
                compressed += W;
                uncompressed += t * 8;

                if( i >= L) //i can be L due to monitor mode (check this first as opposed to in R mode)
                {
                    if (W < 16)
                    {
                        W++;
                        L *= 2;
                    } else if (W == 16 && L == LMAX) {
                        newRatio = uncompressed / compressed;

                        if (oldRatio == 0)
                            oldRatio = newRatio;

                        else if (oldRatio / newRatio > threshold) {
                            st = new String[LMAX];
                            W = 9;
                            L = 512;
                            for (i = 0; i < R; i++)
                                st[i] = "" + (char) i;
                            st[i++] = "";

                            oldRatio = 0;
                        }
                    }
                }

                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                String s = st[codeword];
                if (i == codeword)
                    s = val + val.charAt(0);   // special case hack
                if (i < L)
                    st[i++] = val + s.charAt(0);

                val = s;
            }
        }
        BinaryStdOut.close();
    }


    public static void main(String[] args) {

        if      (args[0].equals("-")) compress(args[1]);
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
