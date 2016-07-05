package knf.animeflv.Utils;


import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import se.simbio.encryption.Encryption;

public class EncryptionHelper {
    public interface EncryptionListenerSingle{
        void onFinish(final String result);
    }

    public interface EncryptionListenerMultiple{
        void onFinish(final String[] results);
    }
    public static void asyncEncrypt(final String request,final EncryptionListenerSingle encryption){
        new AsyncTask<String,String,String>(){
            @Override
            protected String doInBackground(String... strings) {
                encryption.onFinish(Encryption.getDefault("Key", "Salt", new byte[16]).encryptOrNull(request));
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void asyncDecrypt(final String request,final EncryptionListenerSingle encryption){
        new AsyncTask<String,String,String>(){
            @Override
            protected String doInBackground(String... strings) {
                encryption.onFinish(Encryption.getDefault("Key", "Salt", new byte[16]).decryptOrNull(request));
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void asyncEncryptMultiple(final EncryptionListenerMultiple encryption,final String... forEncrypt){
        new AsyncTask<String,String,String>(){
            @Override
            protected String doInBackground(String... strings) {
                List<String> list=new ArrayList<>();
                for (String word:forEncrypt){
                    list.add(Encryption.getDefault("Key", "Salt", new byte[16]).encryptOrNull(word));
                }
                String[] flist=new String[list.size()];
                list.toArray(flist);
                encryption.onFinish(flist);
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void asyncDecryptMultiple(final EncryptionListenerMultiple encryption,final String... forDecrypt){
        new AsyncTask<String,String,String>(){
            @Override
            protected String doInBackground(String... strings) {
                List<String> list=new ArrayList<>();
                for (String word:forDecrypt){
                    list.add(Encryption.getDefault("Key", "Salt", new byte[16]).decryptOrNull(word));
                }
                String[] flist=new String[list.size()];
                list.toArray(flist);
                encryption.onFinish(flist);
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }
}
