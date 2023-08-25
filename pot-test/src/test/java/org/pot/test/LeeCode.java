package org.pot.test;

import org.junit.Test;
import org.pot.test.algorithm.*;
import org.pot.test.struct.ListNode;

public class LeeCode {
    @Test
    public void longSubString() {
        String s = "abcabcbbefjoiosidfjsodfh";
        System.out.println(LongSubString.lengthOfLongestSubstring(s));
    }

    @Test
    public void medianOfTowSorted() {
        System.out.println(MedianOfTowSorted.longestPalindrome("bacbacbcab"));
    }

    @Test
    public void maxSlidingWindow() {
        int[] nums = new int[]{1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        Sliding.maxSlidingWindow(nums, k);
    }

    @Test
    public void addTwoNumbers() {
        ListNode l1_2 = new ListNode(3);
        ListNode l1_1 = new ListNode(4, l1_2);
        ListNode l1 = new ListNode(2, l1_1);

        ListNode l2_2 = new ListNode(4);
        ListNode l2_1 = new ListNode(6, l2_2);
        ListNode l2 = new ListNode(5, l2_1);
        ListNode result = TwoSum.addTwoNumbers(l1, l2);
        System.out.println(result.val);
    }

    @Test
    public void zigzagConvert() {
        System.out.println(Zigzag.convert("PAYPALISHIRING", 3));
    }

    @Test
    public void reverseInteger() {
        System.out.println(ReverseInteger.reverse(312));
    }
}
