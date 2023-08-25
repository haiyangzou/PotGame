package org.pot.test.algorithm;

/**
 * Given a signed 32-bit integer x, return x with its digits reversed.
 * If reversing x causes the value to go outside
 * the signed 32-bit integer range [-231, 231 - 1], then return 0.
 * Assume the environment does not allow you to store
 * 64-bit integers (signed or unsigned).
 * <p>
 * Input: x = 123
 * Output: 321
 * <p>
 * Input: x = -123
 * Output: -321
 * <p>
 * Input: x = 120
 * Output: 21
 */
public class ReverseInteger {
    public static int reverse(int x) {
        long reversedx = 0;
        int remainder = 0;
        int temp = x;
        while (temp != 0) {
            remainder = temp % 10;
            reversedx = (reversedx * 10) + remainder;
            temp /= 10;
        }
        if (reversedx > Integer.MAX_VALUE || reversedx < Integer.MIN_VALUE)
            return 0;
        return (int) reversedx;
    }

}
