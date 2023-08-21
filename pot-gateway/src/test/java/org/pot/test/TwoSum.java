package org.pot.test;

import org.junit.Test;
import org.pot.test.struct.ListNode;

public class TwoSum {
    @Test
    public void test() {
        ListNode l1_2 = new ListNode(3);
        ListNode l1_1 = new ListNode(4, l1_2);
        ListNode l1 = new ListNode(2, l1_1);

        ListNode l2_2 = new ListNode(4);
        ListNode l2_1 = new ListNode(6, l2_2);
        ListNode l2 = new ListNode(5, l2_1);
        ListNode result = addTwoNumbers(l1, l2);
        System.out.println(result.val);
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummyHead = new ListNode(0);
        ListNode tail = dummyHead;
        int carry = 0;

        while (l1 != null || l2 != null || carry != 0) {
            int digit1 = (l1 != null) ? l1.val : 0;
            int digit2 = (l2 != null) ? l2.val : 0;

            int sum = digit1 + digit2 + carry;
            int digit = sum % 10;
            carry = sum / 10;

            ListNode newNode = new ListNode(digit);
            tail.next = newNode;
            tail = tail.next;

            l1 = (l1 != null) ? l1.next : null;
            l2 = (l2 != null) ? l2.next : null;
        }

        ListNode result = dummyHead.next;
        dummyHead.next = null;
        return result;
    }
}
