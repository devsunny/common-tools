package com.asksunny.weka.classifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class ColumnNameClassifier {

	private Classifier classifier = null;
	private Instances trainingModel = null;
	private Evaluation evaluation = null;
	private Attribute classAttribute = null;
	private int lengthOfColumn = 32;

	public ColumnNameClassifier(int lengthOfColumn,
			List<ColumnNameClass> trainDataSet) throws Exception {

		this.lengthOfColumn = lengthOfColumn;
		FastVector structure = new FastVector(lengthOfColumn + 1);
		for (int i = 0; i < lengthOfColumn; i++) {
			structure.addElement(new Attribute(String.format("Letter%03d",
					i + 1)));
		}
		Set<String> clazzs = getClassSet(trainDataSet);

		FastVector classAttributeVector = new FastVector(clazzs.size());
		classAttributeVector.addElement("?");
		for (String clz : clazzs) {
			classAttributeVector.addElement(clz);
		}
		
		

		classAttribute = new Attribute("class", classAttributeVector);
		structure.addElement(classAttribute);
		trainingModel = new Instances("Column Name Classifier", structure,
				10000);
		trainingModel.setClassIndex(lengthOfColumn);
		for (ColumnNameClass colClz : trainDataSet) {
			Instance inst = makeInstance(colClz.getColumnName());	
			inst.setValue(classAttribute, colClz.getClazz());
			trainingModel.add(inst);
		}
		classifier = new J48();
		classifier.buildClassifier(trainingModel);
		evaluation = new Evaluation(trainingModel);
		evaluation.evaluateModel(classifier, trainingModel);
	}
	
	
	protected Instance makeInstance(String text)
	{
		Instance inst = new Instance(lengthOfColumn + 1);
		String colname = text.toUpperCase();
		int strlen = colname.length();
		int idx = 0;
		for (int i = 0; i < strlen; i++) {
			char c = colname.charAt(i);
			if (Character.isLetter(c)) {
				inst.setValue(idx++, (double) c);
			}else{
				continue;
			}			
		}		
		if(idx<lengthOfColumn){			
			for(;idx<lengthOfColumn; idx++){
				inst.setValue(idx++, 0.0);
			}
		}
		return inst;
	}

	public String classify(String columnName) throws Exception {
		Instance inst = makeInstance(columnName);
		inst.setValue(classAttribute, "?");
		inst.setDataset(this.trainingModel);
		double correctValue = (double) inst.classValue();
		double predictedValue = classifier.classifyInstance(inst);
		String predictString = classAttribute.value((int) predictedValue);
		;
		return predictString;
	}

	protected Set<String> getClassSet(List<ColumnNameClass> trainDataSet) {
		Set<String> clazzs = new HashSet<>();
		for (ColumnNameClass colClz : trainDataSet) {
			if (colClz.getClazz() != null
					&& !clazzs.contains(colClz.getClazz())) {
				clazzs.add(colClz.getClazz());
			}
		}
		return clazzs;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		List<ColumnNameClass> trainDataSet = new ArrayList<>();
		trainDataSet.add(new ColumnNameClass("SSN", "PI"));
		trainDataSet.add(new ColumnNameClass("CCN", "PI"));
		trainDataSet.add(new ColumnNameClass("SO_SEC_NO", "PI"));
		trainDataSet.add(new ColumnNameClass("ACCOUNT", "PI"));
		trainDataSet.add(new ColumnNameClass("DESCRIPTION", "NOT PI"));
		trainDataSet.add(new ColumnNameClass("PRICE", "NOT PI"));
		trainDataSet.add(new ColumnNameClass("BALANCE", "NOT PI"));
		trainDataSet.add(new ColumnNameClass("date_of_birth", "PI"));
		trainDataSet.add(new ColumnNameClass("due_date", "NOT PI"));
		ColumnNameClassifier cf = new ColumnNameClassifier(32, trainDataSet);
		System.out.println(cf.classify("BAL"));
	}

}
