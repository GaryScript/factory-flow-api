package be.alb_mar_hen.api;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
	
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import be.alb_mar_hen.database.FactoryFlowConnection;

@Path("/machines")
public class MachineAPI {

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMachines() {
        Connection connection = null;
        CallableStatement stmt = null;

        try {
            connection = FactoryFlowConnection.getInstance();

            // call the sql procedure
            String call = "{CALL fetch_machine_data(?)}";
            stmt = connection.prepareCall(call);
            stmt.registerOutParameter(1, Types.ARRAY, "MACHINE_PACKAGE.MACHINE_DATA_TAB");
            stmt.execute();

            // claims data
            java.sql.Array array = stmt.getArray(1);
            Object[] results = (Object[]) array.getArray();

            // build the JSON response
            JSONArray machinesJson = new JSONArray();
            for (Object result : results) {
                Struct row = (Struct) result;
                Object[] attributes = row.getAttributes();

                // json object for each machine
                JSONObject machineJson = new JSONObject();
                machineJson.put("machineId", attributes[0]);
                machineJson.put("machineTypeName", attributes[1]);
                machineJson.put("machineTypePrice", attributes[2]);
                machineJson.put("machineTypeDaysBeforeMaintenance", attributes[3]);
                machineJson.put("machineName", attributes[4]);
                machineJson.put("machineStatus", attributes[5]);

                JSONObject zoneJson = new JSONObject();
                zoneJson.put("zoneId", attributes[6]);
                zoneJson.put("zoneName", attributes[7]);
                zoneJson.put("zoneColor", attributes[8]);

                JSONObject siteJson = new JSONObject();
                siteJson.put("siteId", attributes[9]);
                siteJson.put("siteCity", attributes[10]);

                JSONObject maintenanceJson = new JSONObject();
                maintenanceJson.put("maintenanceId", attributes[11]);
                maintenanceJson.put("maintenanceStartDate", attributes[12]);
                maintenanceJson.put("maintenanceEndDate", attributes[13]);
                maintenanceJson.put("maintenanceDuration", attributes[14]);
                maintenanceJson.put("maintenanceReport", attributes[15]);
                maintenanceJson.put("maintenanceStatus", attributes[16]);

                JSONObject responsableJson = new JSONObject();
                responsableJson.put("maintenanceResponsableId", attributes[17]);
                responsableJson.put("maintenanceResponsableMatricule", attributes[18]);
                responsableJson.put("maintenanceResponsablePassword", attributes[19]);
                responsableJson.put("maintenanceResponsableFirstName", attributes[20]);
                responsableJson.put("maintenanceResponsableLastName", attributes[21]);

                JSONObject workerJson = new JSONObject();
                workerJson.put("maintenanceWorkerId", attributes[22]);
                workerJson.put("maintenanceWorkerMatricule", attributes[23]);
                workerJson.put("maintenanceWorkerPassword", attributes[24]);
                workerJson.put("maintenanceWorkerFirstName", attributes[25]);
                workerJson.put("maintenanceWorkerLastName", attributes[26]);

                // add sub-objects to the main object
                machineJson.put("zone", zoneJson);
                machineJson.put("site", siteJson);
                machineJson.put("maintenance", maintenanceJson);
                machineJson.put("maintenanceResponsable", responsableJson);
                machineJson.put("maintenanceWorker", workerJson);

                // add the machine to the array
                machinesJson.put(machineJson);
            }

            // return the response with the machines data in JSON format 
            return Response.status(Status.OK).entity(machinesJson.toString()).build();

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An error occurred while fetching machines: " + e.getMessage() + "\"}")
                    .build();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
