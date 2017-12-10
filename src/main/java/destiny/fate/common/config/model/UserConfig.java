package destiny.fate.common.config.model;

/**
 * Created by zhangtianlong01 on 2017/12/10.
 */
public class UserConfig {

    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserConfig{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
