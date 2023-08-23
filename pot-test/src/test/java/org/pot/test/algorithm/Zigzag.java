package org.pot.test.algorithm;

/**
 * The string "PAYPALISHIRING" is written in a zigzag pattern on a given number of rows like this: (you may want to display this pattern in a fixed font for better legibility)
 * 0      6        12
 * 1   5  7    11  13
 * 2 4    8 10     14
 * 3      9        15
 * <p>
 * 0        8           16
 * 1      7 9        15 17
 * 2    6   10     14   18
 * 3  5     11  13      19
 * 4        12          20
 * And then read line by line: "PAHNAPLSIIGYIR"
 * <p>
 * Write the code that will take a string and make this conversion given a number of rows:
 * <p>
 * string convert(string s, int numRows);
 * <p>
 * Input: s = "PAYPALISHIRING", numRows = 3
 * Output: "PAHNAPLSIIGYIR"
 */
public class Zigzag {
    public static String convert(String s, int numRows) {
        int col = 0;                  // variable that will store the number of columns the extended matrix have
        int l = s.length();
        while (l > 0) {                // calculating number of columns
            l -= numRows;
            col++;
            for (int i = 0; i < numRows - 2; i++) {
                if (l > 0) {
                    l--;
                    col++;
                }
            }
        }
        //System.out.println( col);
        Character m[][] = new Character[numRows][col];    // creating matrix to store the asked arrangement
        l = s.length();
        int c = 0;
        int p = 0;
        while (l > 0) {                                     // storing string in the matrix in the asked arrangement
            //l -= numRows;
            for (int i = 0; i < numRows; i++) {
                if (l > 0) {
                    l--;
                    m[i][c] = s.charAt(p++);
                    //p++;
                }
            }
            c++;
            for (int i = numRows - 2; i > 0; i--) {
                if (l > 0) {
                    l--;
                    m[i][c++] = s.charAt(p++);
                    //p++;
                    //c++;
                }
            }
        }
        /*for( int i = 0; i < numRows; i++){       // to print the created matrix
            for( int j = 0; j < col; j++){
                System.out.print( m[i][j] + " " );
            }
            System.out.println("\n");
        }*/
        String f = "";                            // traverse the matrix row-wise and answer is achieved
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < col; j++) {
                if (m[i][j] != null) {
                    f += m[i][j];
                }
            }
        }
        return f;
    }
}
