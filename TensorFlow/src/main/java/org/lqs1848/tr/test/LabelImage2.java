package org.lqs1848.tr.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.exceptions.TensorFlowException;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.StdArrays;
import org.tensorflow.proto.framework.SignatureDef;
import org.tensorflow.proto.framework.TensorInfo;
import org.tensorflow.types.TFloat32;

public class LabelImage2 {

	public static void main(String[] args) {
		String modelDir = "D:\\Workspace\\test\\src\\main\\resources\\models\\";
		String imageFile = "D:\\Workspace\\test\\src\\main\\resources\\images\\example-400x288.jpg";

		try (SavedModelBundle savedModel = SavedModelBundle.load(modelDir)) {
			SignatureDef modelInfo = savedModel.metaGraphDef().getSignatureDefMap().get("serving_default");
			TensorInfo input1 = null;
			TensorInfo output1 = null;
			Map<String, TensorInfo> inputs = modelInfo.getInputsMap();
			for (Map.Entry<String, TensorInfo> input : inputs.entrySet()) {
				if (input1 == null) {
					input1 = input.getValue();
					System.out.println(input1.getName());
				}
				System.out.println(input);
			}
			Map<String, TensorInfo> outputs = modelInfo.getOutputsMap();
			for (Map.Entry<String, TensorInfo> output : outputs.entrySet()) {
				if (output1 == null) {
					output1 = output.getValue();
				}
				System.out.println(output);
			}
			
		} catch (TensorFlowException ex) {
			ex.printStackTrace();
		}
	}

	private static byte[] readAllBytesOrExit(Path path) {
		try {
			return Files.readAllBytes(path);
		} catch (IOException e) {
			System.err.println("Failed to read [" + path + "]: " + e.getMessage());
			System.exit(1);
		}
		return null;
	}

}