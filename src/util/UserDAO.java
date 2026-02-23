package util;

import java.sql.*;

import view.Login;

public class UserDAO {

	public static boolean register(
	        String username,
	        String password,
	        String email,
	        String phone,
	        String address,
	        String gender,
	        String role
	) {

	    String sql = """
	        INSERT INTO users
	        (username, password, email, phone, address, gender, role, balance)
	        VALUES (?, ?, ?, ?, ?, ?, ?, 0)
	    """;

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, username);
	        ps.setString(2, password);
	        ps.setString(3, email);
	        ps.setString(4, phone);
	        ps.setString(5, address);
	        ps.setString(6, gender);
	        ps.setString(7, role);

	        ps.executeUpdate();
	        return true;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}


    public static String login(String username, String password) {
        String sql = "SELECT role FROM users WHERE username=? AND password=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("role"); // CUSTOMER / ADMIN / COURIER
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Login.LoginResult loginByEmail(String email, String password) {

        String sql =
            "SELECT id, username, role FROM users WHERE email = ? AND password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Login.LoginResult(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("role")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
