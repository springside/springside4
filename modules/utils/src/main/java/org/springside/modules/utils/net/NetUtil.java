package org.springside.modules.utils.net;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;

import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.utils.base.SystemPropertiesUtil;
import org.springside.modules.utils.collection.MapUtil;

import com.google.common.annotations.Beta;

/**
 * 关于网络的工具类.
 * 
 * 1. 获取Local Address
 * 
 * 2. 查找空闲端口
 * 
 * @author calvin
 *
 */
@Beta
public abstract class NetUtil {

	private static Logger logger = LoggerFactory.getLogger(NetUtil.class);

	public static final int PORT_RANGE_MIN = 1024;

	public static final int PORT_RANGE_MAX = 65535;

	private static final Random random = new Random();

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
	 * 测试端口是否空闲可用, from Spring SocketUtils
	 */
	public static boolean isPortAvailable(int port) {
		try {
			ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 1,
					InetAddress.getByName("localhost"));
			serverSocket.close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 从1024到65535， 随机找一个空闲端口 from Spring SocketUtils
	 */
	public static int findRandomAvailablePort() {
		return findRandomAvailablePort(PORT_RANGE_MIN, PORT_RANGE_MAX);
	}

	/**
	 * 在范围里随机找一个空闲端口,from Spring SocketUtils.
	 * 
	 * @throws IllegalStateException  最多尝试(maxPort-minPort)次，如无空闲端口，抛出此异常.
	 */
	public static int findRandomAvailablePort(int minPort, int maxPort) {
		int portRange = maxPort - minPort;
		int candidatePort;
		int searchCounter = 0;

		do {
			if (++searchCounter > portRange) {
				throw new IllegalStateException(
						String.format("Could not find an available tcp port in the range [%d, %d] after %d attempts",
								minPort, maxPort, searchCounter));
			}
			candidatePort = minPort + random.nextInt(portRange + 1);
		} while (!isPortAvailable(candidatePort));

		return candidatePort;
	}

	/**
	 * 从某个端口开始，递增直到65535，找一个空闲端口.
	 * 
	 * @throws IllegalStateException 范围内如无空闲端口，抛出此异常
	 */
	public static int findAvailablePortFrom(int minPort) {
		for (int port = minPort; port < PORT_RANGE_MAX; port++) {
			if (isPortAvailable(port)) {
				return port;
			}
		}

		throw new IllegalStateException(
				String.format("Could not find an available tcp port in the range [%d, %d]", minPort, PORT_RANGE_MAX));
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
			// NOSONAR
		}

		// 如果结果为空，或是一个loopback地址(127.0.0.1), 或是ipv6地址，再遍历网卡尝试获取
		if (localAddress == null || nic == null || localAddress.isLoopbackAddress()
				|| localAddress instanceof Inet6Address) {
			InetAddress lookedUpAddr = findLocalAddressViaNetworkInterface();
			// 仍然不符合要求，只好使用127.0.0.1
			try {
				localAddress = lookedUpAddr != null ? lookedUpAddr : InetAddress.getByName("127.0.0.1");
			} catch (UnknownHostException ignored) {
				// NOSONAR
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
		String preferNamePrefix = SystemPropertiesUtil.getString("localhost.prefer.nic.prefix",
				"LOCALHOST_PREFER_NIC_PREFIX", "bond0.");
		// 如果hostname +/etc/hosts 得到的是127.0.0.1, 和首选网卡都不符合要求，则按顺序遍历下面的网卡
		String defaultNicList = SystemPropertiesUtil.getString("localhost.default.nic.list", "LOCALHOST_DEFAULT_NIC_LIST",
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
