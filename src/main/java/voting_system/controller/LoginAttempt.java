package voting_system.controller;

public class LoginAttempt
{
    public final String user_name;
    public final String user_number;
    public final String user_age;
    public final String user_region;

    public LoginAttempt(String user_name, String user_number, String user_age, String user_region)
    {
        this.user_name = user_name;
        this.user_number = user_number;
        this.user_age = user_age;
        this.user_region = user_region;
    }

    @Override
    public String toString() {
        String str = "{\"user_name\":\"" + user_name + "\", \"user_number\":\"" + user_number + "\", \"user_age\":\"" + user_age + "\", \"user_region\":\"" + user_region +"\"}";
        System.out.println(str);    //todo remove
        return str;
    }
}
