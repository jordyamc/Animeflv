package knf.animeflv.Utils.objects;

/**
 * Created by Jordy on 03/05/2016.
 */
public class User {
    private boolean isAdmin;
    private String AdminName;

    public User(boolean isAdmin, String adminName) {
        this.isAdmin = isAdmin;
        AdminName = adminName;
    }

    public User(boolean isAdmin) {
        this.isAdmin = isAdmin;
        AdminName = null;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getName() {
        return AdminName;
    }
}
