package voting_system.controller;

/**
 * A class to represent an attempt by a member to log in.
 */
public class LoginAttempt
{
    public final String user_name;  // The name of the member.
    public final String user_number;    // The membership number of the member.
    public final String user_age;   // The age of the member.
    public final String user_region;    // the region of the member.

    /**
     * A constructor.
     *
     * @param user_name     A String which is the name of the member.
     * @param user_number     A String which is the membership number of the member.
     * @param user_age     A String which is the age of the member.
     * @param user_region     A String which is the region of the member.
     */
    public LoginAttempt(String user_name, String user_number, String user_age, String user_region)
    {
        this.user_name = user_name;
        this.user_number = user_number;
        this.user_age = user_age;
        this.user_region = user_region;
    }

    /**
     * An overrde of the toString method.  Returns a JSON string.
     *
     * @return  A String which is a JSON string containing all the details.
     */
    @Override
    public String toString() {
        String str = "{\"user_name\":\"" + user_name + "\", \"user_number\":\"" + user_number + "\", \"user_age\":\"" + user_age + "\", \"user_region\":\"" + user_region +"\"}";
        return str;
    }
}
