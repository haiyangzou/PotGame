package org.pot.test;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Given a string s, find the length of the longest
 * substring without repeating characters.
 */
public class LongSubString {

    @Test
    public void test() {
        String s = "abcabcbbefjoiosidfjsodfh";
        System.out.println(lengthOfLongestSubstring(s));
    }

    public int lengthOfLongestSubstring(String s) {
        Set<Character> queue = new HashSet<>();
        int n = s.length();
        int left = 0;
        int maxLength = 0;
        for (int right = 0; right < n; right++) {
            char c = s.charAt(right);
            if (!queue.contains(c)) {
                queue.add(c);
                maxLength = Math.max(maxLength, right - left + 1);
            } else {
                while (queue.contains(c)) {
                    queue.remove(s.charAt(left));
                    left++;
                }
                queue.add(c);
            }
        }
        return maxLength;
    }
}
