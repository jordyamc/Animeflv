package knf.animeflv.Jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class JobsCreator implements JobCreator {
    @Override
    public Job create(String s) {
        switch (s) {
            case CheckJob.TAG:
                return new CheckJob();
            default:
                return null;
        }
    }
}
