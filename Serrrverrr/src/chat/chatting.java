package chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class chatting {
	private static final long serialVersionUID = 1L;
	private static boolean isRun = true;
	ExecutorService executorService;

	/**
	 * @param executorService the executorService to set
	 */
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	ServerSocket serverSocket;
	List<AbsClient> connections;

	/**
	 * @param connections the connections to set
	 */
	public void setConnections(List<AbsClient> connections) {
		this.connections = connections;
	}

	private final static chatting CHAT_INS = new chatting();

	/**
	 * 
	 */
	private chatting() {
	}

	public static chatting getInstance() {
		return CHAT_INS;
	}

	/**
	 * 
	 */
	void startServer() {
//		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		System.out.println(executorService);
		try {
			serverSocket = new ServerSocket(9000);
			System.out.println(serverSocket.getInetAddress());
			System.out.println(InetAddress.getLocalHost());
			System.out.println("서버시작");

		} catch (Exception e) {
			if (!serverSocket.isClosed()) {
				stopServer();
			}
			return;
		}

		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				while (isRun) {
					try {
						Socket socket = serverSocket.accept();
						String message = "[연결 수락: " + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName() + "]";

						System.out.println(message);
						Client client = new Client(socket);
						connections.add(client);
					} catch (Exception e) {
						if (!serverSocket.isClosed()) {
							stopServer();
						}
						break;
					}
				}
			}
		};
//		Executors.newSingleThreadExecutor().execute(runnable);

		executorService.submit(runnable);
	}

	void stopServer() {
		try {
			isRun = false;
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
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						while (isRun) {
							byte[] byteArr = new byte[1024];
							InputStream inputStream = socket.getInputStream();

							// 클라이언트가 비저상 종료를 했을 경우 IOException 발생
							int readByteCount = inputStream.read(byteArr);

							// 클라이언트가 정상적으로 Socket의 close()를 호출했을 경우
							if (readByteCount == -1) {
								throw new IOException();
							}

							String message = "[요청 처리: " + socket.getRemoteSocketAddress() + ": "
									+ Thread.currentThread().getName() + "]";

							String data = new String(byteArr, 0, readByteCount, "UTF-8");
							data += " /" + socket.getRemoteSocketAddress();
							System.out.println(data);
							for (AbsClient client : connections) {
								client.send(data);
							}
							System.out.println();
						}
					} catch (Exception e) {
						try {
							connections.remove(Client.this);
							String message = "[클라이언트 통신 안됨: " + socket.getRemoteSocketAddress() + ": "
									+ Thread.currentThread().getName() + "]";
							socket.close();
						} catch (IOException e2) {
						}
					}
				}
			};
			executorService.submit(runnable);
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
