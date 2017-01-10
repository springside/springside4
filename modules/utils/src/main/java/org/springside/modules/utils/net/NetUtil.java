package org.springside.modules.utils.net;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Random;

import javax.net.ServerSocketFactory;

import com.google.common.annotations.Beta;

/**
 * 关于网络的工具类.
 * 
 * 1. Local Address(TODO)
 * 
 * 2. 查找空闲端口
 * 
 * @author calvin
 *
 */
@Beta
public class NetUtil {

	public static final int PORT_RANGE_MIN = 1024;

	public static final int PORT_RANGE_MAX = 65535;

	private static final Random random = new Random();

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
	 * 随机找一个空闲端口,from Spring SocketUtils
	 */
	public static int findRandomAvailablePort() {
		return findRandomAvailablePort(PORT_RANGE_MIN, PORT_RANGE_MAX);
	}

	/**
	 * 在一个范围里随机找一个空闲端口,from Spring SocketUtils
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
	 * 从某个端口开始，递增找一个空闲端口.
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
}
