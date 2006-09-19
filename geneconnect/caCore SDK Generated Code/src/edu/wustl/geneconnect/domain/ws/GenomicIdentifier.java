

package edu.wustl.geneconnect.domain.ws;
import edu.wustl.geneconnect.domain.ws.*;
import edu.wustl.geneconnect.domain.*;
import gov.nih.nci.system.applicationservice.*;
import java.util.*;
/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

public  class GenomicIdentifier 
	implements java.io.Serializable
{
	private static final long serialVersionUID = 1234567890L;

	
	   
	   protected java.lang.Long id;
	   public  java.lang.Long getId(){
	      return id;
	   }
	   
	   public void setId( java.lang.Long id){
	      this.id = id;
	   }
	
	   
	   protected java.lang.String genomicIdentifier;
	   public  java.lang.String getGenomicIdentifier(){
	      return genomicIdentifier;
	   }
	   
	   public void setGenomicIdentifier( java.lang.String genomicIdentifier){
	      this.genomicIdentifier = genomicIdentifier;
	   }
	

	
	   
	   
	   
	      
			
			
			private edu.wustl.geneconnect.domain.ws.ConsensusIdentifierData consensusIdentifierData;
			public edu.wustl.geneconnect.domain.ws.ConsensusIdentifierData getConsensusIdentifierData(){
			  return consensusIdentifierData;			
                        }
                        
	      
	               
	   
	   
	   
	   public void setConsensusIdentifierData(edu.wustl.geneconnect.domain.ws.ConsensusIdentifierData consensusIdentifierData){
		this.consensusIdentifierData = consensusIdentifierData;
	   }	
	   
	   
	

		public boolean equals(Object obj){
			boolean eq = false;
			if(obj instanceof GenomicIdentifier) {
				GenomicIdentifier c =(GenomicIdentifier)obj; 			 
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
