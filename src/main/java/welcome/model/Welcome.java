// A resource, living in the model sub-package.

package welcome.model;

public class Welcome {

    private String lang;
    private String msg;

    // Default constructor, setting attributes to meaningless default values,
    // just to avoid Welcome instances with null fields.
    public Welcome() { lang = "_"; msg = "_"; }

    // Standard constructor
    public Welcome(String lang, String msg) {
        this.lang = lang;
        this.msg = msg;
    }

    // Copy constructor
    public Welcome(Welcome that) {
        this.lang = that.lang;
        this.msg = that.msg;
    }

    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    @Override
    public String toString() {
        return "Welcome{" +
               "lang=\"" + lang + "\"," +
               "msg=\"" + msg + "\"}";
    }

}
