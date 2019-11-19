package chat;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Servlet implementation class ChatServlet
 */
public class ChatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ExecutorService executorService;
	private static List<AbsClient> connections = new Vector<AbsClient>();
	private chatting ch;
	private PhotoServer ps;

	/**
	 * @see HttpServlet#HttpServlet()
	 */

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		executorService = Executors.newCachedThreadPool();
		ch = chatting.getInstance();
		ps = PhotoServer.getInstance();
		ch.setExecutorService(executorService);
		ps.setExecutorService(executorService);
		ch.setConnections(connections);
		ps.setConnections(connections);
		ch.startServer();
		ps.photoServerStrart();

	}

	@Override
	public void destroy() {
		System.out.println("종료할것");
		try {
			ps.StopPhotoServer();
			ch.stopServer();
			if (executorService != null) {
				executorService.shutdown();
			}
			System.out.println(executorService);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
