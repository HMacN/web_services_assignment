package voting_system.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A container class to capture incoming login attempts by admins.
 */
public class AdminLoginAttempt {
    @JsonProperty("admin_pass")
    public final String admin_pass; //The admin password.

    @JsonProperty("message")
    public final String message;    // A filler parameter to make the object parse properly.

    /**
     * The constructor
     *
     * @param admin_pass    A String which is the password of the admin logging in.
     * @param message       A String which is an accompanying message.
     */
    public AdminLoginAttempt(String admin_pass, String message) {
        this.admin_pass = admin_pass;
        this.message = message;
    }

    /**
     * A getter for the admin password.
     *
     * @return  A String which is the admin password.
     */
    public String getAdminPass() {
        return this.admin_pass;
    }
}
