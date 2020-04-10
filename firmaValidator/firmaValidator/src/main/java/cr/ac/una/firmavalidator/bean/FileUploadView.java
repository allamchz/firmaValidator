/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cr.ac.una.firmavalidator.bean;


import java.io.IOException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.primefaces.shaded.commons.io.IOUtils;

/**
 *
 * @author allamchaves
 */
@ManagedBean(name = "fileUploadView")
@RequestScoped
public class FileUploadView {
     
    private UploadedFile file = null;
   
 
    public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
        
    }

 
    public void upload() throws IOException {
        if ((file != null) && (file.getFileName() != null)) {
          
            byte[] bytes = IOUtils.toByteArray(file.getInputstream());
            JsonObject respuesta = validar (bytes);
            if ( respuesta == null ){
               FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error:", "Algo salió muy mal, no pudimos validar el archivo"));
            }
            else{
                
                if (respuesta.getString("status").equals("TOTAL_PASSED"))
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Información:", "El documento es válido, todas sus firmas son correctas"));
                else
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error:", "El documento no posee firmas o son inválidas"));
            }
                
            
        }
        else 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error!", "Debe escoger el  archivo a validar"));
    }
     
    
    
    public JsonObject validar(byte[] pdf) {

        final String SIGN_API_URL = "http://aws-hsm-dw5h.signum.one/signumone-hsm-api/resources/validate/";
        String APPLICATION_ID = "universidad-nacional";
        String USER_ID = "sistema02";
      

        String fileBytes = Base64Util.encode(pdf);
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonObject signRequest = builder
                .add("application-id", APPLICATION_ID)
                .add("user-id", USER_ID)
                .add("file-bytes", fileBytes)
                .add("detailed-report", false)
                .build();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(SIGN_API_URL);
        try {
            // Hacemos la invocación a SignumOne y obtenemos el PDF. enviado, firmado, y codificado en Base64.
            Response response = target.request(MediaType.APPLICATION_JSON).put(Entity.json(signRequest));
            System.out.println("Estado: " + response.getStatus() + " Causa " + response.getHeaderString("X-Error-Cause"));

            return response.readEntity(JsonObject.class);

        } catch (Exception ex) {
            System.out.println("Error al conectar al servicio de sello digital de la Universidad Nacional");
            ex.printStackTrace();
        }

        return null;
    }
}