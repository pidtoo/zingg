package zingg.spark.model;

import java.io.IOException;
import java.util.Map;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Column;

import zingg.client.FieldDefinition;
import zingg.client.ZFrame;
import zingg.feature.Feature;

public class SparkLabelModel extends SparkModel{
	
	public SparkLabelModel(Map<FieldDefinition, Feature> f) {
		super(f);
		// TODO Auto-generated constructor stub
	}

	@Override	
	public void fit(ZFrame<Dataset<Row>,Row,Column> pos, ZFrame<Dataset<Row>,Row,Column> neg) {
		//create features
		Pipeline pipeline = new Pipeline();
		pipeline.setStages(pipelineStage.toArray(new PipelineStage[pipelineStage.size()]));
		PipelineModel pm = pipeline.fit(transform(pos.union(neg)).df().coalesce(1).cache());
		transformer = pm;	
	}
	
	
	@Override
	public void save(String path) throws IOException{
		((PipelineModel) transformer).write().overwrite().save(path);
	}


}
