package db;

import entities.Whitelist;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class WhitelistRepository {

    private static final Connection db = Db.getConnection();

    public static ArrayList<Whitelist> getWhitelist() {
        var results = new ArrayList<Whitelist>();

        try (
                var st = db.prepareStatement("SELECT email from whitelist");
                var rs = st.executeQuery();

        ){
            while(rs.next()){
                results.add(new Whitelist(rs.getString("email")));
            }

        } catch(SQLException e){
            throw new DatabaseException(e);
        }

        return results;
    }

    public static void addEmail(String email) {
        try (var st = db.prepareStatement("INSERT INTO whitelist(email) VALUES (?);")) {
            st.setString(1, email);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static void deleteEmail(String email) {
        try (var st = db.prepareStatement("DELETE FROM whitelist WHERE email = ?;")) {
            st.setString(1, email);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static void updateEmail(String oldEmail, String newEmail) {
        try (var st = db.prepareStatement("UPDATE whitelist SET email = ? WHERE email = ?;")) {
            st.setString(1, newEmail);
            st.setString(2, oldEmail);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static boolean isEmailInWhitelistOrUsersList(String email) {
        try (var st = db.prepareStatement("SELECT ? IN (SELECT email FROM users UNION SELECT email FROM whitelist) AS is_in_whitelist")){
            st.setString(1, email);
            try (var rs = st.executeQuery()) {
                if (!rs.next()) {
                    throw new DatabaseException("No row was returned");
                }
                return rs.getBoolean(1);
            }
        } catch(SQLException e){
            throw new DatabaseException(e);
        }
    }
}
