package edu.ucsf.rbvi.cddApp.internal.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyNetwork;

/**
 * Class for messages storage.
 * 
 * @author Nadezhda Doncheva
 */

public abstract class Messages {

	// Menu items added in Cytoscape
	public static String AC_ABOUT = "About";

	public static String AC_ANALYSISACTION = "Analyze Network";

	public static String AC_BANALYSISACTION = "Batch Analysis";

	public static String AC_COMPARERINS = "Compare RINs";

	public static String AC_EXTRACTCHAINS = "Extract Chain(s)";

	public static String AC_LAYOUT = "Layout";

	public static String AC_MENU_PLUGIN = "Plugins.RINalyzer";

	public static String AC_RINLAYOUT = "RIN Layout";

	public static String AC_RINLAYOUT_SET = "RIN Layout settings";

	public static String AC_SET = "Manage Node Sets";

	public static String AC_STRUCTURE = "Protein Structure";

	public static String AC_STRUCTURE_OPEN = "Open structure from file";

	public static String AC_STRUCTURE_PDB = "Open structure by PDB ID";

	public static String AC_STRUCTURE_CLOSE = "Close structure(s)";

	public static String AC_STRUCTURE_ALL = "all";

	public static String AC_STRUCTURE_EXIT = "Exit Chimera";

	public static String AC_STRUCTURE_SHOWHIDEBB = "Show/Hide backbone";

	public static String AC_STRUCTURE_SHOWBB = "Show backbone";

	public static String AC_STRUCTURE_HIDEBB = "Hide backbone";

	public static String AC_STRUCTURE_SYNC = "Sync 3D view colors";

	public static String AC_VISUALPROPS = "Visual Properties";

	// Labels of items in dialogs
	public static String DI_3POINTS = "...";
	
	public static String DI_ADDINTERFACEEDGES = "Add edges wihtin the interface";

	public static String DI_ANALYSIS_INFO = "General Information";

	public static String DI_ANALYSIS_NODES = "Selected nodes";

	public static String DI_ANALYSIS_SETTINGS = "Analysis settings";

	public static String DI_ANALYSIS_NETWORK = "Analyzed network title";

	public static String DI_ANALYZE = "Analyze";

	public static String DI_APPLY = "Apply";

	public static String DI_ATTRNAME = "Select attribute";

	public static String DI_BACKBONE_EDGES = "Backbone edges";

	public static String DI_BACKBONE_EDGE_WIDTH = "Backbone edge width";

	public static String DI_BACKBONE_ADD = "Add backbone edges";

	public static String DI_BACKBONE_HIDE = "Remove backbone edges";

	public static String DI_BOUND_LOW = "Lower bound:";

	public static String DI_BOUND_HIGH = "Uppper bound:";

	public static String DI_CANCEL = "Cancel";

	public static String DI_CENTANASET = "Centrality analysis settings";

	public static String DI_CENTMEASURES = "Centrality measures to compute";

	public static String DI_CHAINSELECT = "Select chains";

	public static String DI_CHAINS = "Chains";

	public static String DI_CREATENETWORKFOR = "Create network for";

	public static String DI_CHOOSEATTRIBUTE = "Choose attribute as edge weight:";

	public static String DI_CLEAR = "Clear";

	public static String DI_CLOSE = "Close";

	public static String DI_COLORBUTTON = "Click to change";

	public static String DI_COMPARE = "Compare";

	public static String DI_COMPARENETS = "<html>Note that the network nodes are mapped according to the <br>"
			+ "provided structure alignment file and the two networks <br>"
			+ "are compared with respect to the selected edge type.</html>";

	public static String DI_COMPAREOPTIONS = "Comparison options";

	public static String DI_COMPUTE1 = "<html><b>Compute weighted centrality measures for the network <br>";

	public static String DI_COMPUTE2 = " with respect to the selected nodes.</b></html>";

	public static String DI_COMPUTEBATCH = "<html><b>Compute weighted centrality measures for all networks </b></html>";

	public static String DI_COMPUTED = "<html><b>Computed centrality measures:</b></html>";

	public static String DI_COMPCFCENT = "Current flow centralities:";

	public static String DI_COMPRWCENT = "Random walk centralities:";

	public static String DI_COMPSPCENT = "Shortest path centralities:";

	public static String DI_CONVERTWEIGHT = "Convert scores into distances:";

	public static String DI_DEFWEIGHTVALUE = "Default edge weight (if missing):";

	public static String DI_DEGREECUTOFF = "Cutoff for weighted degree:";

	public static String DI_EDGE_COLORS = "Edge (interaction) types";

	public static String DI_EDGELINES = "Edge lines";

	public static String DI_EDGE_PROPS = "Edges";

	public static String DI_EDGE_SPACE = "Space between edges";

	public static String DI_EDGE_WIDTH = "Edge width";

	public static String DI_SELRANGE = "Selection range for ";

	public static String DI_ENTERATTRIBUTE = "Enter attribute name for edge weights:";

	public static String DI_ENTERNAME = "New network name:";

	public static String DI_EXAMPLE = "Example node label:";

	public static String DI_FILEEXISTS = "Warning - File Exists";

	public static String DI_FILTERING = " Filtering";

	public static String DI_GEN_NODE_PROPS = "General & Nodes";

	public static String DI_GENERAL = "General options";

	public static String DI_HELP = "Help";

	public static String DI_INPUTDIR = "Input Directory";

	public static String DI_INTERFACES = "Interfaces";

	public static String DI_LABEL = "ResidueLabel";

	public static String DI_LABEL_SEP = " ";

	public static String DI_LABEL_PDB = "PDB identifier";

	public static String DI_LABEL_CHAIN = "Chain identifier";

	public static String DI_LABEL_INDEX = "Residue index";

	public static String DI_LABEL_ICODE = "Insertion code";

	public static String DI_LABEL_SIZE = "Node label size";

	public static String DI_LABEL_TYPE = "Residue type";

	public static String DI_LABEL_TYPE_1LC = "1-letter";

	public static String DI_LABEL_TYPE_3LC = "3-letter";

	public static String DI_MULTIPLEEDGES = "Handle multiple edges:";

	public static String DI_NEGWEIGHT_IGNORE = "Ignore";

	public static String DI_NEGWEIGHT_REVERT = "Revert";

	public static String DI_NETNAME = "New network name";

	public static String DI_NETWORK1 = "First network";

	public static String DI_NETWORK2 = "Second network";

	public static String DI_NEWCHAINNET = "<html><b>You can select one or more of the protein chain(s)<br>"
			+ "contained in this RIN and create a new network from<br>"
			+ "the chain nodes and their adjacent edges.</b></hmtl>";

	public static String DI_NODE_SIZE = "Node size";

	public static String DI_NONEWEIGHT = "None";

	public static String DI_NORESULTS_TITLE1 = "<html><b>Analysis has failed for network<br>";

	public static String DI_NORESULTS_TITLE2 = "!</b></html>";

	public static String DI_OK = "OK";

	public static String DI_OTHER_OPTIONS = "Other options";

	public static String DI_OUTPUTDIR = "Output Directory";

	public static String DI_REMOVE_NEGWEIGHT = "Handle negative weights:";

	public static String DI_RESULTS_TITLE1 = "<html><b>Analysis finished successfully for network<br>";

	public static String DI_RESULTS_TITLE2 = "!</b></html>";

	public static String DI_RESULTS_NOFILTER = "<html>Filter is not displayed, because all centrality <br>"
			+ "values are equal.</html>";

	public static String DI_RESTORE = "Restore Default";

	public static String DI_SAVE_ALL = "Save all";

	public static String DI_SAVE_LOG = "Save";

	public static String DI_SAVE_CENT = "Save";

	public static String DI_SELECTDIR = "Select Directory";

	public static String DI_SETDEFAULT = "Set as Default";

	public static String DI_SHOW_ALL = "Show all";

	public static String DI_SHOW_CENT = "Show";

	public static String DI_SIMTODIST1 = "1 / value";

	public static String DI_SIMTODIST2 = "max - value";

	public static String DI_SLIDER_NOT_INIT = "The slider is not initialized yet.";

	public static String DI_SS_COLORS = "Secondary structure colors";

	public static String DI_STATISTICS = "Statistics";

	public static String DI_STRAIGHTEDGES = "Straighten edge lines";

	public static String DI_USECONNCOMP = "<html>Exclude paths that connect<br>"
			+ "nodes within the same set:<html>";

	public static String DI_USECONNCOMP2 = "Exclude paths within the same set:";

	public static String DI_VISUALIZE = "Visualize";

	public static String DI_WEIGHTAVE = "Average weight";

	public static String DI_WEIGHTMIN = "Min weight";

	public static String DI_WEIGHTMAX = "Max weight";

	public static String DI_WEIGHTSUM = "Sum of weights";

	// Dialog titles

	public static String DT_ABOUT = "About RINalyzer";

	public static String DT_CHOOSECOLOR = "Choose Color";

	public static String DT_COMPARERINS = "Compare RINs";

	public static String DT_COMPUTING = "Computing centralities for network ";

	public static String DT_VISUALPROPS = "RIN Visual Properties";

	public static String DT_EDITRANGE = "Edit Selection Range";

	public static String DT_ERROR = "RINalyzer Error";

	public static String DT_INFORMATION = "RINalyzer Information";

	public static String DT_MAPPING = "RINalyzer Mapping Information";

	public static String DT_NEWSET = "New Set";

	public static String DT_NEWNETNAME = "New Network Name";

	public static String DT_NEWSETNAME = "New Set Name";

	public static String DT_RESULTS = "RINalyzer Centralities";

	public static String DT_RINLAYOUT_SET = "RIN Layout settings";

	public static String DT_SETS = "RINalyzer Node Sets";

	public static String DT_SETTINGS = "RINalyzer Centrality Analysis";

	public static String DT_SETTINGSOVERVIEW = "Analysis Information";

	public static String DT_SETTINGSHELP = "RINalyzer Analysis Help";

	public static String DT_SUBNETWORK = "Extract Network";

	public static String DT_WARNING = "RINalyzer Warning";

	// Log messages
	public static String LOG_ADDEDNODES = "Nodes added to the set ";

	public static String LOG_AND = " and ";

	public static String LOG_IN = " in ";

	public static String LOG_BUILDDIFF = " is the difference of ";

	public static String LOG_BUILDINTERSEC = " is the intersection of ";

	public static String LOG_BUILDUNION = " is the union of ";

	public static String LOG_COMPCFBETW = "Computing current flow betweenness... ";

	public static String LOG_COMPCFCLOS = "Computing current flow closeness... ";

	public static String LOG_COMPCF_NOT = "Current flow centrality measures will not be computed for this network.";

	public static String LOG_COMPRW_NOT = "Random walk centrality measures will not be computed for this network.";

	public static String LOG_COMPSP_NOT = "Shortest path centrality measures will not be computed for this network.";

	public static String LOG_COMPSPBETW = "Computing shortest path betweenness... ";

	public static String LOG_COMPSPCLOS = "Computing shortest path closeness... ";

	public static String LOG_COMPSPDEG = "Computing shortest path degree... ";

	public static String LOG_COMPRWBETW = "Computing random walk betweenness... ";

	public static String LOG_COMPRWRCLOS = "Computing random walk closeness... ";

	// public static String LOG_COMPRWTCLOS =
	// "Computing random walk transmitter closeness... ";

	public static String LOG_DONE = "done!\n";

	public static String LOG_DELETENODES = "Deleted nodes: ";

	public static String LOG_DELETESET = "Deleted set: ";

	public static String LOG_INVERTEDSET = " is the inversion of ";

	public static String LOG_NAME = "RINalyzer Log";

	public static String LOG_NEWSET = "Created new set: ";

	public static String LOG_RENAMESET1 = "Renamed set from ";

	public static String LOG_RENAMESET2 = " to ";

	public static String LOG_SELECTEDNODES = "Selected nodes: ";

	public static String LOG_SETSAVED = "Saved set(s): ";

	public static String LOG_WARNMODELCHANGE = "The Chimera model has been changed and could not be linked to a Cytoscape network anymore.";

	public static String LOG_WARNSELECT = "Selection could not be performed.";

	public static String LOG_WARNNORESPONSE = "No input received from Chimera.";

	// Short informative messages to the user

	public static String SM_BADINPUT = "<html>Selected input directory is not acceptable. <br> "
			+ "Please make sure you have selected an existing non-empty directory.</html>";

	public static String SM_BADOUTPUT = "Selected output directory is not acceptable. <br> "
			+ "Please make sure you have selected an existing empty directory<br>"
			+ "with write permissions.";

	public static String SM_CHIMERA = "chimera";

	public static String SM_CHIMERAPATH = "Chimera.chimeraPath";

	public static String SM_CHIMERANOTRUN = "Can not run program ";

	public static String SM_CHIMERANOTRUN1 = "\nPlease insert the path to your Chimera application below: \n";

	public static String SM_CHIMERANOTRUN2 = "\nTry to add the property Chimera.chimeraPath (= path to your Chimera application) using Cytoscape Preferences Editor!";

	public static String SM_CHIMERACOMMAND = "Unable to execute command ";

	public static String SM_CHIMERARETURN = "Unexpected return from Chimera ";

	public static String SM_CHIMERA_LOADPDB = "Please load the pdb structure corresponding to the selected network!";

	public static String SM_CHIMERA_CLOSEMODELS = "Please close all structures except for the one corresponding to the selected network!";

	public static String SM_CONNCOMP = "<html><i>Since the network has more than one connected components, <br> "
			+ "only shortest path measures can be computed. </i></html>";

	public static String SM_CREATEVIEW = "Please create a network view!";

	public static String SM_CREATE_LOAD_SET = "Please load or create a set first!";

	public static String SM_DISCRETEEDITOR = "Discrete editor cannot be shown!";

	public static String SM_DIFFNETSERROR = "Please select different networks for the comparison!";

	public static String SM_ENTERNAME = "Please enter a name!";

	public static String SM_ENTERNEWNAME = "Please enter a network name that does not exist already!";

	public static String SM_ERRORCOMP = "An error occurred during computation.";

	public static String SM_FILEEXISTS = "<html>The specified file already exists.<br>Overwrite?";

	public static String SM_GIVEPDB = "Chosen file is not acceptable.";

	public static String SM_GUIERROR = "An error occurred while initializing the window.";

	public static String SM_INVFORMAT_OPEN = "Invalid RIN format. Structure will not be opened, "
			+ "because residues and nodes cannot be mapped.";

	public static String SM_INVFORMAT_CHAIN = "Invalid RIN format. Some node labels do not specify a chain id.";

	public static String SM_INVFORMAT_PDB = "Invalid RIN format. Nodes should have the same pdb id.";

	public static String SM_IOERRORSAVE = "An error occurred while creating or writing to the file.";

	public static String SM_IOERROROPEN = "An error occurred while opening the file.";

	public static String SM_LOADNETWORK = "Please load a network first!";

	public static String SM_LOADNETWORKS = "Please load at least two networks!";

	public static String SM_MATCHING = "Correspondence between network and PDB structure:";

	public static String SM_NOALIGNFILE = "Please provide an alignment file!";

	public static String SM_NOBOOLATTR = "There are no boolean attributes (selections) available.";

	public static String SM_NONETWORK = "No network";

	public static String SM_NONODES = "<html>There were no nodes in the set that are contained in the<br>"
			+ "current network and therefore no network will be created.</html>";

	public static String SM_NOSETFILE = "This is not a valid node set file.";

	public static String SM_NOSETSTOLOAD = "There were no sets to be loaded.";

	public static String SM_NOSTRUCT = "<html> <b>The PDB structure could not be opened!</b><br> "
			+ "Please check if this is a valid PDB file.<br></html>";

	public static String SM_NOSUCCESS = "<html>Successfully loaded the selected PDB structure in Chimera, <br> "
			+ "but could not match the network nodes and the structure entities!<br>"
			+ "Please check if this structure corresponds to the selected network.</html>";

	public static String SM_NOTHINGCOMP = "Centrality measures have not been computed.";

	public static String SM_NOTHINGTOCOMP = "Please select a group of centrality measures to compute!";

	public static String SM_NOTALLNODES1 = "<html> Some of the nodes in the set are not contained in the network<br>";

	public static String SM_NOTALLNODES2 = " and therefore cannot be added.<html>";

	public static String SM_ONLYNODESNET = "<html>Some of the nodes in the set are not contained in the<br>"
			+ "current network and cannot be added to the new network.</html>";

	public static String SM_ONLYNODESANAL = "<html>Some of the nodes in the set are not contained in the<br>"
			+ "current network. The analyzed set will contain only the<br>"
			+ "nodes that are both in the set and in the network.</html>";

	public static String SM_OPENSTRUCT = " is already opened. You are not allowed to open the same model twice.";

	public static String SM_PROPSFAIL = "Could not save current visual properties as default.";

	public static String SM_RENAMED1 = "<html>A set with the name ";

	public static String SM_RENAMED2 = " already exists and the set will be renamed to ";

	public static String SM_LOADSAMENETWORK = "Please load the network to which the nodes in the selected set belong!";

	public static String SM_SAMENETWORKS = "Please select two sets containing nodes from the same network!";

	public static String SM_SELECTNETWORK = "Please select a network from the list of loaded networks.";

	public static String SM_SELECTNODES = "<html>Please select at least 2 nodes for analysis!<br><br>"
			+ "<font size=\"-2\">Centrality measures can be computed only with respect to a set of<br>"
			+ "selected nodes within the network, e.g. all nodes in the network.</font></html>";

	public static String SM_SELECTSINGLENET = "Please select a single network from the list of loaded networks.";

	public static String SM_SELECT_ONE_SET = "Please select one set!";

	public static String SM_SELECT_TWO_SETS = "Please select two sets!";

	public static String SM_SELECT_SET = "Please select a set from the list of sets.";

	public static String SM_SELECT_SETNODES = "Please select set nodes.";

	public static String SM_SETSAVED = "Selected set(s) has been saved successfully.";

	public static String SM_SUCCESS = "<html>Successfully loaded the selected PDB structure in Chimera <br> "
			+ "and matched the network nodes with the structure entities!</html>";

	public static String SM_UNMNODES = "Cytoscape nodes not in the PDB structure: ";

	public static String SM_UNMOUTOF = " out of ";

	public static String SM_UNMRES = "Chimera entities not in the network: ";

	// Tool tip texts
	public static String TT_ANALYSIS_SET = "Select in the current network view the nodes selected for the computation";

	public static String TT_ANALYSIS_SETTINGS = "Show all analysis settings used for the current computation";

	public static String TT_APPLY_VIS = "<html>Apply the current visual properties on the network. <br>"
			+ "Note that a new visual style is created for each network <br>"
			+ "and can be further changed from the VizMapper panel.</html>";

	// public static String TT_BETWPAIRS = "Excluding these paths makes sense
	// only if the network is
	// connected.";

	public static String TT_BETWPAIRS = "<html>This option is enabled when the selected nodes belong to distinct<br>"
			+ "sets and influences the computation of betweennes measures.</html>";

	public static String TT_CONVERTWEIGHT = "<html>Conversion is recommended if the weights are similarity<br>"
			+ "scores because the centrality measures are defined only<br>"
			+ "for distance scores.</html>";

	public static String TT_DEFWEIGHTVALUE = "All edges without an edge weight are assigned the default weight.";

	public static String TT_DEGREECUTOFF = "<html>Shortest path degree counts the neighbors<br>"
			+ "that are at distance &lt;= <i>Cutoff</i>.</html>";

	public static String TT_EDGESPACE = "Recommended size is edge width + 1";

	public static String TT_MULTIPLEEDGES = "<html>Either choose only one edge type or the weights <br>"
			+ "of multiple edges will be averaged.</html>";

	public static String TT_NETWORK = "Network: ";

	public static String TT_NONETWORK = "<html><i>No network</i></html>";

	public static String TT_REMOVE_NEGWEIGHT = "Negative weights have to be removed before computation.";

	public static String TT_RESTORE = "Restore default visual properties";

	public static String TT_SAVE_ALL = "Save all centrality values in a file";

	public static String TT_SAVE_CENT = "Save centrality values in a file";

	public static String TT_SETDEFAULT = "Save current visual properties as default properties";

	public static String TT_SHOW_ALL = "Show all centrality values in a table";

	public static String TT_SHOW_CENT = "Show centrality values in a table";

	// Help page

	public static String HELP_DOCU = "http://www.rinalyzer.de/docu/";

	public static String HELP_ANALYSIS = HELP_DOCU + "cent_analysis.php#settings";

	public static String HELP_COMPARISON = HELP_DOCU + "comparison.php";

	public static String HELP_GENERAL = HELP_DOCU + "index.php";

	public static String HELP_MEASURES = HELP_DOCU + "cent_analysis.php#measures";

	public static String HELP_NODESETS = HELP_DOCU + "nodesets.php";

	public static String HELP_VISPROPS = HELP_DOCU + "visualprops.php";

	// Chimera Commands
	public static String CC_CLOSE = "close";

	public static String CC_COFR = "cofr ";

	public static String CC_COLOR = "color";

	public static String CC_COLORDEF = "colordef";

	public static String CC_CLOSEMODEL = "close #";

	public static String CC_DESELECT = "~sel";

	public static String CC_EXIT = "exit";

	public static String CC_FETCH = "fetch";

	public static String CC_FOCUS = "focus";

	public static String CC_GETCOORD = "getcrd ";

	public static String CC_HIDEBB = "hidebb";

	public static String CC_KSDSSP = "ksdssp";

	public static String CC_LISTCHAINS = "listc";

	public static String CC_LISTMMOL = "listm type molecule";

	public static String CC_LISTMMOLSPEC = "listm type molecule spec #";

	public static String CC_LISTR = "listr spec #";

	public static String CC_LISTSMOL = "lists level molecule";

	public static String CC_LISTSRES = "lists level residue";

	public static String CC_MATCH1 = "matchmaker ";

	public static String CC_MATCH2 = " pair ss verbose true";

	public static String CC_NONE = "none";

	public static String CC_OPENMODEL = "open ";

	public static String CC_OPEN = "open";

	public static String CC_REPR_STICK = "repr stick #";

	public static String CC_REPR_SHOWATOMS = "show #";

	public static String CC_REPR_RIBBON = "ribbon #";

	public static String CC_REPR_NORIBBON = "~ribbon #";

	public static String CC_SELECT = "sel ";

	public static String CC_SHOWBB = "showbb";

	public static String CC_SYNC = "sync";

	public static String CC_STARTMODELS = "listen start models; ";

	public static String CC_STARTSELECT = "listen start select; ";

	public static String CC_STOP = "stop really";

	public static String CC_STOPMODELS = "listen stop models; ";

	public static String CC_STOPSELECT = "listen stop select; ";

	// Other
	public static String BGCOLOR = "Background";

	public static String EDGE_INTERACTIONS = "interaction";

	public static String EDGE_BACKBONE = "backbone";

	public static String EDGE_DEFAULT = "pp";

	public static String EDGE_BELONGSTO = "BelongsTo";

	public static String EXT_RINSTATS = ".centstats";

	public static String EXT_RINSTATSNAME = "Centralities statistics file";

	public static String EXT_HTML = ".html";

	public static String EXT_SET = ".nodeset";

	public static String EXT_SETNAME = "Node set files (*.nodeset)";

	public static String HEADER_PROPS = "# RINalyzer visual properties";

	public static String HEADER_SET = "# RINalyzer node sets";

	public static String NODE_BELONGSTO = "BelongsTo";

	public static String NODE_COMBILABEL = "CombinedLabel";

	public static String NODE_ORIGINALNET1 = "OriginalNet1";

	public static String NODE_ORIGINALNET2 = "OriginalNet2";

	public static String NODE_PDB = "pdbFileName";

	public static String NONE = "None";

	public static String DELIMITER = "\n";

	public static String NET1 = "net1";

	public static String NET2 = "net2";

	public static String BOTH = "net1,net2";

	public static String COMPVISSTYLE = "Comparison style";

	// Secondary structure attribute
	public static String SS_ATTR_NAME = "SS";

	public static String SS_ATTR_NAME_ALT = "SecondaryStructure";

	public static String SS_HELIX = "Helix";

	public static String SS_HELIX_ALT = "H";

	public static String SS_SHEET = "Sheet";

	public static String SS_SHEET_ALT1 = "S";

	public static String SS_SHEET_ALT2 = "E";

	public static String SS_LOOP = "Loop";

	public static String SS_LOOP_ALT1 = "L";

	public static String SS_LOOP_ALT2 = "C";

	public static String SS_DEFAULT = "default";

	public static String SS_DEFAULT_ALT1 = "U";

	public static String SS_DEFAULT_ALT2 = "-";

	// Centrality measures
	public static String CENT_CFB = "Current Flow Betweenness";

	public static String CENT_CFC = "Current Flow Closeness";

	public static String CENT_SPB = "Shortest Path Betweenness";

	public static String CENT_SPC = "Shortest Path Closeness";

	public static String CENT_SPD = "Shortest Path Degree";

	public static String CENT_RWRC = "Random Walk Closeness";

	public static String CENT_RWTC = "Random Walk Transmitter Closeness";

	public static String CENT_RWB = "Random Walk Betweenness";

	public static String CENT_NODEID = "Node ID";

	// Color types
	public static String COLOR_NODE = "Node";

	public static String COLOR_EDGE = "Edge";

	public static String COLOR_GENERAL = "General";

	/**
	 * Array with the names of the centralities that could be computed.
	 */
	public static final String[] centralities = new String[] { CENT_SPB, CENT_SPC, CENT_SPD,
			CENT_CFB, CENT_CFC, CENT_RWB, CENT_RWRC };

	// If you change this array, change method @ChimeraUtils#showMappingInfo
	public static final String[] residueTypes = new String[] { "amino acid residues",
			"water molecules", "het molecules", "other entities" };

	public static final String[] nodesSecondStruct = new String[] { SS_DEFAULT, SS_HELIX, SS_SHEET,
			SS_LOOP };
	
	static final String[] defaultStructureKeys = { "Structure", "pdb", "pdbFileName", "PDB ID",
		"structure", "biopax.xref.PDB", "pdb_ids", "ModelName", "ModelNumber" };

	public static final String SV_RINRESIDUE = "RINalyzerResidue";

	public static final String SV_INTSUBTYPE = "InteractionSubtype";

	public static final String SV_CHIMERATABLE = "ChimeraTable";

	public static final String SV_CHIMERAOUTPUT = "ChimeraOutput";
	
	public static final String SV_CHIMERARESIDUE = "ChimeraResidue";
	
	public static final String SV_RESCOORDINATES = "Coordinates";

	public static final String SV_ANNOTATECOMMANDTASK = "(&(commandNamespace=structureViz)(command=annotateRIN))";

	public static final String SV_CLOSECOMMANDTASK = "(&(commandNamespace=structureViz)(command=closeStructuresNodes))";

	public static final String SV_CREATERINCOMMANDTASK = "(&(commandNamespace=structureViz)(command=createRIN))";

	public static final String SV_OPENCOMMANDTASK = "(&(commandNamespace=structureViz)(command=openStructuresNodes))";

	public static final String SV_SENDCOMMANDTASK = "(&(commandNamespace=structureViz)(command=sendCommand))";

	public static final String SV_SYNCCOLORSTASK = "(&(commandNamespace=structureViz)(command=syncColors))";

	public static final String SV_COMMANDTUNABLE = "command";

	public static final String[] resNameList = { SV_RINRESIDUE, CyNetwork.NAME };
	// , "OriginalNet1", "OriginalNet2"

	public static final Map<String, String> aaNames;

	public static final Map<String, String> edgeTypeNames;

	// public static final Map<String, String> nodeAttrNames;

	public static final Map<String, Color> colors;

	public static final int[] sizeConst;

	static {
		aaNames = new HashMap<String, String>(32);
		aaNames.put("ALA", "A Ala Alanine");
		aaNames.put("ARG", "R Arg Arginine");
		aaNames.put("ASN", "N Asn Asparagine");
		aaNames.put("ASP", "D Asp Aspartic_acid");
		aaNames.put("CYS", "C Cys Cysteine");
		aaNames.put("GLN", "Q Gln Glutamine");
		aaNames.put("GLU", "E Glu Glumatic_acid");
		aaNames.put("GLY", "G Gly Glycine");
		aaNames.put("HIS", "H His Histidine");
		aaNames.put("ILE", "I Ile Isoleucine");
		aaNames.put("LEU", "L Leu Leucine");
		aaNames.put("LYS", "K Lys Lysine");
		aaNames.put("MET", "M Met Methionine");
		aaNames.put("PHE", "F Phe Phenylalanine");
		aaNames.put("PRO", "P Pro Proline");
		aaNames.put("SER", "S Ser Serine");
		aaNames.put("THR", "T Thr Threonine");
		aaNames.put("TRP", "W Trp Tryptophan");
		aaNames.put("TYR", "Y Tyr Tyrosine");
		aaNames.put("VAL", "V Val Valine");
		aaNames.put("ASX", "B Asx Aspartic_acid_or_Asparagine");
		aaNames.put("GLX", "Z Glx Glutamine_or_Glutamic_acid");
		aaNames.put("XAA", "X Xaa Any_or_unknown_amino_acid");

		colors = new HashMap<String, Color>(32);
		colors.put(BGCOLOR, Color.WHITE);
		colors.put(SS_DEFAULT, Color.PINK);
		colors.put(SS_HELIX, Color.RED);
		colors.put(SS_SHEET, Color.BLUE);
		colors.put(SS_LOOP, Color.GRAY);
		colors.put(EDGE_BACKBONE, Color.BLACK);
		colors.put(EDGE_DEFAULT, Color.BLACK);
		colors.put("combi:all_all", Color.BLACK);
		colors.put("cnt:mc_mc", new Color(0, 0, 255));
		colors.put("cnt:mc_sc", new Color(0, 153, 255));
		colors.put("cnt:sc_sc", new Color(153, 204, 255));
		colors.put("hbond:mc_mc", new Color(153, 0, 51));
		colors.put("hbond:mc_sc", new Color(255, 0, 0));
		colors.put("hbond:sc_sc", new Color(255, 204, 204));
		colors.put("ovl:mc_mc", new Color(51, 51, 51));
		colors.put("ovl:mc_sc", new Color(153, 153, 153));
		colors.put("ovl:sc_sc", new Color(204, 204, 204));
		colors.put("cnt:mc_lig", new Color(0, 153, 51));
		colors.put("cnt:sc_lig", new Color(102, 255, 0));
		colors.put("hbond:mc_lig", new Color(153, 153, 0));
		colors.put("hbond:sc_lig", new Color(255, 255, 0));

//		colors.put("contact mc_mc", new Color(0, 0, 255));
//		colors.put("contact mc_sc", new Color(0, 153, 255));
//		colors.put("contact mc_other", new Color(0, 153, 255));
//		colors.put("contact mc_water", new Color(0, 153, 255));
//		colors.put("contact sc_sc", new Color(153, 204, 255));
//		colors.put("contact sc_water", new Color(153, 204, 255));
//		colors.put("contact other_sc", new Color(153, 204, 255));
//		colors.put("contact other_water", new Color(153, 204, 255));
//		colors.put("clash mc_mc", new Color(51, 51, 51));
//		colors.put("clash mc_sc", new Color(153, 153, 153));
//		colors.put("clash mc_other", new Color(153, 153, 153));
//		colors.put("clash mc_water", new Color(153, 153, 153));
//		colors.put("clash sc_sc", new Color(204, 204, 204));
//		colors.put("clash sc_water", new Color(204, 204, 204));
//		colors.put("clash other_sc", new Color(204, 204, 204));
//		colors.put("clash other_water", new Color(204, 204, 204));
//		colors.put("hbond mc_mc", new Color(153, 0, 51));
//		colors.put("hbond mc_sc", new Color(255, 0, 0));
//		colors.put("hbond mc_other", new Color(255, 0, 0));
//		colors.put("hbond mc_water", new Color(255, 0, 0));
//		colors.put("hbond sc_sc", new Color(255, 204, 204));
//		colors.put("hbond sc_water", new Color(255, 204, 204));
//		colors.put("hbond other_sc", new Color(255, 204, 204));
//		colors.put("backbone mc_mc", Color.BLACK);
//		colors.put("distance mc_mc", Color.GREEN);
//		colors.put("combi all_all", Color.BLACK);

		edgeTypeNames = new HashMap<String, String>(16);
		edgeTypeNames.put("combi:all_all", "Combined interaction");
		edgeTypeNames.put("cnt:mc_mc", "Interatomic contact btw main chains");
		edgeTypeNames.put("cnt:mc_sc", "Interatomic contact btw main and side chains");
		edgeTypeNames.put("cnt:sc_sc", "Interatomic contact btw side chains");
		edgeTypeNames.put("hbond:mc_mc", "Hydrogen bond btw main chains");
		edgeTypeNames.put("hbond:mc_sc", "Hydrogen bond btw main and side chains");
		edgeTypeNames.put("hbond:sc_sc", "Hydrogen bond btw side chains");
		edgeTypeNames.put("ovl:mc_mc", "Overlap btw main chains");
		edgeTypeNames.put("ovl:mc_sc", "Overlap btw main and side chains");
		edgeTypeNames.put("ovl:sc_sc", "Overlap btw side chains");
		edgeTypeNames.put("cnt:mc_lig", "Interatomic contact btw main chain and ligand");
		edgeTypeNames.put("cnt:sc_lig", "Interatomic contact btw side chain and ligand");
		edgeTypeNames.put("hbond:mc_lig", "Hydrogen bond btw main chain and ligand");
		edgeTypeNames.put("hbond:sc_lig", "Hydrogen bond btw side chain and ligand");

		edgeTypeNames.put("pp", "Protein-protein interaction");
		edgeTypeNames.put("pd", "Protein-domain interaction");
		edgeTypeNames.put("dd", "Domain-domain interaction");

		sizeConst = new int[6];
		sizeConst[0] = 4;
		sizeConst[1] = 2;
		sizeConst[2] = 3;
		sizeConst[3] = 12;
		sizeConst[4] = 40;
		sizeConst[5] = 1;
	}

	public static String getFullName(String aEntry) {
		if (edgeTypeNames.containsKey(aEntry)) {
			return edgeTypeNames.get(aEntry);
		}
		return null;
	}

}