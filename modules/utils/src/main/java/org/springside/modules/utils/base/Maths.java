package org.springside.modules.utils.base;

import com.google.common.annotations.Beta;

@Beta
public class Maths {

    /**
     * 找出最接近的2的倍数，比如15返回16， 17返回32. From Netty
     * 
     * Fast method of finding the next power of 2 greater than or equal to the supplied value.
     * <p>This method will do runtime bounds checking and call {@link #findNextPositivePowerOfTwo(int)} if within a
     * valid range.
     * @param value from which to search for next power of 2
     * @return The next power of 2 or the value itself if it is a power of 2.
     * <p>Special cases for return values are as follows:
     * <ul>
     *     <li>{@code <= 0} -> 1</li>
     *     <li>{@code >= 2^30} -> 2^30</li>
     * </ul>
     */
    public static int findNextPositivePowerOfTwo(final int value) {
        return value <= 0 ? 1 : value >= 0x40000000 ? 0x40000000 : 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
    }
}
