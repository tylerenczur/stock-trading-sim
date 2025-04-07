package tradingSim.service;
import org.springframework.stereotype.Service;
import tradingSim.exception.*;
import tradingSim.model.User;
import tradingSim.dto.TradableDTO;

import java.util.Collection;
import java.util.TreeMap;

@Service
public class UserManager {
    //TODO Services are beans, and are automatically handled as singletons by Spring, remove singleton functionality
    private static UserManager instance;
    private final TreeMap<String, User> users = new TreeMap<>();

    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void init(String[] usersIn) throws DataValidationException {
        if (usersIn == null) {
            throw new DataValidationException("usersIn is null");
        }
        for (String s : usersIn) {
            if (s == null) {
                throw new DataValidationException("null String in usersIn");
            }
            users.put(s, new User(s));
        }
    }

    public void updateTradable(String userId, TradableDTO o) throws DataValidationException {
        if (userId == null || o == null) {
            throw new DataValidationException("null argument passed");
        }
        if (!users.containsKey(userId)) {
            throw new DataValidationException("user doesn't exist");
        }
        users.get(userId).updateTradable(o);
    }

    public User getUser(String userId) throws DataValidationException, MissingUserException {
        if (userId == null) {
            throw new DataValidationException("userId is null");
        }
        if (!users.containsKey(userId)) {
            throw new MissingUserException("user does not exist");
        }
        return users.get(userId);
    }

    public Collection<User> getAllUsers() {
        if (users.isEmpty()) {
            return null;
        }
        return users.values();
    }

    public void addUser(String userId) throws DataValidationException {
        if (userId == null || users.containsKey(userId)) {
            throw new DataValidationException("userId is null or user already exists");
        }
        users.put(userId, new User(userId));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (User user : users.values()) {
            s.append(String.format("    %s\n", user));
        }
        return s.toString();
    }
}
