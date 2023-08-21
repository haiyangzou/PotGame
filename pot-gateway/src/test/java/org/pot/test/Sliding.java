package org.pot.test;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Sliding Window Maximum
 * You are given an array of integers nums,
 * there is a sliding window of size k which is moving from the very left of the array to the very right.
 * You can only see the k numbers in the window.
 * Each time the sliding window moves right by one position.
 * Return the max sliding window.
 */
public class Sliding {
    @Test
    public void test() {
        int[] nums = new int[]{1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        maxSlidingWindow(nums, k);
    }

    public int[] maxSlidingWindow(int[] nums, int k) {
        List<Integer> res = new ArrayList<>();
        int left = 0;
        int right = 0;
        Deque<Integer> q = new ArrayDeque<>();
        while (right < nums.length) {
            while (!q.isEmpty() && nums[right] > nums[q.peekLast()]) {
                q.pollLast();
            }
            q.offerLast(right);

            if (left > q.peekFirst()) {
                q.pollFirst();
            }

            if (right + 1 >= k) {
                res.add(nums[q.peekFirst()]);
                left++;
            }
            right++;
        }

        return res.stream().mapToInt(Integer::intValue).toArray();
    }
}
