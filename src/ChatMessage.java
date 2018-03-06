package trying;
import java.io.Serializable;

/**
 * 
 * @author Ali Oztas
 *
 */
public class ChatMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -50181201681235828L;

	static final int WHO = 0;
	static final int MSG = 1;
	static final int OFF = 2;
	private int type;
	private String message;

	ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}

	public int getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}
	

}
