

package edu.wustl.geneconnect.domain.ws;
import edu.wustl.geneconnect.domain.ws.*;
import edu.wustl.geneconnect.domain.*;
import gov.nih.nci.system.applicationservice.*;
import java.util.*;
/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

public  class GenBankmRNA 
    extends mRNAGenomicIdentifier
	implements java.io.Serializable
{
	private static final long serialVersionUID = 1234567890L;

	

	

		public boolean equals(Object obj){
			boolean eq = false;
			if(obj instanceof GenBankmRNA) {
				GenBankmRNA c =(GenBankmRNA)obj; 			 
				Long thisId = getId();		
				
					if(thisId != null && thisId.equals(c.getId())) {
					   eq = true;
				    }		
				
			}
			return eq;
		}
		
		public int hashCode(){
			int h = 0;
			
			if(getId() != null) {
				h += getId().hashCode();
			}
			
			return h;
	}
	
	
}
