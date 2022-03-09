
import java.io.Serializable;

public class Envelope implements Serializable {

    private String id;
    private String arg;
    private Object content;

    public Envelope() {
    }

    public Envelope(String id, String arg, Object content) {
        this.id = id;
        this.arg = arg;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
