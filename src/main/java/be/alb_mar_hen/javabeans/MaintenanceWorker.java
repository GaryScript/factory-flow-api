package be.alb_mar_hen.javabeans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.alb_mar_hen.daos.MaintenanceWorkerDAO;
import be.alb_mar_hen.formatters.StringFormatter;
import be.alb_mar_hen.javabeans.Employee;
import be.alb_mar_hen.javabeans.Maintenance;
import be.alb_mar_hen.javabeans.MaintenanceResponsable;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "keyMaintenanceWorker")
public class MaintenanceWorker extends Employee implements Serializable{
	private static final long serialVersionUID = -5505070112612987755L;
	private Set<Maintenance> maintenances;
	
	// Validators
	private ObjectValidator objectValidator;
	
	// Constructors
    public MaintenanceWorker(
        Optional<Integer> id, 
        String matricule, 
        String password,
        String firstName,
        String lastName,
        StringValidator stringValidator, 
        NumericValidator numericValidator,
        StringFormatter stringFormatter,
        ObjectValidator objectValidator
    ) {
        super(id, matricule, password, firstName, lastName, stringValidator, numericValidator, objectValidator, stringFormatter);
        this.objectValidator = objectValidator;
        this.maintenances = new HashSet<Maintenance>();
    }

    public MaintenanceWorker() {
        super();
        this.objectValidator = new ObjectValidator();
        this.maintenances = new HashSet<Maintenance>();
    }
    
    // Methods
    public boolean addMaintenance(Maintenance maintenance) {
        if (!objectValidator.hasValue(maintenance)) {
            throw new IllegalArgumentException("maintenance must have value.");
        }

        /*if (!maintenance.addMaintenanceWorker(this)) {
            return false;
        }*/

        return maintenances.add(maintenance);
    }
    
    public static MaintenanceWorker find(MaintenanceWorkerDAO dao, int id) {
    	return dao.find(id);
    }
	
	// Override methods
	@Override
	public String toString() {
		return super.toString() + "MaintenanceWorker";
	}
	
	@Override
    public boolean equals(Object object) {
    	if(!(object instanceof Employee)) {
    		return false;
    	}
    	
    	return super.equals((Employee) object);
    }
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	//Getters
	public Set<Maintenance> getMaintenances() {
        return maintenances;
    }
}
