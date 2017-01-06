package org.springside.modules.utils.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.utils.base.PropertiesUtil;
import org.springside.modules.utils.collection.MapUtil;
import org.springside.modules.utils.collection.MapUtil.ValueCreator;
import org.springside.modules.utils.number.NumberUtil;
import org.springside.modules.utils.text.MoreStringUtil;

/**
 * InetAddress工具类，基于Guava的InetAddress.
 * 
 * 主要包含int, String/IPV4String, InetAdress/Inet4Address之间的互相转转
 * 
 * 先将字符串传换为byte[]再用InetAddress.getByAddress(byte[])，避免了InetAddress.getByName(ip)可能引起的DNS访问.
 * 
 * @author calvin
 */
public class IPUtil {
	private static Logger logger = LoggerFactory.getLogger(IPUtil.class);

	private static ConcurrentHashMap<String, InetAddress> strToInetCache = MapUtil.newConcurrentHashMap();
	private static ConcurrentHashMap<Integer, Inet4Address> intToInetCache = MapUtil.newConcurrentHashMap();
	private static InetAddress localAddress;
	private static String localHost;

	static {
		initLocalAddress();
	}

	public static InetAddress getLocalAddress() {
		return localAddress;
	}

	public static String getLocalHost() {
		return localHost;
	}

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
		return MapUtil.createIfAbsent(intToInetCache, address, new ValueCreator<Inet4Address>() {
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
		return MapUtil.createIfAbsent(strToInetCache, address, new ValueCreator<InetAddress>() {
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
		return (Inet4Address) MapUtil.createIfAbsent(strToInetCache, address, new ValueCreator<Inet4Address>() {
			public Inet4Address get() {
				return fromIp4String(address);
			}
		});
	}

	/**
	 * int转换到IPV4 String, from Netty NetUtil
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
			return NumberUtil.toInt(byteAddress);
		}
	}

	/**
	 * Ipv4 String 转换到byte[]
	 */
	private static byte[] ip4StringToBytes(String ipv4Str) {
		if (ipv4Str == null) {
			return null;
		}

		List<String> it = MoreStringUtil.split(ipv4Str, '.', 4);
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

	/**
	 * 初始化本地地址
	 */
	private static void initLocalAddress() {
		NetworkInterface nic = null;
		// 根据命令行执行hostname获得本机hostname， 与/etc/hosts 中该hostname的第一条ip配置，获得ip地址
		try {
			localAddress = InetAddress.getLocalHost();
			nic = NetworkInterface.getByInetAddress(localAddress);
		} catch (Exception ignored) {
		}

		// 如果结果为空，或是一个loopback地址(127.0.0.1), 或是ipv6地址，再遍历网卡尝试获取
		if (localAddress == null || nic == null || localAddress.isLoopbackAddress()
				|| localAddress instanceof Inet6Address) {
			InetAddress lookedUpAddr = findLocalAddressViaNetworkInterface();
			// 仍然不符合要求，只好使用127.0.0.1
			try {
				localAddress = (lookedUpAddr != null ? lookedUpAddr : InetAddress.getByName("127.0.0.1"));
			} catch (UnknownHostException ignored) {
			}
		}

		localHost = IPUtil.toString(localAddress);

		logger.info("localhost is {}", localHost);
	}

	/**
	 * 根据preferNamePrefix 与 defaultNicList的配置网卡，找出合适的网卡
	 */
	private static InetAddress findLocalAddressViaNetworkInterface() {
		// 如果hostname +/etc/hosts 得到的是127.0.0.1, 则首选这块网卡
		String preferNamePrefix = PropertiesUtil.readString("localhost.prefer.nic.prefix",
				"LOCALHOST_PREFER_NIC_PREFIX", "bond0.");
		// 如果hostname +/etc/hosts 得到的是127.0.0.1, 和首选网卡都不符合要求，则按顺序遍历下面的网卡
		String defaultNicList = PropertiesUtil.readString("localhost.default.nic.list", "LOCALHOST_DEFAULT_NIC_LIST",
				"bond0,eth0,em0,br0");

		InetAddress resultAddress = null;
		Map<String, NetworkInterface> candidateInterfaces = MapUtil.newHashMap();

		// 遍历所有网卡，找出所有可用网卡，尝试找出符合prefer前缀的网卡
		try {
			for (Enumeration<NetworkInterface> allInterfaces = NetworkInterface.getNetworkInterfaces(); allInterfaces
					.hasMoreElements();) {
				NetworkInterface nic = allInterfaces.nextElement();
				// 检查网卡可用并支持广播
				try {
					if (!nic.isUp() || !nic.supportsMulticast()) {
						continue;
					}
				} catch (SocketException e) {
					continue;
				}

				// 检查是否符合prefer前缀
				String name = nic.getName();
				if (name.startsWith(preferNamePrefix)) {
					// 检查有否非ipv6 非127.0.0.1的inetaddress
					resultAddress = findAvailableInetAddress(nic);
					if (resultAddress != null) {
						return resultAddress;
					}
				} else {
					// 不是Prefer前缀，先放入可选列表
					candidateInterfaces.put(name, nic);
				}
			}

			for (String nifName : defaultNicList.split(",")) {
				NetworkInterface nic = candidateInterfaces.get(nifName);
				resultAddress = findAvailableInetAddress(nic);
				if (resultAddress != null) {
					return resultAddress;
				}
			}
		} catch (SocketException e) {
			return null;
		}
		return null;

	}

	/**
	 * 检查有否非ipv6，非127.0.0.1的inetaddress
	 */
	private static InetAddress findAvailableInetAddress(NetworkInterface nic) {
		for (Enumeration<InetAddress> indetAddresses = nic.getInetAddresses(); indetAddresses.hasMoreElements();) {
			InetAddress inetAddress = indetAddresses.nextElement();
			if (!(inetAddress instanceof Inet6Address) && !inetAddress.isLoopbackAddress()) {
				return inetAddress;
			}
		}
		return null;
	}
}
