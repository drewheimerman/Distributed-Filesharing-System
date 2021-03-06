import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerRequestManager implements Runnable {
	private ReentrantReadWriteLock lock;
	//private UDPUtilities udpUtil;
	private ExecutorService executor;
	private ServerSocket serverSocket;
	private Socket client;
	//private ConcurrentHashMap<Integer, String[]> servers;
	
	public ServerRequestManager(ReentrantReadWriteLock l, int p){
		lock = l;
		try{
			serverSocket = new ServerSocket(p);
			executor = Executors.newFixedThreadPool(4);
		}catch(Exception e){
			e.printStackTrace();
		}
		//servers = c;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try{
			while(true){
				client = serverSocket.accept();
				//String message = new String(p.getData(), 0, p.getLength());
				//executor.execute(new ServerRequestThread(lock, client));
			}
		}catch(IOException e){
			executor.shutdown();
			try {
				executor.awaitTermination(3, TimeUnit.SECONDS);
			} catch (InterruptedException e1) {
				
				e1.printStackTrace();
			}
		}finally{
			executor.shutdownNow();
		}
	}
}