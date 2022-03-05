import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionMysql {
  // NOTA: Variables de conexión a la base de datos
  private String URL = "jdbc:mysql://localhost:3306/javamarket";
  private String USER = "root";
  private String PASS = "0810";
  private Connection con = null;

  public void conectar() {
    try {
      // NOTA: Asignar a la variable con el driver de acceso a la base de datos
      this.con = DriverManager.getConnection(URL, USER, PASS);
      //System.out.println("Conexión a la BD exitosa!!\n");
    } catch (SQLException e) {
      // NOTA: Mensajes de error en caso de alguna falla al intentar conectarse con la BD
      System.out.println("Error al conectar con la base de datos");
      System.out.println(e.toString());

      // NOTA: Regresar la variable con a null
      this.con = null;
    }
  }

  public String[][] get(String SELECT, String TABLE, String WHERE, int CLOSE, String GET[]) {
    // NOTA: Variables
    PreparedStatement ps;
    ResultSet res;
    String[][] response = new String[100][10];
    int i = 0;

    try {
      // NOTA: Sentencia de SQL a utilizar para seleccionar recursos
      ps = con.prepareStatement("SELECT " + SELECT + " FROM " + TABLE + " WHERE " + WHERE);

      // NOTA: Ejecutar sentencia SQL
      res = ps.executeQuery();

      // NOTA: Recorrer todos los resultados de la query y guardarlos en la variable response
      while(res.next()) {
        if (SELECT == "*") {
          response[i][0] = res.getString("id");
          response[i][1] = res.getString("name");
          response[i][2] = res.getString("descrip");
          response[i][3] = res.getString("price");
          response[i][4] = res.getString("stock");
        } else {
          int o = 0;
          while(GET[0] instanceof String) {
            response[i][o] = res.getString(o);
            o++;
          }
        }
        i++;
      }

      return response;
    } catch (Exception e) {
      // NOTA: Capturar errores y mostrar en pantalla
      System.out.println("Error al intentar solicitar los datos");
      System.out.println(e.toString());

      this.con = null;
      return response;
    } finally {
      // NOTA: Cerrar conexión al finalizar el proceso solo si es requerido
      if (CLOSE > 0) {
        this.close();
      }
    }
  }

  public boolean create(String TABLE, String CAMPOS, String name, String descrip, String price, int stock) {
    // NOTA: Variables
    PreparedStatement ps;

    try {
      // NOTA: Sentencia SQL
      ps = con.prepareStatement("INSERT INTO " + TABLE + " (" + CAMPOS + ") VALUES (?,?,?,?)");

      // NOTA: Preparar consulta insertando los datos faltantes en la sentencia SQL
      ps.setString(1, name);
      ps.setString(2, descrip);
      ps.setDouble(3, Double.parseDouble(price));
      ps.setInt(4, stock);

      // NOTA: Ejecutar sentencia
      int res = ps.executeUpdate();

      // NOTA: Regresar respuesta de la sentencia
      if (res > 0) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      // NOTA: Errores
      System.out.println("Error al intentar insertar los datos");
      System.out.println(e.toString());

      this.con = null;
      return false;
    } finally {
      // NOTA: Cerrar la conexión después de cada sentencia
      this.close();
    }
  }

  public boolean update(String CAMPO, String VALUE_S, String VALUE_I, int ID, int CLOSE) {
    // NOTA: Variables
    PreparedStatement ps;

    try {
      // NOTA: Sentencia SQL para actualizar los datos
      ps = con.prepareStatement("UPDATE products SET " + CAMPO + " = ? WHERE id=?");
      ps.setInt(2, ID);

      // NOTA: Verificar si se está necesitando ingresar un string o un número en la BD
      if (VALUE_S instanceof String) {
        ps.setString(1, VALUE_S);
      } else {
        ps.setDouble(1, Double.parseDouble(VALUE_I));
      }

      // NOTA: Ejecutar sentencia
      int res = ps.executeUpdate();

      // NOTA: Regresar respuesta
      if (res > 0) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      // NOTA: Errores
      System.out.println("Error al intentar actualizar los datos");
      System.out.println(e.toString());

      this.con = null;
      return false;
    } finally {
      // NOTA: Cerrar conexión solo si es necesario
      if (CLOSE > 0) {
        this.close();
      }
    }
  }

  public boolean delete(int ID) {
    // NOTA: Variables
    PreparedStatement ps;

    try {
      // NOTA: Sentencia SQL
      ps = con.prepareStatement("DELETE FROM products WHERE id=?");

      // NOTA: Setear los datos faltantes
      ps.setInt(1, ID);

      // NOTA: Ejecutar sentencia
      int res = ps.executeUpdate();

      // NOTA: Regresar resultado
      if (res > 0) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      // NOTA: Errores
      System.out.println("Error al intentar insertar los datos");
      System.out.println(e.toString());

      this.con = null;
      return false;
    } finally {
      // NOTA: Cerrar conexión después de finalizar la query
      this.close();
    }
  }

  public void close() {
    try {
      // NOTA: Cerrar conexión
      this.con.close();
      //System.out.println("Conexión cerrada exitosa!!\n");
    } catch (SQLException e) {
      // NOTA: Capturar errores
      System.out.println("Error al intentar cerrar la conexión con la BD");
      System.out.println(e.toString());

      this.con = null;
    }
  }
}
