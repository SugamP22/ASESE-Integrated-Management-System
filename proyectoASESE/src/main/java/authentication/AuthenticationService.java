package authentication;

import db.UserRepository;
import entities.User;

public class AuthenticationService {
    private static User currentUser;

    public static boolean login(String email, String password) {
        var result = UserRepository.getUserByEmailAndPassword(email, password);
        if (result.isPresent()) {
            currentUser = result.get();
            return true;
        }
        return false;
    }

    public static boolean isLogged() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        if (currentUser == null) {
            return null;
        }

        // Return a copy so the original cannot be modified;
        return new User(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getSurname(),
                currentUser.getEmail(),
                currentUser.getEmailToken(),
                currentUser.getPassword(),
                currentUser.getRol()
        );
    }

    public static boolean logout() {
        currentUser = null;
        return true;
    }
}