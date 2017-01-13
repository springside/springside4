package org.springside.modules.utils.number;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class NumberUtilTest {

	@Test
	public void toBytes() {
		byte[] bytes = NumberUtil.toBytes(1);
		assertThat(bytes).hasSize(4).containsSequence((byte) 0, (byte) 0, (byte) 0, (byte) 1);

		bytes = NumberUtil.toBytes(257);
		assertThat(bytes).containsSequence((byte) 0, (byte) 0, (byte) 1, (byte) 1);
		assertThat(NumberUtil.toInt(bytes)).isEqualTo(257);

		byte[] bytes2 = NumberUtil.toBytes(1L);
		assertThat(bytes2).hasSize(8);

		bytes = NumberUtil.toBytes(257L);
		assertThat(bytes).containsSequence((byte) 0, (byte) 0, (byte) 1, (byte) 1);
		assertThat(NumberUtil.toLong(bytes)).isEqualTo(257L);
	}

	@Test
	public void isNumber() {
		assertThat(NumberUtil.isNumber("123")).isTrue();
		assertThat(NumberUtil.isNumber("-123.1")).isTrue();
		assertThat(NumberUtil.isNumber("-1a3.1")).isFalse();

		assertThat(NumberUtil.isHexNumber("0x12F")).isTrue();
		assertThat(NumberUtil.isHexNumber("-0x12A3")).isTrue();
		assertThat(NumberUtil.isHexNumber("12A3")).isFalse();
	}

	@Test
	public void toNumber() {
		assertThat(NumberUtil.toInt("122")).isEqualTo(122);
		assertThat(NumberUtil.toInt("12A")).isEqualTo(0);
		assertThat(NumberUtil.toInt((String)null)).isEqualTo(0);
		assertThat(NumberUtil.toInt("12A", 123)).isEqualTo(123);
		
		assertThat(NumberUtil.toLong("122")).isEqualTo(122L);
		assertThat(NumberUtil.toLong("12A")).isEqualTo(0L);
		assertThat(NumberUtil.toLong((String)null)).isEqualTo(0);
		assertThat(NumberUtil.toLong("12A", 123)).isEqualTo(123L);
		
		assertThat(NumberUtil.toDouble("122.1")).isEqualTo(122.1);
		assertThat(NumberUtil.toDouble("12A")).isEqualTo(0L);
		assertThat(NumberUtil.toDouble("12A", 123.1)).isEqualTo(123.1);

		assertThat(NumberUtil.toIntObject("122")).isEqualTo(122);
		assertThat(NumberUtil.toIntObject("12A")).isEqualTo(null);
		assertThat(NumberUtil.toIntObject("12A", 123)).isEqualTo(123);
		assertThat(NumberUtil.toIntObject(null, 123)).isEqualTo(123);
		assertThat(NumberUtil.toIntObject("", 123)).isEqualTo(123);

		assertThat(NumberUtil.toLongObject("122")).isEqualTo(122L);
		assertThat(NumberUtil.toLongObject("12A")).isEqualTo(null);
		assertThat(NumberUtil.toLongObject("12A", 123L)).isEqualTo(123L);
		assertThat(NumberUtil.toLongObject(null, 123L)).isEqualTo(123L);
		
		assertThat(NumberUtil.toDoubleObject("122.1")).isEqualTo(122.1);
		assertThat(NumberUtil.toDoubleObject("12A")).isEqualTo(null);
		assertThat(NumberUtil.toDoubleObject("12A", 123.1)).isEqualTo(123.1);
		
		
		assertThat(NumberUtil.hexToIntObject("0x10")).isEqualTo(16);
		assertThat(NumberUtil.hexToIntObject("0X100")).isEqualTo(256);
		assertThat(NumberUtil.hexToIntObject("-0x100")).isEqualTo(-256);
		assertThat(NumberUtil.hexToIntObject("0xHI")).isEqualTo(null);
		assertThat(NumberUtil.hexToIntObject(null)).isEqualTo(null);
		assertThat(NumberUtil.hexToIntObject("0xHI",123)).isEqualTo(123);
		
		assertThat(NumberUtil.hexToLongObject("0x10")).isEqualTo(16L);
		assertThat(NumberUtil.hexToLongObject("0X100")).isEqualTo(256L);
		assertThat(NumberUtil.hexToLongObject("-0x100")).isEqualTo(-256L);
		assertThat(NumberUtil.hexToLongObject("0xHI")).isEqualTo(null);
		assertThat(NumberUtil.hexToLongObject(null)).isEqualTo(null);
		assertThat(NumberUtil.hexToLongObject("0xHI",123L)).isEqualTo(123L);

	}
}
