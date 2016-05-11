package knf.animeflv;

import android.os.AsyncTask;

import knf.animeflv.Interfaces.EncryptionListener;
import se.simbio.encryption.Encryption;

public class BackEncryption extends AsyncTask<Void, Void, Void> {
    private Type type;
    private String toProcess;
    private EncryptionListener listener;

    public BackEncryption(Type type, String toProcess) {
        this.type = type;
        this.toProcess = toProcess.trim();
    }

    public BackEncryption setOnFinishEncryptListener(EncryptionListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
        if (type == Type.ENCRYPT) {
            if (listener != null)
                listener.onFinish(encryption.encryptOrNull(toProcess).replace("=", "IGUAL").replace("&", "AMPERSAND").replace("\"", "COMILLA").replace("?", "PREGUNTA").replace("+", "MAS").replace("/", "SLIDE_DERECHO").replace(",", "COMA").trim());
        }
        if (type == Type.DECRYPT) {
            if (listener != null)
                listener.onFinish(encryption.decryptOrNull(toProcess.replace("IGUAL", "=").replace("AMPERSAND", "&").replace("COMILLA", "\"").replace("PREGUNTA", "?").replace("MAS", "+").replace("SLIDE_DERECHO", "/").replace("COMA", ",")).trim());
        }
        return null;
    }

    public enum Type {
        ENCRYPT(0),
        DECRYPT(1);
        int value;

        Type(int value) {
            this.value = value;
        }
    }
}
