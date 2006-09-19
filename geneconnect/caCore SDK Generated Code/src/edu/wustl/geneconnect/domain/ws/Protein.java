

package edu.wustl.geneconnect.domain.ws;
import edu.wustl.geneconnect.domain.ws.*;
import edu.wustl.geneconnect.domain.*;
import gov.nih.nci.system.applicationservice.*;
import java.util.*;
/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

public  class Protein 
	implements java.io.Serializable
{
	private static final long serialVersionUID = 1234567890L;

	
	   
	   private java.lang.Long id;
	   public  java.lang.Long getId(){
	      return id;
	   }
	   
	   public void setId( java.lang.Long id){
	      this.id = id;
	   }
	
	   
	   private java.lang.String ensemblPeptideId;
	   public  java.lang.String getEnsemblPeptideId(){
	      return ensemblPeptideId;
	   }
	   
	   public void setEnsemblPeptideId( java.lang.String ensemblPeptideId){
	      this.ensemblPeptideId = ensemblPeptideId;
	   }
	
	   
	   private java.lang.String refseqId;
	   public  java.lang.String getRefseqId(){
	      return refseqId;
	   }
	   
	   public void setRefseqId( java.lang.String refseqId){
	      this.refseqId = refseqId;
	   }
	
	   
	   private java.lang.String uniprotkbPrimaryAccession;
	   public  java.lang.String getUniprotkbPrimaryAccession(){
	      return uniprotkbPrimaryAccession;
	   }
	   
	   public void setUniprotkbPrimaryAccession( java.lang.String uniprotkbPrimaryAccession){
	      this.uniprotkbPrimaryAccession = uniprotkbPrimaryAccession;
	   }
	
	   
	   private java.lang.String genbankAccession;
	   public  java.lang.String getGenbankAccession(){
	      return genbankAccession;
	   }
	   
	   public void setGenbankAccession( java.lang.String genbankAccession){
	      this.genbankAccession = genbankAccession;
	   }
	

	
	   
	   
	   
	      
			private java.util.Collection genomicIdentifierSetCollection = new java.util.HashSet();
			public java.util.Collection getGenomicIdentifierSetCollection(){
	              return genomicIdentifierSetCollection;
	          }
			   
			   
			   
			   			   
	      
	               
	   
	   	public void setGenomicIdentifierSetCollection(java.util.Collection genomicIdentifierSetCollection){
	   		this.genomicIdentifierSetCollection = genomicIdentifierSetCollection;
	        }	
	   
	   
	
	   
	   
	   
	      
			private java.util.Collection messengerRNACollection = new java.util.HashSet();
			public java.util.Collection getMessengerRNACollection(){
	              return messengerRNACollection;
	          }
			   
			   
			   
			   			   
	      
	               
	   
	   	public void setMessengerRNACollection(java.util.Collection messengerRNACollection){
	   		this.messengerRNACollection = messengerRNACollection;
	        }	
	   
	   
	
	   
	   
	   
	      
			private java.util.Collection geneCollection = new java.util.HashSet();
			public java.util.Collection getGeneCollection(){
	              return geneCollection;
	          }
			   
			   
			   
			   			   
	      
	               
	   
	   	public void setGeneCollection(java.util.Collection geneCollection){
	   		this.geneCollection = geneCollection;
	        }	
	   
	   
	

		public boolean equals(Object obj){
			boolean eq = false;
			if(obj instanceof Protein) {
				Protein c =(Protein)obj; 			 
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
