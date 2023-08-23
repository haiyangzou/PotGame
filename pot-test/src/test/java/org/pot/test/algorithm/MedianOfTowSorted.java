package org.pot.test.algorithm;

/**
 * Longest Palindromic Substring
 * Given a string s, return the longest palindromic(回文) substring in s.
 * Input: s = "babad"
 * Output: "bab"
 * Explanation: "aba" is also a valid answer.
 */
public class MedianOfTowSorted {
    static int maxLen = 0;
    static int lo = 0;

    public static String longestPalindrome(String s) {
        char[] input = s.toCharArray();
        if (s.length() < 2) {
            return s;
        }

        for (int i = 0; i < input.length; i++) {
            expandPalindrome(input, i, i);
            expandPalindrome(input, i, i + 1);
        }
        return s.substring(lo, lo + maxLen);
    }

    public static void expandPalindrome(char[] s, int j, int k) {
        while (j >= 0 && k < s.length && s[j] == s[k]) {
            j--;
            k++;
        }
        if (maxLen < k - j - 1) {
            maxLen = k - j - 1;
            lo = j + 1;
        }
    }
}
