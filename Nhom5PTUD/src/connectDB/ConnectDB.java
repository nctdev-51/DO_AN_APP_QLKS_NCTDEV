package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	private static Connection con = null;
	private static ConnectDB instance = new ConnectDB();

	public static ConnectDB getInstance() {
		return instance;
	}

	public void connect() {
		// Chỉ kết nối nếu 'con' đang là null
		if (con != null) {
			return; // Đã kết nối rồi
		}

		String url = "jdbc:sqlserver://localhost:1433;"
				+ "databaseName=qlkhachsanTATP_db;"
				+ "encrypt=true;"
				+ "trustServerCertificate=true;";
		String user = "sa";
		String password = "sapassword"; 

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(url, user, password);
			System.out.println("✅ Kết nối CSDL thành công!");
		} catch (ClassNotFoundException e) {
			System.err.println("❌ Không tìm thấy driver SQL Server JDBC! (Hãy kiểm tra file .jar)");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("❌ Kết nối CSDL thất bại! (Kiểm tra username/password/dịch vụ SQL Server)");
			e.printStackTrace();
		}
	}

	public static void disconnect() {
		if (con != null) {
			try {
				con.close();
				con = null; // Đặt lại là null
				System.out.println("🔌 Đã ngắt kết nối CSDL.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static Connection getConnection() {
		if (con == null) {
			instance.connect();
		}
		return con;
	}
}

