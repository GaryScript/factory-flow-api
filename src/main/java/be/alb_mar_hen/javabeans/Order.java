package be.alb_mar_hen.javabeans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import be.alb_mar_hen.javabeans.Machine;
import be.alb_mar_hen.javabeans.Order;
import be.alb_mar_hen.javabeans.PurchasingAgent;
import be.alb_mar_hen.javabeans.Supplier;
import be.alb_mar_hen.validators.DateValidator;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "keyOrder")
public class Order implements Serializable{
	//Constants
	private static final long serialVersionUID = 7636931644402631422L;
	// Validators
	private NumericValidator numericValidator;
	private DateValidator dateValidator;
	private ObjectValidator objectValidator;
	
	private LocalDateTime orderDateTime;
	
	// Relations
	private Supplier supplier;
	private PurchasingAgent purchasingAgent;
	private Machine machine;
	
	// Constructors
	public Order() {}
	
	public Order(
		LocalDateTime orderDate, 
		Supplier supplier,
		PurchasingAgent purchasingAgent,
		Machine machine,
		NumericValidator numericValidator,
		DateValidator dateValidator,
		ObjectValidator objectValidator
	) {
		this.numericValidator = numericValidator;
		this.dateValidator = dateValidator;
		this.objectValidator = objectValidator;
		setOrder(orderDate);
		setMachine(machine);
		setPurchasingAgent(purchasingAgent);
		setSupplier(supplier);
	}
	
	public LocalDateTime getOrder() {
		return orderDateTime;
	}
	
	public Supplier getSupplier() {
		return supplier;
	}
	
	public PurchasingAgent getPurchasingAgent() {
		return purchasingAgent;
	}
	
	public Machine getMachine() {
		return machine;
	}
		
	public void setOrder(LocalDateTime orderDate) {
		if(!objectValidator.hasValue(orderDate)) {
			throw new NullPointerException("The orderDate must have a value.");
		}
		
		if(!dateValidator.isInPast(orderDate)) {
			throw new IllegalArgumentException("The orderDate cannot be in the future.");
		}
		
		this.orderDateTime = orderDate;
	}
	
	public void setMachine(Machine machine) {
		if(!objectValidator.hasValue(machine)) {
			throw new NullPointerException("Machine must have a value.");
		}
		
		this.machine = machine;
	}
	
	public void setPurchasingAgent(PurchasingAgent purchasingAgent) {
		if(!objectValidator.hasValue(purchasingAgent)) {
			throw new NullPointerException("Purchasing agent must have a value.");
		}
		
		this.purchasingAgent = purchasingAgent;
	}
	
	public void setSupplier(Supplier supplier) {
	    if (!objectValidator.hasValue(supplier)) {
	        throw new NullPointerException("Supplier must have a value.");
	    }
	    
	    this.supplier = supplier;
	}


	// Override methods
	@Override
	public String toString() {
		return 
			", orderDateTime=" + orderDateTime + 
			", supplier=" + supplier + 
			", purchasingAgent=" + purchasingAgent + 
			", machine=" + machine + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash( machine, orderDateTime, purchasingAgent, supplier);
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
		
		Order other = (Order) obj;
		return 
			Objects.equals(machine, other.machine)
			&& Objects.equals(orderDateTime, other.orderDateTime)
			&& Objects.equals(purchasingAgent, other.purchasingAgent) 
			&& Objects.equals(supplier, other.supplier);
	}
	
	
}