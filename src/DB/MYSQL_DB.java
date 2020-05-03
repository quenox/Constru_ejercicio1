package DB;
import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author keno
 */
public class MYSQL_DB 
{
    private Connection connection;
    private String db;
    private String usuario;
    private String password;
    private String servidor;
    private String port;
    
    /**
     * 
     * @param db nombre de la base de datos
     * @param usuario nombre del usuario de la base de datos
     * @param password contraseña del usuario
     * @param servidor direccion del servidor (ej:localhost)
     * @param port puerto
     */
    public MYSQL_DB(String db, String usuario, String password, String servidor, String port)
    {
        this.db = db;
        this.usuario = usuario;
        this.password = password;
        this.servidor = servidor;
        this.port = port;
    }
    
    /**
     * Conecta a la base de datos
     * @return Connection si pudo conectar Null en caso contrario
     */
    public Connection conectar()
    {
        try 
        {
            String url = "jdbc:mysql://" + this.servidor + ":"+this.port + "/" + this.db + "?user=" + this.usuario + "&pasword=" + this.password;
            this.connection = (Connection) DriverManager.getConnection(url);
            if (this.connection != null)
            {
                return this.connection;
            }
        }
                
        catch (SQLException ex) {
        }
        return null;
    }
    
    /**
     * Termina la conexion con la base de datos
     */
    public void desconectar()
    {
        try {
            this.connection.close();
            System.out.println("Desconectado");
        } catch (SQLException ex) {
            System.out.println("Error al cerrar desconectar base de datos");
        }
    }
    
    
}
