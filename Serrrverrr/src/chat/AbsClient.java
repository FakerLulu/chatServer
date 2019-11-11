/**
 * 
 */
package chat;

/**
 * @author kjmjs
 *
 */
public interface AbsClient {
	void receive();

	void send(String data);
}
