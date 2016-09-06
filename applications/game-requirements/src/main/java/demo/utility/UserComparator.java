package demo.utility;

import java.util.Comparator;

import demo.model.User;

public class UserComparator implements Comparator<User> {
	@Override
    public int compare(User o1, User o2) {
        return o2.getName().compareTo(o1.getName());
    }
}
