package chat;

import java.io.IOException;
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
	public ChatServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		chatting ch;
		PhotoServer ps;
		try {
			executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

			ch = chatting.getInstance();
			ps = PhotoServer.getInstance();
			ch.setExecutorService(executorService);
			ps.setExecutorService(executorService);
			ch.setConnections(connections);
			ps.setConnections(connections);
			ch.startServer();
			ps.photoServerStrart();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void destroy() {
		ps.StopPhotoServer();
		ch.stopServer();
	}

}
