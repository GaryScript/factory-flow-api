package be.alb_mar_hen.javabeans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import be.alb_mar_hen.enumerations.ZoneColor;
import be.alb_mar_hen.javabeans.Machine;
import be.alb_mar_hen.javabeans.Site;
import be.alb_mar_hen.javabeans.Zone;
import be.alb_mar_hen.validators.NumericValidator;
import be.alb_mar_hen.validators.ObjectValidator;
import be.alb_mar_hen.validators.StringValidator;

public class Zone implements Serializable{
	private static final long serialVersionUID = -2270925246678850136L;
	// Validators
		private NumericValidator numericValidator;
		private ObjectValidator objectValidator;
		private StringValidator stringValidator;
		
		// Attributes
		private Optional<Integer> id;
		private ZoneColor color;
		private String name;
		
		// Relations
		private Site site;
		
		// Constructors
		public Zone(
			Optional<Integer> id, 
			ZoneColor color, 
			String name,
			Optional<Integer> siteId,
			String siteName,
			NumericValidator numericValidator, 
			ObjectValidator objectValidator,
			StringValidator stringValidator
		) {
			this.numericValidator = numericValidator;
			this.stringValidator = stringValidator;
			this.objectValidator = objectValidator;
			setId(id);
			setColor(color);
			setName(name);
			this.site = new Site(siteId, siteName, stringValidator, numericValidator, objectValidator);
		}

		// Getters
		public Optional<Integer> getId() {
			return id;
		}
		
		public ZoneColor getColor() {
			return color;
		}
		
		public String getName() {
			return name;
		}
		
		public Site getSite() {
	        return site;
		}
		
		// Setters
		public void setId(Optional<Integer> id) {
			if (!objectValidator.hasValue(id)) {
				throw new NullPointerException("Id must have a value.");
			}
			
		    if (!numericValidator.isPositiveOrEqualToZero(id)) {
		        throw new IllegalArgumentException("Id must be greater than or equal to 0");
		    }
		    
		    this.id = id;
		}

		public void setColor(ZoneColor color) {
			if(!objectValidator.hasValue(color)) {
				throw new NullPointerException("Color must have a value.");
			}
			
			this.color = color;
		}

		public void setName(String name) {
			if(!stringValidator.hasValue(name)) {
				throw new NullPointerException("Name must have a value.");
			}
			
			this.name = name;
		}
		


		// Override methods
		@Override
		public String toString() {
			return "Zone [id=" + id.orElse(null) + 
				", color=" + color + 
				", name=" + name + 
				", site=" + site + "]";
		}

		@Override
		public int hashCode() {
			return Objects.hash(
				color, 
				id.orElse(null),
				name,
				site
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
			
			Zone other = (Zone) obj;
			return color == other.color 
				&& Objects.equals(id.orElse(null), other.id.orElse(null))
				&& Objects.equals(name, other.name) 
				&& Objects.equals(site, other.site);
		}
}
