

import java.util.ArrayList;
import java.util.Arrays;

import mklab.JGNN.adhoc.IdConverter;
import mklab.JGNN.adhoc.ModelBuilder;
import mklab.JGNN.adhoc.parsers.LayeredBuilder;
import mklab.JGNN.core.Matrix;
import mklab.JGNN.core.Tensor;
import mklab.JGNN.core.matrix.SparseMatrix;
import mklab.JGNN.core.tensor.DenseTensor;
import mklab.JGNN.nn.Loss;
import mklab.JGNN.nn.Model;
import mklab.JGNN.nn.initializers.XavierNormal;
import mklab.JGNN.nn.loss.CategoricalCrossEntropy;
import mklab.JGNN.nn.optimizers.Adam;
import mklab.JGNN.nn.optimizers.BatchOptimizer;

public class GraphClassification {

	public static void main(String[] args) throws Exception {
		IdConverter nodeLabelIds = new IdConverter();
		nodeLabelIds.getOrCreateId("A");
		nodeLabelIds.getOrCreateId("B");
		nodeLabelIds.getOrCreateId("C");
		
		IdConverter graphLabelIds = new IdConverter();
		graphLabelIds.getOrCreateId("0");
		graphLabelIds.getOrCreateId("1");
		
		
		ArrayList<Matrix> graphMatrices = new ArrayList<Matrix>();
		ArrayList<Matrix> nodeFeatures = new ArrayList<Matrix>();
		ArrayList<Tensor> graphLabels = new ArrayList<Tensor>();
		
		graphMatrices.add(new SparseMatrix(3, 3)
				.put(0, 1, 1).put(1, 0, 1)
				.put(1, 2, 1).put(2, 1, 1)
				.put(2, 0, 1).put(0, 2, 1));
		nodeFeatures.add(new SparseMatrix(3, nodeLabelIds.size())
				.put(0, nodeLabelIds.getId("A"), 1)
				.put(1, nodeLabelIds.getId("B"), 1)
				.put(2, nodeLabelIds.getId("B"), 1));
		graphLabels.add(new DenseTensor(graphLabelIds.size()).put(graphLabelIds.getId("0"), 1));
		

		graphMatrices.add(new SparseMatrix(3, 3)
				.put(0, 1, 1).put(1, 0, 1)
				.put(2, 0, 1).put(0, 2, 1));
		nodeFeatures.add(new SparseMatrix(3, nodeLabelIds.size())
				.put(0, nodeLabelIds.getId("B"), 1)
				.put(1, nodeLabelIds.getId("A"), 1)
				.put(2, nodeLabelIds.getId("B"), 1));
		graphLabels.add(new DenseTensor(graphLabelIds.size()).put(graphLabelIds.getId("0"), 1));
		
		
		graphMatrices.add(new SparseMatrix(3, 3)
				.put(0, 1, 1).put(1, 0, 1)
				.put(1, 2, 1).put(2, 1, 1)
				.put(2, 0, 1).put(0, 2, 1));
		nodeFeatures.add(new SparseMatrix(3, nodeLabelIds.size())
				.put(0, nodeLabelIds.getId("A"), 1)
				.put(1, nodeLabelIds.getId("C"), 1)
				.put(2, nodeLabelIds.getId("C"), 1));
		graphLabels.add(new DenseTensor(graphLabelIds.size()).put(graphLabelIds.getId("1"), 1));
		

		graphMatrices.add(new SparseMatrix(3, 3)
				.put(0, 1, 1).put(1, 0, 1)
				.put(1, 2, 1).put(2, 1, 1));
		nodeFeatures.add(new SparseMatrix(3, nodeLabelIds.size())
				.put(0, nodeLabelIds.getId("A"), 1)
				.put(1, nodeLabelIds.getId("A"), 1)
				.put(2, nodeLabelIds.getId("C"), 1));
		graphLabels.add(new DenseTensor(graphLabelIds.size()).put(graphLabelIds.getId("1"), 1));
		
		ModelBuilder builder = new LayeredBuilder()
			    .var("A")  
			    .config("features", nodeLabelIds.size())
			    .config("classes", graphLabelIds.size())
			    .layer("h{l+1}=relu(A@(h{l}@matrix(features, 16)))") 
			    .layer("h{l+1}=relu(A@(h{l}@matrix(16, classes)))") 
			    .layer("h{l+1}=softmax(mean(h{l}, row))")
			    .out("h{l}"); 
		
		Model model = builder.getModel().init(new XavierNormal());
		BatchOptimizer optimizer = new BatchOptimizer(new Adam(0.01));
		Loss loss = new CategoricalCrossEntropy();
		for(int epoch=0; epoch<300; epoch++) {
		    for(int graphId=0; graphId<graphLabels.size(); graphId++) {
		         Matrix adjacency = graphMatrices.get(graphId);
		         Matrix features= nodeFeatures.get(graphId);
		         Tensor graphLabel = graphLabels.get(graphId); 
		         model.train(loss, optimizer, 
		              Arrays.asList(features, adjacency), 
		              Arrays.asList(graphLabel));
		    }
			optimizer.updateAll();
			double acc = 0;
		    for(int graphId=0; graphId<graphLabels.size(); graphId++) {
		         Matrix adjacency = graphMatrices.get(graphId);
		         Matrix features= nodeFeatures.get(graphId);
		         Tensor graphLabel = graphLabels.get(graphId); 
		         if(model.predict(Arrays.asList(features, adjacency)).get(0).argmax()==graphLabel.argmax())
		        	 acc += 1;
		    }
		    System.out.println(acc/graphLabels.size());
		}
		
		
		
	}
}