package knf.animeflv.DownloadManager;

/**
 * Created by Jordy on 04/03/2016.
 */
public class CookieConstructor {
    String cookie;
    String useAgent;
    String referer;

    public CookieConstructor(String cookie, String useAgent, String referer) {
        this.cookie = cookie;
        this.useAgent = useAgent;
        this.referer = referer;
    }

    public String getCookie() {
        return cookie;
    }

    public String getUseAgent() {
        return useAgent;
    }

    public String getReferer() {
        return referer;
    }
}
