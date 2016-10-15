package knf.animeflv.Utils;

import com.loopj.android.http.LogInterface;

/**
 * Created by Jordy on 21/07/2016.
 */

public class NoLogInterface implements LogInterface {
    @Override
    public boolean isLoggingEnabled() {
        return false;
    }

    @Override
    public void setLoggingEnabled(boolean loggingEnabled) {

    }

    @Override
    public int getLoggingLevel() {
        return 0;
    }

    @Override
    public void setLoggingLevel(int loggingLevel) {

    }

    @Override
    public boolean shouldLog(int logLevel) {
        return false;
    }

    @Override
    public void v(String tag, String msg) {

    }

    @Override
    public void v(String tag, String msg, Throwable t) {

    }

    @Override
    public void d(String tag, String msg) {

    }

    @Override
    public void d(String tag, String msg, Throwable t) {

    }

    @Override
    public void i(String tag, String msg) {

    }

    @Override
    public void i(String tag, String msg, Throwable t) {

    }

    @Override
    public void w(String tag, String msg) {

    }

    @Override
    public void w(String tag, String msg, Throwable t) {

    }

    @Override
    public void e(String tag, String msg) {

    }

    @Override
    public void e(String tag, String msg, Throwable t) {

    }

    @Override
    public void wtf(String tag, String msg) {

    }

    @Override
    public void wtf(String tag, String msg, Throwable t) {

    }
}
