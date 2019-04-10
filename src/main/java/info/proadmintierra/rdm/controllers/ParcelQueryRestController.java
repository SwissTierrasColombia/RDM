package info.proadmintierra.rdm.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import info.proadmintierra.rdm.queries.BasicQuery;
import info.proadmintierra.rdm.drivers.Postgres;

/**
 * ParcelQueryRestController
 */
@RestController
@RequestMapping("/query")
@CrossOrigin(origins = { "http://localhost:4200", "*" })
public class ParcelQueryRestController {

    @Value("${rdm.schema}")
    private String rdmSchema;
    @Value("${spring.datasource.url}")
    private String connectionString;
    @Value("${spring.datasource.username}")
    private String connectionUser;
    @Value("${spring.datasource.password}")
    private String connectionPassword;
    @Value("${spring.datasource.driver-class-name}")
    private String classForName;

    @GetMapping(value = "/parcel", produces = { "application/json" })
    public String getParcelBasicInfo(@RequestParam(required = false) String nupre,
            @RequestParam(required = false) String cadastralCode, @RequestParam(required = false) String fmi) {
        String sql = "";
        try {
            Postgres conn = new Postgres();
            conn.connect(this.connectionString, this.connectionUser, this.connectionPassword, this.classForName);
            sql = BasicQuery.getQuery(this.rdmSchema, null, fmi, cadastralCode, null, false, false);
            String response = conn.query(sql);
            conn.disconnect();
            if (response != null)
                return response;
            else
                return "{\"error\":\"No se encontraron registros.\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    @GetMapping(value = "/parcel/geometry", produces = { "application/json" })
    public String getParcelBasicInfo(@RequestParam(required = false) Integer id) {
        String sql = "";
        try {
            Postgres conn = new Postgres();
            conn.connect(this.connectionString, this.connectionUser, this.connectionPassword, this.classForName);
            sql = "select st_asgeojson(terreno.poligono_creado) from " + rdmSchema + ".terreno where terreno.t_id = " + id;
            System.out.println("SQL: "+sql);
            String response = conn.query(sql);
            conn.disconnect();
            if (response != null)
                return response;
            else
                return "{\"error\":\"No se encontraron registros.\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

}