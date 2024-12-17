package com.techxyte.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import javax.print.DocFlavor.URL;

import com.techxyte.beans.Users;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	// Variables
	static boolean accountFound = false;

	// JDBC URL, username and password of Oracle database
	private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521/XE";
	private static final String USERNAME = "system";
	private static final String PASSWORD = "admin";

	// JDBC variables for opening and managing connection
	private Connection conn;

	public void init() throws ServletException {

		try {
			// Load the Oracle JDBC driver
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// Open a connection
			conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		// Create a new User bean
		Users user = new Users();

		// Set properties of the User bean using form data
		user.setUsername(request.getParameter("username"));
		user.setPassword(request.getParameter("password"));

		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
			// SQL query to check login credentials
			String sql = "SELECT * FROM users";

			// Checking for valid username match
			try (Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery(sql);
				loop1: while (rs.next()) {
					if (rs.getString("username").equals(user.getUsername()) == true) {
						accountFound = true;
						break loop1;
					}
				}
			}

			if (accountFound = false) {
				RequestDispatcher rd = request.getRequestDispatcher("loginFailure.html");
				rd.include(request, response);
			} else {
				String sqlp1 = "SELECT PASSWORD FROM users WHERE USERNAME = '" + user.getUsername() + "'";
				
				String storedPassword = "";
				try (Statement stmt1 = connection.createStatement()) {
					ResultSet rs1 = stmt1.executeQuery(sqlp1);
					
					while (rs1.next()) {
						storedPassword = rs1.getString("PASSWORD");
					}	
				}
				if (user.getPassword().equals(storedPassword)) {
					RequestDispatcher rd = request.getRequestDispatcher("accountHome.html");
					rd.forward(request, response);
				} else {
					RequestDispatcher rd = request.getRequestDispatcher("loginFailure.html");
					rd.include(request, response);
				}
			}

		} catch (Exception e) {
			out.println("Error: " + e.getMessage());
		}

	} // doPost

} // LoginServlet
