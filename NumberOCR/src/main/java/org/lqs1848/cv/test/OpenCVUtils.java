package org.lqs1848.cv.test;

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RotatedRect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

public class OpenCVUtils { 
	
	/**
	 * 矩形 矫正角度
	 * 
	 * 非直线矫正！！！！
	 * 
	 * @param gray
	 * @return
	 */
	public static Mat adjustAngle(Mat gray){
		//Mat gray = opencv_imgcodecs.imread("E:\\test\\1.jpg", opencv_imgcodecs.IMREAD_GRAYSCALE);

		Mat binImg = new Mat();
		// 二值化
		opencv_imgproc.threshold(gray, binImg, 100, 255, opencv_imgproc.CV_THRESH_BINARY_INV);
		//opencv_imgcodecs.imwrite("E:\\cv\\ezh222.jpg", binImg);
		
		Mat kernel = new Mat(new Size(30, 30), opencv_core.CV_8UC1, new Scalar(255));
		Mat morphologyDst = new Mat();
		opencv_imgproc.morphologyEx(binImg, morphologyDst, opencv_imgproc.MORPH_GRADIENT, kernel, new Point(-1, -1), 2,
				opencv_imgproc.MORPH_RECT, opencv_imgproc.morphologyDefaultBorderValue());
		//查看腐蚀膨胀的信息
		//opencv_imgcodecs.imwrite("E:\\cv\\morphology.jpg", morphologyDst);

		Mat cannyDst = new Mat();
		opencv_imgproc.Canny(morphologyDst, cannyDst, 150, 200);
		//opencv_imgcodecs.imwrite("E:\\cv\\canny.jpg", cannyDst);
		
		// 获取最大矩形
		RotatedRect rect = findMaxRect(cannyDst);
		
		//gray = rotation(gray , rect);
		//opencv_imgcodecs.imwrite("E:\\cv\\xzo1.jpg", gray);
		//return gray;
		
		if(rect == null) {//找不到矩形就直接抛出
			opencv_core.bitwise_not(binImg, binImg);
			return binImg;
		} 
		
		// 旋转矩形
		Mat CorrectImg = rotation(binImg , rect);
		//反色
		opencv_core.bitwise_not(CorrectImg, CorrectImg);
		//弄成白底黑字好让 Tesseract 识别
		opencv_imgcodecs.imwrite("E:\\cv\\xz1.jpg", CorrectImg);
		return CorrectImg;
	}

	public static RotatedRect findMaxRect(Mat cannyMat) {
		MatVector contours = new MatVector();
		Mat hierarchy = new Mat();
		// 寻找轮廓
		opencv_imgproc.findContours(cannyMat, contours, hierarchy, opencv_imgproc.RETR_EXTERNAL,
				opencv_imgproc.CHAIN_APPROX_NONE, new Point(0, 0));
		
		if(contours.size() == 0) return null;
		
		// 找出匹配到的最大轮廓
		double area = opencv_imgproc.boundingRect(contours.get(0)).area();
		int index = 0;
		// 找出匹配到的最大轮廓
		for (int i = 0; i < contours.size(); i++) {
			double tempArea = opencv_imgproc.boundingRect(contours.get(i)).area();
			if (tempArea > area) {
				area = tempArea;
				index = i;
			}
		}
		Mat matOfPoint2f = new Mat(contours.get(index));
		RotatedRect rect = opencv_imgproc.minAreaRect(matOfPoint2f);
		Rect rect2 = opencv_imgproc.boundingRect(contours.get(index));
		opencv_imgcodecs.imwrite("E:\\test\\lk.jpg", cannyMat.apply(rect2));
		return rect;
	}

	public static Mat rotation(Mat cannyMat, RotatedRect rect) {
		double angle = rect.angle();
		Point2f center = rect.center();
		Mat CorrectImg = new Mat(cannyMat.size(), cannyMat.type());
		cannyMat.copyTo(CorrectImg);
		// 得到旋转矩阵算子
		Mat matrix = opencv_imgproc.getRotationMatrix2D(center, angle, 0.8);
		opencv_imgproc.warpAffine(CorrectImg, CorrectImg, matrix, CorrectImg.size(), 1, 0, new Scalar(0, 0, 0, 0));
		return CorrectImg;
	}
	
}// class
