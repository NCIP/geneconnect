/**
 *<p>Copyright: (c) Washington University, School of Medicine 2006.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.graph.DataSource</p> 
 */
package edu.wustl.geneconnect.graph;


public class DataSource
{
	Integer id;
	
	String name;
	
	int row;
	
	int col;
	
	public DataSource(Integer id, String name, int row, int col)
	{
		this.id = id;
		this.name = name;
		this.row = row;
		this.col = col;
	}
	
	/**
	 * @return Returns the id.
	 */
	public Integer getId()
	{
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(Integer id)
	{
		this.id = id;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	/**
	 * @return Returns the rowNo.
	 */
	public int getRow()
	{
		return row;
	}
	/**
	 * @param rowNo The rowNo to set.
	 */
	public void setRow(int row)
	{
		this.row = row;
	}
	
	/**
	 * @return Returns the col.
	 */
	public int getCol()
	{
		return col;
	}
	/**
	 * @param col The col to set.
	 */
	public void setCol(int col)
	{
		this.col = col;
	}
}
