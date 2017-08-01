package knf.animeflv.Jobs;

import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class JobsCreator implements JobCreator {
    @Override
    public Job create(String s) {
        Log.e("JobCreator", "OnCreate: " + s);
        switch (s) {
            case CheckJob.TAG:
                return new CheckJob();
            default:
                return null;
        }
    }
}
