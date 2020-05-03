package controlador;
import DB.MYSQL_DB;
import com.mysql.jdbc.PreparedStatement;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import modelo.Farmacia;

/**
 *
 * @author eugenio peredo
 */
public class FXMLDocumentController implements Initializable 
{
    @FXML
    private ComboBox<Farmacia>     cBoxFarmacia;
    @FXML
    private ComboBox<String>       cBoxRegion, cBoxComuna;
    @FXML
    private Slider                 sliderMaxPrecio;
    @FXML
    private TextField              txtBuscarMedicamento;
    @FXML
    private Button                 btnBuscarMedicamento;
    @FXML
    private ListView<String>       listaMedicamentos, listaCompra;
    @FXML
    private Button                 btnExportarPDF;
    private MYSQL_DB               conexion;
    private String                 medicamento;
    private ObservableList<String> medicamentos;
    private ObservableList<String> compras;        
    /**
     * Muestra los medicamentos
     */
    @FXML
    void mostrarMedicamentos(ActionEvent event) 
    {
        this.medicamentos.clear();
        this.medicamento = "'"+this.txtBuscarMedicamento.getText()+"'";

        String idfarmacia = cBoxFarmacia.getSelectionModel().getSelectedItem().getId();
        String region     = "'"+cBoxRegion.getSelectionModel().getSelectedItem()+"'";
        String comuna     = "'"+cBoxComuna.getSelectionModel().getSelectedItem()+"'";
        try
        {
            String consulta        = "SELECT * FROM Farmacia, Medicamento, MedicamentoSucursal, Sucursal WHERE Farmacia.id=Sucursal.refFarmacia AND Medicamento.codigo=MedicamentoSucursal.refMedicamento AND MedicamentoSucursal.refSucursal=Sucursal.id AND Farmacia.id="+idfarmacia+" AND Sucursal.region="+region+" AND Sucursal.comuna="+comuna+" AND Medicamento.nombre="+this.medicamento;
            PreparedStatement stmt = (PreparedStatement) this.conexion.conectar().prepareStatement(consulta);
            ResultSet rs           = stmt.executeQuery();
            this.txtBuscarMedicamento.setText("");
            while ( rs.next() )
            {
                String dato = rs.getString("Medicamento.nombre")+" <---------- "+rs.getString("Farmacia.nombre")+" "+rs.getString("Sucursal.region")+"-"+rs.getString("Sucursal.comuna")+"----------> $"+rs.getString("MedicamentoSucursal.precio");
                this.medicamentos.add(dato);
            }
            this.listaMedicamentos.setItems(this.medicamentos);
        }       
        catch (SQLException ex) {
            System.out.println("Error al cargar medicamentos desde la DB");
        }
    }  
    
    @FXML
    void enviarAListaDeCompra(MouseEvent event) 
    {
        if ( event.getClickCount() == 2 )
        {
            String seleccionado = this.listaMedicamentos.getSelectionModel().getSelectedItem();
            this.compras.add(seleccionado);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        this.conexion     = new MYSQL_DB("farmacias", "root", "", "localhost", "3306");

        cargarFarmaciasAlComboBoxFromDB();
        cargarRegionesAlComboBoxFromDB();
        cargarComunasAlComboBoxFromDB();
        inicializarListaMedicamentos();
        inicializarListaDeCompras();
    }
    
    /**
     * 
     */
    private void inicializarListaMedicamentos ()
    {
        this.medicamentos = FXCollections.observableArrayList(); 
        this.listaMedicamentos.setItems(medicamentos);
        cargaInicialMedicamentos();
    }
    
    private void inicializarListaDeCompras() 
    {
        this.compras = FXCollections.observableArrayList();
        this.listaCompra.setItems(compras);
    }
    /**
     * Carga las farmacias al comboBox desde la base de datos
     */
    private void cargarFarmaciasAlComboBoxFromDB()
    {
        ObservableList<Farmacia> farmacias = FXCollections.observableArrayList();
        try 
        {
            String consulta = "SELECT * FROM Farmacia";
            PreparedStatement stmt = (PreparedStatement) this.conexion.conectar().prepareStatement(consulta);
            ResultSet rs = stmt.executeQuery();
            
            while ( rs.next() )
            {
                farmacias.add(new Farmacia(rs.getString("id"), rs.getString("nombre")));
            }
            this.cBoxFarmacia.setItems(farmacias);
            this.cBoxFarmacia.getSelectionModel().selectFirst();
        } 
        catch (SQLException ex) {
        }
    }

    /**
     * 
     */
    private void cargarRegionesAlComboBoxFromDB()
    {
        ObservableList<String> regiones = FXCollections.observableArrayList();        
        try 
        {
            String consulta = "SELECT DISTINCT region FROM Sucursal";
            PreparedStatement stmt = (PreparedStatement) this.conexion.conectar().prepareStatement(consulta);
            ResultSet rs = stmt.executeQuery();
            
            while ( rs.next() )
            {
                String region = rs.getString("region");
                regiones.add(region);
            }
            this.cBoxRegion.setItems(regiones);
            this.cBoxRegion.getSelectionModel().selectFirst();
        } 
        catch (SQLException ex) {
            System.out.println("Error al cargar farmacias desde la DB");
        }        
    }
    
    /**
     * 
     */
    private void cargarComunasAlComboBoxFromDB ()
    {
        ObservableList<String> comunas = FXCollections.observableArrayList();
        try
        {
            String consulta = "SELECT DISTINCT comuna FROM Sucursal";
            PreparedStatement stmt = (PreparedStatement) this.conexion.conectar().prepareStatement(consulta);
            ResultSet rs = stmt.executeQuery();
            
            while ( rs.next() )
            {
                String comuna = rs.getString("comuna");
                comunas.add(comuna);
            }
            this.cBoxComuna.setItems(comunas);
            this.cBoxComuna.getSelectionModel().selectFirst();
        }       
        catch (SQLException ex) {
            System.out.println("Error al cargar farmacias desde la DB");
        }
    }

    private void cargaInicialMedicamentos ()
    {
        String idfarmacia = cBoxFarmacia.getSelectionModel().getSelectedItem().getId();
        String region   = "'"+cBoxRegion.getSelectionModel().getSelectedItem()+"'";
        String comuna   = "'"+cBoxComuna.getSelectionModel().getSelectedItem()+"'";
        try
        {
            String consulta        = "SELECT * FROM Farmacia, Medicamento, MedicamentoSucursal, Sucursal WHERE Farmacia.id=Sucursal.refFarmacia AND Medicamento.codigo=MedicamentoSucursal.refMedicamento AND MedicamentoSucursal.refSucursal=Sucursal.id AND Farmacia.id="+idfarmacia+" AND Sucursal.region="+region+" AND Sucursal.comuna="+comuna;
            PreparedStatement stmt = (PreparedStatement) this.conexion.conectar().prepareStatement(consulta);
            ResultSet rs           = stmt.executeQuery();
            
            while ( rs.next() )
            {
                String dato = rs.getString("Medicamento.nombre")+" <---------- "+rs.getString("Farmacia.nombre")+" "+rs.getString("Sucursal.region")+"-"+rs.getString("Sucursal.comuna")+"----------> $"+rs.getString("MedicamentoSucursal.precio");
                this.medicamentos.add(dato);
            }
            this.listaMedicamentos.setItems(this.medicamentos);
        }       
        catch (SQLException ex) {
            System.out.println("Error al cargar medicamentos desde la DB");
        }  
    }    
}
