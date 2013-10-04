CDDApp User Manual

1. Introduction:
CDDApp is an app that integrates the Conserved Domain Database, the Protein Data
Bank and Chimera with Cytoscape. The app allows the user to load data from the
CDD, display domain information in Custom Graphics and Results Panel, and also
use Chimera to display domains and functional sites on the protein structure.
(Note: In order to display domains using Chimera, Chimera must be installed on
the machine and StructureViz must be installed on Cytoscape.)

2. Loading data from CDD
Before any other features of CDDApp can be used, the user must first load data
from the CDD into the network. To do this, go to the Apps menu and select
cddApp->Load CDD Domains for Network. Select from the drop-down menu the column
containing the identifiers of the protein (the identifier can be the GI Number,
GenBank Accession, Uniprot, or PDB). There can be more than one identifier in
each cell, either as a comma-separated String or list of String(s). After
selecting the column containing the identifiers, it may take a few minutes for
data to be loaded from CDD.

You can also load data from the CDD for a selected set of nodes. Just select a
set of nodes, then go to Apps->Load CDD Domains for selected Node(s).

After the data is loaded, the follow columns will appear in the nodes table:
	 * CDD-Accession accession number of CDD domain
	 * PDB-Chain if it is a PDB ID, the chain ID (in the form of PDB ID + chain)
	 * CDD-Hit-Type type of domain
	 * CDD-From lower limit of the domain
	 * CDD-To upper limit of the domain
	 * CDD-Feature type of feature site
	 * PDB-Chain-Features chain containing the CDD-Feature
	 * CDD-Feature-Type type of CDD feature
	 * CDD-Feature-Site list of residues of the feature site
	 * CDD-Domain-Size size of the domain
	 * CDD-Domain-Chart a Custom Graphics component which shows the domains as
	   a piechart on the node. The size of the domain is proportional to the
	   size of the slice of the pie.
These columns encompass all information downloaded from CDD. Note that the
columns PDB-Chain and PDB-Chain-Features will not always contain PDB IDs, it is
mainly a convenient naming convention. If an ID is a PDB ID, the domains for
each chain in the structure is downloaded, and the ID in PDB-Chain and
PDB-Chain-Features will be in the format of PDB ID + Chain (e. g. chain A of
1JGM will be 1JGMA).

3. Displaying results in Results Panel
Selecting a node will display information about the domains for the protein(s)
of the node in the Results Panel. The domain information is displayed in the
following format:
Node: the name of the nodenode of the network
Protein: name of the protein, followed by a graph with three columns:
	Domain name: Name of the domain (usually a CDD identifier) with a link to
	the CDD with more information about the domain
	Domain Type: The type of domain (whether it is superfamily or specific)
	Domain Range: The residues in the domain

4. Displaying domains using Chimera
From the Apps menu, select cddApp->Open Structure Panel. This will open up a
control panel to open up structures for each node. For each node there is a
drop-down menu for selecting which protein structure to open (in the case of a
node containing more than one protein, there will be many structures to choose
from). Clicking on the button "Open Structure" will open up the structure in
Chimera. After opening the structure, a list of domains and feature sites will
appear in the structure panel, and by clicking the checkbox it will highlight
the domain or feature site on Chimera.