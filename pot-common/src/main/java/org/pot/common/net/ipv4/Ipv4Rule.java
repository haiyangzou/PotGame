package org.pot.common.net.ipv4;

import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class Ipv4Rule {
    private static final class Item {
        private final String ipString;
        private final int maskBitLength;
        private final long netSegmentValue;
        private final long maskValue;

        public Item(String ipString, int maskBitLength, long netSegmentValue, long maskValue) {
            this.ipString = ipString;
            this.maskBitLength = maskBitLength;
            this.netSegmentValue = netSegmentValue;
            this.maskValue = maskValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Item item = (Item) o;
            return maskBitLength == item.maskBitLength &&
                    netSegmentValue == item.netSegmentValue &&
                    maskValue == item.maskValue &&
                    Objects.equal(ipString, item.ipString);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(ipString, maskBitLength, netSegmentValue, maskValue);
        }
    }

    private final CopyOnWriteArrayList<Item> rules = new CopyOnWriteArrayList<>();

    public void addRules(String rules) {
        addRule(StringUtils.split(rules, ","));
    }

    public void addRules(Collection<String> rules) {
        for (String rule : rules) {
            addRule(rule);
        }
    }

    public void addRule(String[] rules) {
        for (String rule : rules) {
            addRule(rule);
        }
    }

    public void addRule(String rule) {
        final String[] array = StringUtils.split(rule, '/');
        String ipString = array[0];
        int maskBitLength = array.length <= 1 ? 32 : Integer.parseInt(array[1]);
        addRule(ipString, maskBitLength);
    }

    public void addRule(String ipString, int maskBitLength) {
        String maskString = Ipv4Util.toMaskString(maskBitLength);
        Ipv4Util.ensureIpv4Valid(ipString);
        Ipv4Util.ensureIpv4Valid(maskString);
        final long netSegmentValue = Ipv4Util.toNetSegmentValue(ipString, maskString);
        final long maskValue = Ipv4Util.toIpv4Value(maskString);
        rules.addIfAbsent(new Item(ipString, maskBitLength, netSegmentValue, maskValue));
    }

    public boolean isEmpty() {
        return rules.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        int index = 0;
        for (Item rule : rules) {
            if (index++ > 0)
                builder.append(',');
            builder.append(rule.ipString).append('/').append(rule.maskBitLength);
        }
        builder.append(']');
        return builder.toString();
    }

    /**
     * 某个IP地址是否在规则内
     */

    public boolean contains(String ipString) {
        return rules.stream()
                .anyMatch(item -> item.netSegmentValue == Ipv4Util.toNetSegmentValue(ipString, item.maskValue));
    }
}
