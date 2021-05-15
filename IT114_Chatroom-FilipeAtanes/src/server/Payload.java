package server;
import java.io.Serializable;
import java.awt.Font;

public class Payload implements Serializable {

    /**
     * baeldung.com/java-serial-version-uid
     */
    private static final long serialVersionUID = -6687715510484845706L;

    private String clientName;

    public void setClientName(String s) {
	this.clientName = s;
    }

    public String getClientName() {
	return clientName;
    }

    private String message;
    private Font style;

    public void setMessage(String s, Font Style) {
	this.message = s;
	this.style = Style;
    }

    public String getMessage() {
	return this.message;
    }

    public Font getStyle() {
	return this.style;
    }
	
    private PayloadType payloadType;

    public void setPayloadType(PayloadType pt) {
	this.payloadType = pt;
    }

    public PayloadType getPayloadType() {
	return this.payloadType;
    }

    private int number;

    public void setNumber(int n) {
	this.number = n;
    }

    public int getNumber() {
	return this.number;
    }

    @Override
    public String toString() {
	return String.format("Type[%s], Number[%s], Message[%s]", getPayloadType().toString(), getNumber(),
		getMessage());
    }
}