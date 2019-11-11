/**
 * 
 */
package chat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

/*class UploadServer {
	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(7777);
			System.out.println("Server ready..");
			while (true) {
				Socket s = ss.accept();
				System.out.println("접속 성공");
				System.out.println(s.getInetAddress());// Ip address
				ServerThread st = new ServerThread(s);
				st.start();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}// end of class
*/
public class PhotoServer {
	ExecutorService executorService;
	ServerSocket serverSocket;
	List<AbsClient> connections;
	private static final PhotoServer ps = new PhotoServer();

	/**
	 * 
	 */
	/**
	 * 
	 */
	private PhotoServer() {
	}

	/**
	 * @param executorService the executorService to set
	 */
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	/**
	 * @param connections the connections to set
	 */
	public void setConnections(List<AbsClient> connections) {
		this.connections = connections;
	}

	public static PhotoServer getInstance() {
		return ps;
	}

	public void photoServerStrart() {

		try {
			serverSocket = new ServerSocket(7777);
			System.out.println(serverSocket.getInetAddress());
			System.out.println(InetAddress.getLocalHost());
			System.out.println("서버시작");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				while (true) {
					try {
						Socket socket = serverSocket.accept();
						String message = "[연결 수락: " + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName() + "]";

						System.out.println(message);
						Client client = new Client(socket);
						client.receive();
					} catch (Exception e) {
						if (!serverSocket.isClosed()) {
							StopPhotoServer();
						}
						break;
					}
				}
			}
		};
		executorService.submit(runnable);

	}

	void StopPhotoServer() {
		try {
			Iterator<AbsClient> iterator = connections.iterator();
			while (iterator.hasNext()) {
				AbsClient ac = iterator.next();
				if (ac instanceof Client) {
					Client client = (Client) ac;
					client.socket.close();

				}
				iterator.remove();
			}
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			if (executorService != null && !executorService.isShutdown()) {
				executorService.shutdown();
			}

		} catch (Exception e) {
		}
	}

	class Client implements AbsClient {
		Socket socket;

		Client(Socket socket) {
			this.socket = socket;
			receive();
		}

		@Override
		public void receive() {
			// Runnable photoRecieving = () -> {
			if (socket.isClosed()) {
				return;
			}
			try {
				// client단에서 전송된 file내용 read 계열 stream
				String url = this.getClass().getResource("").getPath();
				url = url.substring(1, url.indexOf(".metadata")) + "Serrrverrr/photo/";
				BufferedInputStream up = new BufferedInputStream(socket.getInputStream());
				DataInputStream fromClient = new DataInputStream(up);

				// 전송된 file명 reading
				System.out.println("파일명 받기 대기중...");
				String filename = fromClient.readUTF();
				int filesize = Integer.parseInt(fromClient.readUTF());

				System.out.println(filename + "\t을 받습니다.");

				// client단에서 전송되는 file 내용을 server단에 생성시킨 file에 write할수 있는 stream
				FileOutputStream toFile = new FileOutputStream(url + filename);
				BufferedOutputStream outFile = new BufferedOutputStream(toFile);
				System.out.println((filename + " " + filesize));
				byte[] bb = new byte[filesize];
				int ch = 0;
				while ((ch = up.read()) != -1) {
					outFile.write(ch);
				}
				outFile.flush();
				outFile.close();
				fromClient.close();
				for (AbsClient client : connections) {
					client.send(socket.getRemoteSocketAddress() + "님이  " + filename + " 업로드");
				}
				System.out.println(filename + " 수신완료");
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			// };

			// executorService.submit(photoRecieving);
		}

		@Override
		public void send(String data) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						byte[] byteArr = data.getBytes("UTF-8");
						OutputStream outputStream = socket.getOutputStream();
						outputStream.write(byteArr);
						outputStream.flush();
					} catch (Exception e) {
						try {
							String message = "[클라이언트 통신 안됨: " + socket.getRemoteSocketAddress() + ": "
									+ Thread.currentThread().getName() + "]";
							connections.remove(Client.this);
							System.out.println(message);
							socket.close();
						} catch (IOException e2) {
						}
					}
				}
			};
			executorService.submit(runnable);
		}
	}

}
