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
import java.awt.Image;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
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
    
    private Map highlightPathsSortedKeys = null;
    
    private Vertex[] v; 
    
    private boolean isHighlight;
    
    private int highlightPathNo;
    
    private boolean isGrayedEdge;
    
    private boolean intialGraph = true;
    
    private boolean showGrayedPaths = true;
    
    protected JButton initialGraphButton;
    protected JCheckBox grayedPathsCheckBox;
    protected JLabel pathCountMessage;
    
    protected Color pathListBackgroundColor = new Color(-4665371);
    
    //this function draws the graph
    public void init()
    {
    	//call to function to create list of Nodes of the graph
    	createNodeList();
    	
    	//call to calculate no of Rows and Columns to draw in the grpah
    	calculateRowsCols();
    	
    	//create a simple graph for the demo
	    graph = new SparseGraph();  
	    
	    //Initializing map of the nodes of the graph
	    nodeMap = new HashMap();
	    
	    //Initializing array of the Vertices of the graph
	    v = createVertices(nodeList.size());
	    
	    //Initializing renderer object for the graph
	    pr = new CustomPluggableRenderer();
	    
	    //Initiliazing map for the node labels
        Map map = new HashMap();
        for(int i=0; i<v.length; i++) 
        {
        	map.put(v[i], (String)((DataSource)nodeList.get(i)).getName());
        }
        
        //Initializing labeller of the vertices of the grpah
        vertexStringerImpl = new VertexStringerImpl(map);
        
        //setting the populated labeller of the vertices
        pr.setVertexStringer(vertexStringerImpl);
        
        //Initializing the instance of the shape of the Vertices of the graph
        cvs= new CustomVertexShape();
        
        //setting instance of the set of the Vertices
        pr.setVertexShapeFunction(cvs);
        
        //Initializing layout instance of the graph
	    Layout layout = new MyLayout(graph);
	    
	    //Initializing viewer instance of the graph
	    vv =  new VisualizationViewer(layout, pr, new Dimension(500,500));
	    
	    //setting pickup type of the graph shape
	    vv.setPickSupport(new ShapePickSupport());
	    
	    //setting shape of the Edges of the graph
	    pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
	    
	    //setting shape of the edge arrows of the graph
	    pr.setEdgeArrowFunction(new DirectionalEdgeArrowFunction(10,8,4));
	    
	    //setting background color of the graph viewer and initial location of the graph
	    vv.setBackground(Color.white);
	    vv.setLocation(0,0);
	    
	    //Initializing weight stroke of the edges of the graph
	    ewcs = new EdgeWeightStrokeFunction(edge_weight);
	    ewcs.setWeighted(true);
	    
	    //setting weight stroke of the edges of the grpah
	    pr.setEdgeStrokeFunction(ewcs);
	    
	    //call to the function to create list of paths to display in the graph
	    createPathList();
	    
	    //call to the function to create list of paths to highlight in the graph
	    createHighlightPathList();
	    
	    //call to function to create list of the edges in the graph
	    createEdges(pathList);
	    
	    vv.updateUI();
	    
	    //setSize(500, 500);
	    
	    setLocation(0,0);
	    
	    final Indexer ind = Indexer.newIndexer(graph, 0);
	    
        EdgeStringer stringer = new EdgeStringer(){
            public String getLabel(ArchetypeEdge e) {
                return e.toString();
            }
        };
//        pr.setEdgeStringer(stringer);
        
        //setting edge paint function of the graph  
        pr.setEdgePaintFunction(new PickableEdgePaintFunction(pr, Color.black, Color.cyan));
        pr.setEdgePaintFunction(new CustomEdgePaintFunction(Color.red));
        
        // add my listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
        
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        vv.setGraphMouse(graphMouse);
        
        // create a frame to hold the graph
        panel = new GraphZoomScrollPane(vv);
        
        panel.setSize(500, 500);
        
        Container content = getContentPane();
        
        //setting layout of the container containing the graph
        content.setLayout(new GridBagLayout());
        content.setBackground(Color.white);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
        
		//adding different components of the graph into container of the graph
		URL url = this.getClass().getResource("/GraphHeader.JPG"); 
		ImagePanel headerPanel = new ImagePanel(new ImageIcon(url).getImage());
		
		c.ipadx = 0;
		c.ipady = 10;
		c.anchor = GridBagConstraints.FIRST_LINE_START; 
//		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0,0,0,0);
		content.add(headerPanel, c);
		
		
//        c.ipadx = 500;
		c.ipady = 475;
//		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 1;
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
//		pathScrollPane.setSize(450, 20);
		
		c.ipadx = 10;
		c.ipady = 65;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 3;
		c.insets = new Insets(0,0,0,0);
		content.add(pathScrollPane, c);

		
		JPanel centerPanel = new JPanel();
		
		centerPanel.setBackground(Color.white);
		
		centerPanel.setSize(300, 20);
		
		
		
		final ScalingControl scaler = new CrossoverScalingControl();
		
		url = this.getClass().getResource("/ZoomOut.gif"); 
		
		Icon zoomOutIcon = new ImageIcon(url);
		JButton plus = new JButton(zoomOutIcon);
		plus.setBackground(Color.white);
		plus.setBorder(new EmptyBorder(0,0,0,0));
		
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
        minus.setBackground(Color.white);
        minus.setBorder(new EmptyBorder(0,0,0,0));
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
		grayedPathsCheckBox.setBackground(Color.white);
		grayedPathsCheckBox.addActionListener(this);
		centerPanel.add(grayedPathsCheckBox);
		
		initialGraphButton=new JButton("Draw initial graph");
		initialGraphButton.setBackground(Color.white);
		initialGraphButton.addActionListener(this);
		centerPanel.add(initialGraphButton);
		
		c.ipadx = 10;
		c.ipady = 20;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.insets = new Insets(0,0,0,0);
		content.add(centerPanel, c);
		
		JPanel messagePanel = new JPanel();
		
		messagePanel.setBackground(Color.white);
		
		messagePanel.setSize(300, 20);
		
		pathCountMessage = new JLabel("{#} = No. of times ONT traversed to obtain the result");
		pathCountMessage.setBackground(Color.white);
		messagePanel.add(pathCountMessage);
		
		c.ipadx = 10;
		c.ipady = 20;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		c.insets = new Insets(0,0,0,0);
		content.add(messagePanel, c);
		
		content.setSize(600, 1000);
		
    }
    
    public void setSize(int width, int height)
    {
       super.setSize(width,height);
       validate();
    }
    
    /**
     * this function clean up frees memory allocated to attributes
     */
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
    
    /**
     * This function creates list of Nodes for the graph
     */
    private void createNodeList()
    {
    	nodeList = new ArrayList();
    	
//    	Logger.out.debug(this.getParameter("noOfDatasources"));
    	
    	int noOfDataSources = Integer.parseInt(this.getParameter("noOfDatasources"));
    	
    	String dataSourceString;
    	for(int i=0; i<noOfDataSources; i++)
    	{
    		dataSourceString = this.getParameter("DataSource_"+(i+1));
    		
    		StringTokenizer dataSourceToken = new StringTokenizer(dataSourceString, ",");
    		
    		nodeList.add(new DataSource(new Integer(dataSourceToken.nextToken()), dataSourceToken.nextToken(), (new Integer(dataSourceToken.nextToken())).intValue(), (new Integer(dataSourceToken.nextToken())).intValue()));
    	}
    }
    
    /**
     * This function creates list of possible paths to draw in the graph
     */
    private void createPathList()
    {
    	pathList = new ArrayList();
    	
//    	Logger.out.debug(this.getParameter("noOfDatasourcesLinks"));
    	
    	int noOfDataSourcesLinks = Integer.parseInt(this.getParameter("noOfDatasourcesLinks"));
    	
    	String dataSourceLinkString;
    	for(int i=0; i<noOfDataSourcesLinks; i++)
    	{
    		dataSourceLinkString = this.getParameter("DataSourceLink_"+(i+1));
    		
    		StringTokenizer dataSourceLinkToken = new StringTokenizer(dataSourceLinkString, ",");
    		
    		pathList.add(new Path(new Integer(dataSourceLinkToken.nextToken()), new Integer(dataSourceLinkToken.nextToken()), Integer.parseInt(dataSourceLinkToken.nextToken())));
    	}
    }
    
    /**
     * This function creates list of paths to highlight in the graph
     *
     */
    private void createHighlightPathList()
    {
    	ArrayList highlightVertexList;
    	
    	ArrayList highlightLinksList;
    	
    	highlightPathsMap = new HashMap();
    	
    	highlightPathsSortedKeys = new TreeMap();
    	
    	//Map to store Unique ONTs in sorted order
    	Map sortedUniquePaths = new TreeMap();
    	
    	int noOfPaths = 0;
    	
    	if(this.getParameter("noOfHighlightPaths") != null)
    	{
	    	int noOfHighlightPaths = Integer.parseInt(this.getParameter("noOfHighlightPaths"));
	    	
//	    	System.out.println("No. of Paths-->"+noOfHighlightPaths);
	    	
	    	highlightPathList = new String[noOfHighlightPaths];
	    	
	    	String highlightNodeList;
	    	String highlightLinkTypes;
	    	String highlightPathCount;
	    	
	    	for(int i=0; i<noOfHighlightPaths; i++)
	    	{
	    		highlightVertexList = new ArrayList();
	    		highlightLinksList = new ArrayList();
	    		
	    		highlightNodeList = this.getParameter("highlightNodeList_"+(i+1));
	    		
	    		highlightLinkTypes = this.getParameter("highlightLinkTypes_"+(i+1));
	    		
	    		highlightPathCount = this.getParameter("highlightPathCount_"+ (i+1));
	    		
//	    		Logger.out.debug("Highlight Path-->"+highlightNodeList+"  "+highlightLinkTypes);
	    		
	    		StringTokenizer highlightToken = new StringTokenizer(highlightNodeList, ">");   
	    		
	    		String highlightPathString = "";
	    		
	    		while(highlightToken.hasMoreTokens())
	    		{
	    			String nodeId = highlightToken.nextToken();
	    			
	    			Vertex v = fetchVertex(new Integer(nodeId));
	    			
	    			highlightVertexList.add(v);
	    			
	    			highlightPathString+= vertexStringerImpl.getLabel(v);
	    			highlightPathString+=" --> ";
	    			
//	    			Logger.out.debug("NodeName to Highlight-->"+vertexStringerImpl.getLabel(v));
	    		}
	    		
//	    		if(highlightPathCount.equals("0"))
//	    			highlightPathList[i]=highlightPathString.substring(0, highlightPathString.length()-5)+":"+i;
//	    		else
//	    			highlightPathList[i]=highlightPathString.substring(0, highlightPathString.length()-5)+":"+i + " {"+highlightPathCount+"}";

	    		if(highlightPathCount.equals("0"))
	    			highlightPathList[i]=highlightPathString.substring(0, highlightPathString.length()-5);
	    		else
	    			highlightPathList[i]=highlightPathString.substring(0, highlightPathString.length()-5)+ " {"+highlightPathCount+"}";

	    		
//	    		Logger.out.debug("Size of highlightVertexList==>"+highlightVertexList.size());
	    		
	    		StringTokenizer highlightLinkToken = new StringTokenizer(highlightLinkTypes, ",");
	    		
	    		int counter = 1;
	    		while(highlightLinkToken.hasMoreTokens())
	    		{
	    			String linkType = highlightLinkToken.nextToken();
	    			
	    			highlightLinksList.add(new CustomEdge((CustomVertex)highlightVertexList.get(counter-1), (CustomVertex)highlightVertexList.get(counter), Integer.parseInt(linkType)));
	    			
	    			counter+=1;
	    		}
	    		highlightPathsMap.put(new Integer(i),highlightLinksList);
	    		
	    		
	    		//checking if ONT is already traversed
	    		if(sortedUniquePaths.containsKey(highlightPathList[i]))
	    		{
	    			//getting list of Links for ONT
	    			ArrayList stackOfLinks = (ArrayList) sortedUniquePaths.get(highlightPathList[i]);
	    			boolean duplicate = false;
	    			
	    			//traversing list of links to check uniqueness of ONT
	    			for(int k=0; k<stackOfLinks.size(); k++)
	    			{
	    				ArrayList links = (ArrayList)stackOfLinks.get(k);

	    				if(links.equals(highlightLinksList))
	    					duplicate = true;
	    			}
	    			
	    			//if ONT already available
	    			if(duplicate)
	    			{
	    				//Duplicate Path found
//	    				System.out.println("Duplicate path at Solution Map..."+highlightPathList[i]);
	    			}
	    			//add ONT as links are different
	    			else
	    			{
	    				stackOfLinks.add(highlightLinksList);
	    				
	    				sortedUniquePaths.put(highlightPathList[i], stackOfLinks);
	    				
	    				noOfPaths++;
	    			}
	    		}
	    		//add ONT into sortedUniquePaths map
	    		else
	    		{
	    			ArrayList stackOfLinks = new ArrayList();
	    			
	    			stackOfLinks.add(highlightLinksList);
	    			
	    			sortedUniquePaths.put(highlightPathList[i], stackOfLinks);
	    			noOfPaths++;
	    		}
	    	}
	    	
	    	//array of ONT label
	    	String[] tempPathList = new String[noOfPaths];
	    	
	    	//map of ONT links
	    	Map tempPathsMap = new HashMap();

	    	Set keys = (Set)sortedUniquePaths.keySet();
			Iterator keysIterator = keys.iterator();
			
			int pathCounter=0;
			
			//iterating sortedUniquePaths and creating required list of Paths and map of Links
			while(keysIterator.hasNext())
			{
				String key = (String)keysIterator.next();
				
				ArrayList stackOfLinks = (ArrayList) sortedUniquePaths.get(key);
				
				for(int k=0; k<stackOfLinks.size(); k++)
				{
					tempPathList[pathCounter] = key;
					tempPathsMap.put(new Integer(pathCounter), stackOfLinks.get(k));
					pathCounter++;
				}
			}
	    	
			//assigning sorted list of Unique paths to appropriate attributes...
			highlightPathList = tempPathList;
			highlightPathsMap = tempPathsMap;
    	}
    }
    
    /**
     * This listener function gets called when user changes value of the list of highlight paths
     */
    public void valueChanged(ListSelectionEvent evt) 
    {
//    	Logger.out.debug(highlightList.getSelectedIndex()+"-->"+highlightList.getSelectedValue());
    	
    	isHighlight = true;
    	
    	intialGraph = false;
    	
    	String selectedValue = (String)highlightList.getSelectedValue();
    	
    	highlightPathNo = highlightList.getSelectedIndex();
    	
//    	highlightPathNo = ((Integer)highlightPathsSortedKeys.get(selectedValue)).intValue();
    	
    	if(highlightList.getSelectionBackground() == Color.white)
    	{
//    		Logger.out.debug("Changing selectionBackground Color...");
    		highlightList.setSelectionBackground(pathListBackgroundColor);
    	}
    	
    	panel.repaint();
    }
    
    /**
     * This listener function gets called when User clicks button to draw initial graph 
     * or selected checkbox to show paths in Gray color
     */
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) 
    {
    	AbstractButton abstractButton = (AbstractButton)actionEvent.getSource();
        
    	if(abstractButton == grayedPathsCheckBox)
    	{
	    	boolean selected = abstractButton.getModel().isSelected();
	        
	        if(selected)
	        {
//	        	Logger.out.debug("showGrayedPaths = true");
	        	showGrayedPaths = true;
	        	panel.repaint();
	        }
	        else
	        {
//	        	Logger.out.debug("showGrapyedPaths = false");
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
    
    /**
     * This listener function gets called when highlight paths listbox gets the focus
     */
    public void focusGained(FocusEvent fe) //method of focuslistener
	{
//    	Logger.out.debug("Focus Gained...");
    	
    	highlightList.setSelectionBackground(pathListBackgroundColor);
    	
    	highlightList.setSelectedIndex(highlightList.getSelectedIndex());
    	
    	panel.repaint();
	}
 
    /**
     * This listener function gets called when highlight paths listbox losses the focus
     */
	public void focusLost(FocusEvent fe) //in focusevent "getID()"is a method
	{
//		Logger.out.debug("Focus Lost...");
	}
	
	/**
	 * This listener function gets called when User clicks to select an option of highlight paths listbox
	 */
	public void mouseClicked(MouseEvent e) 
	{
//    	Logger.out.debug(highlightList.getSelectedIndex()+"-->"+highlightList.getSelectedValue());
    	
    	isHighlight = true;
    	
    	intialGraph = false;
    	
    	String selectedValue = (String)highlightList.getSelectedValue();
    	
    	highlightPathNo = highlightList.getSelectedIndex();
    	
//    	highlightPathNo = ((Integer)highlightPathsSortedKeys.get(selectedValue)).intValue();
    	
    	if(highlightList.getSelectionBackground() == Color.white)
    	{
//    		Logger.out.debug("Changing selectionBackground Color...");
    		highlightList.setSelectionBackground(pathListBackgroundColor);
    	}
    	
    	panel.repaint();
    }

	/**
	 * below 4 methods required to implement for MouseListener
	 */
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
     * create vertices of the graph
     * @param count how many to create
     * @return the Vertices in an array
     */
    private Vertex[] createVertices(int count) 
    {
    	Vertex[] v = new Vertex[count];
        for (int i = 0; i < count; i++) 
        {
            v[i] = graph.addVertex(new CustomVertex((DataSource)nodeList.get(i)));
//            Logger.out.debug("In createVertices==>"+((DataSource)nodeList.get(i)).getId()+ " "+v[i].getClass());
            nodeMap.put( (Integer)((DataSource)nodeList.get(i)).getId(), v[i]);
        }
        return v;
    }
    
    /**
     * This function calculate no. of Rows and Columns to creat in the graph
     */
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
//    	Logger.out.debug("Rows=="+noOfRows+"  Cols=="+noOfCols);
    }
    
    /**
     * This functions creates edges of the graph
     * @param pathList - list of the paths
     */
    private void createEdges(ArrayList pathList)
    {
    	Path path;
    	for(int i=0; i<pathList.size(); i++)
    	{
    		path = (Path)pathList.get(i);
    		
//    		Logger.out.debug("Creating Edge between==>"+path.getSource() +"--"+path.getDestination());
    		
    		graph.addEdge(new CustomEdge(fetchVertex(path.getSource()), fetchVertex(path.getDestination()), path.getPathType()));
    	}
    }
    
    /**
     * This class overrides the functionality provided by basic Vertex
     */
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
    
    /**
     *This class overrides the functionality provided by basic Edge 
     */
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

//    	Logger.out.debug("Fetched Vertex==>"+v);
    	return v;
    }
    
    /**
     *This class overrides the funcionality provided by basic EdgePaint functionality 
     */
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
        	
//        	Logger.out.debug("In getDrawPaint...");
        	
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
    
    /**
     *This class overrides StaticLayout class and sets locaion of the componenets of the graph as per the need 
     */
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
    		
//    		Logger.out.debug("Inside InitializeLocation==>"+cv.getDataSource().getName()+ "  "+cv.getDataSource().getRow()+ " "+cv.getDataSource().getCol());
    	   	
    	   	double xPosition = (cv.getDataSource().getCol() - 0.5 )* rowSize;
    	   	double yPosition = (cv.getDataSource().getRow() - 0.5 )* colSize;
    	   	
//    	   	Logger.out.debug("Drawing at ==>"+xPosition+" - "+yPosition);
    	   	
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
    
    /**
     *This class overrides the funcionality provided by basic EdgeStroke functionality
     *It provides functionality to draw the edges with the thickness required to highlight 
     */
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
//        	Logger.out.debug("In getStroke...");
        	
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
    
    /**
     *This class overrides basic functionality provided by VertedShape to draw different shapes for vertices of the graph 
     */
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
    
    /**
     *This class overrides basic Renderer of the graph to set location of the label of vertices on the grpah  
     */
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
	
    private class ImagePanel extends JPanel
	{
    	private Image img;

    	  public ImagePanel(String img) 
    	  {
    	    this(new ImageIcon(img).getImage());
    	  }

    	  public ImagePanel(Image img) 
    	  {
    	    this.img = img;
    	    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
    	    setPreferredSize(size);
    	    setMinimumSize(size);
    	    setMaximumSize(size);
    	    setSize(size);
    	    setLayout(null);
    	  }

    	  public void paintComponent(Graphics g) 
    	  {
    	    g.drawImage(img, 0, 0, null);
    	  }
	}
    
    public static void main(String[] args)
    {
    	JFrame frame = new JFrame();
    	frame.setSize(new Dimension(500, 1000));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container content = frame.getContentPane();
        
        content.add(new GeneConnectChart());
        
        frame.pack();
        frame.setVisible(true);
    }
}