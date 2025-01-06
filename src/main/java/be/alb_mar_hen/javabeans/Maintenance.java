package be.alb_mar_hen.javabeans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import be.alb_mar_hen.daos.MaintenanceDAO;
import be.alb_mar_hen.enumerations.MaintenanceStatus;
import be.alb_mar_hen.javabeans.Maintenance;
import be.alb_mar_hen.utils.CustomDateDeserializer;
import be.alb_mar_hen.utils.OptionalLocalDateTimeDeserializer;
import be.alb_mar_hen.validators.DateValidator;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "keyMaintenance")
public class Maintenance implements Serializable{
	//Constants
	private static final long serialVersionUID = 9182766473158370811L;
	private final static int MIN_LENGTH_REPORT = 10;
	
	// Validators
	private NumericValidator numericValidator;
	private StringValidator stringValidator;
	private ObjectValidator objectValidator;
	private DateValidator dateValidator;
	
	// Attributes
	@JsonDeserialize(using = be.alb_mar_hen.utils.OptionalIntegerDeserializer.class)
	private Optional<Integer> id;
	@JsonDeserialize(using = CustomDateDeserializer.class)
	private LocalDateTime startDateTime;
	@JsonDeserialize(using = OptionalLocalDateTimeDeserializer.class)
	private Optional<LocalDateTime> endDateTime;
	@JsonDeserialize(using = be.alb_mar_hen.utils.OptionalIntegerDeserializer.class)
	private Optional<Integer> duration;
	@JsonDeserialize(using = be.alb_mar_hen.utils.OptionalStringDeserializer.class)
	private Optional<String> report;
	private MaintenanceStatus status;
	
	// Relations
	private Set<MaintenanceWorker> maintenanceWorkers;
	
	//@JsonBackReference
	private Machine machine;
	private MaintenanceResponsable maintenanceResponsable;
		
	// Constructors
	public Maintenance() {
		numericValidator = new NumericValidator();
		stringValidator = new StringValidator();
		objectValidator = new ObjectValidator();
		dateValidator = new DateValidator();
		maintenanceWorkers = new HashSet<>();
	}
	
	public Maintenance(
		Optional<Integer> id, 
		LocalDateTime startDateTime, 
		Optional<LocalDateTime> endDateTime,
		Optional<Integer> duration, 
		Optional<String> report, 
		MaintenanceStatus status,
		Machine machine, 
		MaintenanceWorker maintenanceWorker,
		MaintenanceResponsable maintenanceResponsable,
		NumericValidator numericValidator,
		StringValidator stringValidator,
		ObjectValidator objectValidator,
		DateValidator dateValidator
	) {
		this.numericValidator = numericValidator;
		this.stringValidator = stringValidator;
		this.objectValidator = objectValidator;
		this.dateValidator = dateValidator;
		maintenanceWorkers = new HashSet<>();
		setId(id);
		setStartDateTime(startDateTime);
		setEndDateTime(endDateTime);
		setDuration(duration);
		setReport(report);
		setStatus(status);
		setMachine(machine);
		setMaintenanceResponsable(maintenanceResponsable);
		addMaintenanceWorker(maintenanceWorker);
	}
	
	public Maintenance(
		Optional<Integer> id, 
		LocalDateTime startDateTime, 
		Optional<LocalDateTime> endDateTime,
		Optional<Integer> duration, 
		Optional<String> report, 
		MaintenanceStatus status,
		Machine machine, 
		Set<MaintenanceWorker> maintenanceWorkers,
		MaintenanceResponsable maintenanceResponsable,
		NumericValidator numericValidator,
		StringValidator stringValidator,
		ObjectValidator objectValidator,
		DateValidator dateValidator
	) {
		this.numericValidator = numericValidator;
		this.stringValidator = stringValidator;
		this.objectValidator = objectValidator;
		this.dateValidator = dateValidator;
		this.maintenanceWorkers = new HashSet<>();
		setId(id);
		setStartDateTime(startDateTime);
		setEndDateTime(endDateTime);
		setDuration(duration);
		setReport(report);
		setStatus(status);
		setMachine(machine);
		setMaintenanceResponsable(maintenanceResponsable);
		setMaintenanceWorkers(maintenanceWorkers);
	}

	// Getters
	public Optional<Integer> getId() {
		return id;
	}
	
	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}
	
	public Optional<LocalDateTime> getEndDateTime() {
		return endDateTime;
	}
	
	public Optional<Integer> getDuration() {
		return duration;
	}
	
	public Optional<String> getReport() {
		return report;
	}
	
	public MaintenanceStatus getStatus() {
		return status;
	}
	
	public Machine getMachine() {
		return machine;
	}
	
	public MaintenanceResponsable getMaintenanceResponsable() {
		return maintenanceResponsable;
	}
	
	public Set<MaintenanceWorker> getMaintenanceWorkers() {
		return maintenanceWorkers;
	}
	
	// Setters
	public void setId(Optional<Integer> id) {
		if (!objectValidator.hasValue(id)) {
			this.id = Optional.empty();
		} else {
			if (!numericValidator.isPositiveOrEqualToZero(id)) {
				throw new IllegalArgumentException("Id must be greater than 0");
			}
			
			this.id = id;			
		}
	}
	
	public void setStartDateTime(LocalDateTime startDateTime) {
		if(!objectValidator.hasValue(startDateTime)) {
			throw new NullPointerException("Start date must have a value.");
		}
		
		if(!dateValidator.isInPast(startDateTime)) {
			throw new IllegalArgumentException("Date must be in the past.");
		}
		
		this.startDateTime = startDateTime;
	}
	
	public void setEndDateTime(Optional<LocalDateTime> endDateTime) {
		if(!objectValidator.hasValue(endDateTime) || endDateTime.isEmpty()) {
			this.endDateTime = Optional.empty();
		} else {
			if (endDateTime.get().isBefore(startDateTime)) {
				throw new IllegalArgumentException("End date must be after start date.");
			}
			
			this.endDateTime = endDateTime;
		}
	}
	
	public void setDuration(Optional<Integer> duration) {
		if (!objectValidator.hasValue(duration)) {
			this.duration = Optional.empty();
		} else {
			this.duration = duration;			
		}
	}
	
	public void setReport(Optional<String> report) {
		if(!objectValidator.hasValue(report)) {
			this.report = Optional.empty();
		} else {
			if(!stringValidator.isLongerOrEqualsThan(report, MIN_LENGTH_REPORT)) {
				throw new IllegalArgumentException("Report must be at least " + MIN_LENGTH_REPORT + " long.");
			}
			
			this.report = report;			
		}
	}
	
	public void setStatus(MaintenanceStatus status) {
		if(!objectValidator.hasValue(status)) {
			throw new NullPointerException("Status must have a value.");
		}
		
		this.status = status;
	}

	public void setMachine(Machine machine) {
//		if(!objectValidator.hasValue(machine)) {
//			throw new NullPointerException("Machine must have a value.");
//		}
//		
//		if (this.machine != machine) {
//			this.machine = machine;
//			machine.addMaintenance(this);
//		}
		
		this.machine = machine;
	}
	
	public void setMaintenanceResponsable(MaintenanceResponsable responsable) {
	    if (!objectValidator.hasValue(responsable)) {
	        throw new NullPointerException("Responsable must have a value.");
	    }

	    if (this.maintenanceResponsable != responsable) {
	        this.maintenanceResponsable = responsable;
	    }
	}
	
	public void setMaintenanceWorkers(Set<MaintenanceWorker> workers) {
        if (!objectValidator.hasValue(workers)) {
            throw new NullPointerException("Workers must have a value.");
        }

        workers.forEach(this::addMaintenanceWorker);
	}
	
	// Methods
	public boolean addMaintenanceWorker(MaintenanceWorker worker) {
	    if (!objectValidator.hasValue(worker)) {
	        throw new NullPointerException("Worker must have a value.");
	    }

	    boolean added = maintenanceWorkers.add(worker);

	    return added;
	}
	
	public boolean updateInDatabase(MaintenanceDAO dao) {
		return dao.update(this);
	}
	
	public int insertInDatabase(MaintenanceDAO dao) {
		return dao.create(this);
	}
	
	//Override methods
	@Override
	public String toString() {
		return "Maintenance [id=" + id.orElse(null) 
			+ ", startDateTime=" 
			+ startDateTime + ", endDateTime=" 
			+ endDateTime.orElse(null) + ", duration="
			+ duration.orElse(0) + ", reportString=" 
			+ report.orElse(null) + ", status=" 
			+ status + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			startDateTime,
			endDateTime.orElse(null),
			duration.orElse(0),
			id.orElse(0),
			report.orElse(null), 
			status
		);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;			
		}
		
		if (
			!objectValidator.hasValue(obj) || 
			getClass() != obj.getClass()
		) {
			return false;			
		}
		
		Maintenance other = (Maintenance) obj;
		return Objects.equals(startDateTime, other.startDateTime) 
			&& Objects.equals(endDateTime.orElse(null), other.endDateTime.orElse(null))
			&& Objects.equals(duration.orElse(0), other.duration.orElse(0))
			&& Objects.equals(id.orElse(0), other.id.orElse(0))
			&& Objects.equals(report.orElse(null), other.report.orElse(null))
			&& Objects.equals(status, other.status);
	}
}
