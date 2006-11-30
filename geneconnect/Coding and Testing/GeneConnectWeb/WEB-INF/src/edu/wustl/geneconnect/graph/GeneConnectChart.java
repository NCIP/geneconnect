/**
 *<p>Copyright: (c) Washington University, School of Medicine 2006.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.graph.GeneConnectGraph</p> 
 */
package edu.wustl.geneconnect.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.AbstractVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.ConstantEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.DirectionalEdgeArrowFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.decorators.NumberEdgeValue;
import edu.uci.ics.jung.graph.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberEdgeValue;
import edu.uci.ics.jung.graph.decorators.VertexAspectRatioFunction;
import edu.uci.ics.jung.graph.decorators.VertexSizeFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.visualization.Coordinates;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.StaticLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
/**
 * This class draws GeneConnect graph.
 * @author krunal_thakkar
 * @version 1.0
 */
public class GeneConnectChart extends JApplet implements ListSelectionListener, ActionListener, FocusListener, MouseListener
{
	/**
     * the graph
     */
    private Graph graph;

    /**
     * the visual component and renderer for the graph
     */
    private GraphZoomScrollPane panel;
	
    private VisualizationViewer vv;
    
    private PluggableRenderer pr;
    
    protected EdgeWeightStrokeFunction ewcs;
    
    protected NumberEdgeValue edge_weight = new UserDatumNumberEdgeValue("edge_weight");
    
    protected CustomVertexShape cvs;
    
    protected VertexStringerImpl vertexStringerImpl;
    
    static int noOfRows;
    static int noOfCols;
    
    private ArrayList nodeList;
    
    private ArrayList pathList;
    
    private String[] highlightPathList = null;;
    
    private JList highlightList = null; 
    
    private Map nodeMap;
    
    private Map highlightPathsMap = null;
    
    private Vertex[] v; 
    
    private boolean isHighlight;
    
    private int highlightPathNo;
    
    private boolean isGrayedEdge;
    
    private boolean intialGraph = true;
    
    private boolean showGrayedPaths = true;
    
    protected JButton initialGraphButton;
    protected JCheckBox grayedPathsCheckBox;
    
    protected Color pathListBackgroundColor = new Color(-4665371);
    
    public void init()
    {
    	createNodeList();
    	
    	calculateRowsCols();
    	
    	//create a simple graph for the demo
	    graph = new SparseGraph();  
	    nodeMap = new HashMap();
	    
	    v = createVertices(nodeList.size());
	    
	    pr = new CustomPluggableRenderer();
	    
	    //a Map for the node labels
        Map map = new HashMap();
        for(int i=0; i<v.length; i++) 
        {
        	map.put(v[i], (String)((DataSource)nodeList.get(i)).getName());
        	
//        	nodeMap.put(((DataSource)nodeList.get(i)).getId(), v[i]);
        }
        
        
        vertexStringerImpl = new VertexStringerImpl(map);
        
        pr.setVertexStringer(vertexStringerImpl);
        
        cvs= new CustomVertexShape();
        
        pr.setVertexShapeFunction(cvs);
        
        
	    Layout layout = new MyLayout(graph);
	    vv =  new VisualizationViewer(layout, pr, new Dimension(500,500));
	    vv.setPickSupport(new ShapePickSupport());
	    pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
	    
	    pr.setEdgeArrowFunction(new DirectionalEdgeArrowFunction(10,8,4));
	    
	    vv.setBackground(Color.white);
	    vv.setLocation(0,0);
	    
	    ewcs = new EdgeWeightStrokeFunction(edge_weight);
	    ewcs.setWeighted(true);
	    pr.setEdgeStrokeFunction(ewcs);
	    
	    createPathList();
	    
	    createHighlightPathList();
	    
	    createEdges(pathList);
	    
	    vv.updateUI();
	    
	    setSize(500, 550);
	    
	    setLocation(0,0);
	    
	    final Indexer ind = Indexer.newIndexer(graph, 0);
	    
        EdgeStringer stringer = new EdgeStringer(){
            public String getLabel(ArchetypeEdge e) {
                return e.toString();
            }
        };
//        pr.setEdgeStringer(stringer);
        
        pr.setEdgePaintFunction(new PickableEdgePaintFunction(pr, Color.black, Color.cyan));
        pr.setEdgePaintFunction(new CustomEdgePaintFunction(Color.red));
        
        // add my listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
        
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        vv.setGraphMouse(graphMouse);
        
        // create a frome to hold the graph
        panel = new GraphZoomScrollPane(vv);
        
        panel.setSize(500, 500);
        
        Container content = getContentPane();
        
        content.setLayout(new GridBagLayout());
        content.setBackground(Color.white);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
        
        c.ipadx = 500;
		c.ipady = 450;
//		c.anchor = GridBagConstraints.FIRST_LINE_START; 
//		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0,0,0,0);
		content.add(panel, c);

		if(highlightPathList != null)
		{
			highlightList = new JList(highlightPathList);
		}
		else
		{
			highlightPathList = new String[0];
			highlightList = new JList(highlightPathList);
		}
		highlightList.addListSelectionListener(this);
		highlightList.addFocusListener(this);
		highlightList.addMouseListener(this);
		
		JScrollPane pathScrollPane = new JScrollPane(highlightList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pathScrollPane.setSize(450, 20);
		
		c.ipadx = 0;
		c.ipady = 65;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0,0,0,0);
		content.add(pathScrollPane, c);
		
		
		JPanel centerPanel = new JPanel();
		
		centerPanel.setSize(300, 20);
		
		final ScalingControl scaler = new CrossoverScalingControl();
		
		URL url = this.getClass().getResource("/ZoomOut.gif"); 
		
		Icon zoomOutIcon = new ImageIcon(url);
		JButton plus = new JButton(zoomOutIcon);
		
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        plus.setToolTipText("Zoom Out");
        plus.setSize(15,15);
        
        url = this.getClass().getResource("/ZoomIn.gif");
        Icon zoomInIcon = new ImageIcon(url);
        
        JButton minus = new JButton(zoomInIcon);
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 0.9f, vv.getCenter());
            }
        });
        minus.setToolTipText("Zoom In");
        minus.setSize(2,2);
        
        centerPanel.add(plus);
        centerPanel.add(minus);
        
        
		grayedPathsCheckBox = new JCheckBox("Show Paths in Gray", true);
		grayedPathsCheckBox.addActionListener(this);
		centerPanel.add(grayedPathsCheckBox);
		
		initialGraphButton=new JButton("Draw initial graph");
		initialGraphButton.addActionListener(this);
		centerPanel.add(initialGraphButton);
		
		c.ipadx = 0;
		c.ipady = 10;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = new Insets(0,0,0,0);
		content.add(centerPanel, c);
		
    }
    
    
    public void destroy() 
    {
    	highlightPathList = null;
    	highlightList = null;
    	isHighlight = false;
    	isGrayedEdge = false;
    	intialGraph = true;
    	showGrayedPaths = true;
    }
	
    public GeneConnectChart()
    {
	}
    
    private void createNodeList()
    {
    	nodeList = new ArrayList();
    	
//    	System.out.println(this.getParameter("noOfDatasources"));
    	
    	int noOfDataSources = Integer.parseInt(this.getParameter("noOfDatasources"));
    	
    	String dataSourceString;
    	for(int i=0; i<noOfDataSources; i++)
    	{
    		dataSourceString = this.getParameter("DataSource_"+(i+1));
    		
    		StringTokenizer dataSourceToken = new StringTokenizer(dataSourceString, ",");
    		
//    		nodeList.add(new DataSource(new Integer(1),"Ensembl Gene", 1,1));
    		
    		nodeList.add(new DataSource(new Integer(dataSourceToken.nextToken()), dataSourceToken.nextToken(), (new Integer(dataSourceToken.nextToken())).intValue(), (new Integer(dataSourceToken.nextToken())).intValue()));
    		
    	}
    	
    }
    
    private void createPathList()
    {
    	pathList = new ArrayList();
    	
//    	System.out.println(this.getParameter("noOfDatasourcesLinks"));
    	
    	int noOfDataSourcesLinks = Integer.parseInt(this.getParameter("noOfDatasourcesLinks"));
    	
    	String dataSourceLinkString;
    	for(int i=0; i<noOfDataSourcesLinks; i++)
    	{
    		dataSourceLinkString = this.getParameter("DataSourceLink_"+(i+1));
    		
    		StringTokenizer dataSourceLinkToken = new StringTokenizer(dataSourceLinkString, ",");
    		
//    		pathList.add(new Path(((DataSource)nodeList.get(0)).getId(), ((DataSource)nodeList.get(1)).getId(), 1));
    		
    		pathList.add(new Path(new Integer(dataSourceLinkToken.nextToken()), new Integer(dataSourceLinkToken.nextToken()), Integer.parseInt(dataSourceLinkToken.nextToken())));
    		
    	}
    	
    }
    
    private void createHighlightPathList()
    {
    	ArrayList highlightVertexList;
    	
    	ArrayList highlightLinksList;
    	
    	highlightPathsMap = new HashMap();
    	
    	if(this.getParameter("noOfHighlightPaths") != null)
    	{
	    	int noOfHighlightPaths = Integer.parseInt(this.getParameter("noOfHighlightPaths"));
	    	
	    	highlightPathList = new String[noOfHighlightPaths];
	    	
	    	String highlightNodeList;
	    	String highlightLinkTypes;
	    	
	    	for(int i=0; i<noOfHighlightPaths; i++)
	    	{
	    		highlightVertexList = new ArrayList();
	    		highlightLinksList = new ArrayList();
	    		
	    		highlightNodeList = this.getParameter("highlightNodeList_"+(i+1));
	    		
	    		highlightLinkTypes = this.getParameter("highlightLinkTypes_"+(i+1));
	    		
//	    		System.out.println("Highlight Path-->"+highlightNodeList+"  "+highlightLinkTypes);
	    		
	    		StringTokenizer highlightToken = new StringTokenizer(highlightNodeList, ">");   
	    		
	    		String highlightPathString = "";
	    		
	    		while(highlightToken.hasMoreTokens())
	    		{
	    			String nodeId = highlightToken.nextToken();
	    			
	    			Vertex v = fetchVertex(new Integer(nodeId));
	    			
	    			highlightVertexList.add(v);
	    			
	    			highlightPathString+= vertexStringerImpl.getLabel(v);
	    			highlightPathString+=" --> ";
	    			
//	    			System.out.println("NodeName to Highlight-->"+vertexStringerImpl.getLabel(v));
	    		}
	    		
	    		highlightPathList[i]=highlightPathString.substring(0, highlightPathString.length()-5);
	    		
//	    		System.out.println("Size of highlightVertexList==>"+highlightVertexList.size());
	    		
	    		StringTokenizer highlightLinkToken = new StringTokenizer(highlightLinkTypes, ",");
	    		
	    		int counter = 1;
	    		while(highlightLinkToken.hasMoreTokens())
	    		{
	    			String linkType = highlightLinkToken.nextToken();
	    			
	//    			new CustomEdge(fetchVertex(path.getSource()), fetchVertex(path.getDestination()), path.getPathType());
	    			
	    			highlightLinksList.add(new CustomEdge((CustomVertex)highlightVertexList.get(counter-1), (CustomVertex)highlightVertexList.get(counter), Integer.parseInt(linkType)));
	    			
	    			counter+=1;
	    			
	    		}
	    		
	    		highlightPathsMap.put(new Integer(i),highlightLinksList);
	    	}
    	}
    }
    
    public void valueChanged(ListSelectionEvent evt) 
    {
//    	System.out.println(highlightList.getSelectedIndex()+"-->"+highlightList.getSelectedValue());
    	
    	isHighlight = true;
    	
    	intialGraph = false;
    	
    	highlightPathNo = highlightList.getSelectedIndex();
    	
    	if(highlightList.getSelectionBackground() == Color.white)
    	{
//    		System.out.println("Changing selectionBackground Color...");
    		highlightList.setSelectionBackground(pathListBackgroundColor);
    	}
    	
    	panel.repaint();
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) 
    {
    	AbstractButton abstractButton = (AbstractButton)actionEvent.getSource();
        
    	if(abstractButton == grayedPathsCheckBox)
    	{
	    	boolean selected = abstractButton.getModel().isSelected();
	        
	        if(selected)
	        {
//	        	System.out.println("showGrayedPaths = true");
	        	showGrayedPaths = true;
	        	panel.repaint();
	        }
	        else
	        {
//	        	System.out.println("showGrapyedPaths = false");
	        	showGrayedPaths = false;
	        	panel.repaint();
	        }
    	}
    	else if(abstractButton == initialGraphButton)
    	{
    		isHighlight = false;
        	isGrayedEdge = false;
        	intialGraph = true;
        	showGrayedPaths = true;
        	
        	grayedPathsCheckBox.setSelected(true);

        	highlightList.setSelectedIndex(-1);
        	highlightList.repaint();
        	highlightList.setSelectionBackground(Color.white);
			
        	panel.repaint();
    	}
    	
    }
    
    public void focusGained(FocusEvent fe) //method of focuslistener
	{
//    	System.out.println("Focus Gained...");
    	
    	highlightList.setSelectionBackground(pathListBackgroundColor);
    	
    	highlightList.setSelectedIndex(highlightList.getSelectedIndex());
    	
    	panel.repaint();
	}
 
	public void focusLost(FocusEvent fe) //in focusevent "getID()"is a method
	{
//		System.out.println("Focus Lost...");
	}
	
	public void mouseClicked(MouseEvent e) 
	{
//    	System.out.println(highlightList.getSelectedIndex()+"-->"+highlightList.getSelectedValue());
    	
    	isHighlight = true;
    	
    	intialGraph = false;
    	
    	highlightPathNo = highlightList.getSelectedIndex();
    	
    	if(highlightList.getSelectionBackground() == Color.white)
    	{
//    		System.out.println("Changing selectionBackground Color...");
    		highlightList.setSelectionBackground(pathListBackgroundColor);
    	}
    	
    	panel.repaint();
    }

    public void mouseEntered(MouseEvent e) 
    {
    }

    public void mouseExited(MouseEvent e) 
    {
    }

    public void mousePressed(MouseEvent e) 
    {
    }

    public void mouseReleased(MouseEvent e) 
    {
    }
	    
    /**
     * create some vertices
     * @param count how many to create
     * @return the Vertices in an array
     */
    private Vertex[] createVertices(int count) {
        Vertex[] v = new Vertex[count];
        for (int i = 0; i < count; i++) {
            v[i] = graph.addVertex(new CustomVertex((DataSource)nodeList.get(i)));
            
//            System.out.println("In createVertices==>"+((DataSource)nodeList.get(i)).getId()+ " "+v[i].getClass());
            
            nodeMap.put( (Integer)((DataSource)nodeList.get(i)).getId(), v[i]);
        }
        return v;
    }
    
    private void calculateRowsCols()
    {
    	DataSource tempDataSource;
    	for(int i=0; i<nodeList.size(); i++)
    	{
    		tempDataSource = (DataSource)nodeList.get(i);
    		
    		if(tempDataSource.getRow() > noOfRows)
    			noOfRows = tempDataSource.getRow();
    		
    		if(tempDataSource.getCol() > noOfCols)
    			noOfCols = tempDataSource.getCol();
    	}
    	
//    	System.out.println("Rows=="+noOfRows+"  Cols=="+noOfCols);
    }
    
    private void createEdges(ArrayList pathList)
    {
    	Path path;
    	for(int i=0; i<pathList.size(); i++)
    	{
    		path = (Path)pathList.get(i);
    		
//    		System.out.println("Creating Edge between==>"+path.getSource() +"--"+path.getDestination());
    		
    		graph.addEdge(new CustomEdge(fetchVertex(path.getSource()), fetchVertex(path.getDestination()), path.getPathType()));
    	}
    }
    
    final class CustomVertex extends SparseVertex
	{
    	private DataSource dataSource;
    	
    	public CustomVertex(DataSource dataSource)
    	{
    		super();
    		
    		this.dataSource = dataSource;
    	}
    	
    	public DataSource getDataSource()
    	{
    		return this.dataSource;
    	}
    	
    	public boolean equals(Object o)
    	{
    		CustomVertex vertexToCompare = (CustomVertex) o;
    		
    		if(this.getDataSource().getId() == vertexToCompare.getDataSource().getId())
    			return true;
    		else
    			return false;
    	}
	}
    
    final class CustomEdge extends DirectedSparseEdge
	{
    	private int edgeType;
    	public CustomEdge(Vertex source, Vertex destination, int edgeType)
    	{
    		super(source, destination);
    		
    		this.edgeType = edgeType;
    	}
    	
    	public int getEdgeType()
    	{
    		return this.edgeType;
    	}
    	
    	public boolean equals(Object o)
    	{
    		CustomEdge edgeToCompare = (CustomEdge)o;
    		
    		CustomVertex source = (CustomVertex)this.getSource();
    		CustomVertex dest = (CustomVertex) this.getDest();
    		
    		if(source.equals(edgeToCompare.getSource()) & dest.equals(edgeToCompare.getDest()) & (this.getEdgeType() == edgeToCompare.getEdgeType()))
    			return true;
    		else
    			return false;
    	}
	}
    
    private Vertex fetchVertex(Integer id)
    {
    	Vertex v = (Vertex)nodeMap.get(id);

//    	System.out.println("Fetched Vertex==>"+v);
    	return v;
    }
    
    final class CustomEdgePaintFunction extends ConstantEdgePaintFunction
	{
    	public CustomEdgePaintFunction(Paint draw_paint)
    	{
    		super(draw_paint, Color.white);
    	}
    	
    	/**
         * @see edu.uci.ics.jung.graph.decorators.EdgePaintFunction#getDrawPaint(edu.uci.ics.jung.graph.Edge)
         */
        public Paint getDrawPaint(Edge e) 
        {
        	CustomEdge edge = (CustomEdge)e;
        	
//        	System.out.println("In getDrawPaint...");
        	
        	if(isGrayedEdge & showGrayedPaths)
        		return Color.gray;
        	else if( (!showGrayedPaths) &  (!isGrayedEdge) )
        	{
	        	if(edge.getEdgeType() == 1)
	        		return draw_paint;
	        	else if(edge.getEdgeType() == 2)
	        		return draw_paint;
	        	else if(edge.getEdgeType() == 4)
	        		return Color.green;
	        	else if(edge.getEdgeType() == 8)
	        		return Color.blue;
	        	else
	        		return draw_paint;
        	}
        	else if(showGrayedPaths & !isGrayedEdge)
        	{
        		if(edge.getEdgeType() == 1)
	        		return draw_paint;
	        	else if(edge.getEdgeType() == 2)
	        		return draw_paint;
	        	else if(edge.getEdgeType() == 4)
	        		return Color.green;
	        	else if(edge.getEdgeType() == 8)
	        		return Color.blue;
	        	else
	        		return draw_paint;
        	}
        	else
        		return null;
        }
        
        /**
         * @see edu.uci.ics.jung.graph.decorators.EdgePaintFunction#getFillPaint(edu.uci.ics.jung.graph.Edge)
         */
        public Paint getFillPaint(Edge e) {
            return null;
        }
    	
	}
    
    
    private final class MyLayout extends StaticLayout
	{
    	int rowSize = 500 / noOfCols;
	   	int colSize = 500 / noOfRows;
	   	
    	public MyLayout(Graph g)
    	{
    		super(g);
    	}
    	
    	protected void initializeLocation(Vertex v, Coordinates coord, Dimension d) 
    	{
    		CustomVertex cv =(CustomVertex)v;
    		
//    		System.out.println("Inside InitializeLocation==>"+cv.getDataSource().getName()+ "  "+cv.getDataSource().getRow()+ " "+cv.getDataSource().getCol());
    	   	
    	   	int xPosition = cv.getDataSource().getCol() * rowSize;
    	   	int yPosition = cv.getDataSource().getRow() * colSize;
    	   	
//    	   	System.out.println("Drawing at ==>"+xPosition+" - "+yPosition);
    	   	
    	   	coord.setX(xPosition);
    	   	
    	   	coord.setY(yPosition);
    	 }
	}

    /**
     * A simple implementation of VertexStringer that
     * gets Vertex labels from a Map  
     */
    public static class VertexStringerImpl implements VertexStringer {
        
        Map map = new HashMap();
        
        public VertexStringerImpl(Map map) 
        {
            this.map = map;
        }
        
        /* (non-Javadoc)
         * @see edu.uci.ics.jung.graph.decorators.VertexStringer#getLabel(edu.uci.ics.jung.graph.Vertex)
         */
        public String getLabel(ArchetypeVertex v) 
        {
                return (String)map.get(v);
        }
    }
    
    private final  class EdgeWeightStrokeFunction implements EdgeStrokeFunction
    {
    	protected final float[] dashing = {5.0f};
    	
        protected final Stroke basic = new BasicStroke(1);
        protected final Stroke heavy = new BasicStroke(3);
        protected final Stroke dotted = PluggableRenderer.DASHED;
        
        protected final Stroke heavyDotted = new BasicStroke(3.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1.0f, dashing, 0f);
        
        protected boolean weighted = true;
        protected NumberEdgeValue edge_weight;
        
        public EdgeWeightStrokeFunction(NumberEdgeValue edge_weight)
        {
            this.edge_weight = edge_weight;
            
        }
        
        public void setWeighted(boolean weighted)
        {
            this.weighted = weighted;
        }
        
        public Stroke getStroke(Edge e)
        {
//        	System.out.println("In getStroke...");
        	
        	CustomEdge edge = (CustomEdge)e;
        	
        	boolean toHighlight = edgeToHighlight(edge);
        	
        	if(toHighlight)
        	{
        		isGrayedEdge = false;
        		
        		pr.setEdgeArrowFunction(new DirectionalEdgeArrowFunction(25,15,8));
        		
        		if(edge.getEdgeType() == 1)
	        		return heavy;
	        	else if(edge.getEdgeType() == 2)
	        		return heavyDotted;
	        	else if(edge.getEdgeType() == 4)
	        		return heavy;
	        	else if(edge.getEdgeType() == 8)
	        		return heavy;
	            else
	                return heavy;
        	}
        	else
        	{
        		if(!intialGraph)
        			isGrayedEdge = true;
        		
        		
        		
        		if(showGrayedPaths)
        		{
        			pr.setEdgeArrowFunction(new DirectionalEdgeArrowFunction(10,8,4));
        			
		        	if(edge.getEdgeType() == 1)
		        		return basic;
		        	else if(edge.getEdgeType() == 2)
		        		return dotted;
		        	else if(edge.getEdgeType() == 4)
		        		return basic;
		        	else if(edge.getEdgeType() == 8)
		        		return basic;
		            else
		                return basic;
        		}
        		else
        		{
        			if(intialGraph)
        			{
        				pr.setEdgeArrowFunction(new DirectionalEdgeArrowFunction(10,8,4));
            			
    		        	if(edge.getEdgeType() == 1)
    		        		return basic;
    		        	else if(edge.getEdgeType() == 2)
    		        		return dotted;
    		        	else if(edge.getEdgeType() == 4)
    		        		return basic;
    		        	else if(edge.getEdgeType() == 8)
    		        		return basic;
    		            else
    		                return basic;
        				
        			}
        			else
        			{
        				pr.setEdgeArrowFunction(new DirectionalEdgeArrowFunction(1,1,1));
        			
        				return null;
        			}
        		}
        	}
        }
        
        private boolean edgeToHighlight(CustomEdge edge)
        {
        	if(isHighlight)
        	{
        		ArrayList edgesToHighlight = (ArrayList)highlightPathsMap.get(new Integer(highlightPathNo));
        		
        		for(int i=0; i<edgesToHighlight.size(); i++)
        		{
        			if(edge.equals(edgesToHighlight.get(i)))
        				return true;
        		}
        	}
        	else
        		return false;
        	
        	
			
        	return false;
        }
    }
    
    private final static class CustomVertexShape extends AbstractVertexShapeFunction implements VertexSizeFunction, VertexAspectRatioFunction
	{
    	public CustomVertexShape()
        {
            setSizeFunction(this);
            setAspectRatioFunction(this);
        }
    	
    	public Shape getShape(Vertex v)
        {
           int sides = Math.max(v.degree(), 3);
           return factory.getRegularPolygon(v, 4);
           
        }
    	
    	public int getSize(Vertex v)
        {
    		return 10;
        }
    	
    	public float getAspectRatio(Vertex v)
        {
    		return 1.0f;
        }
	}
    
    private final class CustomPluggableRenderer extends PluggableRenderer
	{
    	protected void labelVertex(Graphics g, Vertex v, String label, int x, int y)
        {
    		Component component = prepareRenderer(graphLabelRenderer, label, true, v);

            Dimension d = component.getPreferredSize();
            
            int h_offset;
            int v_offset;
            if (centerVertexLabel)
            {
                h_offset = -d.width / 2;
                v_offset = -d.height / 2;

            }
            else
            {
                Rectangle2D bounds = vertexShapeFunction.getShape(v).getBounds2D();
                h_offset = (int)(bounds.getWidth() / 2) - 40;
                v_offset = (int)(bounds.getHeight() / 2 )- 20 - d.height;
            }
            
            rendererPane.paintComponent(g, component, screenDevice, x+h_offset, y+v_offset,
                    d.width, d.height, true);
            
        }
	}
	
    public static void main(String[] args)
    {
    	JFrame frame = new JFrame();
    	frame.setSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container content = frame.getContentPane();
        
        content.add(new GeneConnectChart());
        
        frame.pack();
        frame.setVisible(true);
        
    }
	   
}





