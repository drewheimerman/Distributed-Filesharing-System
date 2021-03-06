import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.*;
public class Server {
	
	/* SERVER INFORMATION
	 * 
	 * RemoteManager: 7777
	 * Multicast: 227.1.2.3 ---- from config.properties
	 * 		port: 5656 ---- from config.properties
	 * udpUtil: Autogenerated
	 * 		destination: RemoteManager IP, args[1]
	 * udpUtilGen: 6189
	 * udpUpdates: 7778
	 * 		destination: RemoteManager IP, args[1]
	 * ServerSocket: 25055
	 * 
	 */
	
	public static void main(String[] args) throws IOException{
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		//List transactions = Collections.synchronizedList(new ArrayList<String>());		
		
		Properties properties = new Properties();
		Properties appProps = new Properties(properties);
		System.out.println(System.getProperty("user.dir"));
		InputStream in = new FileInputStream("./src/resources/config.properties");
		appProps.load(in);
		in.close();
		
		
		//Get port data from configs.properties//
		int id = Integer.parseInt(args[0]);
		InetAddress ip = InetAddress.getByName(appProps.getProperty("multicast"+(id%3)));
		int mport = Integer.parseInt(appProps.getProperty("mport"+(id%3)));
		int serverUDPPort = Integer.parseInt(appProps.getProperty("port"+id));
		//int rmPort = Integer.parseInt(appProps.getProperty("rmPort"+(id%3)));
		int rmPort = 7777;
		
		
		ServerMulticast mUtil = new ServerMulticast(ip, mport, 1);
		UDPUtilities udpUtil = new UDPUtilities();
		UDPUtilities udpUtilGen = new UDPUtilities(serverUDPPort);
		UDPUtilities udpUpdates = new UDPUtilities();
		
		
		udpUtil.setDestination(InetAddress.getByName(args[1]));
		udpUtil.setDestPort(rmPort);
		
		//udpUpdates.setDestination(InetAddress.getByName(args[1]));
		//udpUpdates.setDestPort(7778);

		byte[] b;
		ByteBuffer bb = ByteBuffer.allocate(8);
		//bb.putInt(id);
		bb.putInt(serverUDPPort);
		b = bb.array();
		
		udpUtil.sendBytes(b);

		//HeartbeatMonitor monitor = new HeartbeatMonitor(mUtil);
		//monitor.setId(id);
		//monitor.setServerPort(serverPort);
		
		HeartbeatPulse pulse = new HeartbeatPulse(udpUtil);
		//ServerRequestManager manager = new ServerRequestManager(lock, serverPort);
		
		MulticastUpdater updater = new MulticastUpdater(lock, mUtil, InetAddress.getByName(args[1]),7778);
		
		Thread update = new Thread(updater);
		
		
		System.out.println("id: "+id);
		System.out.println("serverPort: "+serverUDPPort);
		System.out.println("localhost: "+InetAddress.getByName(args[1]));
		System.out.println("rmPort: "+rmPort);
		Thread ps = new Thread(pulse);
		//Thread m = new Thread(manager);
		ps.start();
		update.start();
		//m.start();
		ServerSocket serverSocket = new ServerSocket(25057);
		while(true){
			DatagramPacket p = udpUtilGen.listen();
			udpUtilGen.setDestination(p.getAddress());
			udpUtilGen.setDestPort(p.getPort());
			
			
			String message = ""+serverSocket.getLocalPort();
			udpUtilGen.sendString(message);
			Socket client = serverSocket.accept();
			ServerRequestThread request = new ServerRequestThread(lock,client, mUtil);
			Thread req = new Thread(request);
			req.start();
			System.out.println(message);
			//System.out.println("Transactions: "+transactions.size());
		}
		
		/*try{
			ps.join();
			//m.join();
		}catch(Exception e){
			
		}
		mUtil.close();
		udpUtil.close();*/
	}
	
	/*private ConcurrentHashMap getAvailableServers(){
		
	}*/
	
	
}
