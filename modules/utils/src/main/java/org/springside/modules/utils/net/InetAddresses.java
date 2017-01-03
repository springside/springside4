package org.springside.modules.utils.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springside.modules.utils.collection.Maps;
import org.springside.modules.utils.collection.Maps.ValueCreator;
import org.springside.modules.utils.text.Strings;

/**
 * InetAddress工具类，基于Guava的InetAddress.
 * 
 * 主要包含int, String/IPV4String, InetAdress/Inet4Address之间的互相转转
 * 
 * 先将字符串传换为byte[]再用InetAddress.getByAddress(byte[])，避免了InetAddress.getByName(ip)可能引起的DNS访问.
 * 
 * @author calvin
 */
public class InetAddresses {

	private static ConcurrentHashMap<String, InetAddress> strToInetCache = Maps.newConcurrentHashMap();
	private static ConcurrentHashMap<Integer, Inet4Address> intToInetCache = Maps.newConcurrentHashMap();

	/**
	 * 从InetAddress转化到int, 传输和存储时, 用int代表InetAddress是最小的开销.
	 * 
	 * InetAddress可以是IPV4或IPV6，都会转成IPV4.
	 * 
	 * @see com.google.common.net.InetAddresses#coerceToInteger(InetAddress)
	 */
	public int toInt(InetAddress address) {
		return com.google.common.net.InetAddresses.coerceToInteger(address);
	}

	/**
	 * InetAddress转换为String.
	 * 
	 * InetAddress可以是IPV4或IPV6. 其中IPV4直接调用getHostAddress()
	 * 
	 * @see com.google.common.net.InetAddresses#toAddrString(InetAddress)
	 */
	public static String toString(InetAddress ip) {
		return com.google.common.net.InetAddresses.toAddrString(ip);
	}

	/**
	 * 从int转换为Inet4Address(仅支持IPV4)
	 */
	public Inet4Address fromInt(int address) {
		return com.google.common.net.InetAddresses.fromInteger(address);
	}

	/**
	 * 从int转换为Inet4Address(仅支持IPV4)
	 * 
	 * 如果应用中需要处理的InetAddress非常有限，使用又非常频繁时，可使用本方法。
	 */
	public Inet4Address fromIntInCache(final int address) {
		return Maps.createIfAbsent(intToInetCache, address, new ValueCreator<Inet4Address>() {
			public Inet4Address get() {
				return com.google.common.net.InetAddresses.fromInteger(address);
			}
		});
	}

	/**
	 * 从String转换为InetAddress.
	 * 
	 * IpString可以是ipv4 或 ipv6 string, 但不可以是域名.
	 * 
	 * 先字符串传换为byte[]再调getByAddress(byte[])，避免了调用getByName(ip)可能引起的DNS访问.
	 */
	public InetAddress fromIpString(String address) {
		return com.google.common.net.InetAddresses.forString(address);
	}

	/**
	 * 从String转换为InetAddress.
	 * 
	 * IpString可以是ipv4 或 ipv6 string, 但不可以是域名.
	 * 
	 * 先字符串传换为byte[]再用getByAddress(byte[])，避免了getByName(name)可能引起的DNS访问.
	 */
	public InetAddress fromIpStringInCache(final String address) {
		return Maps.createIfAbsent(strToInetCache, address, new ValueCreator<InetAddress>() {
			public InetAddress get() {
				return com.google.common.net.InetAddresses.forString(address);
			}
		});
	}

	/**
	 * 从IPv4String转换为InetAddress.
	 * 
	 * IpString如果确定ipv4, 使用本方法减少字符分析消耗 .
	 * 
	 * 先字符串传换为byte[]再调getByAddress(byte[])，避免了调用getByName(ip)可能引起的DNS访问.
	 */
	public Inet4Address fromIp4String(String address) {
		byte[] bytes = ip4StringToBytes(address);
		if (bytes == null) {
			return null;
		} else {
			try {
				return (Inet4Address) Inet4Address.getByAddress(bytes);
			} catch (UnknownHostException e) {
				throw new AssertionError(e);
			}
		}
	}

	/**
	 * 从IPv4String转换为InetAddress.
	 * 
	 * IpString如果确定ipv4, 使用本方法减少字符分析消耗 .
	 * 
	 * 先字符串传换为byte[]再用InetAddress.getByAddress(byte[])，避免了InetAddress.getByName(ip)可能引起的DNS访问.
	 */
	public Inet4Address fromIp4StringInCache(final String address) {
		return (Inet4Address) Maps.createIfAbsent(strToInetCache, address, new ValueCreator<Inet4Address>() {
			public Inet4Address get() {
				return fromIp4String(address);
			}
		});
	}

	/**
	 * int转换到IPV4 String
	 */
	public static String intToIpv4String(int i) {
		StringBuilder buf = new StringBuilder(15);
		buf.append(i >> 24 & 0xff);
		buf.append('.');
		buf.append(i >> 16 & 0xff);
		buf.append('.');
		buf.append(i >> 8 & 0xff);
		buf.append('.');
		buf.append(i & 0xff);
		return buf.toString();
	}

	/**
	 * Ipv4 String 转换到int
	 */
	public static int Ipv4StringToInt(String ipv4Str) {
		byte[] byteAddress = ip4StringToBytes(ipv4Str);
		if (byteAddress == null) {
			return 0;
		} else {
			return ((byteAddress[0] & 0xff) << 24) | ((byteAddress[1] & 0xff) << 16) | ((byteAddress[2] & 0xff) << 8)
					| (byteAddress[3] & 0xff);
		}
	}

	/**
	 * Ipv4 String 转换到byte[]
	 */
	private static byte[] ip4StringToBytes(String ipv4Str) {
		if (ipv4Str == null) {
			return null;
		}

		List<String> it = Strings.split(ipv4Str, '.', 4);
		if (it.size() != 4) {
			return null;
		}

		byte[] byteAddress = new byte[4];
		for (int i = 0; i < 4; i++) {
			int tempInt = Integer.parseInt(it.get(i));
			if (tempInt > 255) {
				return null;
			}
			byteAddress[i] = (byte) tempInt;
		}
		return byteAddress;
	}
}
