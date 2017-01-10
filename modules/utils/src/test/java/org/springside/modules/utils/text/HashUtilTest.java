package org.springside.modules.utils.text;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.springside.modules.utils.collection.ListUtil;
import org.springside.modules.utils.io.ResourceUtil;

public class HashUtilTest {
	@Test
	public void hashCodeTest() {
		assertThat(HashUtil.hashCode("a", "b") - HashUtil.hashCode("a", "a")).isEqualTo(1);
		assertThat(HashUtil.hashCode("a", "b")).isEqualTo(HashUtil.hashCode(ListUtil.newArrayList("a", "b")));
	}

	@Test
	public void hashSha1() {
		// 普通
		String result = EncodeUtil.encodeBase64(HashUtil.sha1("hhahah"));
		System.out.println("sha1:" + result);
		assertThat(result).isEqualTo("sCtJLx2IJNto032AhdkP64t/os4=");

		// 带盐, 每次salt值不一样，所以值也不一样。
		result = EncodeUtil.encodeBase64(HashUtil.sha1("hhahah", HashUtil.generateSalt(5)));
		System.out.println("sha1 with salt:" + result);

		// 带盐，固定的盐
		result = EncodeUtil.encodeBase64(HashUtil.sha1("hhahah", new byte[] { 1, 2, 3 }));
		System.out.println("sha1 with salt:" + result);
		assertThat(result).isEqualTo("U/7wy5R1sVrjEf3dOTAPz383g2k=");

		// 带盐迭代, 每次salt值不一样，所以值也不一样。
		result = EncodeUtil.encodeBase64(HashUtil.sha1("hhahah", HashUtil.generateSalt(5), 2));
		System.out.println("sha1 with salt with iteration:" + result);

		// 带盐迭代, 固定的盐
		result = EncodeUtil.encodeBase64(HashUtil.sha1("hhahah", new byte[] { 1, 2, 3 }, 2));
		assertThat(result).isEqualTo("n9O7laits+ovoK8X8xde+XrsCtM=");
	}

	@Test
	public void hashFile() throws IOException {
		InputStream in = ResourceUtil.asStream("test.txt");
		String result = EncodeUtil.encodeBase64(HashUtil.sha1File(in));
		assertThat(result).isEqualTo("DmSnwK/Fl0Jplrwtm9tfi7cb/js=");
		result = EncodeUtil.encodeBase64(HashUtil.md5File(in));
		assertThat(result).isEqualTo("1B2M2Y8AsgTpgAmY7PhCfg==");
	}

	@Test
	public void crc32() {
		assertThat(HashUtil.crc32AsInt("hahhha1")).isEqualTo(-625925593);
		assertThat(HashUtil.crc32AsInt("hahhha2")).isEqualTo(1136161693);
		assertThat(HashUtil.crc32AsLong("hahhha1")).isEqualTo(3669041703L);
		assertThat(HashUtil.crc32AsLong("hahhha2")).isEqualTo(1136161693L);
	}

	@Test
	public void murmurhash() {
		assertThat(HashUtil.murmur32AsInt("hahhha1")).isEqualTo(106210656);
		assertThat(HashUtil.murmur32AsInt("hahhha2")).isEqualTo(1140739654);
		assertThat(HashUtil.murmur32AsInt("hahhha3")).isEqualTo(1660833342);
		assertThat(HashUtil.murmur32AsInt("hahhha4")).isEqualTo(-1044105167);
		assertThat(HashUtil.murmur32AsInt("hahhha5")).isEqualTo(-93397348);
		assertThat(HashUtil.murmur32AsInt("hahhha6")).isEqualTo(1844168902);
	}

}
