package WebServices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Path("database")
public class GenericResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    /**
     * Retrieves representation of an instance of RestAPIWithDB.GenericResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

   

    static Connection conn = null;
    static Statement stm = null;
    static ResultSet rs = null;

    static JSONObject Obj1 = new JSONObject();

    String loc_id,street_add, postal_code, city, province, country_id;

    
    
    
        public static Connection getConnection() {

        try {

            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "hr", "inf5180");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return conn;
    }

    public static void closeConnection() {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                /* ignored */
            }
        }
        if (stm != null) {
            try {
                stm.close();
            } catch (SQLException e) {
                /* ignored */
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                /* ignored */
            }
        }
    }

    @GET
    @Path("locationsingle&{locid}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSingle(@PathParam("locid") int locId) {
        Obj1.clear();
        conn = getConnection();

        String sql = "SELECT * FROM LOCATIONS WHERE LOCATION_ID = '" + locId + "'";

        if (conn != null) {
            try {
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);

                rs = stm.executeQuery(sql);

                if (rs.next() == true) {
                    Obj1.accumulate("Status", "OK");
                    Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
                    do {

                        loc_id = rs.getString("LOCATION_ID");
                        street_add = rs.getString("STREET_ADDRESS");
                        postal_code = rs.getString("POSTAL_CODE");
                        city = rs.getString("CITY");
                        province = rs.getString("STATE_PROVINCE");
                        country_id = rs.getString("COUNTRY_ID");
                        Obj1.accumulate("LocationID", loc_id);
                        Obj1.accumulate("Street address",street_add );
                        Obj1.accumulate("Postal Code", postal_code);
                        Obj1.accumulate("City", city);
                        Obj1.accumulate("State Province", province);
                        Obj1.accumulate("Country ID", country_id);
                    } while (rs.next());
                } else {

                    Obj1.accumulate("Status", "Error");
                    Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
                    Obj1.accumulate("Message", "Record Not Found");
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            Obj1.accumulate("Status", "Error");
            Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
            Obj1.accumulate("Message", "Connection Error");

        }
        return Obj1.toString();
    }

    
    @GET
    @Path("insertloc&{loc_id}&{street_add}&{postal_code}&{city}&{province}&{country_id}")                
    @Produces(MediaType.TEXT_PLAIN)
    public String insertData(@PathParam("loc_id") int loc_Id, @PathParam("street_add") String street_Add, @PathParam("postal_code") String postal_Code, @PathParam("city") String City, @PathParam("province") String state_Province, @PathParam("country_id") int con_Id) {

        Obj1.clear();
        conn = getConnection();

        String sql = "INSERT INTO LOCATIONS(LOCATION_ID,STREET_ADDRESS,POSTAL_CODE,CITY,STATE_PROVINCE,COUNTRY_ID) VALUES('" + loc_Id + "','" + street_Add + "','" + postal_Code + "','" + City + "','" + state_Province + "','" + con_Id + "')";


        if (conn != null) {
            try {
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);

                if (i > 0) {
                    Obj1.accumulate("Status", "OK");
                    Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
                    Obj1.accumulate("Message", "Record inserted");

                } else {
                    Obj1.accumulate("Status", "Error");
                    Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
                    Obj1.accumulate("Message", "Record Not inserted");
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            Obj1.accumulate("Status", "Error");
            Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
            Obj1.accumulate("Message", "Connection Error");
        }
        return Obj1.toString();
    }

    @GET
    @Path("update&{locID}&{city}")
    @Produces(MediaType.TEXT_PLAIN)
    public String updateData(@PathParam("locID")String loc_id,@PathParam("city")String City) {
    
        Obj1.clear();
        
        conn = getConnection();
        
        String sql = "UPDATE LOCATION SET CITY='" + City + "' WHERE LOCATION_ID='" + loc_id + "')";
        
        if (conn != null) {
            try {
                stm = conn.createStatement();

                int i = stm.executeUpdate(sql);

                if (i > 0) {
                    Obj1.accumulate("Status", "OK");
                    Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
                    Obj1.accumulate("Message", "Record Updated");
                } else {
                    Obj1.accumulate("Status", "Error");
                    Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
                    Obj1.accumulate("Message", "Record Not Updated");
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            Obj1.accumulate("Status", "Error");
            Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
            Obj1.accumulate("Message", "Connection Error");
        }
        return Obj1.toString();
    }
    
     @GET
    @Path("deleteloc&{locID}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteData(@PathParam("locID")String loc_id) {
    
        Obj1.clear();
        
        conn = getConnection();
        
        String sql = "DELETE FROM LOCATION WHERE LOCATION_ID='" + loc_id + "'";
        
        if (conn != null) {
            try {
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);
                if (i > 0) {
                    Obj1.accumulate("Status", "OK");
                    Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
                    Obj1.accumulate("Message", "Record Deleted");
                } else {
                    Obj1.accumulate("Status", "Error");
                    Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
                    Obj1.accumulate("Message", "Record not Deleted");
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            Obj1.accumulate("Status", "Error");
            Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
            Obj1.accumulate("Message", "Connection Error");
        }
        
        return Obj1.toString();
    }
    
    
    @GET
    @Path("locatelist")
    @Produces(MediaType.TEXT_PLAIN)
    public String getloc() {
        
        Obj1.clear();
     

        JSONArray arr1 = new JSONArray();
        JSONObject obj2 = new JSONObject();
        
        
        conn = getConnection();

        if (conn != null) {
            try {
                String sql = "SELECT LOCATION_ID,STREET_ADDRESS,POSTAL_CODE,CITY,STATE_PROVINCE,COUNTRY_ID FROM LOCATIONS";
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);

                rs = stm.executeQuery(sql);

                if(rs.next() == true){
                    Obj1.accumulate("Status", "OK");
                    Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
                    do {

                        loc_id = rs.getString("LOCATION_ID");
                        street_add = rs.getString("STREET_ADDRESS");
                        postal_code = rs.getString("POSTAL_CODE");
                        city = rs.getString("CITY");
                        province = rs.getString("STATE_PROVINCE");
                        country_id = rs.getString("COUNTRY_ID");
                        obj2.accumulate("LocationID", loc_id);
                        obj2.accumulate("Street address",street_add );
                        obj2.accumulate("Postal Code", postal_code);
                        obj2.accumulate("City", city);
                        obj2.accumulate("State Province", province);
                        obj2.accumulate("Country ID", country_id);
                        arr1.add(obj2);
                        obj2.clear();

                    }while (rs.next());
                    Obj1.accumulate("Departments", arr1);
                }else{
                    
                    Obj1.accumulate("Status", "Error");
                    Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
                    Obj1.accumulate("Message", "No Records Found");
                    
                }
            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            Obj1.accumulate("Status", "Error");
            Obj1.accumulate("TimeStamp", System.currentTimeMillis()/1000);
            Obj1.accumulate("Message", "Connection Error");
        }
        return Obj1.toString();
    }
    
    
    
    
    
    
    
}
