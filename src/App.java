import java.text.DecimalFormat;
import java.util.Scanner;

public class App {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        // NOTA: Variables
        int op = -1;

        // NOTA: Bucle main
        while (op != 0) {
            // NOTA: Opciones
            if (op == 1) {
                shopping();
            }else if (op == 2) {
                admin();
            }

            // NOTA: Menú visual
            System.out.println("Trabajo prático 2\n");
            System.out.println("[1] Ir de compras");
            System.out.println("[2] Administrar productos");
            System.out.println("\n[0] Terminar programa");

            // NOTA: Scan var
            op = sc.nextInt();

            // NOTA: Clear
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
        sc.close();
    }

    public static void shopping() {
        // NOTA: Variables
        int op = -1, confirm;
        double monedero;

        System.out.println("Ingrese la cantidad de dinero que dispone para la compra: ");
        monedero = sc.nextDouble();

        monedero = Math.round(monedero*100.0)/100.0;

        // NOTA: Clear
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // NOTA: Conexión a la BD
        ConnectionMysql conex = new ConnectionMysql();
        conex.conectar();

        while (op != 0) {
            // NOTA: Get list
            String[][] response = conex.get("*", "products", "stock>0", 0, null);

            if (op > 0 && response[op-1][0] instanceof String) {
                System.out.println("Datos del producto\n");
                System.out.println("Nombre: " + response[op-1][1]);
                System.out.println("Descripción: " + response[op-1][2]);
                System.out.println("Precio: " + response[op-1][3] +"Bs");
                System.out.println("Stock: "+ response[op-1][4]);

                System.out.println("\n[1] Comprar");
                System.out.println("[0] No comprar");

                confirm = sc.nextInt();

                // NOTA: Clear
                System.out.print("\033[H\033[2J");
                System.out.flush();

                if (confirm == 1 && (monedero - Double.parseDouble(response[op-1][3]) >= 0)) {
                    String rest = (Integer.parseInt(response[op-1][4]) - 1) + "";
                    System.out.println(rest);
                    boolean query = conex.update("stock", null, rest, Integer.parseInt(response[op-1][0]), 0);

                    if (query) {
                        System.out.println("Compra exitosa!!");

                        monedero = monedero - Double.parseDouble(response[op-1][3]);

                        // NOTA: Get list
                        response = conex.get("*", "products", "stock>0", 0, null);
                    } else {
                        System.out.println("No se pudo realizar la compra");
                    }
                } else if ( (monedero - Double.parseDouble(response[op-1][3]) < 0) ) {
                    System.out.println("No tiene suficiente dinero en el monedero para comprar este producto");
                }
            }

            System.out.println("Lista de productos disponibles\n");

            // NOTA: print products
            int i=0;
            while (response[i][0] instanceof String) {
                System.out.println("["+(i+1)+"]: " + response[i][1]);
                i++;
            }
            System.out.println("\n[0] Salir de la tienda");

            System.out.println("Saldo disponible: "+ monedero + "Bs");
            System.out.println("\nSeleccione un producto para comprar");

            op = sc.nextInt();

            // NOTA: Clear
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    public static void admin() {
         // NOTA: Variables
         int op = -1;

         // NOTA: Bucle main
        while (op != 0) {
             // NOTA: Opciones
            if (op == 1) {
                crear();
            } else if (op == 2) {
                editar();
            } else if (op == 3) {
                eliminar();
            } else if (op == 4) {
                listar();
            }

            // NOTA: Menú visual
            System.out.println("Administrar productos\n");
            System.out.println("[1] Crear producto");
            System.out.println("[2] Editar producto");
            System.out.println("[3] Eliminar producto");
            System.out.println("[4] Listar productos existentes");
            System.out.println("\n[0] Regresar al menú anterior");

            // NOTA: Scan var
            op = sc.nextInt();

            // NOTA: Clear
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    public static void listar() {
        // NOTA: Conexión a la BD
        ConnectionMysql conex = new ConnectionMysql();
        conex.conectar();
        String[][] response = conex.get("*", "products", "1", 1, null);

        // NOTA: Variables
        int op = -1;
        int i = 0;

        // NOTA: Bucle main
       while (op != 0) {
           // NOTA: Menú visual
           System.out.println("Administrar productos\n");
           
           //NOTA: Verificar existencia de datos
           if (!(response[0][0] instanceof String)) {
               System.out.println("No hay datos registrados\n");
           }
            
           while (response[i][0] instanceof String) {
            System.out.println("ID: " + response[i][0]);
            System.out.println("Nombre: " + response[i][1]);
            System.out.println("Descripción: " + response[i][2]);
            System.out.println("Precio: " + response[i][3]);
            System.out.println("Disponibles: " + response[i][4] + "\n");
            i++;
           }

           System.out.println("\n[0] Regresar al menú anterior");

           // NOTA: Scan var
           op = sc.nextInt();

           // NOTA: Clear
           System.out.print("\033[H\033[2J");
           System.out.flush();
       }
    }

    public static void crear() {
        // NOTA: Conexión a la BD
        ConnectionMysql conex = new ConnectionMysql();
        conex.conectar();

        // NOTA: Variables
        String nombre;
        String descrip;
        Double precio;
        String price;
        int stock;
        int sig;
        // NOTA: Fix Scan
        Scanner scs = new Scanner(System.in);

        System.out.println("Ingrese el nombre del producto: ");

        // NOTA: Scan var
        nombre = scs.nextLine();

        System.out.println("Ingrese la descripción del producto: ");

        // NOTA: Scan var
        descrip = scs.nextLine();

        System.out.println("Ingrese el precio del producto: ");

        // NOTA: Scan var
        precio = scs.nextDouble();

        DecimalFormat formato = new DecimalFormat("#.00");
        price = formato.format(precio).replace(",", ".");

        System.out.println("Ingrese la cantidad disponible del producto: ");

        // NOTA: Scan var
        stock = scs.nextInt();
        boolean res = conex.create("products", "name, descrip, price, stock", nombre,descrip,price,stock);

        if (res) {
            System.out.println("Producto creado correctamente!!\n");
        } else {
            System.out.println("No se pudo crear el producto\n");
        }

        System.out.println("Escriba cualquier numero para continuar");

        // NOTA: Scan var
        sig = scs.nextInt();

        // NOTA: Clear
        System.out.print("\033[H\033[2J");
        System.out.flush();
   }

   public static void editar() {
        // NOTA: Conexión a la BD
        ConnectionMysql conex = new ConnectionMysql();
        conex.conectar();

        // NOTA: Clear
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // NOTA: Variables
        int id;
        int sig;

        System.out.println("Ingrese el ID del producto que desea editar: ");

       id = sc.nextInt();

       String[][] res = conex.get("*", "products", "id = " + id, 0, null);

       // NOTA: Clear
       System.out.print("\033[H\033[2J");
       System.out.flush();

       if (res[0][0] instanceof String) {
           int op = -11;
           String text, precio;
           Double price;
           int stock;

           // NOTA: Fix Scan
           Scanner scs = new Scanner(System.in);
           while(op != 0) {
                if (op == 1) {
                    System.out.println("Ingrese el nuevo nombre: ");;
                    text = scs.nextLine();
                    boolean query = conex.update("name", text, null, id, 0);

                    if (query) {
                        res[0][1] = text;
                        System.out.println("Se actualizó el nombre correctamente!!\n");
                    }
                } else if (op == 2) {
                    System.out.println("Ingrese la nueva descripción: ");;
                    text = scs.nextLine();
                    boolean query = conex.update("descrip", text, null, id, 0);

                    if (query) {
                        res[0][2] = text;
                        System.out.println("Se actualizó la descripción correctamente!!\n");
                    }
                } else if (op == 3) {
                    System.out.println("Ingrese el nuevo precio: ");;
                    price = scs.nextDouble();
                    DecimalFormat formato = new DecimalFormat("#.00");
                    precio = formato.format(price).replace(",", ".");
                    boolean query = conex.update("price", null, precio, id, 0);

                    if (query) {
                        res[0][3] = precio;
                        System.out.println("Se actualizó el precio correctamente!!\n");
                    }
                } else if (op == 4) {
                    System.out.println("Ingrese el nuevo stock: ");
                    stock = scs.nextInt();
                    
                    boolean query = conex.update("stock", null, stock+"", id, 0);

                    if (query) {
                        res[0][4] = stock+"";
                        System.out.println("Se actualizó el stock correctamente!!\n");
                    }
                }

               System.out.println("Editar producto\n");
               System.out.println("ID: "+res[0][0]);
               System.out.println("Nombre: "+res[0][1]);
               System.out.println("Descripción: "+res[0][2]);
               System.out.println("Precio: "+res[0][3]);
               System.out.println("Stock: "+res[0][4]);

               System.out.println("\n[1] Editar nombre");
               System.out.println("[2] Editar descripción");
               System.out.println("[3] Editar precio");
               System.out.println("[4] Editar Stock");
               System.out.println("\n[0] Salir del menu");

               System.out.println("\nIndique qué desea hacer");
               op = sc.nextInt();

               // NOTA: Clear
               System.out.print("\033[H\033[2J");
               System.out.flush();
           }
       } else {
           System.out.println("producto no encontrado");
       }

       System.out.println("Ingrese cualquier número para continuar...");
       sig = sc.nextInt();

       // NOTA: Clear
       System.out.print("\033[H\033[2J");
       System.out.flush();

       conex.close();
   }

   public static void eliminar() {
       // NOTA: Conexión a la BD
       ConnectionMysql conex = new ConnectionMysql();
       conex.conectar();

       // NOTA: Variables
       int id;
       int sig;
       boolean res;

       System.out.println("Ingrese el ID del producto que desea eliminar: ");

       id = sc.nextInt();

       res = conex.delete(id);

       if (res) {
           System.out.println("Se ha eliminado el producto correctamente!!\n");
       } else {
           System.out.println("No se ha podido encontrar el producto solicitado\n");
       }

       System.out.println("Escriba cualquier numero para continuar");
       
       // NOTA: Scan var
       sig = sc.nextInt();
       
       // NOTA: Clear
       System.out.print("\033[H\033[2J");
       System.out.flush();
   }
}
