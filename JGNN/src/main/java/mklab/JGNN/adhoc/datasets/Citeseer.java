package mklab.JGNN.adhoc.datasets;

import mklab.JGNN.adhoc.Dataset;

public class Citeseer extends Dataset {
	public Citeseer() {
		downloadIfNotExists("downloads/citeseer/citeseer.feats", 
				"https://github.com/maniospas/graph-data/raw/main/citeseer/citeseer.feats");
		downloadIfNotExists("downloads/citeseer/citeseer.graph", 
				"https://github.com/maniospas/graph-data/raw/main/citeseer/citeseer.graph");
		loadFeatures("downloads/citeseer/citeseer.feats");
		loadGraph("downloads/citeseer/citeseer.graph");
	}
}
